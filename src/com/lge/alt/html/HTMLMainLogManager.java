package com.lge.alt.html;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.util.Log;

import com.lge.alt.data.DataManager;
import com.lge.alt.data.MainLogData;
import com.lge.alt.data.DataManager.DataType;

public class HTMLMainLogManager extends HTMLManager {

    public static final int HTML_REPORT_TABLE_LAUNCHINFO = 1;
    public static final int HTML_REPORT_TABLE_GCINFO     = 2;

    public HTMLMainLogManager(BufferedWriter bw, DataManager dm) {

        this.mBW = bw;
        this.mDm = dm;
        this.TAG = "HTMLMainLogManager";
    }

    @Override
    public String getHtmlHeader() {
        return "";
    }

    @Override
    public void writeHtml(int type) {

        MainLogData data = (MainLogData)mDm.getData(DataType.MAIN);

        switch(type) {

        case HTML_REPORT_TABLE_LAUNCHINFO  :

            writeTable("Detected app", data.getLaunchInfo(), -1,
                    HTMLManager.TABLE_HEADER_LEFT);
            break;

        case HTML_REPORT_TABLE_GCINFO  :

            writeTable("GC Information", data.getGCInfo(), -1,
                    HTMLManager.TABLE_HEADER_TOP);
            break;

        default :
            Log.e(TAG, "Not support writeHtml type");

        }
    }

    @Override
    public void clearChartData() {
        //do nothing
    }

    @Override
    public String getTitleString() {
        return "Main";
    }

    @Override
    public void writeHtmlScriptHead() {
    }
}
