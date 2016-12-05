package com.lge.alt.data.node;

import java.io.PrintWriter;
import java.util.ArrayList;

import com.lge.alt.ALTService;

import android.util.Log;

public abstract class NodeInfoData {

    private static final int PRE_BUFFER_TIME = 3*1000;

    public ArrayList<String> mInfo = new ArrayList<>();

    public int getPreBufferCnt() {

        // pre-logging period : 3s
        return PRE_BUFFER_TIME / ALTService.NODE_PERIOD;
    }

    public void clearData() {

        int nRemove = getDataRaw().size() - getPreBufferCnt();

        getDataHtml().clear();

        if (nRemove > 0) {
            while (nRemove-- != 0) {
                getDataRaw().remove(0);
            }
        }
    }

    public void prepareDataList() {

        String[] header = getHeader();
        ArrayList<NodeInfoData> dataRaw = getDataRaw();
        ArrayList<ArrayList<String>> dataHtml = getDataHtml();

        ArrayList<String> list = new ArrayList<>();

        for (int idx = 0; idx < header.length; idx++) {

            list.add(header[idx]);
        }

        dataHtml.add(list);

        for (NodeInfoData raw : dataRaw) {
            dataHtml.add(raw.mInfo);
        }
    }

    protected void makeOutPutTitle(PrintWriter pw) {

        pw.println("******************** " + getTitle() + " ********************\n");
    }

    public void makeOutPut(PrintWriter pw) {

        makeOutPutTitle(pw);

        for (ArrayList<String> list : getData()) {

            int cols = 0;
            String printLine = "";

            for (String str : list) {

                if (cols++ == 0) {
                    printLine += String.format("%-20s", str);
                } else {
                    printLine += String.format("%-10s", str);
                }
            }

            pw.println(printLine);

        }
        pw.println("");
    }

    public ArrayList<ArrayList<String>> getData() {

        return getDataHtml();
    }

    public abstract String getTag();

    public abstract String[] getHeader();

    public abstract ArrayList<NodeInfoData> getDataRaw();

    public abstract ArrayList<ArrayList<String>> getDataHtml();

    public abstract String getTitle();

}