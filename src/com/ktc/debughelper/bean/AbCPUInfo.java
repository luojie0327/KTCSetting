package com.ktc.debughelper.bean;

public class AbCPUInfo {

    public String User;

    public String System;

    public String IOW;

    public String IRQ;

    public AbCPUInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

    public AbCPUInfo(String user, String system, String iOW, String iRQ) {
        super();
        User = user;
        System = system;
        IOW = iOW;
        IRQ = iRQ;
    }

    @Override
    public String toString() {
        return "AbCPUInfo [User=" + User + ", System=" + System + ", IOW="
                + IOW + ", IRQ=" + IRQ + "]";
    }

}
