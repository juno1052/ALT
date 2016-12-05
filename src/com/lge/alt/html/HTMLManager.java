package com.lge.alt.html;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.lge.alt.data.DataManager;

import android.util.Log;

public abstract class HTMLManager {

    String TAG = "HTMLManager";

    public static final int TABLE_HEADER_TOP = 1;
    public static final int TABLE_HEADER_LEFT = -1;
    public static final int TABLE_HEADER_NONE = 0;

    public String mLogDirPath;
    public BufferedWriter mBW;
    public String mPath;
    public HTMLSource mSource;
    public DataManager mDm;
    public HashMap<Integer, HTMLChartType> mChart = new HashMap<Integer, HTMLChartType>();

    public abstract String getHtmlHeader();

    public abstract String getTitleString();

    public abstract void writeHtmlScriptHead();

    public abstract void clearChartData();

    public abstract void writeHtml(int type);

    public void writeHtml(String html) {
        if (this.mBW == null)
            return;
        try {
            this.mBW.write(html + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getResultFilePath() {
        return this.mPath;
    }

    public void writeHtmlHeader() {
        writeHtml(getHtmlHeader());
    }

    public void writeHtmlTitle() {
        writeHtml(HTMLSource.HTML_TITLE);
    }

    public void writeHtmlParagrph(String data) {

        String html = HTMLSource.HTML_PARAGRAPH;

        html = html.replace("%data%", data);
        html = html.replace("%font-size%",
                String.valueOf(HTMLSource.FONTSIZE_SMALL));
        html = html.replace("%font-color%", HTMLSource.FONTCOLOR_BLACK);

        writeHtml(html);
    }

    public void writeHtmlParagrph(String data, int fontSize, String fontColor) {

        String html = HTMLSource.HTML_PARAGRAPH;

        html = html.replace("%data%", data);
        html = html.replace("%font-size%", String.valueOf(fontSize));
        html = html.replace("%font-color%", fontColor);

        writeHtml(html);
    }

    public void writeHtmlTableStart(String Title) {

        String html = "<br><br><b>" + Title + "</b><br>";
        html = html + HTMLSource.HTML_TABLE_HEAD;
        html = html.replace("%TableWidth%", HTMLSource.TABLE_SIZE_LARGE_PER);
        html = html.replace("%TableColor%", HTMLSource.TABLE_HEAD_BORDER_COLOR);

        writeHtml(html);

    }

    public void writeHtmlTableEnd() {
        writeHtml("</table><br>\n");
    }

    public void writeTable(String title, ArrayList<ArrayList<String>> list,
            int highLight, int direction) {

        if (this.mBW == null)
            return;

        if (list.size() == 0) {
            Log.e(TAG, "html data is null !!");
            return;
        }

        writeHtmlTableStart(title);

        int row = 0;
        int column = 0;
        String html = null;

        for (ArrayList<String> subList : list) {

            html = "<tr>\n";

            for (String data : subList) {

                boolean isHeader = false;

                if (direction == TABLE_HEADER_TOP) {
                    if (row == 0) {
                        isHeader = true;
                    }
                } else if (direction == TABLE_HEADER_LEFT) {
                    if (column == 0) {
                        isHeader = true;
                    }
                }

                if (isHeader) {

                    html = html + HTMLSource.HTML_TABLE_TD;

                    html = html.replace("%Align%",
                            String.valueOf(HTMLSource.HTML_ALIGN_CENTER));
                    html = html.replace("%BGColor%",
                            HTMLSource.TABLE_TITLE_TD_BG_COLOR);
                    html = html.replace("%BorderColor%",
                            HTMLSource.TABLE_TD_BORDER_COLOR);
                    html = html.replace("%FontColor%",
                            HTMLSource.FONTCOLOR_BROWN);
                    html = html.replace("%FontSize%",
                            String.valueOf(HTMLSource.FONTSIZE_SMALL));

                    html = html + "<b>" + data + "</b></font></td>\n";

                } else {

                    html = html + HTMLSource.HTML_TABLE_TD;

                    html = html.replace("%Align%",
                            String.valueOf(HTMLSource.HTML_ALIGN_CENTER));
                    html = html.replace("%BGColor%",
                            HTMLSource.TABLE_TD_BG_COLOR);
                    html = html.replace("%BorderColor%",
                            HTMLSource.TABLE_TD_BORDER_COLOR);
                    html = html.replace("%FontColor%",
                            HTMLSource.FONTCOLOR_BLACK);
                    html = html.replace("%FontSize%",
                            String.valueOf(HTMLSource.FONTSIZE_SMALL));

                    html = html + data + "</font></td>";

                }
                column++;
                isHeader = false;
            }

            html += "\n</tr>\n";

            try {
                this.mBW.write(html + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            row++;
            column = 0;
        }

        writeHtmlTableEnd();
    }

    public void writeTableString(String title, ArrayList<String> list,
            String fontcolorRed, int fontSize, String align) {

        if (this.mBW == null)
            return;

        if (list.size() == 0) {
            Log.e(TAG, "html data is null !!");
            return;
        }

        writeHtmlTableStart(title);

        String html = null;

        for (String data : list) {

            html = "<tr>\n";
            html = html + HTMLSource.HTML_TABLE_TD;

            html = html.replace("%Align%", align);
            html = html.replace("%BGColor%", HTMLSource.TABLE_TD_BG_COLOR);
            html = html.replace("%BorderColor%",
                    HTMLSource.TABLE_TD_BORDER_COLOR);
            html = html.replace("%FontColor%", fontcolorRed);
            html = html.replace("%FontSize%",
                    String.valueOf(fontSize));

            html = html + data + "</font></td>";

            html += "\n</tr>\n";
        }

        try {
            this.mBW.write(html + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        writeHtmlTableEnd();
    }

    public class HTMLChartType {
        String chartName;
        String chartType;
        String chartTitle;
        String chartFunction;
        String chartWidth;
        String chartHeight;
        String chartDataSet;
        String chartOptionA;
        String chartOptionB;
    };

    public void writeChartTypeAHead(HTMLChartType chart) {

        String html = HTMLSource.GOOGLECHART_CORECHART_TYPE_A_HEAD;

        html = html.replace("%ChartName%", chart.chartName);
        html = html.replace("%ChartType%", chart.chartType);
        html = html.replace("%ChartTitle%", chart.chartTitle);
        html = html.replace("%ChartFunction%", chart.chartFunction);
        html = html.replace("%ChartDataSet%", chart.chartDataSet);

        try {
            this.mBW.write(html);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeChartTypeBHead(HTMLChartType chart) {

        String html = HTMLSource.GOOGLECHART_CORECHART_TYPE_B_HEAD;

        html = html.replace("%ChartName%", chart.chartName);
        html = html.replace("%ChartType%", chart.chartType);
        html = html.replace("%ChartTitle%", chart.chartTitle);
        html = html.replace("%ChartWidth%", chart.chartWidth);
        html = html.replace("%ChartHeight%", chart.chartHeight);
        html = html.replace("%ChartFunction%", chart.chartFunction);
        html = html.replace("%ChartDataSet%", chart.chartDataSet);

        if (chart.chartOptionA == null) {
            html = html.replace("%OptionA%", "");
        } else {
            html = html.replace("%OptionA%", chart.chartOptionA);
        }

        if (chart.chartOptionB == null) {
            html = html.replace("%OptionB%", "");
        } else {
            html = html.replace("%OptionB%", chart.chartOptionB);
        }

        try {
            this.mBW.write(html);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeHtmlScriptBodyStart(String Title) {
        writeHtml("<br><br><b>" + Title
                + "</b><br><table border=1 cellspacing=0 cellpadding=0>\n");
    }

    public void writeHtmlScriptBodyEnd() {
        writeHtml("</table>\n");
    }

    public void writeChartTypeBody(String title, HTMLChartType chart) {

        String html = HTMLSource.GOOGLECHART_CORECHART_TYPE_BODY;

        html = html.replace("%ChartName%", chart.chartName);
        html = html.replace("%ChartWidth%", chart.chartWidth);
        html = html.replace("%ChartHeight%", chart.chartHeight);

        try {
            this.mBW.write(html);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeChartTypeBodyTable(String title, HTMLChartType chart) {

        String html = HTMLSource.GOOGLECHART_CORECHART_TYPE_BODY_TABLE_TD;

        html = html.replace("%ChartName%", chart.chartName);

        try {
            this.mBW.write(html);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeHtmlCommonMenu(HashMap<String, String> menuList) {
        Set keySet = menuList.keySet();
        Iterator it = keySet.iterator();
        int i = 0;
        String html = "";
        html = html + "<hr border=1>\n";
        html = html + "<p><b>Menu</b> : ";

        String key = "Main";
        String href = menuList.get(key);
        if (href != null) {
            html = html + " | ";
            html = html + "<a href=\"" + href + "\">" + key + "</a>";
        }

        key = "Memory";
        href = menuList.get(key);
        if (href != null) {
            html = html + " | ";
            html = html + "<a href=\"" + href + "\">" + key + "</a>";
        }

        key = "Open Log Folder";
        href = menuList.get(key);
        if (href != null) {
            html = html + " | ";
            html = html + "<a href=\"" + href + "\">" + key + "</a>";
        }

        html = html + "</p>\n";
        html = html + "<hr border=1>\n";
        html = html + "<br>\n";
        writeHtml(html);
    }
}
