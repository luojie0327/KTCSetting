package com.ktc.setting.helper;

import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.ProxyInfo;
import android.net.RouteInfo;
import android.net.StaticIpConfiguration;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.ktc.setting.model.WifiItem;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetworkHelper {

    public static final int SECURITY_NONE = 0;
    public static final int SECURITY_WEP = 1;
    public static final int SECURITY_PSK = 2;
    public static final int SECURITY_EAP = 3;

    private static NetworkHelper networkHelperInstance;
    private Context mContext;
    private ConnectivityManager mConnectivityManager;
    private WifiManager mWifiManager;
    private EthernetManager mEthernetManager;

    private String mInterfaceName;

    private OnStartTetheringCallback mStartTetheringCallback;
    private Handler mHandler = new Handler();
    private boolean mRestartApAfterConfigChange = false;
    private static final String DEFAULT_ADDRESS = "0.0.0.0";
    private static final String WIFI_STATUS = "persist.sys.wifistatus";

    private NetworkHelper(Context context) {
        mContext = context.getApplicationContext();
        mConnectivityManager = (ConnectivityManager) mContext.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mEthernetManager = (EthernetManager) mContext.getSystemService(Context.ETHERNET_SERVICE);
        mStartTetheringCallback = new OnStartTetheringCallback();
        loadEthernet();
    }

    public static synchronized NetworkHelper getInstance(Context context) {
        if (networkHelperInstance == null) {
            synchronized (NetworkHelper.class) {
                if (networkHelperInstance == null) {
                    networkHelperInstance = new NetworkHelper(context);
                }
            }
        }
        return networkHelperInstance;
    }

    /**
     * int类型转ip地址
     *
     * @param paramInt
     * @return ip地址
     */
    public static String int2Ip(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }

    public static String[] resolutionIP(String ip) {
        return ip.split("\\.");
    }

    /**
     * 判断是否正常的ip地址
     *
     * @param ip
     * @return
     */
    public static boolean matchIP(String ip) {
        if (ip == null) {
            return false;
        }
        String regex = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

    /**
     * 通过ip类型子网掩码获取子网掩码前缀长度
     *
     * @param ipAddr
     * @return
     */
    public static int getPrefixLength(String ipAddr) {
        return findNumberOf1(ipToInt(ipAddr));
    }

    /**
     * ip类型转换为二进制
     *
     * @param ipAddr
     * @return
     */
    private static int ipToInt(String ipAddr) {
        String[] ipArr = ipAddr.split("\\.");
        return ((Integer.parseInt(ipArr[0]) & 0xFF) << 24)
                + ((Integer.parseInt(ipArr[1]) & 0xFF) << 16)
                + ((Integer.parseInt(ipArr[2]) & 0xFF) << 8)
                + (Integer.parseInt(ipArr[3]) & 0xFF);
    }

    private static int findNumberOf1(int n) {
        int countOf1 = 0;
        int tag = 1;
        while (tag != 0) {
            if ((tag & n) != 0)
                countOf1++;
            tag = tag << 1;
        }
        return countOf1;
    }

    private static String join(List<String> list, String s) {
        if (list.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size() - 1; i++) {
            sb.append(list.get(i)).append(s);
        }
        sb.append(list.get(list.size() - 1));
        return sb.toString();
    }

    //wifi start
    public boolean isWifiOpen() {
        return mWifiManager.isWifiEnabled();
    }

    /**
     * open wifi when boot completed
     */
    public void setWifiStatusBoot() {
        Executors.newScheduledThreadPool(1).schedule(new Runnable() {
            @Override
            public void run() {
                if (SystemProperties.get(WIFI_STATUS, "true").equals("true"))
                    setWifiOpen(true);
            }
        }, 1000, TimeUnit.MILLISECONDS);
    }

    public void setWifiOpen(boolean toOpen) {
        if (mWifiManager.getWifiApState() == WifiManager.WIFI_AP_STATE_ENABLED) {
            setHotpotEnable(false);
        }
        while (mWifiManager.getWifiApState() == WifiManager.WIFI_AP_STATE_DISABLING) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        SystemProperties.set(WIFI_STATUS, toOpen ? "true" : "false");
        mWifiManager.setWifiEnabled(toOpen);
    }

    private void disconnectEthernet() {
        saveEthernetAsStatic(DEFAULT_ADDRESS, DEFAULT_ADDRESS
                , DEFAULT_ADDRESS, DEFAULT_ADDRESS, DEFAULT_ADDRESS);
    }

    public boolean isWifiConnected() {
        if (!isWifiOpen()) {
            return false;
        } else {
            NetworkInfo.State wifi = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            // wifi have not connected
            WifiInfo info = mWifiManager.getConnectionInfo();
            if (info == null
                    || info.getSSID() == null
                    || info.getNetworkId() == WifiConfiguration.INVALID_NETWORK_ID
                    || wifi != NetworkInfo.State.CONNECTED) {
                return false;
            }
            return true;
        }
    }

    private static int getLevel(int level) {
        if (level == Integer.MAX_VALUE) {
            return -1;
        }

        return WifiManager.calculateSignalLevel(level, 5);
    }

    private static int getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return SECURITY_PSK;
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP)
                || config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return SECURITY_EAP;
        }
        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
    }

    private static int getSecurity(ScanResult result) {
        if (result.capabilities.contains("WEP")) {
            return SECURITY_WEP;
        } else if (result.capabilities.contains("PSK")) {
            return SECURITY_PSK;
        } else if (result.capabilities.contains("EAP")) {
            return SECURITY_EAP;
        }
        return SECURITY_NONE;
    }

    public boolean startScan() {
        return mWifiManager.startScan();
    }

    public List<WifiItem> getAvailableNetworks() {
        final List<ScanResult> results = mWifiManager.getScanResults();
        if (results.size() == 0) {
            mWifiManager.startScan();
        }

        final HashMap<Pair<String, Integer>, WifiItem> consolidatedScanResults =
                new HashMap<Pair<String, Integer>, WifiItem>();
        for (ScanResult result : results) {
            if (TextUtils.isEmpty(result.SSID)) {
                continue;
            }

            Pair<String, Integer> key = Pair.create(result.SSID, getSecurity(result));
            WifiItem existing = consolidatedScanResults.get(key);
            if (existing == null) {
                String ssid = result.SSID;
                String bssid = result.BSSID;
                int level = getLevel(result.level);
                int security = getSecurity(result);
                boolean isConnected = isConnectedWifi(result);
                int connectedState = isConnected ? WifiItem.STATE_CONNECTED : WifiItem.STATE_DISCONNECTED;
                boolean isSaved = isConnected || isSavedWifi(result);
                WifiItem item = new WifiItem(ssid, bssid, security, level, connectedState, isSaved);
                consolidatedScanResults.put(key, item);
            }
        }

        ArrayList<WifiItem> networkList = new ArrayList<WifiItem>(consolidatedScanResults.size());
        networkList.addAll(consolidatedScanResults.values());
        Collections.sort(networkList, new WifiItemComparator());
        return networkList;
    }

    private static String addDoubleQuotes(String ssid) {
        return "\"".concat(ssid.replace("\"", "\\\"")).concat("\"");
    }

    /**
     * 连接未保存的wifi
     *
     * @param ssid     网络名称
     * @param security 安全类型
     * @param password 密码
     */
    public void connectWifi(String ssid, int security, String password) {
//        disconnectEthernet();
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = addDoubleQuotes(ssid);
        config.allowedKeyManagement.clear();
        config.allowedAuthAlgorithms.clear();
        switch (security) {
            case SECURITY_NONE:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
            case SECURITY_WEP:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);

                int length = password.length();
                // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
                if ((length == 10 || length == 26 || length == 58)
                        && password.matches("[0-9A-Fa-f]*")) {
                    config.wepKeys[0] = password;
                } else {
                    config.wepKeys[0] = '"' + password + '"';
                }
                break;
            case SECURITY_PSK:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                config.preSharedKey = '"' + password + '"';
                break;
            case SECURITY_EAP:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
                config.preSharedKey = '"' + password + '"';
                break;
        }

        int networkId = mWifiManager.addNetwork(config);
        if (networkId != -1
                && mWifiManager.enableNetwork(networkId, true)
                && mWifiManager.saveConfiguration()) {
            mWifiManager.connect(config, null);
        }
    }

    /**
     * 连接隐藏wifi
     *
     * @param ssid     网络名称
     * @param security 安全类型
     * @param password 密码
     */
    public void connectHideWifi(String ssid, int security, String password) {
//        disconnectEthernet();
        WifiConfiguration config = new WifiConfiguration();
        config.hiddenSSID = true;

        // if this looks like a BSSID, don't quote it
        if (!Pattern.matches("[a-fA-F0-9]{12}", ssid)) {
            config.SSID = addDoubleQuotes(ssid);
        } else {
            config.SSID = ssid;
        }

        config.allowedKeyManagement.clear();
        config.allowedAuthAlgorithms.clear();
        switch (security) {
            case SECURITY_NONE:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
            case SECURITY_WEP:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                break;
            case SECURITY_PSK:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                break;
            /*case SECURITY_EAP:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
                break;*/
        }

        if (security == SECURITY_WEP) {
            int length = password.length();
            // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
            if ((length == 10 || length == 26 || length == 58)
                    && password.matches("[0-9A-Fa-f]*")) {
                config.wepKeys[0] = password;
            } else {
                config.wepKeys[0] = '"' + password + '"';
            }
        } else if (security == SECURITY_NONE) {
            config.preSharedKey = null;
        } else {
            config.preSharedKey = '"' + password + '"';
        }

        int networkId = mWifiManager.addNetwork(config);
        if (networkId != -1
                && mWifiManager.enableNetwork(networkId, true)
                && mWifiManager.saveConfiguration()) {
            mWifiManager.connect(config, null);
        }
    }

    /**
     * 连接已保存的wifi
     *
     * @param ssid     网络名称
     * @param security 安全类型
     */
    public void connectSavedWifi(String ssid, int security) {
//        disconnectEthernet();
        WifiConfiguration configuration = getWifiConfiguration(ssid, security);
        if (configuration != null) {
            mWifiManager.connect(configuration, null);
        }
    }

    public void disconnectWifi() {
        mWifiManager.disable(getCurrentWifiConfiguration().networkId, null);
        mWifiManager.disconnect();
    }

    public void forgetWifi(String ssid, int security) {
        WifiConfiguration configuration = getWifiConfiguration(ssid, security);
        Log.d("hjy", "config: " + configuration);
        if (configuration != null) {
            mWifiManager.forget(configuration.networkId, null);
        }
    }

    public void disableWifi() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        Log.d("hjy", "wifiInfo: " + wifiInfo);
        if (wifiInfo != null) {
            int networkId = wifiInfo.getNetworkId();
            if (networkId != -1) {
                mWifiManager.disable(networkId, null);
            }
        }
    }

    public void saveWifiAsHdcp() {
        WifiConfiguration configuration = getCurrentWifiConfiguration();
        IpConfiguration mIpConfiguration = configuration.getIpConfiguration();
        mIpConfiguration.setIpAssignment(IpConfiguration.IpAssignment.DHCP);
        configuration.setIpConfiguration(mIpConfiguration);
        mWifiManager.save(configuration, null);
    }

    /**
     * 设置wifi静态ip
     *
     * @param ip      ip地址
     * @param subnet  子网掩码
     * @param gateway 网关
     * @param dns1
     * @param dns2
     */
    public void saveWifiAsStatic(String ip, String subnet, String gateway, String dns1, String dns2) {
        WifiConfiguration configuration = getCurrentWifiConfiguration();
        IpConfiguration mIpConfiguration = configuration.getIpConfiguration();
        mIpConfiguration.setIpAssignment(IpConfiguration.IpAssignment.STATIC);

        StaticIpConfiguration staticConfig = new StaticIpConfiguration();
        mIpConfiguration.setStaticIpConfiguration(staticConfig);

        int prefixLength = getPrefixLength(subnet);
        Inet4Address net4Addr = (Inet4Address) NetworkUtils.numericToInetAddress(ip);
        staticConfig.ipAddress = new LinkAddress(net4Addr, prefixLength);
        staticConfig.gateway = (Inet4Address) NetworkUtils.numericToInetAddress(gateway);
        staticConfig.dnsServers.add((Inet4Address) NetworkUtils.numericToInetAddress(dns1));
        staticConfig.dnsServers.add((Inet4Address) NetworkUtils.numericToInetAddress(dns2));

        configuration.setIpConfiguration(mIpConfiguration);
        mWifiManager.save(configuration, null);
    }

    public boolean isWifiStaticIP() {
        WifiConfiguration wifiConfiguration = getCurrentWifiConfiguration();
        if (wifiConfiguration != null)
            return (wifiConfiguration.getIpAssignment() == IpConfiguration.IpAssignment.STATIC);
        return false;
    }

    public int getWifiIp() {
        DhcpInfo info = mWifiManager.getDhcpInfo();
        return info.ipAddress;
    }

    public String getWifiSubnetMask() {
        LinkProperties linkProperties = mConnectivityManager.getLinkProperties(ConnectivityManager.TYPE_WIFI);
        if (linkProperties != null) {
            String ip = int2Ip(getWifiIp());
            String gateway = int2Ip(getWifiGateWay());
            String[] ip_ary = resolutionIP(ip);
            String[] gateway_ary = resolutionIP(gateway);
            String subnet;
            if (ip_ary.length < 4 || gateway_ary.length < 4
                    || (ip.equals("0.0.0.0") && gateway.equals("0.0.0.0"))) {
                return "0.0.0.0";
            }
            if (ip_ary[0].equals(gateway_ary[0])) {
                subnet = "255";
            } else {
                subnet = "0";
            }
            if (ip_ary[1].equals(gateway_ary[1])) {
                subnet += ".255";
            } else {
                subnet += ".0";
            }
            if (ip_ary[2].equals(gateway_ary[2])) {
                subnet += ".255";
            } else {
                subnet += ".0";
            }
            if (ip_ary[3].equals(gateway_ary[3])) {
                subnet += ".255";
            } else {
                subnet += ".0";
            }
            return subnet;
        }
        return "0.0.0.0";
    }

    public int getWifiGateWay() {
        DhcpInfo info = mWifiManager.getDhcpInfo();
        return info.gateway;
    }

    public int getWifiDns1() {
        DhcpInfo info = mWifiManager.getDhcpInfo();
        return info.dns1;
    }

    public int getWifiDns2() {
        DhcpInfo info = mWifiManager.getDhcpInfo();
        return info.dns2;
    }

    public WifiConfiguration getCurrentWifiConfiguration() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            int networkId = wifiInfo.getNetworkId();
            List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
            if (configuredNetworks != null) {
                for (WifiConfiguration configuredNetwork : configuredNetworks) {
                    if (configuredNetwork.networkId == networkId) {
                        return configuredNetwork;
                    }
                }
            }
        }
        return null;
    }

    public WifiConfiguration getWifiConfiguration(int id) {
        List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration configuredNetwork : configuredNetworks) {
                if (configuredNetwork.networkId == id) {
                    return configuredNetwork;
                }
            }
        }
        return null;
    }

    public WifiConfiguration getWifiConfiguration(String ssid, int security) {
        Log.d("hjy", "ssid: " + ssid + ", security: " + security);
        List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration configuredNetwork : configuredNetworks) {
                if (configuredNetwork == null || configuredNetwork.SSID == null) {
                    continue;
                }

                String configuredSsid = WifiInfo.removeDoubleQuotes(configuredNetwork.SSID);
                Log.d("hjy", "config ssid: " + configuredSsid);
                if (TextUtils.equals(configuredSsid, ssid)) {
                    int configuredSecurity = getSecurity(configuredNetwork);
                    if (configuredSecurity == security) {
                        return configuredNetwork;
                    }
                }
            }
        }

        return null;
    }

    public WifiInfo getConnectionInfo() {
        return mWifiManager.getConnectionInfo();
    }

    private boolean isConnectedWifi(ScanResult scanResult) {
        if (!isWifiConnected()) {
            return false;
        }

        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();

        if (scanResult == null || wifiInfo == null) {
            return false;
        }
        if (scanResult.SSID == null || wifiInfo.getSSID() == null) {
            return false;
        }
        if (scanResult.BSSID == null || wifiInfo.getBSSID() == null) {
            return false;
        }
        String wifiInfoSSID = WifiInfo.removeDoubleQuotes(wifiInfo.getSSID());
        String wifiInfoBSSID = wifiInfo.getBSSID();
        int scanResultSecurity = getSecurity(scanResult);
        int wifiInfoSecurity = getSecurity(wifiInfo);

        return (TextUtils.equals(scanResult.SSID, wifiInfoSSID) &&
                TextUtils.equals(scanResult.BSSID, wifiInfoBSSID) &&
                scanResultSecurity == wifiInfoSecurity);
    }

    private boolean isSavedWifi(ScanResult scanResult) {
        List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            int scanResultSecurity = getSecurity(scanResult);
            for (WifiConfiguration configuredNetwork : configuredNetworks) {
                if (configuredNetwork == null || configuredNetwork.SSID == null) {
                    continue;
                }

                int wifiInfoSecurity = getSecurity(configuredNetwork);
                if (TextUtils.equals(scanResult.SSID, WifiInfo.removeDoubleQuotes(configuredNetwork.SSID))
                        && scanResultSecurity == wifiInfoSecurity) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getSecurity(WifiInfo info) {
        if (info != null) {
            WifiConfiguration wifiConfiguration = getWifiConfiguration(info.getNetworkId());
            if (wifiConfiguration != null) {
                return getSecurity(wifiConfiguration);
            }
        }
        return -1;
    }

    //ethernet start

    private void loadEthernet() {
        String[] ifaces = mEthernetManager.getAvailableInterfaces();
        if (ifaces.length > 0) {
            mInterfaceName = ifaces[0];
        }
    }

    public boolean isEthernetAvailable() {
        if (mConnectivityManager.isNetworkSupported(ConnectivityManager.TYPE_ETHERNET)) {
            return mEthernetManager.isAvailable();
        }

        return false;
    }

    public boolean isEthernetConnected() {
        NetworkInfo ethNetInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if (ethNetInfo != null && ethNetInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public boolean isEthernetAutoIP() {
        IpConfiguration ipConfiguration = mEthernetManager.getConfiguration(mInterfaceName);
        if (null != ipConfiguration
                && ipConfiguration.getStaticIpConfiguration() == null) {
            return true;
        }
        return false;
    }

    public String getEthernetIp() {
        //        LinkProperties linkProperties = mConnectivityManager.getLinkProperties(ConnectivityManager.TYPE_ETHERNET);
        //        if (linkProperties != null) {
        //            Iterator<LinkAddress> iterator = linkProperties.getLinkAddresses().iterator();
        //            if (iterator.hasNext()) {
        //                LinkAddress linkAddress = iterator.next();
        //                InetAddress address = linkAddress.getAddress();
        //                if (address instanceof Inet4Address) {
        //                    return address.getHostAddress();
        //                }
        //            }
        //        }

        try {
            // 获取本地设备的所有网络接口
            Enumeration<NetworkInterface> enumerationNi = NetworkInterface.getNetworkInterfaces();
            while (enumerationNi.hasMoreElements()) {
                NetworkInterface networkInterface = enumerationNi.nextElement();
                String interfaceName = networkInterface.getDisplayName();

                if (interfaceName.equals("eth0")) {
                    Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses();
                    while (enumIpAddr.hasMoreElements()) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "0.0.0.0";
    }

    public String getEthernetGateway() {
        LinkProperties linkProperties = mConnectivityManager.getLinkProperties(ConnectivityManager.TYPE_ETHERNET);
        if (linkProperties != null) {
            // gateway
            for (RouteInfo route : linkProperties.getRoutes()) {
                if (route.isDefaultRoute()) {
                    InetAddress address = route.getGateway();
                    if (address instanceof Inet4Address) {
                        return address.getHostAddress();
                    }
                }
            }
        }
        return "0.0.0.0";
    }

    public String getEthernetSubnet() {
        LinkProperties linkProperties = mConnectivityManager.getLinkProperties(ConnectivityManager.TYPE_ETHERNET);
        if (linkProperties != null) {
            String ip = getEthernetIp();
            String gateway = getEthernetGateway();
            String[] ip_ary = resolutionIP(ip);
            String[] gateway_ary = resolutionIP(gateway);
            String subnet;
            if (ip_ary.length < 4 || gateway_ary.length < 4
                    || (ip.equals("0.0.0.0") && gateway.equals("0.0.0.0"))) {
                return "0.0.0.0";
            }
            if (ip_ary[0].equals(gateway_ary[0])) {
                subnet = "255";
            } else {
                subnet = "0";
            }
            if (ip_ary[1].equals(gateway_ary[1])) {
                subnet += ".255";
            } else {
                subnet += ".0";
            }
            if (ip_ary[2].equals(gateway_ary[2])) {
                subnet += ".255";
            } else {
                subnet += ".0";
            }
            if (ip_ary[3].equals(gateway_ary[3])) {
                subnet += ".255";
            } else {
                subnet += ".0";
            }

            return subnet;
        }
        return "0.0.0.0";
    }

    public String getEthernetDns1() {
        LinkProperties linkProperties = mConnectivityManager.getLinkProperties(ConnectivityManager.TYPE_ETHERNET);
        if (linkProperties != null) {
            Iterator<InetAddress> dnsIterator = linkProperties.getDnsServers().iterator();
            if (dnsIterator.hasNext()) {
                String dns1 = dnsIterator.next().getHostAddress();
                if (matchIP(dns1)) {
                    return dns1;
                }
            }
        }
        return "0.0.0.0";
    }

    public String getEthernetDns2() {
        LinkProperties linkProperties = mConnectivityManager.getLinkProperties(ConnectivityManager.TYPE_ETHERNET);
        if (linkProperties != null) {
            Iterator<InetAddress> dnsIterator = linkProperties.getDnsServers().iterator();
            if (dnsIterator.hasNext()) {
                dnsIterator.next();
                if (dnsIterator.hasNext()) {
                    String dns2 = dnsIterator.next().getHostAddress();
                    if (matchIP(dns2)) {
                        return dns2;
                    }
                }
            }
        }
        return "0.0.0.0";
    }

    /**
     * 设置有线网络静态ip
     *
     * @param ip
     * @param subnet
     * @param gateway
     * @param dns1
     * @param dns2
     */
    public void saveEthernetAsStatic(String ip, String subnet, String gateway, String dns1, String dns2) {
        IpConfiguration ipConfiguration = mEthernetManager.getConfiguration(mInterfaceName);
        if (ipConfiguration == null) {
            ipConfiguration = new IpConfiguration();
        }
        ipConfiguration.setIpAssignment(IpConfiguration.IpAssignment.STATIC);
        StaticIpConfiguration staticConfig = new StaticIpConfiguration();
        ipConfiguration.setStaticIpConfiguration(staticConfig);

        Inet4Address inetAddr = (Inet4Address) NetworkUtils.numericToInetAddress(ip);
        int networkPrefixLength = getPrefixLength(subnet);
        staticConfig.ipAddress = new LinkAddress(inetAddr, networkPrefixLength);
        staticConfig.gateway = (Inet4Address) NetworkUtils.numericToInetAddress(gateway);
        staticConfig.dnsServers.add((Inet4Address) NetworkUtils.numericToInetAddress(dns1));
        staticConfig.dnsServers.add((Inet4Address) NetworkUtils.numericToInetAddress(dns2));

        mEthernetManager.setConfiguration(mInterfaceName, ipConfiguration);
    }

    public void saveEthernetAsHdcp() {
        IpConfiguration ipConfiguration = mEthernetManager.getConfiguration(mInterfaceName);
        if (ipConfiguration == null) {
            ipConfiguration = new IpConfiguration();
        }
        ipConfiguration.setIpAssignment(IpConfiguration.IpAssignment.DHCP);
        ipConfiguration.setStaticIpConfiguration(null);
        mEthernetManager.setConfiguration(mInterfaceName, ipConfiguration);
    }

    /**
     * 验证代理是否有效
     *
     * @param host     代理主机名
     * @param port     代理端口
     * @param exclList 限制域名列表
     * @return
     */
    public boolean validateProxy(String host, String port, List<String> exclList) {
        String hc = "a-zA-Z0-9_";
        Matcher match = Pattern.compile("^$|^[" + hc + "]+(-[" + hc + "]+)*(\\.[" + hc + "]+(-[" + hc + "]+)*)*$").matcher(host);
        Pattern exclusionPattern = Pattern.compile("$|^(\\*)?\\.?[" + hc + "]+(-[" + hc + "]+)*(\\.[" + hc + "]+(-[" + hc + "]+)*)*$");

        if (!match.matches())
            return false;

        for (String excl : exclList) {
            Matcher m = exclusionPattern.matcher(excl);
            if (!m.matches())
                return false;
        }

        if (host.length() > 0 && port.length() == 0) {
            return false;
        }

        if (port.length() > 0) {
            if (host.length() == 0) {
                return false;
            }
            int portVal = -1;
            try {
                portVal = Integer.parseInt(port);
            } catch (NumberFormatException ex) {
                return false;
            }
            if (portVal <= 0 || portVal > 0xFFFF) {
                return false;
            }
        }
        return true;
    }

    /**
     * 设置有线网络代理
     *
     * @param host     代理主机名
     * @param port     代理端口
     * @param exclList 限制域名列表
     */
    public void setEthernetProxy(String host, String port, List<String> exclList) {
        IpConfiguration ipConfiguration = mEthernetManager.getConfiguration(mInterfaceName);
        if (ipConfiguration == null) {
            ipConfiguration = new IpConfiguration();
        }

        if (TextUtils.isEmpty(host) && TextUtils.isEmpty(port) && exclList.size() == 0) {
            ipConfiguration.setProxySettings(IpConfiguration.ProxySettings.NONE);
            ipConfiguration.setHttpProxy(null);
        } else {
            ipConfiguration.setProxySettings(IpConfiguration.ProxySettings.STATIC);
            ipConfiguration.setHttpProxy(new ProxyInfo(host, Integer.parseInt(port), join(exclList, ",")));
        }
        mEthernetManager.setConfiguration(mInterfaceName, ipConfiguration);
    }

    public String getEthernetProxyHost() {
        IpConfiguration ipConfiguration = mEthernetManager.getConfiguration(mInterfaceName);
        if (ipConfiguration != null) {
            ProxyInfo info = ipConfiguration.getHttpProxy();
            if (info != null) {
                return info.getHost();
            }
        }
        return "";
    }

    public String getEthernetProxyPort() {
        IpConfiguration ipConfiguration = mEthernetManager.getConfiguration(mInterfaceName);
        if (ipConfiguration != null) {
            ProxyInfo info = ipConfiguration.getHttpProxy();
            if (info != null) {
                return Integer.toString(info.getPort());
            }
        }
        return "";
    }

    public String[] getEthernetProxyExcludeList() {
        IpConfiguration ipConfiguration = mEthernetManager.getConfiguration(mInterfaceName);
        if (ipConfiguration != null) {
            ProxyInfo info = ipConfiguration.getHttpProxy();
            if (info != null) {
                return info.getExclusionList();
            }
        }
        return null;
    }


    //Hotpot
    public boolean isHotpotOpen() {
        int state = mWifiManager.getWifiApState();
        return state == WifiManager.WIFI_AP_STATE_ENABLED;
    }

    private static final class OnStartTetheringCallback extends
            ConnectivityManager.OnStartTetheringCallback {

        OnStartTetheringCallback() {
        }

        @Override
        public void onTetheringStarted() {
        }

        @Override
        public void onTetheringFailed() {
        }

    }

    public boolean setHotpotEnable(boolean enable) {
        final ContentResolver cr = mContext.getContentResolver();
        int wifiState = mWifiManager.getWifiState();

        if (enable && ((wifiState == WifiManager.WIFI_STATE_ENABLING) ||
                (wifiState == WifiManager.WIFI_STATE_ENABLED))) {
            mWifiManager.setWifiEnabled(false);
            Settings.Global.putInt(cr, Settings.Global.WIFI_SAVED_STATE, 1);
        }

        if (enable) {
            mConnectivityManager.startTethering(ConnectivityManager.TETHERING_WIFI, true, mStartTetheringCallback, mHandler);
        } else {
            mConnectivityManager.stopTethering(ConnectivityManager.TETHERING_WIFI);
        }

        /**
         *  If needed, restore Wifi on tether disable
         */
        if (!enable) {
            int wifiSavedState = 0;
            try {
                wifiSavedState = Settings.Global.getInt(cr, Settings.Global.WIFI_SAVED_STATE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            if (wifiSavedState == 1) {
                mWifiManager.setWifiEnabled(true);
                Settings.Global.putInt(cr, Settings.Global.WIFI_SAVED_STATE, 0);
            }
        }
        return true;
    }

    public void setHotpotConfig(String name, int security, String password) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = name;

        switch (security) {
            case SECURITY_NONE:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;

            case SECURITY_PSK:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA2_PSK);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                if (password.length() != 0) {
                    config.preSharedKey = password;
                }
                break;
        }
        if (mWifiManager.getWifiApState() == WifiManager.WIFI_AP_STATE_ENABLED) {
            setHotpotEnable(false);
            mRestartApAfterConfigChange = true;
        }
        mWifiManager.setWifiApConfiguration(config);
        setHotpotEnable(true);
    }

    public String getHotpotName() {
        WifiConfiguration config = mWifiManager.getWifiApConfiguration();
        if (config != null) {
            return config.SSID;
        }
        return mContext.getString(com.android.internal.R.string.wifi_tether_configure_ssid_default);
    }

    public int getHotpotSecurity() {
        WifiConfiguration config = mWifiManager.getWifiApConfiguration();
        if (config != null) {
            if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA2_PSK)) {
                return SECURITY_PSK;
            }
        }
        return SECURITY_NONE;
    }

    public String getHotpotPassword() {
        WifiConfiguration config = mWifiManager.getWifiApConfiguration();
        if (config != null) {
            if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA2_PSK)) {
                return config.preSharedKey;
            }
        }
        return "";
    }
}