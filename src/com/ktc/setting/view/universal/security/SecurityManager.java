package com.ktc.setting.view.universal.security;

import android.Manifest;
import android.app.ActivityThread;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.UserInfo;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;

import com.android.internal.util.ArrayUtils;

import java.util.ArrayList;

/**
 * install permission manager
 * framework 兼容问题，AppOpsManager.OP_REQUEST_INSTALL_PACKAGES用66代替
 */
public class SecurityManager {

    private static final String TAG = SecurityManager.class.getSimpleName();

    private ArrayList<ApplicationInfo> installApps;
    private Context mContext;
    private AppOpsManager mAppOpsManager;
    private PackageManager mPackageManager;
    private IPackageManager mIPackageManager;

    //filter some apps has system signature
    private static final String[] FILTER_APPS = new String[]{
            "com.android.bluetooth",
            "com.ktc.media",
            "com.ktc.assistant",
            "com.ktc.filemanager",
            "com.zeasn.services.general",
            "com.zeasn.deviceportal.asdprovider.ktc"
    };

    public SecurityManager(Context context) {
        mContext = context;
        mAppOpsManager = mContext.getSystemService(AppOpsManager.class);
        mPackageManager = context.getPackageManager();
        mIPackageManager = ActivityThread.getPackageManager();
    }

    public ArrayList<ApplicationInfo> getInstallerPackages() {
        loadInstallerApps();
        return installApps;
    }

    /**
     * 获取所有带安装权限的应用
     */
    private void loadInstallerApps() {
        installApps = new ArrayList<>();
        IPackageManager mIpm = AppGlobals.getPackageManager();
        UserManager mUm = mContext.getSystemService(UserManager.class);
        int mAdminRetrieveFlags = PackageManager.MATCH_ANY_USER |
                PackageManager.MATCH_DISABLED_COMPONENTS |
                PackageManager.MATCH_DISABLED_UNTIL_USED_COMPONENTS;
        int mRetrieveFlags = PackageManager.MATCH_DISABLED_COMPONENTS |
                PackageManager.MATCH_DISABLED_UNTIL_USED_COMPONENTS;
        for (UserInfo user : mUm.getProfiles(UserHandle.myUserId())) {
            try {
                @SuppressWarnings("unchecked")
                ParceledListSlice<ApplicationInfo> list =
                        mIpm.getInstalledApplications(
                                user.isAdmin() ? mAdminRetrieveFlags : mRetrieveFlags,
                                user.id);
                for (int i = 0; i < list.getList().size(); i++) {
                    ApplicationInfo applicationInfo = list.getList().get(i);
                    PermissionState permissionState = createPermissionStateFor(applicationInfo.packageName
                            , applicationInfo.uid);
                    boolean canAdd = permissionState.isPermissible()
                            && !filterApps(applicationInfo.packageName)
                            /*&& !isSystemApp(applicationInfo.packageName)*/;
                    if (canAdd) {
                        installApps.add(applicationInfo);
                        Log.d(TAG, "package : " + applicationInfo.packageName);
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean filterApps(String pkgName) {
        for (String pkg : FILTER_APPS) {
            if (pkg.equals(pkgName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSystemApp(String pkgName) {
        boolean isSystemApp = false;
        PackageInfo pi = null;
        try {
            PackageManager pm = mContext.getPackageManager();
            pi = pm.getPackageInfo(pkgName, 0);
        } catch (Throwable t) {
            Log.w(TAG, t.getMessage(), t);
        }
        if (pi != null) {
            boolean isSysApp = (pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
            boolean isSysUpd = (pi.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1;
            isSystemApp = isSysApp || isSysUpd;
        }
        return isSystemApp;
    }

    /**
     * 设置安装权限
     */
    public void setCanInstall(ApplicationInfo applicationInfo, boolean newState) {
        try {
            String packageName = applicationInfo.packageName;
            ApplicationInfo ai = mPackageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            mAppOpsManager.setMode(/*AppOpsManager.OP_REQUEST_INSTALL_PACKAGES*/66,
                    ai.uid, packageName,
                    newState ? AppOpsManager.MODE_ALLOWED : AppOpsManager.MODE_ERRORED);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否具有安装权限
     */
    public boolean canInstall(ApplicationInfo applicationInfo) {
        PermissionState permissionState = createPermissionStateFor(applicationInfo.packageName
                , applicationInfo.uid);
        return permissionState.isAllowed();
    }

    private PermissionState createPermissionStateFor(String packageName, int uid) {
        return new PermissionState(
                hasRequestedAppOpPermission(getPermission(), packageName),
                hasPermission(uid),
                getAppOpMode(uid, packageName));
    }

    private boolean hasPermission(int uid) {
        try {
            int result = mIPackageManager.checkUidPermission(getPermission(), uid);
            return result == PackageManager.PERMISSION_GRANTED;
        } catch (RemoteException e) {
            Log.e(TAG, "PackageManager dead. Cannot get permission info");
            return false;
        }
    }

    private int getAppOpMode(int uid, String packageName) {
        return mAppOpsManager.checkOpNoThrow(getAppOpsOpCode(), uid, packageName);
    }

    public int getAppOpsOpCode() {
        return /*AppOpsManager.OP_REQUEST_INSTALL_PACKAGES*/66;
    }

    private boolean hasRequestedAppOpPermission(String permission, String packageName) {
        try {
            String[] packages = mIPackageManager.getAppOpPermissionPackages(permission);
            return ArrayUtils.contains(packages, packageName);
        } catch (RemoteException exc) {
            Log.e(TAG, "PackageManager dead. Cannot get permission info");
            return false;
        }
    }

    public String getPermission() {
        return Manifest.permission.REQUEST_INSTALL_PACKAGES;
    }

    public static class PermissionState {

        public final boolean permissionRequested;
        public final boolean permissionGranted;
        public final int appOpMode;

        private PermissionState(boolean permissionRequested, boolean permissionGranted,
                                int appOpMode) {
            this.permissionRequested = permissionRequested;
            this.permissionGranted = permissionGranted;
            this.appOpMode = appOpMode;
        }

        /**
         * @return True if the permission is granted
         */
        public boolean isAllowed() {
            if (appOpMode == AppOpsManager.MODE_DEFAULT) {
                return permissionGranted;
            } else {
                return appOpMode == AppOpsManager.MODE_ALLOWED;
            }
        }

        /**
         * @return True if the permission is relevant
         */
        public boolean isPermissible() {
            return appOpMode != AppOpsManager.MODE_DEFAULT || permissionRequested;
        }

        @Override
        public String toString() {
            return "[permissionGranted: " + permissionGranted
                    + ", permissionRequested: " + permissionRequested
                    + ", appOpMode: " + appOpMode
                    + "]";
        }
    }
}
