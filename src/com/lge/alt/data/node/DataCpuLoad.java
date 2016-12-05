package com.lge.alt.data.node;

import java.io.PrintWriter;
import java.util.ArrayList;

import android.util.Log;

public class DataCpuLoad extends NodeInfoData {

    private static final String TAG = "DataMemInfo";
    private static final String mTitle = "Cpu Load";
    private static final String[] mHeader = { "Period", "Load" };

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
    public void prepareDataList() {

        String[] header = getHeader();
        ArrayList<NodeInfoData> dataRaw = getDataRaw();
        ArrayList<ArrayList<String>> dataHtml = getDataHtml();

        ArrayList<String> list = new ArrayList<>();

        for (int idx = 0; idx < header.length; idx++) {

            list.add(header[idx]);
        }

        dataHtml.add(list);

        list = new ArrayList<>();
        list.add("1 min");
        list.add(dataRaw.get(0).mInfo.get(0));
        dataHtml.add(list);

        list = new ArrayList<>();
        list.add("5 mins");
        list.add(dataRaw.get(0).mInfo.get(1));
        dataHtml.add(list);

        list = new ArrayList<>();
        list.add("15 mins");
        list.add(dataRaw.get(0).mInfo.get(2));
        dataHtml.add(list);
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