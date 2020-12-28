package com.ktc.setting.view.restore.restoreTool;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import com.ktc.setting.helper.VersionUtil;
import com.ktc.setting.view.universal.storage.DiskInfo;
import com.ktc.setting.view.universal.storage.StorageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AndroidRestoreTool {

    private static final String SHUTDOWN_INTENT_EXTRA = "shutdown";


    public static void androidReset(Context context) {

        deleteInternalSD(context);

        if (VersionUtil.isCurrentAndroidOreoOrHigher()) {
            if (!ActivityManager.isUserAMonkey()) {
                Intent resetIntent = new Intent("android.intent.action.FACTORY_RESET");
                resetIntent.setPackage("android");
                resetIntent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                if (((Activity) context).getIntent().getBooleanExtra(SHUTDOWN_INTENT_EXTRA, false)) {
                    resetIntent.putExtra(SHUTDOWN_INTENT_EXTRA, true);
                }
                context.sendBroadcast(resetIntent);
            }
        } else {
            Intent resetIntent = new Intent("android.intent.action.MASTER_CLEAR");
            context.sendBroadcast(resetIntent);
        }
    }

    private static void deleteInternalSD(Context context) {
        List<DiskInfo> diskInfos = StorageUtil.getMountedDisksList(context);
        List<String> paths = new ArrayList<>();
        for (DiskInfo diskInfo : diskInfos) {
            paths.add(diskInfo.getPath());
        }
        if (paths != null && paths.size() > 0) {
            deleteFile(paths.get(0));
        }
    }

    private static void deleteFile(String path) {
        File file = new File(path);
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                String[] childFiles = file.list();
                for (int i = 0; i < childFiles.length; i++) {
                    deleteFile(file.getAbsolutePath() + "/" + childFiles[i]);
                }
                file.delete();
            } else {
                file.delete();
            }
        }
    }


}
