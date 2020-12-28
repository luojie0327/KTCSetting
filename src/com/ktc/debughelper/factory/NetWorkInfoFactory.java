package com.ktc.debughelper.factory;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.RouteInfo;
import android.net.StaticIpConfiguration;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.ktc.debughelper.bean.AvailableNetWorkInfo;
import com.ktc.setting.R;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NetWorkInfoFactory {
    private Context mContext;
    private ConnectivityManager mConnectivityManager;
    private WifiManager mWifiManager;
    private EthernetManager mEthernetManager;

    public NetWorkInfoFactory(Context context) {
        this.mContext = context;
        mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mEthernetManager = (EthernetManager) mContext.getSystemService(Context.ETHERNET_SERVICE);
    }

    private boolean isEthernetConnect() {
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET && networkInfo.isConnected();
    }

    private boolean isWifiConnect() {
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected();
    }

    private void constructorEthernetInfo(AvailableNetWorkInfo availableNetWorkInfo) {
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        IpConfiguration ipConfiguration = mEthernetManager.getConfiguration(getInterfaceName());
        StaticIpConfiguration staticIpConfiguration = ipConfiguration.getStaticIpConfiguration();
        availableNetWorkInfo.setNetWorkType(networkInfo.getTypeName());
        availableNetWorkInfo.setNetWorkName("有线网络");
        availableNetWorkInfo.setNetWorkProxy(ipConfiguration.getProxySettings() == IpConfiguration.ProxySettings.STATIC
                ? mContext.getString(R.string.wifi_action_proxy_manual) : mContext.getString(R.string.wifi_action_proxy_none));
        availableNetWorkInfo.setNetWorkModel(ipConfiguration.getIpAssignment() == IpConfiguration.IpAssignment.STATIC ?
                mContext.getString(R.string.wifi_action_static) : mContext.getString(R.string.wifi_action_dhcp));

        availableNetWorkInfo.setNetWorkStatus(networkInfo.isConnected() ? mContext.getString(R.string.wifi_action_connected) :
                mContext.getString(R.string.wifi_action_none_connected));
        getEthernetIpAddress(availableNetWorkInfo);
        availableNetWorkInfo.setNetWorkMacAddress(networkInfo.getExtraInfo());
    }

    private String getInterfaceName(){
        String[] ifaces = mEthernetManager.getAvailableInterfaces();
        if (ifaces.length > 0) {
            return ifaces[0];
        }
        return null;
    }

    private void constructorWifiInfo(AvailableNetWorkInfo availableNetWorkInfo) {
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        DhcpInfo dhcpInfo = mWifiManager.getDhcpInfo();
        WifiConfiguration wifiConfiguration = null;
        List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            int networkId = wifiInfo.getNetworkId();
            for (int i = 0; i < configuredNetworks.size(); i++) {
                WifiConfiguration configuredNetwork = configuredNetworks.get(i);
                if (configuredNetwork.networkId == networkId) {
                    wifiConfiguration = configuredNetwork;
                }
            }
        }
        availableNetWorkInfo.setNetWorkType(networkInfo.getTypeName());
        availableNetWorkInfo.setNetWorkName(wifiInfo.getSSID());
        if (wifiConfiguration != null) {
            availableNetWorkInfo.setNetWorkProxy(wifiConfiguration.getProxySettings() == IpConfiguration.ProxySettings.NONE ?
                    mContext.getString(R.string.wifi_action_proxy_none) : mContext.getString(R.string.wifi_action_proxy_manual));
            availableNetWorkInfo.setNetWorkModel(wifiConfiguration.getIpAssignment() == IpConfiguration.IpAssignment.STATIC ?
                    mContext.getString(R.string.wifi_action_static) : mContext.getString(R.string.wifi_action_dhcp));
        }

        availableNetWorkInfo.setNetWorkStatus(networkInfo.isConnected() ? mContext.getString(R.string.wifi_action_connected) : mContext.getString(R.string.wifi_action_none_connected));
        availableNetWorkInfo.setNetWorkIp(intToIp(wifiInfo.getIpAddress()));
        availableNetWorkInfo.setNetWorkMask(intToIp(dhcpInfo.netmask));
        availableNetWorkInfo.setNetWorkGateway(intToIp(dhcpInfo.gateway));
        availableNetWorkInfo.setNetWorkDns1(intToIp(dhcpInfo.dns1));
        availableNetWorkInfo.setNetWorkDns2(intToIp(dhcpInfo.dns2));
        availableNetWorkInfo.setNetWorkMacAddress(wifiInfo.getMacAddress());
    }

    /**
     * --------------functions------------
     **/
    private String getMaskUtil(int lenth) {
        lenth = -1 >> 31 - (lenth - 1) << 31 - (lenth - 1);
        StringBuilder maskStr = new StringBuilder();
        byte[] maskIp = new byte[4];
        for (int i = 0; i < maskIp.length; i++) {
            maskIp[i] = (byte) (lenth >> (maskIp.length - 1 - i) * 8);
            maskStr.append(maskIp[i] & (byte) 0xff);
            if (i < maskIp.length - 1) {
                maskStr.append(".");
            }
        }
        return maskStr.toString();
    }

    private Network getFirstEthernet() {
        final Network[] networks = mConnectivityManager.getAllNetworks();
        for (final Network network : networks) {
            NetworkInfo networkInfo = mConnectivityManager.getNetworkInfo(network);
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                return network;
            }
        }
        return null;
    }

    public void getEthernetIpAddress(AvailableNetWorkInfo availableNetWorkInfo) {
        final Network network = getFirstEthernet();
        if (network == null) {
            return;
        }
        final LinkProperties linkProperties = mConnectivityManager.getLinkProperties(network);

        // MStar Android Patch Begin
        if (linkProperties != null) {
            for (LinkAddress linkAddress : linkProperties.getLinkAddresses()) {
                InetAddress inetAddres = linkAddress.getAddress();
                if (inetAddres instanceof Inet4Address)
                    availableNetWorkInfo.setNetWorkIp(inetAddres.getHostAddress());
            }
            String[] ips = resolutionIP(availableNetWorkInfo.getNetWorkIp());
            // gateway
            String[] gateways = null;
            if (!(linkProperties.getRoutes() == null)) {
                for (RouteInfo route : linkProperties.getRoutes()) {
                    if (route.isDefaultRoute()) {
                        availableNetWorkInfo.setNetWorkGateway(route.getGateway()
                                .getHostAddress());
                        gateways = resolutionIP(availableNetWorkInfo.getNetWorkGateway());
                        break;
                    }
                }
            }
            // dns1
            Iterator<InetAddress> dnsIterator = linkProperties
                    .getDnsServers().iterator();
            if (dnsIterator.hasNext()) {
                String dns1 = dnsIterator.next().getHostAddress();
                if (matchAddress(dns1)) {
                    availableNetWorkInfo.setNetWorkDns1(dns1);
                }
            }
            // dns2
            if (dnsIterator.hasNext()) {
                String dns2 = dnsIterator.next().getHostAddress();
                if (matchAddress(dns2)) {
                    availableNetWorkInfo.setNetWorkDns1(dns2);
                }
            }
            String mask = null;
            if (null != ips && null != gateways) {
                if (ips[0].equals(gateways[0])) {
                    mask = "255";
                } else {
                    mask = "0";
                }
                if (ips[1].equals(gateways[1])) {
                    mask += ".255";
                } else {
                    mask += ".0";
                }
                if (ips[2].equals(gateways[2])) {
                    mask += ".255";
                } else {
                    mask += ".0";
                }
                if (ips[3].equals(gateways[3])) {
                    mask += ".255";
                } else {
                    mask += ".0";
                }
            }
            availableNetWorkInfo.setNetWorkMask(mask);
        }
        // MStar Android Patch End
    }

    public boolean matchAddress(String address) {
        if (address == null) {
            return false;
        }
        String regex = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(address);
        return matcher.matches();
    }

    public String[] resolutionIP(String ip) {
        if (ip == null) {
            return null;
        }
        return ip.split("\\.");
    }

    private String intToIp(int paramInt) {
        return String.format(
                "%d.%d.%d.%d", paramInt & 0xff, paramInt >> 8 & 0xff,
                paramInt >> 16 & 0xff, paramInt >> 24 & 0xff
        );
    }

    public List<String> constructorNetWorkInfo() {
        AvailableNetWorkInfo netWorkInfo = new AvailableNetWorkInfo();
        if (isEthernetConnect()) {
            constructorEthernetInfo(netWorkInfo);
        } else if (isWifiConnect()) {
            constructorWifiInfo(netWorkInfo);
        } else {
            return null;
        }
        return constructorResultList(netWorkInfo);
    }

    private List<String> constructorResultList(AvailableNetWorkInfo netWorkInfo) {
        List<String> resultNetWorkInfoList = new ArrayList<>();
        resultNetWorkInfoList.add(mContext.getString(R.string.str_network_type) + (": ") + (netWorkInfo.getNetWorkType()));
        resultNetWorkInfoList.add(mContext.getString(R.string.str_network_name) + (": ") + (netWorkInfo.getNetWorkName()));
        resultNetWorkInfoList.add(mContext.getString(R.string.str_network_proxy) + (": ") + (netWorkInfo.getNetWorkProxy()));
        resultNetWorkInfoList.add(mContext.getString(R.string.str_network_mode) + (": ") + (netWorkInfo.getNetWorkModel()));
        resultNetWorkInfoList.add(mContext.getString(R.string.str_network_status) + (": ") + (netWorkInfo.getNetWorkStatus()));
        resultNetWorkInfoList.add(mContext.getString(R.string.str_network_ip) + (": ") + (netWorkInfo.getNetWorkIp()));
        resultNetWorkInfoList.add(mContext.getString(R.string.str_network_mask) + (": ") + (netWorkInfo.getNetWorkMask()));
        resultNetWorkInfoList.add(mContext.getString(R.string.str_network_gateway) + (": ") + (netWorkInfo.getNetWorkGateway()));
        resultNetWorkInfoList.add(mContext.getString(R.string.str_network_dns1) + (": ") + (netWorkInfo.getNetWorkDns1()));
        resultNetWorkInfoList.add(mContext.getString(R.string.str_network_dns2) + (": ") + (netWorkInfo.getNetWorkDns2()));
        resultNetWorkInfoList.add(mContext.getString(R.string.str_network_mac) + (": ") + (netWorkInfo.getNetWorkMacAddress()));
        return resultNetWorkInfoList;
    }
}