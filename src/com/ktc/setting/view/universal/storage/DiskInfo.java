package com.ktc.setting.view.universal.storage;

public class DiskInfo {

    private String name;
    private long availSpace;
    private String path;
    private long totalSpace;
    private long usedSpace;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAvailSpace() {
        return availSpace;
    }

    public void setAvailSpace(long availSpace) {
        this.availSpace = availSpace;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(long totalSpace) {
        this.totalSpace = totalSpace;
    }

    public long getUsedSpace() {
        return usedSpace;
    }

    public void setUsedSpace(long usedSpace) {
        this.usedSpace = usedSpace;
    }
}
