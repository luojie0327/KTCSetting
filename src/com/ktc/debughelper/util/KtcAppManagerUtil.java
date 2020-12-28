package com.ktc.debughelper.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.ktc.debughelper.bean.AbCPUInfo;
import com.ktc.debughelper.bean.AppProcessInfo;
import com.ktc.debughelper.bean.ProcessInfo;
import com.ktc.debughelper.bean.RecentAppInfo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TODO 应用管理工具类
 *
 * @author Arvin
 * @Time 2018-11-1 上午09:02:10
 */
public class KtcAppManagerUtil {

    private static final String TAG = "KtcAppManagerUtil";
    private static Context mContext;

    private static KtcAppManagerUtil mKtcAppManagerUtil;

    public static KtcAppManagerUtil getInstance(Context context) {
        mContext = context;
        if (mKtcAppManagerUtil == null) {
            mKtcAppManagerUtil = new KtcAppManagerUtil();
        }
        return mKtcAppManagerUtil;
    }

    public List<RecentAppInfo> getRecentTasks() {
        if (Build.VERSION.SDK_INT >= 26) {
            return getRecentTasksForOreo();
        }
        return getRecentTasksForNormal();
    }

    /**
     * @return List<RecentAppInfo>
     * @TODO 获取当前后台任务列表（适配Android8.0以下版本）
     */
    private List<RecentAppInfo> getRecentTasksForNormal() {
        KtcLogerUtil.I(TAG, "---getRecentTasksForNormal----");
        int MAX_RECENT_TASKS = 15;
        List<RecentAppInfo> applist = new ArrayList<RecentAppInfo>();
        PackageManager pm = mContext.getPackageManager();
        ActivityManager am = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);

        final List<ActivityManager.RecentTaskInfo> recentTasks = am
                .getRecentTasks(MAX_RECENT_TASKS, ActivityManager.RECENT_IGNORE_UNAVAILABLE);
        //		List<ActivityManager.RecentTaskInfo> recentTasks = am
        //				.getRecentTasksForUser(
        //						MAX_RECENT_TASKS,
        //						ActivityManager.RECENT_IGNORE_UNAVAILABLE | ActivityManager.RECENT_INCLUDE_PROFILES,
        //		                UserHandle.CURRENT.getIdentifier());

        KtcLogerUtil.I(TAG, "getRecentTasksForNormal:  " + recentTasks.size());
        ActivityInfo mHomeInfo = new Intent(Intent.ACTION_MAIN).addCategory(
                Intent.CATEGORY_HOME).resolveActivityInfo(pm, 0);

        if (recentTasks != null) {
            for (int i = 0; i < Math.min(MAX_RECENT_TASKS, recentTasks.size()); i++) {
                final ActivityManager.RecentTaskInfo info = recentTasks.get(i);
                //info.persistentId ;//返回真实的标识符（id），不论task是否在运行。
                //info.id ;//描述当前Task是否在运行状态，如果不在运行状态，id为-1。
                Intent intent = new Intent(info.baseIntent);
                if (info.origActivity != null) {
                    intent.setComponent(info.origActivity);
                }

                if (mHomeInfo != null) {
                    if (mHomeInfo.packageName.equals(intent.getComponent().getPackageName())
                            && mHomeInfo.name.equals(intent.getComponent().getClassName())) {
                        continue;
                    }
                }
                intent.setFlags((intent.getFlags() & ~Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                final ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
                if (resolveInfo != null) {
                    ActivityInfo activityInfo = resolveInfo.activityInfo;

                    if (activityInfo != null) {
                        ApplicationInfo mApplicationInfo = activityInfo.applicationInfo;
                        if (mApplicationInfo != null) {
                            boolean isSystem = (mApplicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM;
                            applist.add(new RecentAppInfo(
                                    info.persistentId,
                                    mApplicationInfo.name,
                                    mApplicationInfo.packageName,
                                    mApplicationInfo.processName,
                                    isSystem,
                                    mApplicationInfo.permission,
                                    mApplicationInfo.icon));
                            KtcLogerUtil.I(TAG, "mApplicationInfo.processName:  " + mApplicationInfo.processName);
                        }
                    }
                }
            }
        }

        return applist;
    }

    /**
     * @TODO 获取当前后台任务列表（适配Android8.0及以上版本）
     */
    private List<RecentAppInfo> getRecentTasksForOreo() {
        KtcLogerUtil.I(TAG, "---getRecentTasksForOreo----");
        int MAX_RECENT_TASKS = 15;
        List<RecentAppInfo> applist = new ArrayList<RecentAppInfo>();
        PackageManager pm = mContext.getPackageManager();
        ActivityManager am = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> recentTasks = am.getRunningTasks(MAX_RECENT_TASKS);

        KtcLogerUtil.I(TAG, "getRecentTasksForOreo:  " + recentTasks.size());
        ActivityInfo mHomeInfo = new Intent(Intent.ACTION_MAIN).addCategory(
                Intent.CATEGORY_HOME).resolveActivityInfo(pm, 0);

        if (recentTasks != null) {
            for (int i = 0; i < Math.min(MAX_RECENT_TASKS, recentTasks.size()); i++) {
                final ActivityManager.RunningTaskInfo info = recentTasks.get(i);
                Intent intent = new Intent(info.baseActivity.getPackageName());
                if (info.baseActivity != null) {
                    intent.setComponent(info.baseActivity);
                }

                if (mHomeInfo != null) {
                    if (mHomeInfo.packageName.equals(intent.getComponent().getPackageName())
                            && mHomeInfo.name.equals(intent.getComponent().getClassName())) {
                        continue;
                    }
                }
                intent.setFlags((intent.getFlags() & ~Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                final ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
                if (resolveInfo != null) {
                    ActivityInfo activityInfo = resolveInfo.activityInfo;

                    if (activityInfo != null) {
                        ApplicationInfo mApplicationInfo = activityInfo.applicationInfo;
                        if (mApplicationInfo != null) {
                            boolean isSystem = (mApplicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM;
                            applist.add(new RecentAppInfo(
                                    info.id,
                                    mApplicationInfo.name,
                                    mApplicationInfo.packageName,
                                    mApplicationInfo.processName,
                                    isSystem,
                                    mApplicationInfo.permission,
                                    mApplicationInfo.icon));
                            KtcLogerUtil.I(TAG, "mApplicationInfo.processName:  " + mApplicationInfo.processName);
                        }
                    }
                }
            }
        }

        return applist;
    }

    /**
     * @TODO 获取所有运行的进程列表.
     */
    public List<AppProcessInfo> getRunningAppProcesses() {
        ActivityManager activityManager = null;
        List<AppProcessInfo> list = null;
        PackageManager packageManager = null;
        try {
            activityManager = (ActivityManager) mContext
                    .getSystemService(Context.ACTIVITY_SERVICE);
            packageManager = mContext.getApplicationContext()
                    .getPackageManager();
            list = new ArrayList<AppProcessInfo>();
            // 所有运行的进程
            List<RunningAppProcessInfo> appProcessList = activityManager
                    .getRunningAppProcesses();
            KtcLogerUtil.I(TAG, "getRunningAppProcesses--->appProcessList:  " + appProcessList.size());

            for (RunningAppProcessInfo appProcessInfo : appProcessList) {
                AppProcessInfo abAppProcessInfo = new AppProcessInfo(
                        appProcessInfo.processName, appProcessInfo.pid,
                        appProcessInfo.uid);
                ApplicationInfo mApplicationInfo = getApplicationInfo(appProcessInfo.processName);
                // appInfo.flags;
                KtcLogerUtil.I(TAG, "getRunningAppProcesses--->appProcessInfo:  " + abAppProcessInfo.toString());

                if (mApplicationInfo != null) {
                    //if (mApplicationInfo.isSystemApp() || mApplicationInfo.isUpdatedSystemApp())
                    boolean isSystem = (mApplicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM;
                    if (isSystem) {
                        abAppProcessInfo.isSystem = true;
                    } else {
                        abAppProcessInfo.isSystem = false;
                    }
                    Drawable icon = mApplicationInfo.loadIcon(packageManager);
                    String appName = mApplicationInfo.loadLabel(packageManager).toString();
                    abAppProcessInfo.icon = icon;
                    abAppProcessInfo.appName = appName;
                } else {
                    // :服务的命名
                    if (appProcessInfo.processName.indexOf(":") != -1) {
                        mApplicationInfo = getApplicationInfo(appProcessInfo.processName.split(":")[0]);
                        Drawable icon = mApplicationInfo.loadIcon(packageManager);
                        abAppProcessInfo.icon = icon;
                    }
                    abAppProcessInfo.isSystem = true;
                    abAppProcessInfo.appName = appProcessInfo.processName;
                }

                /*
                 * AbPsRow psRow = getPsRow(appProcessInfo.processName);
                 * if(psRow!=null){ abAppProcessInfo.memory = psRow.mem; }
                 */

				/*ProcessInfo processInfo = getMemoryInfoOfProcess(appProcessInfo.processName , mProcessList);
				abAppProcessInfo.memory = processInfo.memory;
				abAppProcessInfo.cpu = processInfo.cpu;
				abAppProcessInfo.status = processInfo.status;
				abAppProcessInfo.threadsCount = processInfo.threadsCount;*/
                list.add(abAppProcessInfo);

                KtcLogerUtil.I(TAG, "getRunningAppProcesses:  " + abAppProcessInfo.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * @param mPackageName
     * @return PackageInfo
     * @TODO 根据包名获取对应的PackageInfo.
     */
    public PackageInfo getPackageInfo(String mPackageName) {
        PackageInfo info = null;
        try {
            info = mContext.getPackageManager().getPackageInfo(mPackageName,
                    PackageManager.GET_ACTIVITIES);
            KtcLogerUtil.I(TAG, "getPackageInfo:  " + info.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 描述：根据进程名返回应用程序.
     */
    public ApplicationInfo getApplicationInfo(String processName) {
        if (processName == null) {
            return null;
        }

        PackageManager packageManager = mContext.getApplicationContext()
                .getPackageManager();
        List<ApplicationInfo> appList = packageManager
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (ApplicationInfo appInfo : appList) {
            if (processName.equals(appInfo.processName)) {
                KtcLogerUtil.I(TAG, "getApplicationInfo:  " + appInfo.toString());
                return appInfo;
            }
        }
        return null;
    }

    /**
     * 描述：根据进程名获取CPU和内存信息.
     *
     * @param processName
     * @return
     */
    public ProcessInfo getMemoryInfoOfProcess(String processName, List<String[]> mProcessList) {
        ProcessInfo process = new ProcessInfo();
        String processNameTemp = "";

        for (Iterator<String[]> iterator = mProcessList.iterator(); iterator.hasNext(); ) {
            String[] item = (String[]) iterator.next();
            processNameTemp = item[9];
            if (processNameTemp != null && processNameTemp.equals(processName)) {
                // Process ID
                process.pid = Integer.parseInt(item[0]);
                // CPU
                process.cpu = item[2];
                // S
                process.status = item[3];
                // thread
                process.threadsCount = item[4];
                // Mem
                long mem = 0;
                if (item[6].indexOf("M") != -1) {
                    mem = Long.parseLong(item[6].replace("M", "")) * 1000 * 1024;
                } else if (item[6].indexOf("K") != -1) {
                    mem = Long.parseLong(item[6].replace("K", "")) * 1000;
                } else if (item[6].indexOf("G") != -1) {
                    mem = Long.parseLong(item[6].replace("G", "")) * 1000 * 1024 * 1024;
                }
                process.memory = mem;
                // UID
                process.uid = item[8];
                // Process Name
                process.processName = item[9];
                break;
            }
        }
        KtcLogerUtil.I(TAG, "getMemoryInfoOfProcess-->process:  " + process.toString());
        return process;
    }

    /**
     * 描述：根据进程ID获取CPU和内存信息.
     *
     * @param pid
     * @return
     */
    public ProcessInfo getMemoryInfoOfPid(int pid, List<String[]> mProcessList) {
        ProcessInfo process = new ProcessInfo();
        String tempPidString = "";
        int tempPid = 0;
        int count = mProcessList.size();
        for (int i = 0; i < count; i++) {
            String[] item = mProcessList.get(i);
            tempPidString = item[0];
            if (tempPidString == null) {
                continue;
            }
            tempPid = Integer.parseInt(tempPidString);
            if (tempPid == pid) {
                // Process ID
                process.pid = Integer.parseInt(item[0]);
                // CPU
                process.cpu = item[2];
                // S
                process.status = item[3];
                // thread
                process.threadsCount = item[4];
                // Mem
                long mem = 0;
                if (item[6].indexOf("M") != -1) {
                    mem = Long.parseLong(item[6].replace("M", "")) * 1000 * 1024;
                } else if (item[6].indexOf("K") != -1) {
                    mem = Long.parseLong(item[6].replace("K", "")) * 1000;
                } else if (item[6].indexOf("G") != -1) {
                    mem = Long.parseLong(item[6].replace("G", "")) * 1000 * 1024 * 1024;
                }
                process.memory = mem;
                // UID
                process.uid = item[8];
                // Process Name
                process.processName = item[9];
                break;
            }
        }

        KtcLogerUtil.I(TAG, "getMemoryInfoOfPid-->process:  " + process.toString());
        return process;
    }

    /**
     * 描述：使用内核kill进程.
     *
     * @param pid
     */
    public void killProcesses(int pid, String processName) {
        KtcLogerUtil.I(TAG, "killProcesses-->pid:  " + pid + "  processName:  " + processName);
        String cmd = "kill -9 " + pid;
        String Command = "am force-stop " + processName + "\n";
        Process sh = null;
        DataOutputStream os = null;
        try {
            sh = Runtime.getRuntime().exec("/system/xbin/su");
            os = new DataOutputStream(sh.getOutputStream());
            os.writeBytes(Command + "\n");
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            if (sh != null) {
                sh.waitFor();
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 描述：获取进程运行的信息.
     *
     * @return
     */
    public List<String[]> getProcessRunningInfo() {
        List<String[]> processList = null;
        try {
            String result = runCmdTopN1();
            processList = parseProcessRunningInfo(result);

            KtcLogerUtil.I(TAG, "getProcessRunningInfo-->processList:  " + processList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return processList;
    }

    /**
     * @param command
     * @param workdirectory(shell命令所在目录)
     * @return
     * @TODO 执行shell命令.
     */
    private String runCommand(String[] command, String workdirectory) {
        String result = "";
        try {
            ProcessBuilder builder = new ProcessBuilder(command);
            // set working directory
            if (workdirectory != null) {
                builder.directory(new File(workdirectory));
            }
            builder.redirectErrorStream(true);
            Process process = builder.start();
            InputStream in = process.getInputStream();
            byte[] buffer = new byte[1024];
            while (in.read(buffer) != -1) {
                String str = new String(buffer);
                result = result + str;
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @return String
     * @TODO 获取top -n 1.信息，需要3s左右
     */
    private String runCmdTopN1() {
        String result = null;
        try {
            String[] args = {"/system/bin/top", "-n", "1"};
            result = runCommand(args, "/system/bin/");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 描述：解析数据.
     *
     * @param info User 39%, System 17%, IOW 3%, IRQ 0% PID PR CPU% S #THR VSS
     *             RSS PCY UID Name 31587 0 39% S 14 542288K 42272K fg u0_a162
     *             cn.amsoft.process 313 1 17% S 12 68620K 11328K fg system
     *             /system/bin/surfaceflinger 32076 1 2% R 1 1304K 604K bg
     *             u0_a162 /system/bin/top
     * @return
     */
    private List<String[]> parseProcessRunningInfo(String info) {
        List<String[]> processList = new ArrayList<String[]>();
        int Length_ProcStat = 10;
        String tempString = "";
        boolean bIsProcInfo = false;
        String[] rows = null;
        String[] columns = null;
        rows = info.split("[\n]+");
        // 使用正则表达式分割字符串
        for (int i = 0; i < rows.length; i++) {
            tempString = rows[i];
            if (tempString.indexOf("PID") == -1) {
                if (bIsProcInfo == true) {
                    tempString = tempString.trim();
                    columns = tempString.split("[ ]+");
                    if (columns.length == Length_ProcStat) {
                        // 把/system/bin/的去掉
                        if (columns[9].startsWith("/system/bin/")) {
                            continue;
                        }
                        processList.add(columns);
                    }
                }
            } else {
                bIsProcInfo = true;
            }
        }
        return processList;
    }

    /**
     * 描述：解析数据.
     *
     * @param info User 39%, System 17%, IOW 3%, IRQ 0%
     * @return
     */
    private AbCPUInfo parseCPUInfo(String info) {
        AbCPUInfo CPUInfo = new AbCPUInfo();
        String tempString = "";
        String[] rows = null;
        String[] columns = null;
        rows = info.split("[\n]+");
        // 使用正则表达式分割字符串
        for (int i = 0; i < rows.length; i++) {
            tempString = rows[i];
            // AbLogUtil.d(AbAppUtil.class, tempString);
            if (tempString.indexOf("User") != -1
                    && tempString.indexOf("System") != -1) {
                tempString = tempString.trim();
                columns = tempString.split(",");
                for (int j = 0; j < columns.length; j++) {
                    String col = columns[j].trim();
                    String[] cpu = col.split(" ");
                    if (j == 0) {
                        CPUInfo.User = cpu[1];
                    } else if (j == 1) {
                        CPUInfo.System = cpu[1];
                    } else if (j == 2) {
                        CPUInfo.IOW = cpu[1];
                    } else if (j == 3) {
                        CPUInfo.IRQ = cpu[1];
                    }
                }
            }
        }

        KtcLogerUtil.I(TAG, "parseCPUInfo-->CPUInfo:  " + CPUInfo.toString());
        return CPUInfo;
    }

    /**
     * @param pkgName
     * @return boolean
     * @TODO 判断pkgName是否为顶层进程
     */
    public boolean isTopRunningPackage(String pkgName) {
        if (pkgName == null || pkgName.equals("")) {
            return false;
        } else {
            String result = getTopProcessResult();
            if (result != null && result.contains(pkgName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return String
     * @TODO 使用shell命令获取顶层进程信息
     */
    public String getTopProcessResult() {
        try {
            String cmd = "dumpsys activity activities";
            String filter = "mFocusedActivity";
            if (Build.VERSION.SDK_INT >= 26) {
                filter = "mResumedActivity";
            }
            String result = command(cmd, filter);

            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * TODO 运行shell命令过滤指定信息
     *
     * @param cmd
     * @param filter
     * @return String
     * @throws IOException
     */
    private String command(String cmd, String filter) throws IOException {
        Process process = Runtime.getRuntime().exec(cmd);
        InputStream stream = process.getInputStream();
        if (stream != null) {
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader bufferedReader = new BufferedReader(reader, 1024);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(filter)) {
                    break;
                }
            }
            bufferedReader.close();
            reader.close();
            stream.close();
            process.destroy();
            return line;
        }
        return null;
    }

}
