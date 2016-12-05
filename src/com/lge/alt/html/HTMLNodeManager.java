package com.lge.alt.html;

import java.io.BufferedWriter;
import java.util.ArrayList;

import android.util.Log;

import com.lge.alt.data.DataManager;
import com.lge.alt.data.NodeData;
import com.lge.alt.data.DataManager.DataType;
import com.lge.alt.data.node.INodeInfo;
import com.lge.alt.data.node.NodeInfoData;

public class HTMLNodeManager extends HTMLManager implements INodeInfo {

    public static final int HTML_REPORT_CHART_MEMINFO  = 1;
    public static final int HTML_REPORT_CHART_ZONEINFO = 2;
    public static final int HTML_REPORT_CHART_CPUINFO  = 3;
    public static final int HTML_REPORT_CHART_CPULOAD  = 4;
    public static final int HTML_REPORT_TABLE_BATTINFO = 5;

    public HTMLNodeManager(BufferedWriter bw, DataManager dm) {

        this.mBW = bw;
        this.mDm = dm;
        this.TAG = "HTMLNodeManager";
    }

    @Override
    public String getHtmlHeader() {
        return "";
    }

    @Override
    public void writeHtml(int type) {

        switch(type) {

        case HTML_REPORT_CHART_MEMINFO  :

            writeHtmlTableOpen();
            writeChartTypeBodyTable(null, mChart.get(MEMINFO));
            writeChartTypeBodyTable(null, mChart.get(FRAGINFO));
            writeHtmlTableClose();
            break;

        case HTML_REPORT_CHART_ZONEINFO :

            writeHtmlTableOpen();
            writeChartTypeBodyTable(null, mChart.get(ZONEINFO_NORMAL));
            writeChartTypeBodyTable(null, mChart.get(ZONEINFO_HIGH));
            writeHtmlTableClose();
            break;

        case HTML_REPORT_CHART_CPUINFO  :

            writeHtmlTableOpen();
            writeChartTypeBodyTable(null, mChart.get(CPUINFO));
            writeChartTypeBodyTable(null, mChart.get(GPUINFO));
            writeHtmlTableClose();
            break;

        case HTML_REPORT_CHART_CPULOAD  :

            writeHtmlTableOpen();
            writeChartTypeBodyTable(null, mChart.get(CPUUSAGE));
            writeChartTypeBodyTable(null, mChart.get(CPULOAD));
            writeHtmlTableClose();
            break;

        case HTML_REPORT_TABLE_BATTINFO  :
            writeNodeTable(BATTINFO);
            break;

        default :
            Log.e(TAG, "Not support writeHtml type");

        }
    }

    private void writeHtmlTableOpen() {
        writeHtml("<table width=100%>");
        writeHtml("<tr>");
    }

    private void writeHtmlTableClose() {
        writeHtml("</tr>");
        writeHtml("</table>");
    }

    @Override
    public void clearChartData() {
        mChart.clear();
    }

    @Override
    public String getTitleString() {
        return "Main";
    }

    @Override
    public void writeHtmlScriptHead() {

        writeChartTypeAHead(buildChart(MEMINFO));
        writeChartTypeAHead(buildChart(FRAGINFO));
        writeChartTypeAHead(buildChart(ZONEINFO_NORMAL));
        writeChartTypeAHead(buildChart(ZONEINFO_HIGH));
        writeChartTypeAHead(buildChart(CPUINFO));
        writeChartTypeAHead(buildChart(GPUINFO));
        writeChartTypeAHead(buildChart(CPUUSAGE));
        writeChartTypeAHead(buildChart(CPULOAD));
    }

    private void writeNodeTable(int dataType) {

        NodeData data = (NodeData)mDm.getData(DataType.NODE);
        NodeInfoData info = data.getDataObj(dataType);

        writeTable(info.getTitle(), info.getData(), -1,
                HTMLManager.TABLE_HEADER_TOP);
    }

    private HTMLChartType buildChart(int dataType) {

        NodeData nodeData = (NodeData)mDm.getData(DataType.NODE);
        if (nodeData == null) {
            return null;
        }

        NodeInfoData nodeInfo = nodeData.getDataObj(dataType);
        if (nodeInfo == null) {
            return null;
        }

        ArrayList<ArrayList<String>> data = nodeInfo.getData();

        if (data == null) {
            return null;
        }

        HTMLChartType chart = new HTMLChartType();

        buildChartOption(chart, nodeInfo);

        if (dataType == CPUUSAGE) {
            buildPieChart(chart, data);
        } else if (dataType == CPULOAD) {
            buildBarChart(chart, data);
        } else {
            buildLineChart(chart, data);
        }

        mChart.put(dataType, chart);

        return chart;
    }

    private void buildChartOption(HTMLChartType chart, NodeInfoData info) {

        String name = info.getClass().getSimpleName();

        chart.chartName = name + "Chart";
        chart.chartTitle = info.getTitle();
        chart.chartFunction = "Function" + name;
        chart.chartWidth = HTMLSource.CHART_SIZE_NORMAL_PER;
        chart.chartHeight = HTMLSource.CHART_SIZE_NORMAL_PER;
    }

    private void buildLineChart(HTMLChartType chart,
            ArrayList<ArrayList<String>> data) {

        chart.chartType = "LineChart";
        chart.chartDataSet = "\n";

        int sizeRow = data.size();
        int sizeCol = data.get(0).size();

        for (int row = 0; row < sizeRow; row++) {

            ArrayList<String> dataRow = data.get(row);

            chart.chartDataSet += "[";

            for (int col = 0; col < sizeCol; col++) {

                if (row == 0 || col == 0) {
                    chart.chartDataSet += "'";
                }

                chart.chartDataSet += dataRow.get(col);

                if (row == 0 || col == 0) {
                    if (col != sizeCol - 1) {
                        chart.chartDataSet += "', ";
                    } else {
                        chart.chartDataSet += "'";
                    }
                } else {
                    if (col != sizeCol - 1) {
                        chart.chartDataSet += ", ";
                    } else {
                        chart.chartDataSet += "";
                    }
                }
            }

            chart.chartDataSet += "]";

            if (row != sizeRow - 1) {
                chart.chartDataSet += ",";
            }

            chart.chartDataSet += "\n";
        }
    }

    private void buildPieChart(HTMLChartType chart,
            ArrayList<ArrayList<String>> data) {

        chart.chartType = "PieChart";
        chart.chartDataSet = "\n";

        int sizeRow = data.get(0).size();

        for (int row = 0; row < sizeRow; row++) {

            chart.chartDataSet += "['";
            chart.chartDataSet += data.get(0).get(row);
            chart.chartDataSet += "', ";
            if (row == 0) {
                chart.chartDataSet += "'";
            }
            chart.chartDataSet += data.get(1).get(row);
            if (row == 0) {
                chart.chartDataSet += "'";
            }
            chart.chartDataSet += "]";

            if (row != sizeRow - 1) {
                chart.chartDataSet += ",\n";
            }
        }
    }

    private void buildBarChart(HTMLChartType chart,
            ArrayList<ArrayList<String>> data) {

        chart.chartType = "BarChart";
        chart.chartDataSet = "\n";

        int sizeRow = data.size();
        int sizeCol = data.get(0).size();

        for (int row = 0; row < sizeRow; row++) {

            ArrayList<String> dataRow = data.get(row);

            chart.chartDataSet += "[";

            for (int col = 0; col < sizeCol; col++) {

                if (row == 0 || col == 0) {
                    chart.chartDataSet += "'";
                }

                chart.chartDataSet += dataRow.get(col);

                if (row == 0 || col == 0) {
                    if (col != sizeCol - 1) {
                        chart.chartDataSet += "', ";
                    } else {
                        chart.chartDataSet += "'";
                    }
                } else {
                    if (col != sizeCol - 1) {
                        chart.chartDataSet += ", ";
                    } else {
                        chart.chartDataSet += "";
                    }
                }
            }

            chart.chartDataSet += "]";

            if (row != sizeRow - 1) {
                chart.chartDataSet += ",";
            }

            chart.chartDataSet += "\n";
        }
    }

}
