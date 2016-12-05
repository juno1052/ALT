package com.lge.alt.data.node;

import java.util.ArrayList;

public class DataZoneInfoNormal extends NodeInfoData {

    private static final String TAG = "DataZoneInfoNormal";
    private static final String mTitle = "Zone Info (Normal)";
    private static final String[] mHeader = { "Time", "FreeMem", "WMK_Min",
            "WMK_Low", "WMK_High" };

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