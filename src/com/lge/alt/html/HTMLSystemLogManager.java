package com.lge.alt.html;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.util.Log;

import com.lge.alt.data.DataManager;
import com.lge.alt.data.MainLogData;
import com.lge.alt.data.SystemLogData;
import com.lge.alt.data.DataManager.DataType;

public class HTMLSystemLogManager extends HTMLManager {

    public static final int HTML_REPORT_TABLE_PROCINFO = 1;

    public HTMLSystemLogManager(BufferedWriter bw, DataManager dm) {

        this.mBW = bw;
        this.mDm = dm;
        this.TAG = "HTMLSystemLogManager";
    }

    @Override
    public void writeHtml(int type) {

        SystemLogData data = (SystemLogData)mDm.getData(DataType.SYSTEM);

        switch(type) {

        case HTML_REPORT_TABLE_PROCINFO :

            writeTable("Proc Information", data.getProcInfo(), -1,
                    HTMLManager.TABLE_HEADER_TOP);
            break;

            // writeTable("StartProc Information", data.getStartProcInfo(), -1);
            // writeTable("RestartProc Information", data.getRestartProcInfo(), -1);
            // writeTable("DiedProc Information", data.getDiedProcInfo(), -1);

        default :
            Log.e(TAG, "Not support writeHtml type");

        }
    }

    @Override
    public void clearChartData() {
        //do nothing
    }

    @Override
    public String getHtmlHeader() {
        return "";
    }


    public void writeHtml() {

        SystemLogData data = (SystemLogData)mDm.getData(DataType.SYSTEM);

        // writeTable("StartProc Information", data.getStartProcInfo(), -1);
        // writeTable("RestartProc Information", data.getRestartProcInfo(), -1);
        // writeTable("DiedProc Information", data.getDiedProcInfo(), -1);
        writeTable("Proc Information", data.getProcInfo(), -1,
                HTMLManager.TABLE_HEADER_TOP);
    }

    @Override
    public String getTitleString() {
        return "Main";
    }

    @Override
    public void writeHtmlScriptHead() {
    }
}
