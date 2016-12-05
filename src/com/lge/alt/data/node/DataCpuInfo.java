package com.lge.alt.data.node;

import java.util.ArrayList;

public class DataCpuInfo extends NodeInfoData {

    private static final String TAG = "DataCpuInfo";
    private static final String mTitle = "Cpu Clock";
    private static final String[] mHeader = { "Time", "Cpu0", "Cpu1", "Cpu2", "Cpu3" };

    public static ArrayList<NodeInfoData> mDataRaw = new ArrayList<NodeInfoData>();
    public static ArrayList<ArrayList<String>> mData = new ArrayList<>();

    @Override
    public String getTag() {

        return TAG;
    }

    @Override
    public String[] getHeader() {

        return mHeader;
    }

    @Override
    public ArrayList<NodeInfoData> getDataRaw() {

        return mDataRaw;
    }

    @Override
    public ArrayList<ArrayList<String>> getDataHtml() {

        return mData;
    }

    @Override
    public String getTitle() {

        return mTitle;
    }
}