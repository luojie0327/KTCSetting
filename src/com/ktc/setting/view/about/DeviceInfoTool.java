package com.ktc.setting.view.about;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;

import com.ktc.setting.view.universal.storage.StorageUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

public class DeviceInfoTool {

    public static String getStorageAndMemory() {
        return getTotalMemory() + " " + getTotalInternalStorage();
    }

    /**
     * 获取系统flash的规格
     */
    private static String getTotalInternalStorage() {
        long size = StorageUtil.getTotalSpace(Environment.getDataDirectory().getPath()) 
			+ StorageUtil.getTotalSpace(Environment.getRootDirectory().getPath())
			+ StorageUtil.getTotalSpace(Environment.getVendorDirectory().getPath());
        return StorageUtil.formatFileSize(size) + " eMMC";
    }

    /**
     * 获取系统内存规格
     */
    private static String getTotalMemory() {
        String str1 = "/proc/meminfo";
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }
            initial_memory = Long.valueOf(arrayOfString[1]) * 1024;
            localBufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return StorageUtil.formatDDRString(initial_memory) + " DDR3";
    }

    /**
     * 获取系统版本，该版本为device.mk中定义的版本
     */
    public static String getSoftWareVersion() {
        return SystemProperties.get("ro.product.version");
    }

    /**
     * 获取软件更新时间
     */
    public static String getSoftwareDate() {
        String utcTime = SystemProperties.get("ro.build.date.utc");
        long time = Long.parseLong(utcTime) * 1000L;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date(time);
        return sdf.format(date);
    }

    /**
     * 获取Android版本
     */
    public static String getAndroidVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取机型号
     */
    public static String getMachineModel() {
//        return SystemProperties.get("ktc.board.model");
	String edidmod = getProp("/vendor/tvconfig/config/model/Customer_1.ini", "Monitor_Name");
        edidmod = edidmod.replaceAll("\"", "");
        edidmod = edidmod.replaceAll(";", "");
        edidmod = edidmod.replaceAll(" ", "");
        return edidmod;
    }
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

    /**
     * 获取有线MAC地址
     */
    public static String getEthernetMacAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface netWork = interfaces.nextElement();
                if (!netWork.getName().equals("eth0"))
                    continue;
                byte[] macBytes = netWork.getHardwareAddress();
                if (macBytes == null) return "";
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }
                if (!TextUtils.isEmpty(res1)) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }


    /**
     * 获取WiFi MAC地址（wifi连接时为wifi地址，否则为有线mac地址）
     */
    public static String getWirelessMacAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String wifiMacAddress = "NA";
        if (wifiInfo != null) {
            if ("02:00:00:00:00:00".equals(wifiInfo.getMacAddress())) {
                wifiMacAddress = getMacAddress();
            } else {
                wifiMacAddress = wifiInfo.getMacAddress();
            }
        }
        return wifiMacAddress == null ? "NA" : wifiMacAddress.toUpperCase();
    }

    public static String getMacAddress() {
        String address = null;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface netWork = interfaces.nextElement();
                byte[] by = netWork.getHardwareAddress();
                if (by == null || by.length == 0) {
                    continue;
                }
                StringBuilder builder = new StringBuilder();
                for (byte b : by) {
                    builder.append(String.format("%02X:", b));
                }
                if (builder.length() > 0) {
                    builder.deleteCharAt(builder.length() - 1);
                }
                String mac = builder.toString();
                if (netWork.getName().equals("wlan0")) {
                    address = mac;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return address;
    }

}
