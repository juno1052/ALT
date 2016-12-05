package com.lge.alt.data.node;

import java.io.PrintWriter;
import java.util.ArrayList;

import android.util.Log;

public class DataCpuUsage extends NodeInfoData {

    private static final String TAG = "DataCpuUsage";
    private static final String mTitle = "Cpu Usage";
    private static final String[] mHeader = { "cpu", "user", "nice", "sys",
            "idle", "iow", "irq", "sirq" };
    private static final int PRE_BUFFER_CNT = 1;

    public static ArrayList<NodeInfoData> mDataRaw = new ArrayList<NodeInfoData>();
    public static ArrayList<ArrayList<String>> mData = new ArrayList<>();

    @Override
    public String getTag() {

        return TAG;
    }

    @Override
    public String[] getHeader() {

        return mHeader;
    }

    @Override
    public ArrayList<NodeInfoData> getDataRaw() {

        return mDataRaw;
    }

    @Override
    public ArrayList<ArrayList<String>> getDataHtml() {

        return mData;
    }

    @Override
    public String getTitle() {

        return mTitle;
    }

    @Override
    public int getPreBufferCnt() {

        return PRE_BUFFER_CNT;
    }

    @Override
    public void prepareDataList() {

        String[] header = getHeader();
        ArrayList<NodeInfoData> dataRaw = getDataRaw();
        ArrayList<ArrayList<String>> dataHtml = getDataHtml();

        ArrayList<String> list = new ArrayList<>();

        for (int idx = 0; idx < header.length; idx++) {

            list.add(header[idx]);
        }

        dataHtml.add(list);

        int dataSize = dataRaw.size();
        int listSize = dataRaw.get(0).mInfo.size();

        for (int listIdx = 0; listIdx < listSize; listIdx++) {

            String[] strArrayStart = dataRaw.get(0).mInfo.get(listIdx).split(
                    " +");
            String[] strArrayEnd = dataRaw.get(dataSize - 1).mInfo.get(listIdx)
                    .split(" +");

            list = new ArrayList<>();

            for (int dataIdx = 0; dataIdx < strArrayStart.length; dataIdx++) {

                if (dataIdx == 0) {
                    list.add(strArrayStart[dataIdx]);
                    continue;
                }

                list.add(Integer.toString(Integer
                        .parseInt(strArrayEnd[dataIdx])
                        - Integer.parseInt(strArrayStart[dataIdx])));
            }

            dataHtml.add(list);
        }
    }

    @Override
    public void makeOutPut(PrintWriter pw) {

        makeOutPutTitle(pw);

        for (ArrayList<String> list : getData()) {

            String printLine = "";

            for (String str : list) {
                printLine += String.format("%-10s", str);
            }

            pw.println(printLine);

        }
        pw.println("");
    }
}