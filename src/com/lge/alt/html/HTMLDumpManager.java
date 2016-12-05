package com.lge.alt.html;

import java.io.BufferedWriter;
import java.util.ArrayList;

import android.util.Log;

import com.lge.alt.data.DataManager;
import com.lge.alt.data.KernelLogData;
import com.lge.alt.data.DataManager.DataType;
import com.lge.alt.data.KernelLogData.LMKData;
import com.lge.alt.data.DumpData;
import com.lge.alt.data.dump.DumpDataCpuInfo;
import com.lge.alt.data.dump.DumpDataInfo;
import com.lge.alt.data.dump.DumpDataProcStats;
import com.lge.alt.data.dump.IDumpInfo;
import com.lge.alt.html.HTMLManager.HTMLChartType;

public class HTMLDumpManager extends HTMLManager implements IDumpInfo {

    private final String LMK_CHART_TITLE = "LMK Info";
    private final int MAIN_THREAD_CNT = 2;

    public HTMLDumpManager(BufferedWriter bw, DataManager dm) {

        this.mBW = bw;
        this.mDm = dm;
        this.TAG = "HTMLDumpDataManager";
    }

    @Override
    public String getHtmlHeader() {
        return "";
    }

    @Override
    public void writeHtml(int type) {

        switch (type) {

        case HTML_REPORT_DUMP_PROCSTAT:

            writeHtmlDumpProcStats();
            break;

        case HTML_REPORT_DUMP_CPUINFO:
            DumpData data = (DumpData)mDm.getData(DataType.DUMP);
            DumpDataInfo info = data.getDataObj(DUMP_CPUINFO);
            writeChartTypeBody(info.getTitle(), mChart.get(CHART_CPU_INFO));
            writeHtml("<br>");
            writeHtml("<br>");
            break;

        default:
            Log.e(TAG, "Not support writeHtml type");

        }
    }

    @Override
    public void clearChartData() {
        // do nothing
        mChart.clear();
    }

    public void writeHtmlDumpProcStats() {
        DumpData data = (DumpData)mDm.getData(DataType.DUMP);
        DumpDataInfo info = data.getDataObj(DUMP_PROCSTATS);

        writeHtml("<br><b>" + info.getTitle() + "</b>\n");

        for (DumpDataInfo dump : info.getDataList()) {
            String item = ((DumpDataProcStats)dump).mProcStatsItem;

            if (item.trim().startsWith("*")) {

                writeHtmlParagrph("<b>" + item + "</b>",
                        HTMLSource.FONTSIZE_NORMAL, HTMLSource.FONTCOLOR_BLUE);
            } else if (item.trim().startsWith("Process Stats")) {

                writeHtmlParagrph(item, HTMLSource.FONTSIZE_NORMAL,
                        HTMLSource.FONTCOLOR_RED);
            } else {

                writeHtmlParagrph(item);
            }
        }
    }

    public HTMLChartType getChartCpuInfo() {

        HTMLChartType chart = null;
        StringBuffer dataSet = new StringBuffer();
        int maxWorkthread = 0;
        int maxListSize = 0;

        DumpData data = (DumpData)mDm.getData(DataType.DUMP);
        DumpDataInfo info = data.getDataObj(DUMP_CPUINFO);
        ArrayList<ArrayList<String>> htmlList = info.getDataHtml();

        maxListSize = htmlList.get(0).size();
        maxWorkthread = maxListSize - MAIN_THREAD_CNT;

        ArrayList<String> al = htmlList.get(0);

        for (ArrayList<String> list : htmlList) {

            if (maxListSize < list.size()) {

                maxListSize = list.size();
                maxWorkthread = maxListSize - 2;

            }
        }

        chart = new HTMLChartType();

        chart.chartName = "chart_DumpCpuInfo";
        chart.chartType = "BarChart";
        chart.chartTitle = info.getTitle() + "(showing only over cpu load 1%) "
                + ((DumpDataCpuInfo)info).getTimeInfo();

        chart.chartFunction = "drawDumpCpuInfo";
        chart.chartWidth = HTMLSource.CHART_SIZE_LARGE_PER;
        chart.chartHeight = HTMLSource.CHART_SIZE_LARGE_PER;

        chart.chartOptionA = "fontSize:9,";
        chart.chartOptionB = "chartArea: { height:500 },";

        dataSet.append("data.addColumn('string', 'Main');").append("\n");
        dataSet.append("data.addColumn('number', 'Main Thread');").append("\n");
        dataSet.append("data.addColumn({type:'number', role:'annotation'});")
                .append("\n");

        for (int i = 0; i < maxWorkthread; i = i + 2) {

            dataSet.append("data.addColumn('number', 'Thread');").append("\n");
            dataSet.append("data.addColumn({type:'string', role:'tooltip'});")
                    .append("\n");
            dataSet.append(
                    "data.addColumn({type:'number', role:'annotation'});")
                    .append("\n"); // same value as 'number'
        }

        dataSet.append("data.addRows([").append("\n");

        for (ArrayList<String> list : info.getDataHtml()) {

            dataSet.append("[").append("'").append(list.get(0)).append("'")
                    .append(",");
            dataSet.append(list.get(1)).append(",");
            dataSet.append(list.get(1)).append(",");

            int listSize = list.size();
            int emptySpace = maxListSize - listSize; // be filled with ''
            emptySpace = emptySpace + (emptySpace / 2); // for 'annotation' this
                                                        // value was not
                                                        // inclused in datalist.

            for (int i = 2; i < listSize; i++) {

                if (i % 2 == 0) {
                    dataSet.append(list.get(i)); // cpu load
                } else {
                    dataSet.append("'").append(list.get(i)).append("'"); // thread
                                                                         // name
                    dataSet.append(",");
                    dataSet.append(list.get(i - 1)); // cpu load annotation

                }
                dataSet.append(",");
            }

            if (emptySpace > 0) {

                for (int j = 1; j <= emptySpace; j++) {
                    dataSet.append(",");
                }

            }

            dataSet.append("]").append(",").append("\n");
        }

        chart.chartDataSet = dataSet.toString();

        mChart.put(CHART_CPU_INFO, chart);

        return chart;
    }

    @Override
    public String getTitleString() {
        return "DumpData";
    }

    @Override
    public void writeHtmlScriptHead() {
        writeChartTypeBHead(getChartCpuInfo());
    }

}
