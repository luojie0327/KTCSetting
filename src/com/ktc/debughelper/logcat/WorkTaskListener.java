package com.ktc.debughelper.logcat;


/**
 * @author Arvin
 * @TODO 下载监听器接口
 * @Date 2019.2.15
 */
public interface WorkTaskListener {
    /**
     * @param WorkTaskInfo
     * @TODO 回传下载信息
     */
    void sendWorkTaskInfo(WorkTaskInfo mWorkTaskInfo);

    /**
     * @TODO 任务成功
     */
    void onStarted();

    /**
     * @TODO 任务失败
     */
    void onFailed();

    /**
     * @TODO 任务暂停
     */
    void onPaused();

    /**
     * @TODO 任务取消
     */
    void onCanceled();

    /**
     * @TODO 任务停止
     */
    void onStoped();
}
