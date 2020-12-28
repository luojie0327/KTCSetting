package com.ktc.debughelper.bean;

public class AvailableNetWorkInfo {
    public String netWorkType = "";
    public String netWorkName = "";
    public String netWorkProxy = "";
    public String netWorkModel = "";
    public String netWorkStatus = "";
    public String netWorkIp = "";
    public String netWorkMask = "";
    public String netWorkGateway = "";
    public String netWorkDns1 = "";
    public String netWorkDns2 = "";
    public String netWorkMacAddress = "";

    public String getNetWorkType() {
        return netWorkType;
    }

    public void setNetWorkType(String netWorkType) {
        this.netWorkType = netWorkType;
    }

    public String getNetWorkName() {
        return netWorkName;
    }

    public void setNetWorkName(String netWorkName) {
        this.netWorkName = netWorkName;
    }

    public String getNetWorkProxy() {
        return netWorkProxy;
    }

    public void setNetWorkProxy(String netWorkProxy) {
        this.netWorkProxy = netWorkProxy;
    }

    public String getNetWorkModel() {
        return netWorkModel;
    }

    public void setNetWorkModel(String netWorkModel) {
        this.netWorkModel = netWorkModel;
    }

    public String getNetWorkStatus() {
        return netWorkStatus;
    }

    public void setNetWorkStatus(String netWorkStatus) {
        this.netWorkStatus = netWorkStatus;
    }

    public String getNetWorkIp() {
        return netWorkIp;
    }

    public void setNetWorkIp(String netWorkIp) {
        this.netWorkIp = netWorkIp;
    }

    public String getNetWorkMask() {
        return netWorkMask;
    }

    public void setNetWorkMask(String netWorkMask) {
        this.netWorkMask = netWorkMask;
    }

    public String getNetWorkGateway() {
        return netWorkGateway;
    }

    public void setNetWorkGateway(String netWorkGateway) {
        this.netWorkGateway = netWorkGateway;
    }

    public String getNetWorkDns1() {
        return netWorkDns1;
    }

    public void setNetWorkDns1(String netWorkDns1) {
        this.netWorkDns1 = netWorkDns1;
    }

    public String getNetWorkDns2() {
        return netWorkDns2;
    }

    public void setNetWorkDns2(String netWorkDns2) {
        this.netWorkDns2 = netWorkDns2;
    }

    public String getNetWorkMacAddress() {
        return netWorkMacAddress;
    }

    public void setNetWorkMacAddress(String netWorkMacAddress) {
        this.netWorkMacAddress = netWorkMacAddress;
    }

    @Override
    public String toString() {
        return "AvailableNetWorkInfo{" +
                "netWorkType='" + netWorkType + '\'' +
                ", netWorkName='" + netWorkName + '\'' +
                ", netWorkProxy='" + netWorkProxy + '\'' +
                ", netWorkModel='" + netWorkModel + '\'' +
                ", netWorkStatus='" + netWorkStatus + '\'' +
                ", netWorkIp='" + netWorkIp + '\'' +
                ", netWorkMask='" + netWorkMask + '\'' +
                ", netWorkGateway='" + netWorkGateway + '\'' +
                ", netWorkDns1='" + netWorkDns1 + '\'' +
                ", netWorkDns2='" + netWorkDns2 + '\'' +
                ", netWorkMacAddress='" + netWorkMacAddress + '\'' +
                '}';
    }
}