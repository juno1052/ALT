package com.lge.alt.html;

import java.io.BufferedWriter;
import java.util.ArrayList;

import android.util.Log;
import com.lge.alt.R;

import com.lge.alt.ALTService;
import com.lge.alt.data.DataManager;
import com.lge.alt.data.DataManager.DataType;
import com.lge.alt.data.KernelLogData;
import com.lge.alt.data.KernelLogData.LMKData;

public class HTMLKernelManager extends HTMLManager {

    private static final int CHART_LMK_INFO = 1;
    private static final int CHART_MMC_INFO = 2;

    public static final int HTML_REPORT_TABLE_LMK = 3;
    public static final int HTML_REPORT_CHART_LMK = 4;
    public static final int HTML_REPORT_TABLE_EMMC = 5;

    private final String LMK_TABLE_TITLE = "LMK Info : Total count : ";
    private final String LMK_CHART_TITLE = "LMK Info";
    private final String EMMC_TABLE_TITLE = "eMMC Loading Info.";

    public HTMLKernelManager(BufferedWriter bw, DataManager dm) {

        this.mBW = bw;
        this.mDm = dm;
        this.TAG = "HTMLKernelManager";
    }

    @Override
    public String getHtmlHeader() {
        return "";
    }

    @Override
    public void writeHtml(int type) {

        KernelLogData data = (KernelLogData)mDm.getData(DataType.KERNEL);
        int lmkCount = LMKData.getLMKTotalCnt();

        switch (type) {

        case HTML_REPORT_TABLE_LMK:
            // LMK info table

            if (lmkCount > 0) {

                writeTable(LMK_TABLE_TITLE + lmkCount,
                        KernelLogData.getLMKHtmlList(), -1,
                        HTMLManager.TABLE_HEADER_TOP);

            } else {

                // writeHtmlParagrph("<b>" + " === No LMK information === " +
                // ALTService.getContext().getString(R.string.guide_selinux_mode)
                // + "</b>",
                // HTMLSource.FONTSIZE_NORMAL, HTMLSource.FONTCOLOR_BLUE);

                String str = ALTService.getContext().getString(
                        R.string.guide_selinux_mode);

                ArrayList<String> list = new ArrayList<String>();

                list.add(str);

                writeTableString(LMK_TABLE_TITLE + lmkCount, list,
                        HTMLSource.FONTCOLOR_GRAY, HTMLSource.FONTSIZE_SMALL,
                        HTMLSource.HTML_ALIGN_LEFT);

            }

            break;

        case HTML_REPORT_CHART_LMK:

            if (lmkCount > 0) {

                writeChartTypeBody(LMK_CHART_TITLE, mChart.get(CHART_LMK_INFO));
                writeHtml("<br>");
                writeHtml("<br>");

            } else {

                Log.d(TAG, "No LMK Info, thus not provide LMK CHAT");

            }

            break;

        case HTML_REPORT_TABLE_EMMC:
            // eMMC info table

            int eMMCSize = KernelLogData.getMMCInfoHtmlList().size();

            if (eMMCSize > 1) {

                writeTable(EMMC_TABLE_TITLE,
                        KernelLogData.getMMCInfoHtmlList(), -1,
                        HTMLManager.TABLE_HEADER_TOP);

            } else {

                ArrayList<String> list = new ArrayList<String>();
                String str = ALTService.getContext().getString(
                        R.string.guide_emmc);

                list.add(str);

                writeTableString(EMMC_TABLE_TITLE, list,
                        HTMLSource.FONTCOLOR_GRAY,
                        HTMLSource.FONTSIZE_SMALL,
                        HTMLSource.HTML_ALIGN_LEFT);
            }

            break;

        default:
            Log.e(TAG, "Not support writeHtml type");

        }
    }

    @Override
    public void clearChartData() {
        mChart.clear();
    }

    @Override
    public String getTitleString() {
        return "Kernel";
    }

    @Override
    public void writeHtmlScriptHead() {

        writeChartTypeBHead(getChartLMK(KernelLogData.getLMKDataList()));

    }

    public HTMLChartType getChartLMK(ArrayList<LMKData> data) {

        HTMLChartType chart = null;
        StringBuffer dataSet = new StringBuffer();
        int cnt = 1;

        if (data == null) {
            return null;
        }

        chart = new HTMLChartType();

        chart.chartName = "chart_LMKInfo";
        chart.chartType = "LineChart";
        chart.chartTitle = "LMK Info";
        chart.chartFunction = "drawLMKInfo";
        chart.chartWidth = HTMLSource.CHART_SIZE_NORMAL_PER;
        chart.chartHeight = HTMLSource.CHART_SIZE_NORMAL_PER;

        dataSet.append("data.addColumn('string', 'count');").append("\n");
        dataSet.append("data.addColumn('number', 'Adj score');").append("\n");
        dataSet.append("data.addColumn({type:'number', role:'interval'});")
                .append("\n"); // interval role col.
        dataSet.append("data.addColumn({type:'number', role:'interval'});")
                .append("\n"); // interval role col.
        dataSet.append("data.addColumn({type:'string', role:'annotation'});")
                .append("\n");
        dataSet.append(
                "data.addColumn({type:'string', role:'annotationText'});")
                .append("\n");
        dataSet.append("data.addColumn({type:'boolean',role:'certainty'});")
                .append("\n");
        dataSet.append("data.addRows([").append("\n");

        for (LMKData lmkData : data) {

            dataSet.append("[").append("'").append(cnt).append("'").append(",")
                    .append(lmkData.getAdjScore()).append(",")
                    .append("0")
                    .append(",")
                    // adj score boundary max
                    .append("1000")
                    .append(",")
                    // adj score boundary min
                    .append("'").append("#").append("'").append(",")
                    .append("'").append(lmkData.getProcessName()).append("'")
                    .append(",").append("true").append("],").append("\n");

            cnt++;
        }

        chart.chartDataSet = dataSet.toString();

        mChart.put(CHART_LMK_INFO, chart);

        return chart;
    }

}
