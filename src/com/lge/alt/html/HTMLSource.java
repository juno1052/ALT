package com.lge.alt.html;

public class HTMLSource {

    public static final int FONTSIZE_LARGE  = 5;
    public static final int FONTSIZE_LITTLE_LARGE  = FONTSIZE_LARGE - 1;

    public static final int FONTSIZE_NORMAL = 3;
    public static final int FONTSIZE_LITTLE_NORMAL = FONTSIZE_NORMAL - 1;

    public static final int FONTSIZE_SMALL  = 2;
    public static final int FONTSIZE_LITTLE_SMALL  = FONTSIZE_SMALL - 1;

    public static final String TABLE_SIZE_LARGE_PER  = "98%";
    public static final String TABLE_SIZE_NORMAL_PER = "50%";
    public static final String TABLE_SIZE_SMALL_PER  = "25%";

    public static final String CHART_SIZE_LARGE_PER  = "98%";
    public static final String CHART_SIZE_NORMAL_PER = "60%";
    public static final String CHART_SIZE_SMALL_PER  = "30%";

    public static final String FONTCOLOR_RED     = "red";
    public static final String FONTCOLOR_GREEN   = "green";
    public static final String FONTCOLOR_YELLOW  = "yellow";
    public static final String FONTCOLOR_GRAY    = "gray";
    public static final String FONTCOLOR_BLUE    = "blue";
    public static final String FONTCOLOR_BLACK   = "black";
    public static final String FONTCOLOR_BROWN   = "brown";

    public static final String TABLE_HEAD_BORDER_COLOR = "#8C8C8C";
    public static final String TABLE_TITLE_TD_BG_COLOR = "#A6A6A6";
    public static final String TABLE_TD_BORDER_COLOR = "#D5D5D5";
    public static final String TABLE_TD_BG_COLOR = "#F6F6F6";

    public static final String HTML_ALIGN_LEFT = "left";
    public static final String HTML_ALIGN_CENTER = "center";
    public static final String HTML_ALIGN_RIGHT = "right";

    public static final String HTML_BEGIN = "<html>\n";
    public static final String HTML_END = "</html>\n";
    public static final String HTML_HEAD_BEGIN = "<head>\n";
    public static final String HTML_HEAD_END = "</head>\n";
    public static final String HTML_BODY_BEGIN = "<body>\n";
    public static final String HTML_BODY_END = "</body>\n";
    public static final String HTML_TITLE =
            "<table>\n"
            + "<tr>\n"
            + "<td bgcolor=black width=1000 height=50 align=center>\n"
            + "<font color=white style='font-size:20pt'><b>Application Launching Tracker</b></font>\n"
            + "</td>\n"
            + "</tr>\n"
            + "<tr>\n"
            + "<td bgcolor=#dddddd width=1000 height=30 align=center>\n"
            + "<font color=black style='font-size:15pt'>developed by 3team 3Part</font></td>\n"
            + "</tr>\n" +
            "</table>\n";

    public static final String HTML_PARAGRAPH =
            "<p>"
            + "<font size=%font-size%>"
            + "<font color=%font-color%>"
            + "%data%"
            + "</font>"
            + "</p>";

    public static final String HTML_TABLE_HEAD =
            "<table align=center style=\"width:%TableWidth%;"
            + "border:2px "
            + "dashed %TableColor%;"
            + "border-radius: 5px; "
            + "border-spacing:1px 1px; "
            + "table-layout:fixed; "
            + "word-break:break-all;"
            + "padding:1px;"
            + "text-align:center\">";

    public static final String HTML_TABLE_TD =
            "<td align=\"%Align%\" bgcolor=\"%BGColor%\" style=\"border:2px solid %BorderColor%;\">"
            + "<font color=\"%FontColor%\" size=%FontSize%>";


    public static final String GOOGLECHART_LOAD_AJAX =
            "<script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script>\n";

    public static final String GOOGLECHART_CORECHART_TYPE_A_HEAD =
            "<script type=\"text/javascript\">\n"
            + "google.load(\"visualization\", \"1\", {packages:[\"corechart\"]});\n"
            + "google.setOnLoadCallback(%ChartFunction%);\n\n"
            + "function %ChartFunction%() {\n\n"
            + "var data = google.visualization.arrayToDataTable([\n"
            + "%ChartDataSet%\n"
            + "]);\n\n"
            + "var options = {\n"
            + "title: '%ChartTitle%'\n"
            + "};\n\n"
            + "var chart = new google.visualization.%ChartType%(document.getElementById('%ChartName%'));\n\n"
            + "chart.draw(data, options);\n" + "}\n" + "</script>\n\n";

    public static final String GOOGLECHART_CORECHART_TYPE_B_HEAD =
            "<script type=\"text/javascript\">\n"
            + "google.load(\"visualization\", \"1\", {packages:[\"corechart\"]});\n"
            + "google.setOnLoadCallback(%ChartFunction%);\n\n"
            + "function %ChartFunction%() {\n\n"
            + "var data = new google.visualization.DataTable();\n"
            + "%ChartDataSet%\n"
            + "]);\n"
            + "var options = {\n"
            + "title: '%ChartTitle%', \n"
            + "%OptionA% \n"
            + "%OptionB% \n"
            + "};\n"
            + "var chart = new google.visualization.%ChartType%(document.getElementById('%ChartName%'));\n\n"
            + "chart.draw(data, options);\n" + "}\n" + "</script>\n\n";

    public static final String GOOGLECHART_CORECHART_TYPE_BODY =
            "<br>"
            + "<div id=\"%ChartName%\" style=\"width: %ChartWidth%; height: %ChartHeight%;border :dashed 2px blue; border-radius:5px\">"
            + "</div>";

    public static final String GOOGLECHART_CORECHART_TYPE_BODY_TABLE_TD =
            "<td width=50%>"
             + "<div id=\"%ChartName%\" style=\"border :dashed 2px blue; border-radius:5px\">"
             + "</div>"
             + "</td>";

}
