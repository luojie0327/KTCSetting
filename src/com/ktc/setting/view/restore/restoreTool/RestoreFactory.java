package com.ktc.setting.view.restore.restoreTool;

/**
 * 复位工厂类
 */
public class RestoreFactory {

    public static IRestore createRestore(Class<? extends IRestore> clz) {
        IRestore restore = null;
        try {
            restore = (IRestore) Class.forName(clz.getName()).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return restore;
    }

}
