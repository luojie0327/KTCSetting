package com.ktc.setting.view.universal.storage;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import com.ktc.setting.R;
import com.ktc.setting.helper.StringEncoderUtil;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StorageUtil {

    private static final long KB = 1024;
    private static final long MB = KB * 1024;
    private static final long GB = MB * 1024;
    private static final String INTERNAL_STORAGE_PATH = "/storage/emulated/0";

    public static long getTotalSpace(CharSequence path) {
        try {
            StatFs sf = new StatFs((String) path);
            return ((long) sf.getBlockSize()) * ((long) sf.getBlockCount());
        } catch (Exception e) {
            return 0;
        }
    }

    public static long getAvailSpace(CharSequence path) {
        try {
            StatFs sf = new StatFs((String) path);
            return ((long) sf.getBlockSize()) * ((long) sf.getAvailableBlocks());
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 计算内存规格
     *
     * @param size 单位为B
     */
    public static String formatFileSize(long size) {
        String fileSize = "0MB";
        if (size < 512 * MB) {
            fileSize = 512 + "MB";
        } else if (size < 768 * MB) {
            fileSize = 768 + "MB";
        } else if (size < GB) {
            fileSize = 1 + "GB";
        } else if (size < 2 * GB) {
            fileSize = 2 + "GB";
        } else if (size < 4 * GB) {
            fileSize = 4 + "GB";
        } else if (size < 8 * GB) {
            fileSize = 8 + "GB";
        } else if (size < 16 * GB) {
            fileSize = 16 + "GB";
        } else if (size < 32 * GB) {
            fileSize = 32 + "GB";
        }
        return fileSize;
    }

    public static String formatDDRString(long size) {
        String fileSize = "0MB";
        if (size < 512 * MB) {
            fileSize = 512 + "MB";
        } else if (size < 768 * MB) {
            fileSize = 768 + "MB";
        } else if (size < GB) {
            fileSize = 1 + "GB";
        } else if (size < 1.5 * GB) {
            fileSize = 1.5 + "GB";
        } else if (size < 2 * GB) {
            fileSize = 2 + "GB";
        } else if (size < 3 * GB) {
            fileSize = 3 + "GB";
        } else if (size < 4 * GB) {
            fileSize = 4 + "GB";
        } else if (size < 5 * GB) {
            fileSize = 5 + "GB";
        } else if (size < 6 * GB) {
            fileSize = 6 + "GB";
        }
        return fileSize;
    }

    /**
     * * @param 获取显示的存储空间大小
     */
    public static String getFileSizeDescription(long size) {
        String fileSizeString = "0KB";
        DecimalFormat decimalFormat = new DecimalFormat(".0");
        float resultSize;
        if (size > KB && size <= MB) {
            resultSize = size / KB;
            fileSizeString = String.valueOf(resultSize) + "KB";
        } else if (size > MB && size <= GB) {
            resultSize = size / MB;
            fileSizeString = String.valueOf(resultSize) + "MB";
        } else {
            resultSize = size * 1.0f / GB;
            fileSizeString = decimalFormat.format(resultSize) + "G";
        }
        return fileSizeString;
    }

    public static String getTotalSpaceDescription(DiskInfo diskInfo) {
        long size = diskInfo.getTotalSpace();
        if (diskInfo.getPath().equals(INTERNAL_STORAGE_PATH)) {
            return formatFileSize(size);
        }
        String fileSizeString = "0KB";
        DecimalFormat decimalFormat = new DecimalFormat(".0");
        float resultSize;
        if (size > KB && size <= MB) {
            resultSize = size / KB;
            fileSizeString = String.valueOf(resultSize) + "KB";
        } else if (size > MB && size <= GB) {
            resultSize = size / MB;
            fileSizeString = String.valueOf(resultSize) + "MB";
        } else {
            resultSize = size * 1.0f / GB;
            fileSizeString = decimalFormat.format(resultSize) + "G";
        }
        return fileSizeString;
    }

    public static List<DiskInfo> getMountedDisksList(Context context) {
        List<DiskInfo> diskInfos = new ArrayList<>();
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        StorageVolume[] volumes = storageManager.getVolumeList();
        for (StorageVolume sv : volumes) {
            String path = sv.getPath();
            DiskInfo diskInfo = new DiskInfo();
            String state = sv.getState();
            if (state == null || !state.equals(Environment.MEDIA_MOUNTED)) {
                continue;
            }
            String volumeLabel = sv.getDescription(context);
            if (StringEncoderUtil.isUTF8(path)) {
                path = StringEncoderUtil.convertString(path, "UTF-8");
            } else {
                path = StringEncoderUtil.convertString(path, "GBK");
            }
            diskInfo.setPath(path);
            diskInfo.setTotalSpace(getTotalSpace(path));
            if (diskInfo.getTotalSpace() != 0) {
                diskInfo.setAvailSpace(getAvailSpace(diskInfo.getPath()));
                diskInfo.setUsedSpace(diskInfo.getTotalSpace() - diskInfo.getAvailSpace());
            }
            if (path.equals(INTERNAL_STORAGE_PATH)) {
                diskInfo.setName(context.getString(R.string.str_universal_storage_internal));
            } else {
                if (volumeLabel == null) {
                    diskInfo.setName(context.getString(R.string.str_universal_storage_default));
                } else {
                    diskInfo.setName(volumeLabel);
                }
            }
            diskInfos.add(diskInfo);
        }
        return diskInfos;
    }

    public static DiskInfo getDiskInfoByPath(Context context, String path) {
        File file = new File(path);
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        StorageVolume storageVolume = storageManager.getStorageVolume(file);
        DiskInfo diskInfo = new DiskInfo();
        String state = storageVolume.getState();
        if (state == null || !state.equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }
        String volumeLabel = storageVolume.getDescription(context);
        if (StringEncoderUtil.isUTF8(path)) {
            path = StringEncoderUtil.convertString(path, "UTF-8");
        } else {
            path = StringEncoderUtil.convertString(path, "GBK");
        }
        diskInfo.setPath(path);
        diskInfo.setTotalSpace(getTotalSpace(path));
        if (diskInfo.getTotalSpace() != 0) {
            diskInfo.setAvailSpace(getAvailSpace(diskInfo.getPath()));
            diskInfo.setUsedSpace(diskInfo.getTotalSpace() - diskInfo.getAvailSpace());
        }
        if (path.equals(INTERNAL_STORAGE_PATH)) {
            diskInfo.setName(context.getString(R.string.str_universal_storage_internal));
        } else {
            if (volumeLabel == null) {
                diskInfo.setName(context.getString(R.string.str_universal_storage_default));
            } else {
                diskInfo.setName(volumeLabel);
            }
        }
        return diskInfo;
    }

    public static void formatStorage(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                String[] childFiles = file.list();
                for (int i = 0; i < childFiles.length; i++) {
                    formatStorage(file.getAbsolutePath() + "/" + childFiles[i]);
                }
                file.delete();
            } else {
                file.delete();
            }
        }
    }
}
