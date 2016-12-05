package com.lge.alt.data.node;

import java.util.ArrayList;

public class DataMemInfo extends NodeInfoData {

    private static final String TAG = "DataMemInfo";
    private static final String mTitle = "Memory Info";
    private static final String[] mHeader = { "Time", "MemTotal", "MemFree", "Cached",
            "SwapTotal", "SwapFree", "Slab" };

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