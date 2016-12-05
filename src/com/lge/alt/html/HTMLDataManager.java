package com.lge.alt.html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.lge.alt.data.DataManager;
import com.lge.alt.data.KernelLogData;

public class HTMLDataManager extends HTMLManager {

    private static HTMLDataManager mInstance = null;

    private HTMLMainLogManager mMainLogManager;
    private HTMLSystemLogManager mSystemLogManager;
    private HTMLKernelManager mKernelManager;
    private HTMLNodeManager mNodeManager;
    private HTMLDumpManager mDumpDataManager;

    private HTMLDataManager() {
    }

    public static HTMLDataManager getInstance() {

        if (mInstance == null) {
            mInstance = new HTMLDataManager();
        }

        return mInstance;
    }

    public void makeOutputDataHtml(File dir) {

        DataManager dm = DataManager.getInstance();
        ArrayList<HTMLManager> managerList = new ArrayList<HTMLManager>();

        String fileName = "report.html";
        File file = new File(dir, fileName);

        try {
            this.mBW = new BufferedWriter(new FileWriter(file), 1048576);

            mMainLogManager = new HTMLMainLogManager(this.mBW, dm);
            mSystemLogManager = new HTMLSystemLogManager(this.mBW, dm);
            mKernelManager = new HTMLKernelManager(this.mBW, dm);
            mNodeManager = new HTMLNodeManager(this.mBW, dm);
            mDumpDataManager = new HTMLDumpManager(this.mBW, dm);

        } catch (IOException e) {
            e.printStackTrace();
        }

        synchronized (dm) {
            open();

            //1.launch info
            mMainLogManager.writeHtml(HTMLMainLogManager.HTML_REPORT_TABLE_LAUNCHINFO);

            //2. proc info
            mSystemLogManager.writeHtml(HTMLSystemLogManager.HTML_REPORT_TABLE_PROCINFO);

            //3. lmk table
            mKernelManager.writeHtml(HTMLKernelManager.HTML_REPORT_TABLE_LMK);

            //4. lmk chart
            mKernelManager.writeHtml(HTMLKernelManager.HTML_REPORT_CHART_LMK);

            //5. meminfo, frag info
            mNodeManager.writeHtml(HTMLNodeManager.HTML_REPORT_CHART_MEMINFO);

            //6. gcinfo
            mMainLogManager.writeHtml(HTMLMainLogManager.HTML_REPORT_TABLE_GCINFO);

            //6. zoneinfo normal/high
            mNodeManager.writeHtml(HTMLNodeManager.HTML_REPORT_CHART_ZONEINFO);

            //7. cpu/gpu clock info
            mNodeManager.writeHtml(HTMLNodeManager.HTML_REPORT_CHART_CPUINFO);

            //8. cpu usage / load
            mNodeManager.writeHtml(HTMLNodeManager.HTML_REPORT_CHART_CPULOAD);

            //9. cpu load per process
            mDumpDataManager.writeHtml(HTMLDumpManager.HTML_REPORT_DUMP_CPUINFO);

            //10. emmc workload
            mKernelManager.writeHtml(HTMLKernelManager.HTML_REPORT_TABLE_EMMC);

            //11. battery info
            mNodeManager.writeHtml(HTMLNodeManager.HTML_REPORT_TABLE_BATTINFO);

            //12. proc stats dump
            mDumpDataManager.writeHtml(HTMLDumpManager.HTML_REPORT_DUMP_PROCSTAT);

            //chart list clear
            mMainLogManager.clearChartData();
            mSystemLogManager.clearChartData();
            mKernelManager.clearChartData();
            mNodeManager.clearChartData();
            mDumpDataManager.clearChartData();

            close();
        }
    }

    public void open() {
        writeHtml(HTMLSource.HTML_BEGIN);
        writeHtml(HTMLSource.HTML_HEAD_BEGIN);
        writeHtmlScriptHead();
        writeHtml(HTMLSource.HTML_HEAD_END);
        writeHtml(HTMLSource.HTML_BODY_BEGIN);
        writeHtmlTitle();
    }

    public void close() {

        writeHtml(HTMLSource.HTML_BODY_END);
        writeHtml(HTMLSource.HTML_END);

        if (this.mBW != null)
            try {
                this.mBW.close();
                this.mBW = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public String getHtmlHeader() {
        return "";
    }

    @Override
    public String getTitleString() {
        return "Main";
    }

    @Override
    public void writeHtmlScriptHead() {
        writeHtml(HTMLSource.GOOGLECHART_LOAD_AJAX);

        mMainLogManager.writeHtmlScriptHead();
        mSystemLogManager.writeHtmlScriptHead();
        mKernelManager.writeHtmlScriptHead();
        mNodeManager.writeHtmlScriptHead();
        mDumpDataManager.writeHtmlScriptHead();
    }

    @Override
    public void clearChartData() {
        // TODO Auto-generated method stub
    }

    @Override
    public void writeHtml(int type) {
        // TODO Auto-generated method stub

    }
}
