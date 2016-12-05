package com.lge.alt.data.dump;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.util.Log;

public class DumpDataCpuInfo extends DumpDataInfo {

    private static final String TITLE_STRING = "CPU Load Per Process.";

    private static String mTimeLine = "";

    private String mThreadName = "";
    private String mCpuLoad = "";
    private boolean mMainThread = false;
    private boolean mEOF = false;

    public static ArrayList<String> mDumpRawList = new ArrayList<>();
    static ArrayList<DumpDataInfo> mDumpDataList = new ArrayList<DumpDataInfo>();
    static ArrayList<ArrayList<String>> mDumpHtmlList = new ArrayList<ArrayList<String>>();

    public DumpDataCpuInfo() {
        super();
    }

    public DumpDataCpuInfo(String name, String load) {

        this.mThreadName = name;
        this.mCpuLoad =  load;
        this.mMainThread = true;
        this.mEOF = false;
    }

    public DumpDataCpuInfo(String name, String load, boolean mainThread) {

        this.mThreadName = name;
        this.mCpuLoad =  load;
        this.mMainThread = mainThread;
        this.mEOF = false;
    }

    public DumpDataCpuInfo(String name, String load, boolean mainThread, boolean EOF) {

        this.mThreadName = name;
        this.mCpuLoad =  load;
        this.mMainThread = mainThread;
        this.mEOF = EOF;
    }


    public void setTimeInfo(String time) {
        mTimeLine = time;
    }

    public String getTimeInfo() {
       return mTimeLine;
    }

    public String getThreadName() {
        return mThreadName;
    }

    public String getCpuLoad() {
        return mCpuLoad;
    }

    public boolean isMainThread() {
        return mMainThread;
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

        pw.println("");
        pw.println(getTitle());
        pw.println("");

        for (String info : mDumpRawList) {
            pw.println(info);
        }
    }

    @Override
    public void clearData() {

        Log.d(TAG, "clearData !!");

        mDumpDataList.clear();
        mDumpRawList.clear();
        mDumpHtmlList.clear();

    }

    @Override
    public void prepareHtmlDataList() {

        setTitle(TITLE_STRING);
        makeCPUInfoHtmlList();
    }

    private void makeCPUInfoHtmlList() {

        ArrayList<String> list = new ArrayList<>();

        list.add(((DumpDataCpuInfo)mDumpDataList.get(0)).mThreadName);
        list.add(((DumpDataCpuInfo)mDumpDataList.get(0)).mCpuLoad);

        for(int i=1; i < mDumpDataList.size(); i++) {

            DumpDataCpuInfo dump = (DumpDataCpuInfo)(mDumpDataList.get(i));

          if(dump.mEOF) {
              mDumpHtmlList.add(list);
              break;
          }

          if(dump.mMainThread) {

             mDumpHtmlList.add(list);

             list = new ArrayList<>();

             list.add(dump.mThreadName);
             list.add(dump.mCpuLoad);

          } else {

              list.add(dump.mCpuLoad);
              list.add(dump.mThreadName);

          }
        }

    }

}
