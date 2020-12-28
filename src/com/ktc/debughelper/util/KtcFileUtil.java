package com.ktc.debughelper.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.DiskInfo;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.text.TextUtils;
import android.util.Log;

import com.ktc.debughelper.bean.CpuInfoBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class KtcFileUtil {
    private static final String TAG = "KtcFileUtil";

    public static void zip(String src, String dest) throws IOException {
        ZipOutputStream out = null;
        try {
            File outFile = new File(dest);
            File fileOrDirectory = new File(src);
            out = new ZipOutputStream(new FileOutputStream(outFile));
            if (fileOrDirectory.isFile()) {
                zipFileOrDirectory(out, fileOrDirectory, "");
            } else {
                //否则列出目录中的所有文件递归进行压缩
                File[] entries = fileOrDirectory.listFiles();
                for (int i = 0; i < entries.length; i++) {
                    zipFileOrDirectory(out, entries[i], "");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                    //deleteKtcLogReportFolder();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static void zipFileOrDirectory(ZipOutputStream out, File fileOrDirectory, String curPath) throws IOException {
        FileInputStream in = null;
        try {
            if (!fileOrDirectory.isDirectory()) {
                byte[] buffer = new byte[4096];
                int bytes_read;
                in = new FileInputStream(fileOrDirectory);
                ZipEntry entry = new ZipEntry(curPath + fileOrDirectory.getName());
                out.putNextEntry(entry);
                while ((bytes_read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytes_read);
                }
                out.closeEntry();
            } else {
                File[] entrie = fileOrDirectory.listFiles();
                for (int i = 0; i < entrie.length; i++) {
                    zipFileOrDirectory(out, entrie[i], curPath + fileOrDirectory.getName() + "/");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void deleteKtcLogReportFolder(Context context) {
        String rootDir = KtcFileUtil.getAllExternalSdcardPath(context).get(0) + "/" + "KtcLogReport" + "/";
        File file = new File(rootDir);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                f.delete();
            }
            file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            file.delete();
        }
    }

    public static String getKtcLogReportFolder(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String rootDir = KtcFileUtil.getAllExternalSdcardPath(context).get(0) + "/" + "KtcLogReport" + "/";
            File file = new File(rootDir);
            if (file.exists()) {
                file.delete();
            }
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    return null;
                }
            }
            return rootDir;
        } else {
            return null;
        }
    }

    /**
     * @return String
     * @TODO 获取录屏所在文件夹
     */
    public static String getRecordFolder(Context context) {
        String rootDir = getKtcLogReportFolder(context);
        if (!TextUtils.isEmpty(rootDir)) {
            return rootDir;
        }
        return null;
    }

    /**
     * @return String
     * @TODO 获取Log日志所在文件夹
     */
    public static String getLogFolder(Context context) {
        String rootDir = getKtcLogReportFolder(context);
        if (!TextUtils.isEmpty(rootDir)) {
            return rootDir;
        }
        return null;
    }

    public static List<String> getAllExternalSdcardPath(Context context) {
        List<String> PathList = new ArrayList<>();
        String firstPath = Environment.getExternalStorageDirectory().getPath();
        Log.i(TAG, "firstPath：  " + firstPath);
        try {
            List<VolumeInfo> infos = ((StorageManager) context.getSystemService(Context.STORAGE_SERVICE)).getVolumes();
            if (infos != null && infos.size() > 0) {
                for (VolumeInfo info : infos) {
                    DiskInfo dinfo = info.getDisk();
                    if (dinfo != null) {
                        PathList.add(info.getPath().toString());
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //没有USB时，添加/mnt/sdcard
        if (PathList.size() < 1) {
            PathList.add(firstPath);
        }

        Log.i(TAG, "PathList：  " + PathList.size());

        return PathList;
    }

    public static CpuInfoBean getCpuInfo() {
        int cpuCount = 0;
        String cpuBitCount = "";
        Runtime runtime = Runtime.getRuntime();
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            Process proc = runtime.exec("cat /proc/cpuinfo");
            is = proc.getInputStream();
            isr = new InputStreamReader(is);
            String line;
            br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("processor")) {
                    cpuCount++;
                } else if (line.contains("architecture")) {
                    cpuBitCount = line.split(":")[1];
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            CloseUtil.close(is);
            CloseUtil.close(isr);
            CloseUtil.close(br);
        }
        return new CpuInfoBean(cpuCount, cpuBitCount);
    }

    /**
     * 获取当前data分区剩余空间大小
     * @return long
     */
    public static long getDataFreeSize() {
        StatFs sf = new StatFs("/data");
        long blockSize = sf.getBlockSize();
        long availCount = sf.getAvailableBlocks();

        return availCount * blockSize;
    }

    /**
     * 获取指定文件大小
     * @param filePath
     * @return long
     */
    public static long getFileSize(String filePath) {
        File f = new File(filePath);
        if (f.exists()) {
            return f.length();
        }
        return 0;
    }

    /**
     * 格式化文件大小(KB;MB;GB)
     * @param length
     * @return String
     */
    public static String formatSize(long length) {
        if (length < 1024) {
            return String.valueOf(length) + "B";
        } else if (length / 1024 > 0 && length / 1024 / 1024 == 0) {
            return String.valueOf(length / 1024) + "KB";
        } else if (length / 1024 / 1024 > 0) {
            return String.valueOf(length / 1024 / 1024) + "MB";
        } else {
            return String.valueOf(length / 1024 / 1024 / 1024) + "GB";
        }
    }
}
