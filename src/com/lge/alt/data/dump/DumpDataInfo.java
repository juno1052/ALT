package com.lge.alt.data.dump;

import java.io.PrintWriter;
import java.util.ArrayList;

public abstract class DumpDataInfo {

    public static final String TAG = "DumpInfoData";

    public String mTitle = "";

    public ArrayList<String> mInfo = new ArrayList<String>();

    public void clearData() {}

    public abstract void makeOutPut(PrintWriter pw);

    public abstract ArrayList<DumpDataInfo> getDataList();
    public abstract ArrayList<ArrayList<String>> getDataHtml();
    public abstract void prepareHtmlDataList();

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

}