package com.lge.alt.data.node;

import java.io.PrintWriter;
import java.util.ArrayList;

import android.util.Log;

public class DataBlockInfo extends NodeInfoData {

    private static final String TAG = "DataBlockInfo";
    private static final String mTitle = "Block Info";
    private static final String[] mHeader = { "Zone", "Unmovable",
            "Reclaimable", "Movable", "Reserve", "CMA", "Isolate" };

    public static ArrayList<NodeInfoData> mDataRaw = new ArrayList<NodeInfoData>();
    public static ArrayList<ArrayList<String>> mData = new ArrayList<>();

    public String mTime = null;

    public DataBlockInfo() {

        StringBuffer sb = new StringBuffer();

        for (int idx = 0; idx < mHeader.length; idx++) {

            sb.append(String.format("%-15s", mHeader[idx]));
        }

        mInfo.add(sb.toString());
    }

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

    @Override
    public void prepareDataList() {

        // DO NOTTHING
    }

    @Override
    public void makeOutPut(PrintWriter pw) {

        makeOutPutTitle(pw);

        for (NodeInfoData data : mDataRaw) {

            pw.println("[ " + ((DataBlockInfo)data).mTime + " ]");

            for (String str : data.mInfo) {

                pw.println(str);
            }

            pw.println("");
        }
    }
}