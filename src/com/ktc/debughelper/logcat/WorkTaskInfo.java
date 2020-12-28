package com.ktc.debughelper.logcat;

import java.io.Serializable;

/**
 * @author Arvin
 * @TODO 当前正在进行/中断的任务状态
 * @Date 2019.1.24
 */
public class WorkTaskInfo implements Serializable {

    private int workStatus;
    private String logPath;
    private String recordPath;

    public WorkTaskInfo() {
        super();
    }

    public WorkTaskInfo(int workStatus, String logPath, String recordPath) {
        super();
        this.workStatus = workStatus;
        this.logPath = logPath;
        this.recordPath = recordPath;
    }

    public int getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(int workStatus) {
        this.workStatus = workStatus;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public String getRecordPath() {
        return recordPath;
    }

    public void setRecordPath(String recordPath) {
        this.recordPath = recordPath;
    }

    public String toString() {
        // TODO Auto-generated method stub
        return "WorkTaskInfo:  " + workStatus
                + " : " + logPath + " : " + recordPath;
    }
}
