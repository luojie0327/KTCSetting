package com.ktc.debughelper.bean;

/**
 * @author Arvin
 * @version v1.0
 * @date：2018-10-30 上午10:00:00
 */
public class RecentAppInfo {

    public int persistentId;
    public String name;
    public String packageName;
    public String processName;
    public boolean isSystemApp;
    public String permission;
    public int icon;


    public RecentAppInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

    public RecentAppInfo(int persistentId, String name, String packageName,
                         String processName, boolean isSystemApp, String permission, int icon) {
        super();
        this.persistentId = persistentId;
        this.name = name;
        this.packageName = packageName;
        this.processName = processName;
        this.isSystemApp = isSystemApp;
        this.permission = permission;
        this.icon = icon;
    }

    public int getPersistentId() {
        return persistentId;
    }

    public void setPersistentId(int persistentId) {
        this.persistentId = persistentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public boolean isSystemApp() {
        return isSystemApp;
    }

    public void setSystemApp(boolean isSystemApp) {
        this.isSystemApp = isSystemApp;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "ApplicationInfo [name=" + name + ", packageName=" + packageName
                + ", processName=" + processName + ", isSystemApp="
                + isSystemApp + ", permission=" + permission + ", icon=" + icon
                + "]";
    }

}