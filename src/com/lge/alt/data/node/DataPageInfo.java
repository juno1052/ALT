package com.lge.alt.data.node;

import java.io.PrintWriter;
import java.util.ArrayList;

import android.util.Log;

public class DataPageInfo extends NodeInfoData {

    private static final String TAG = "DataPageInfo";
    private static final String mTitle = "Page Info";
    private static final String[] mHeader = { "Zone", "Type", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "10" };

    public static ArrayList<NodeInfoData> mDataRaw = new ArrayList<NodeInfoData>();
    public static ArrayList<ArrayList<String>> mData = new ArrayList<>();

    public String mTime = null;
    public String mBlockOrder = null;
    public String mBlockPages = null;

    public DataPageInfo() {

        StringBuffer sb = new StringBuffer();

        for (int idx = 0; idx < mHeader.length; idx++) {

            if (idx == 1) {
                sb.append(String.format("%-15s", mHeader[idx]));
            } else {
                sb.append(String.format("%-10s", mHeader[idx]));
            }
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

        pw.println(((DataPageInfo)mDataRaw.get(0)).mBlockOrder);
        pw.println(((DataPageInfo)mDataRaw.get(0)).mBlockPages);
        pw.println("");

        for (NodeInfoData data : mDataRaw) {

            pw.println("[ " + ((DataPageInfo)data).mTime + " ]");

            for (String str : data.mInfo) {

                pw.println(str);
            }

            pw.println("");
        }
    }
}