package com.example.myapplication.utils.bluetooth.interfaces;


import android.bluetooth.BluetoothDevice;

import java.util.List;

/**
 *@desc 蓝牙搜索监听
 *
 */

public interface IBTScanListener {


    /**
     * 搜索开始
     */
    void onScanStart();

    /**
     * 搜索结束
     *
     * @param deviceList
     */
    void onScanStop(List<BluetoothDevice> deviceList);

    /**
     * 发现新设备
     * @param device
     */
    void onFindDevice(BluetoothDevice device);
}
