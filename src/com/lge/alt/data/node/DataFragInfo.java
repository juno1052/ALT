package com.lge.alt.data.node;

import java.io.PrintWriter;
import java.util.ArrayList;

import android.util.Log;

public class DataFragInfo extends NodeInfoData {

    private static final String TAG = "DataFragInfo";
    private static final String mTitle = "Fragmentation Level";
    private static final String[] mHeaderRaw = { "Zone", "0", "1", "2", "3", "4", "5", "6",
            "7", "8", "9", "10" };
    private static final String[] mHeader = { "Time", "AvrFragNormal", "AvrFragHigh" };

    public static ArrayList<NodeInfoData> mDataRaw = new ArrayList<NodeInfoData>();
    public static ArrayList<ArrayList<String>> mData = new ArrayList<>();

    public String mTime = null;
    public String mAvrFragNormal = null;
    public String mAvrFragHigh = null;

    public DataFragInfo() {

        StringBuffer sb = new StringBuffer();

        for (int idx = 0; idx < mHeaderRaw.length; idx++) {

            sb.append(String.format("%-10s", mHeaderRaw[idx]));
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

        String[] header = getHeader();
        ArrayList<NodeInfoData> dataRaw = getDataRaw();
        ArrayList<ArrayList<String>> dataHtml = getDataHtml();

        ArrayList<String> list = new ArrayList<>();

        for (int idx = 0; idx < header.length; idx++) {

            list.add(header[idx]);
        }

        dataHtml.add(list);

        for (NodeInfoData raw : dataRaw) {
            list = new ArrayList<>();

            list.add(((DataFragInfo)raw).mTime);
            list.add(((DataFragInfo)raw).mAvrFragNormal);
            list.add(((DataFragInfo)raw).mAvrFragHigh);

            dataHtml.add(list);
        }
    }

    @Override
    public void makeOutPut(PrintWriter pw) {

        makeOutPutTitle(pw);

        for (NodeInfoData data : mDataRaw) {

            pw.println("[ " + ((DataFragInfo)data).mTime + " ]");

            pw.println("Average Frag Level (Normal) : " + ((DataFragInfo)data).mAvrFragNormal + " %");
            pw.println("Average Frag Level (High) : " + ((DataFragInfo)data).mAvrFragHigh + " %");

            for (String str : data.mInfo) {

                pw.println(str);
            }

            pw.println("");
        }
    }
}