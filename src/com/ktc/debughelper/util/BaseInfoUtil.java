package com.ktc.debughelper.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.SystemProperties;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;

import com.ktc.debughelper.ui.acty.other.HardWareUpdateActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;

public class BaseInfoUtil {

    private static String port;
    private static String exhub;

    public static String getMacAddress() {
        /*String str_mac = "";
        try {
            str_mac = TvManager.getInstance().getEnvironment("ethaddr");
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return str_mac;*/
        return null;
    }

    public static void setMacAddress(String value) {
       /* try {
            TvManager.getInstance().setEnvironment("ethaddr", value);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }*/
    }

    public static String getProjectName() {
        /*try {
            String[] value = TvManager.getInstance().getSystemBoardName().split("#");
            if (value.length >= 1)
                return value[0];
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return "";*/
        return null;
    }

    public static String getPanelName() {
        return null;
    }

    public static String getBoardType() {
        String boardType = SystemProperties.get("ktc.board.type");
        if (boardType == null || boardType.length() == 0) {
            boardType = "";
        }
        if (boardType.contains(" ")) {
            boardType = boardType.replaceAll(" ", "");
        }
        return boardType;
    }

    public static String getCustomer() {
        String customer = SystemProperties.get("ktc.ota.customer");
        if (customer == null || customer.length() == 0) {
            customer = "";
        }
        if (customer.contains(" ")) {
            customer = customer.replaceAll(" ", "");
        }
        return customer;
    }

    public static String getOtaProductName() {
        //modify for new ota server, "ro.product.model" change to "ktc.ota.model", zjd20160428
        String productName = SystemProperties.get("ktc.ota.model");
        if (productName.contains(" ")) {
            productName = productName.replaceAll(" ", "");
        }
        return productName;
    }

    public static String getBoardType_Timezone() {
        Properties props = new Properties();
        String values = "";
        InputStream in = null;
        String value = "";
        try {
            in = new BufferedInputStream(new FileInputStream("/system/build.prop"));
            props.load(in);
            value = props.getProperty("persist.sys.timezone");
            values = value.substring(value.indexOf("/") + 1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return values.trim();
    }

    public static String getBoardType_Language() {

        Properties props = new Properties();
        InputStream in = null;
        String value = "";
        try {
            in = new BufferedInputStream(new FileInputStream("/system/build.prop"));
            props.load(in);
            value = props.getProperty("persist.sys.language");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value.trim();
    }

    public static String getBoardType_Reserved() {
        //modify for new ota server, "ro.product.model" change to "ktc.ota.model", zjd20160428
        String productName = SystemProperties.get("ktc.board.reserved");
        if (productName.contains(" ")) {
            productName = productName.replaceAll(" ", "");
        }
        productName = productName.replaceAll("\"", "");
        productName = productName.replaceAll(";", "");
        productName = productName.replaceAll(" ", "");
        return productName;
    }

    public static String getSystemVersion() {
        String version = SystemProperties.get("ro.product.version");
        if (version == null || version.length() == 0) {
            version = "1.0.0";
        }
        return version;
    }

    //get board_memory from systemInfo
    public static String getBoardMemory() {
        String boardMemory = SystemProperties.get("ktc.board.memory");
        if (boardMemory == null || boardMemory.length() == 0) {
            boardMemory = "";
        }
        boardMemory = boardMemory.replaceAll("\"", "");
        boardMemory = boardMemory.replaceAll(";", "");
        boardMemory = boardMemory.replaceAll(" ", "");

        return boardMemory;
    }

    //get lcd_density from systemInfo
    public static String getLcd_Density() {
        String dpi = SystemProperties.get("ro.sf.lcd_density");
        if (dpi == null || dpi.length() == 0) {
            dpi = "";
        }
        dpi = dpi.replaceAll("\"", "");
        dpi = dpi.replaceAll(";", "");
        dpi = dpi.replaceAll(" ", "");

        return dpi;
    }

    // Get SDA_NO from Supernova(Customer_1.ini) by PRODUCT_SDA_NO
    public static String getSDANum_Ini() {
        String sdaNum = getProp("/config/model/Customer_1.ini", "PRODUCT_SDA_NO");
        sdaNum = sdaNum.replaceAll("\"", "");
        sdaNum = sdaNum.replaceAll(";", "");
        sdaNum = sdaNum.replaceAll(" ", "");

        return sdaNum;
    }

    // Get BOARD_NO from Supernova by ktc.sn.ota.board.type
    public static String getBoardType_ota() {
        String boardType = SystemProperties.get("ktc.sn.ota.board.type");
        boardType = boardType.replaceAll("\"", "");
        boardType = boardType.replaceAll(";", "");
        boardType = boardType.replaceAll(" ", "");

        return boardType;
    }

    //to parser Customer_1.ini
    public static String getProp(String file, String key) {
        String value = "";
        Properties props = new Properties();
        InputStream in;
        try {
            in = new BufferedInputStream(new FileInputStream(file));// "/system/build.prop"
            props.load(in);
            value = props.getProperty(key);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (value != null) {
            String[] array = value.split(";");
            if (array[0].length() > 0) {
                value = array[0];
            }
            value = value.replace("\"", "");
            value = value.trim();
            return value;
        } else
            return "";
    }

    public static String getPropValue(String key) {
        String value = SystemProperties.get(key);
        if (value == null || value.length() == 0) {
            value = "";
        }
        return value;
    }

    public static long getRomSpace() {
        return Environment.getDataDirectory().getFreeSpace();
    }

    // add for usb device is mounted
    private static String[] getExtSDCardPath(Context context) {
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
            getVolumePathsMethod.setAccessible(true);
            Object[] params = {};
            Object invoke = getVolumePathsMethod.invoke(storageManager, params);
            return (String[]) invoke;
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean checkMounted(Context context, String mountPoint) {
        if (mountPoint == null) {
            return false;
        }
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Method getVolumeState = storageManager.getClass().getMethod("getVolumeState", String.class);
            String state = (String) getVolumeState.invoke(storageManager, mountPoint);
            return Environment.MEDIA_MOUNTED.equals(state);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isUsbMounted(Context context) {
        String[] devices = getExtSDCardPath(context);
        if (devices != null && devices.length > 0) {
            for (String deviceName : devices) {
                Log.d("panzq", "deviceName = " + deviceName);
                if (checkMounted(context, deviceName)) {
                    return true;
                }
            }
        } else {
            return false;
        }
        return false;
    }

    public static int UpgradeMainFun(Context context, String fileName) {
        int ret = 0;
        /*// TODO UpgradeMain function
        String mainPath = FindFileOnUSB(fileName, context);// should not change this
        String bootPath = FindFileOnUSB("MbootUpgrade.bin", context);// file name
        Log.v("UpgradeMainFun", "mainPath=" + mainPath + "==bootPath="
                + bootPath);
        if ("".equals(mainPath) && "".equals(bootPath)) {
            ret = HardWareUpdateActivity.EnumUpgradeStatus.E_UPGRADE_FILE_NOT_FOUND.ordinal();
        } else {
            try {
                if (TvManager.getInstance().setEnvironment("upgrade_mode", "usb")) {
                    String path = ("".equals(mainPath) ? bootPath : mainPath);
                    if (!changeToPhyPath(path)) {
                        Log.d("UpgradeMainFun:", "changeToPhyPath Failed!");
                        ret = HardWareUpdateActivity.EnumUpgradeStatus.E_UPGRADE_FAIL.ordinal();
                    }
                    ret = HardWareUpdateActivity.EnumUpgradeStatus.E_UPGRADE_SUCCESS.ordinal();
                } else {
                    Log.d("UpgradeMainFun:", "setEnvironment Failed!");
                    ret = HardWareUpdateActivity.EnumUpgradeStatus.E_UPGRADE_FAIL.ordinal();
                }
            } catch (TvCommonException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }*/
        return ret;
    }

    public static boolean changeToPhyPath(String filepath) {
        boolean ret = false;
        /*Log.d("changeToPhyPort:", "filepath = " + filepath);
        String sdindex;
        sdindex = filepath.substring(5, 15);// /mnt/factoryusb/ ==>"factoryusb"
        //sdindex = filepath.substring(9, 13); // "/mnt/usb/sdx1" ==> "sdx1"
        // Hisa 2015.09.01 usbMstarUpgrade.bin end
        Log.d("changeToPhyPort:", "sdindex = " + sdindex);
        ret = getPhyPortInfo(sdindex);
        if (ret) {
            try {
                ret = TvManager.getInstance().setEnvironment(
                        "usb_upgrade_port", port);
                ret = TvManager.getInstance().setEnvironment(
                        "usb_upgrade_exhub_port ", exhub);
            } catch (TvCommonException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (!ret) {
                Log.d("changeToPhyPort:", "setEnvironment failed!");
            }
        } else {
            Log.d("changeToPhyPort:", "getPhyPortInfo failed!");
        }*/

        return ret;
    }

    static boolean getPhyPortInfo(String sdx) {
        boolean ret = false;
        FileReader filereader = null;
        try {
            filereader = new FileReader("/proc/partitions");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (filereader == null) {
            Log.d("getPhyPortInfo:", "Can't find the file /proc/partitions");
        }
        BufferedReader bufferedreader = new BufferedReader(filereader);
        String lineString = bufferedreader.toString();
        while (lineString != null) {
            try {
                lineString = bufferedreader.readLine();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // Log.d("sdx = ", sdx);
            if (lineString == null) {
                Log.d("getPhyPortInfo:", "Can't find " + sdx + "on partitions");
                break;
            }

            if (lineString.indexOf(sdx) >= 0) {
                if (lineString.indexOf("s=") == -1
                        || lineString.indexOf("p=") == -1
                        || lineString.indexOf("b=") == -1) {
                    Log.d("getPhyPortInfo:",
                            "Can't get the right infomation of " + sdx);
                    break;
                }

                char sss = lineString.charAt(lineString.indexOf("s=") + 2);
                char ppp = lineString.charAt(lineString.indexOf("p=") + 2);
                char bbb = lineString.charAt(lineString.indexOf("b=") + 2);
                port = "" + bbb;
                exhub = "" + ppp;
                if (sss == '3') // not hub
                {
                    exhub = "0";
                    Log.d("getPhyPortInfo:", "No hub on port" + port);
                }
                ret = true;
                break;
            }
        }
        return ret;
    }

    public static String FindFileOnUSB(String filename, Context context) {
        String filepath = "";
        // Hisa 2015.09.01 usb MstarUpgrade.bin start
        //File usbroot = new File("/mnt/usb/");
        String[] paths = getVolumePaths(context);
        if (paths == null || paths.length < 2) {
            return filepath;
        }
        File usbroot = new File(paths[1]);

        File targetfile;
        if (usbroot != null && usbroot.exists()) {
            targetfile = new File(usbroot + "/" + filename);
            if (targetfile != null && targetfile.exists()) {
                filepath = targetfile.getAbsolutePath();
            }
        }
        return filepath;
    }

    public static String[] getVolumePaths(Context context) {
        String[] paths = null;
        StorageManager mStorageManager;
        Method mMethodGetPaths = null;
        try {
            mStorageManager = (StorageManager) context.getSystemService(Activity.STORAGE_SERVICE);
            mMethodGetPaths = mStorageManager.getClass().getMethod("getVolumePaths");
            paths = (String[]) mMethodGetPaths.invoke(mStorageManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paths;
    }

    /**
     * 判断服务是否开启
     *
     * @return
     */
    public static boolean isServiceRunning(Context context, String ServiceName) {
        if (TextUtils.isEmpty(ServiceName)) {
            return false;
        }
        ActivityManager myManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName()
                    .equals(ServiceName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSerialPortOpen() {
        //FactoryManager fm = TvManager.getInstance().getFactoryManager();
        boolean ret = false;
        /*try {
            ret = fm.getUartEnv();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }*/
        return ret;
    }

    public static boolean setUartOnOff(boolean isEnable) {
        /*try {
            FactoryManager fm = TvManager.getInstance().getFactoryManager();
            fm.setUartEnv(isEnable);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }*/
        return true;
    }

    public static Locale getLocale(Context context) {
        Locale locale = null;
        locale = context.getResources().getConfiguration().locale;
        return locale;
    }

    public static boolean isRtlLayout() {
        return SystemProperties.get("debug.force_rtl").equals("1");
    }
}
