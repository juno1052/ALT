package com.lge.alt.data.dump;

import java.io.PrintWriter;
import java.util.ArrayList;

import android.util.Log;

public class DumpDataProcStats extends DumpDataInfo {

    private static final String TITLE_STRING = "Process stats";
    public String mProcStatsItem = "";

    static ArrayList<DumpDataInfo> mDumpDataList = new ArrayList<DumpDataInfo>();
    static ArrayList<ArrayList<String>> mDumpHtmlList = new ArrayList<ArrayList<String>>();

    public DumpDataProcStats() {
        super();
    }

    public DumpDataProcStats(String data) {

        this.mProcStatsItem = data;
    }

    @Override
    public ArrayList<DumpDataInfo> getDataList() {
        return mDumpDataList;
    }

    @Override
    public ArrayList<ArrayList<String>> getDataHtml() {
        return mDumpHtmlList;
    }

    @Override
    public void makeOutPut(PrintWriter pw) {

        pw.println(getTitle());

        for (DumpDataInfo info : mDumpDataList) {
            pw.println(((DumpDataProcStats)info).mProcStatsItem);
        }
    }

    @Override
    public void clearData() {

        Log.d(TAG, "clearData !!");
        getDataList().clear();
    }

    @Override
    public void prepareHtmlDataList() {
        //do nothing for HTML
        setTitle(TITLE_STRING);
    }

}
