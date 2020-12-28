package com.ktc.debughelper.util;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.text.format.Formatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

/**
 * TODO KTC系统信息获取接口
 *
 * @author Arvin
 * @Time 2018-1-10 上午11:20:33
 */
public class KtcSystemUtil {

    private static final String TAG = "KtcSystemUtil";
    private static KtcSystemUtil mKtcSystemUtil;
    private static Context mContext = null;

    public static KtcSystemUtil getInstance(Context context) {
        mContext = context;
        if (mKtcSystemUtil == null) {
            mKtcSystemUtil = new KtcSystemUtil();
        }
        return mKtcSystemUtil;
    }

    /**
     * @return Long[]{Long[0] = mi.totalMem ; Long[1] = mi.availMem}
     * @TODO 获取系统当前的运行内存信息(含总运存及可用运存)
     */
    public Long[] getSystemMemoryInfo() {
        Long[] MemInfos = new Long[2];
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        //mi.availMem; 当前系统支持应用运行的可用运行内存  (MemFree+Buffers+Cached)
        //mi.totalMem; 当前系统的总运行内存  
        MemInfos[0] = mi.totalMem;
        MemInfos[1] = mi.availMem;
        return MemInfos;
    }

    /**
     * 描述：总内存.KB
     *
     * @return
     */
    public String getTotalMemory() {
        // 系统内存信息文件
        String file = "/proc/meminfo";
        String memInfo;
        String[] strs;
        long memory = 0;

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader, 8192);
            // 读取meminfo第一行，系统内存大小
            memInfo = bufferedReader.readLine();
            strs = memInfo.split("\\s+");
            for (String str : strs) {
                //                L.d(AppUtil.class, str + "\t");
            }
            // 获得系统总内存，单位KB
            memory = Integer.valueOf(strs[1]).intValue() * 1024;
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Byte转位KB或MB
        return Formatter.formatFileSize(mContext, memory);
    }

    /**
     * @return int
     * @TODO 获取CPU核数
     */
    public int getNumOfCPUCores() {
        try {
            // Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            // Filter to only list the devices we care about
            File[] files = dir.listFiles(new FileFilter() {

                @Override
                public boolean accept(File pathname) {
                    // Check if filename is "cpu", followed by a single digit
                    // number
                    if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                        return true;
                    }
                    return false;
                }
            });
            // Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public String getCurCpuInfo() {
        try {
            String cmd = "dumpsys cpuinfo";
            Process process = Runtime.getRuntime().exec(cmd);
            InputStream stream = process.getInputStream();
            StringBuffer sb = new StringBuffer();
            if (stream != null) {
                InputStreamReader reader = new InputStreamReader(stream);
                BufferedReader bufferedReader = new BufferedReader(reader, 1024);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.contains("TOTAL:")
                            || (!line.replace(" ", "").startsWith("0")
                            && !line.replace(" ", "").startsWith("+0"))) {
                        sb.append(line + "\n");
                    }
                }
                bufferedReader.close();
                reader.close();
                stream.close();
                process.destroy();

                return sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
