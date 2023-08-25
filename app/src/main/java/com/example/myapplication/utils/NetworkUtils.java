package com.example.myapplication.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

public class NetworkUtils {
    private static final String TAG = "ConnectManager";

    /**
     * Indicates this network uses a Cellular transport.
     */
    public static final int TRANSPORT_CELLULAR = 0;

    /**
     * Indicates this network uses a Wi-Fi transport.
     */
    public static final int TRANSPORT_WIFI = 1;

    /**
     * Indicates this network uses a Bluetooth transport.
     */
    public static final int TRANSPORT_BLUETOOTH = 2;

    /**
     * Indicates this network uses an Ethernet transport.
     */
    public static final int TRANSPORT_ETHERNET = 3;

    /**
     * Indicates this network uses a VPN transport.
     */
    public static final int TRANSPORT_VPN = 4;

    /**
     * Indicates this network uses a Wi-Fi Aware transport.
     */
    public static final int TRANSPORT_WIFI_AWARE = 5;

    /**
     * Indicates this network uses a LoWPAN transport.
     */
    public static final int TRANSPORT_LOWPAN = 6;

    /**
     * Indicates this network uses a Test-only virtual interface as a transport.
     * @hide
     */
    public static final int TRANSPORT_TEST = 7;

    /**
     * Indicates this network uses a USB transport.
     */
    public static final int TRANSPORT_USB = 8;



    /**
     * >= Android 10（Q版本）推荐
     *
     * 当前使用MOBILE流量上网
     */
    public static boolean isMobileNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        Network network = cm.getActiveNetwork();
        if (null == network) {
            return false;
        }

        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        if (null == capabilities) {
            return false;
        }
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }


    /**
     * >= Android 10（Q版本）推荐
     *
     * 当前使用WIFI上网
     */

    public static boolean isWifiNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();
        if (null == network) {
            return false;
        }
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        if (null == capabilities) {
            return false;
        }
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }


    /**
     *  >= Android 10（Q版本）推荐
     *
     * 当前使用以太网上网
     */
    public static boolean isEthernetNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();
        if (null == network) {
            return false;
        }
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        if (null == capabilities) {
            return false;
        }
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }



    /**
     * >= Android 10（Q版本）推荐
     *
     * NetworkCapabilities.NET_CAPABILITY_INTERNET，表示此网络应该(maybe)能够访问internet
     *
     *  判断当前网络可以正常上网
     *  表示此连接此网络并且能成功上网。  例如，对于具有NET_CAPABILITY_INTERNET的网络，这意味着已成功检测到INTERNET连接。
     */
    public static boolean isConnectedAvailableNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();
        if (null == network) {
            return false;
        }
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        if (null == capabilities) {
            return false;
        }
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }


    /**
     * >= Android 10（Q版本）推荐
     *
     * 获取成功上网的网络类型
     * value = {
     *    TRANSPORT_CELLULAR,   0 表示此网络使用蜂窝传输。
     *    TRANSPORT_WIFI,       1 表示此网络使用Wi-Fi传输。
     *    TRANSPORT_BLUETOOTH,  2 表示此网络使用蓝牙传输。
     *    TRANSPORT_ETHERNET,   3 表示此网络使用以太网传输。
     *    TRANSPORT_VPN,        4 表示此网络使用VPN传输。
     *    TRANSPORT_WIFI_AWARE, 5 表示此网络使用Wi-Fi感知传输。
     *    TRANSPORT_LOWPAN,     6 表示此网络使用LoWPAN传输。
     *    TRANSPORT_TEST,       7 指示此网络使用仅限测试的虚拟接口作为传输。
     *    TRANSPORT_USB,        8 表示此网络使用USB传输
     * }
     */
//    public static int getConnectedNetworkType(Context context) {
//        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        Network network = cm.getActiveNetwork();
//        if (null == network) {
//            return -1;
//        }
//        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
//        if (null == capabilities) {
//            return -1;
//        }
//        if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
//            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
//                return NetworkCapabilities.TRANSPORT_CELLULAR;
//            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
//                return NetworkCapabilities.TRANSPORT_WIFI;
//            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)) {
//                return NetworkCapabilities.TRANSPORT_BLUETOOTH;
//            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
//                return NetworkCapabilities.TRANSPORT_ETHERNET;
//            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
//                return NetworkCapabilities.TRANSPORT_VPN;
//            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE)) {
//                return NetworkCapabilities.TRANSPORT_WIFI_AWARE;
//            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN)) {
//                return NetworkCapabilities.TRANSPORT_LOWPAN;
//            }
//        }
//        return -1;
//    }
    public static String getConnectedNetworkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();
        if (null == network) {
            return "无网络连接";
        }
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        if (null == capabilities) {
            return "网络不可用";
        }
        if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return "蜂窝数据";
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return "WIFI";
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)) {
                return "BLUETOOTH";
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return "ETHERNET";
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                return "VPN";
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE)) {
                return "WIFI_AWARE";
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN)) {
                return "LOWPAN";
            }
        }
        return null;
    }
}
