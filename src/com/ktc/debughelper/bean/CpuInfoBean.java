package com.ktc.debughelper.bean;

public class CpuInfoBean {
    private int cpuCount = 0;
    private String cpuBitCount = "";

    public CpuInfoBean(int cpuCount, String cpuBitCount) {
        this.cpuCount = cpuCount;
        this.cpuBitCount = cpuBitCount;
    }

    public int getCpuCount() {
        return cpuCount;
    }

    public void setCpuCount(int cpuCount) {
        this.cpuCount = cpuCount;
    }

    public String getCpuBitCount() {
        return cpuBitCount;
    }

    public void setCpuBitCount(String cpuBitCount) {
        this.cpuBitCount = cpuBitCount;
    }

    @Override
    public String toString() {
        return "CpuInfoBean{" +
                "cpuCount=" + cpuCount +
                ", cpuBitCount='" + cpuBitCount + '\'' +
                '}';
    }
}