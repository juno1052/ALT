package com.lge.alt.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.util.Log;

import com.lge.alt.data.KernelLogData.MMCBlockDataComparator.MMCDataSortType;

public class KernelLogData implements IData {

    private static final String TAG = "KernelData";
    private static final String DELIMETER = ";";

    static ArrayList<LMKData> mLMKDataList = new ArrayList<LMKData>();
    static ArrayList<MMCBlockData> mMMCBlockDataList = new ArrayList<MMCBlockData>();

    static ArrayList<ArrayList<String>> mLMKHtmlList = new ArrayList<ArrayList<String>>(); //use for html table
    static ArrayList<ArrayList<String>> mMMCInfoHtmlList = new ArrayList<ArrayList<String>>(); //use for html table


    @Override
    public void addDataToList(Object data) throws DataTypeMismatchException {

        if (data instanceof LMKData) {
            getLMKDataList().add((LMKData)data);
        } else if (data instanceof MMCBlockData) {
            getMMCBlockDataList().add((MMCBlockData)data);
        } else {
            throw new DataTypeMismatchException(
                    "can't add data to map. due to not support data type");
        }
    }

    public static ArrayList<LMKData> getLMKDataList() {

        return mLMKDataList;
    }

    public static ArrayList<MMCBlockData> getMMCBlockDataList() {

        return mMMCBlockDataList;
    }

    public static ArrayList<ArrayList<String>> getLMKHtmlList() {

        return mLMKHtmlList;
    }

    public static ArrayList<ArrayList<String>> getMMCInfoHtmlList() {

        return mMMCInfoHtmlList;
    }

    @Override
    public void prepareHtmlDataList() {
        makeLMKHtmlList();
        makeMMCBlockHtmlList();
    }

    private void makeLMKHtmlList() {
        ArrayList<String> rowTitle = new ArrayList<String>();

        //set Title
        for(String title : LMKData.sTitle) {
            rowTitle.add(title);
        }

        mLMKHtmlList.add(rowTitle);

        for (int i=0; i < mLMKDataList.size(); i++) {

            ArrayList<String> rowData = new ArrayList<String>();
            String[] datas = mLMKDataList.get(i).toString().split(DELIMETER);

            for(String data : datas){
                rowData.add(data);
            }
            mLMKHtmlList.add(rowData);
        }
    }

    private void makeMMCBlockHtmlList() {
        ArrayList<String> rowTitle = new ArrayList<String>();

        //set Title
        for(String title : MMCBlockData.sTitle) {
            rowTitle.add(title);
        }

        mMMCInfoHtmlList.add(rowTitle);

        for (int i=0; i < mMMCBlockDataList.size(); i++) {

            ArrayList<String> rowData = new ArrayList<String>();
            String[] datas = mMMCBlockDataList.get(i).toString().split(DELIMETER);

            for(String data : datas){
                rowData.add(data);
            }
            mMMCInfoHtmlList.add(rowData);
        }
    }

    @Override
    public void makeOutPutData(File file) {

        prepareHtmlDataList();// To-do
        PrintWriter pw = null;
        int top5_count = 0;

        try {
            pw = new PrintWriter(new FileOutputStream(file, true));

            pw.println("******************** LMK Info ********************");
            pw.println("");
            pw.println(String.format("LMK Total count : %5s",
                    LMKData.getLMKTotalCnt() + " times"));
            pw.println("");

            pw.println(String.format("%-25s", "time")
                    + String.format("%-25s", "adj")
                    + String.format("%-20s", "process")
                    + String.format("%-20s", "free mem"));

            for (LMKData data : mLMKDataList) {
                pw.println(String.format("%-25s", data.date)
                        + String.format("%-25s", data.adjName + " ("
                                + data.adjScore + ")")
                        + String.format("%-20s", data.processName)
                        + String.format("%-20s", data.freemem));
            }

            pw.println("");
            pw.println("******************** MMC load Info ********************");
            pw.println("");
            pw.println(String.format("%-20s",
                    "*TOP 5 MMC load (sorted by workload) "));
            pw.println("");

            ArrayList<MMCBlockData> workLoadList = (ArrayList<MMCBlockData>)getMMCBlockDataList().clone();
            Collections.sort(workLoadList, new MMCBlockDataComparator(
                    MMCDataSortType.WORKLOAD));

            pw.println(String.format("%-25s", "date")
                    + String.format("%-10s", "workload")
                    + String.format("%-15s", "totalReq")
                    + String.format("%-15s", "readReq")
                    + String.format("%-15s", "readTime")
                    + String.format("%-15s", "readTime")
                    + String.format("%-20s", "readSize")
                    + String.format("%-20s", "readThroughPut")
                    + String.format("%-15s", "writeReq")
                    + String.format("%-15s", "writeTime")
                    + String.format("%-20s", "writeSize")
                    + String.format("%-20s", "writeThroughPut"));

            for (MMCBlockData data : workLoadList) {

                if (top5_count > 5)
                    break;

                pw.println(String.format("%-25s", data.date)
                        + String.format("%-10s", data.workLoad + "%")
                        + String.format("%-15s", data.totalReqCnt)
                        + String.format("%-15s", data.readReqCnt)
                        + String.format("%-15s", data.readTime)
                        + String.format("%-20s", data.readSize)
                        + String.format("%-20s", data.readThroughtput)
                        + String.format("%-15s", data.writeReqCnt)
                        + String.format("%-15s", data.writeTime)
                        + String.format("%-20s", data.writeSize)
                        + String.format("%-20s", data.writeThroughput));
                top5_count++;
            }

            pw.println("");
            pw.println(String.format("%-20s",
                    "*All MMC load info (sorted by date)"));
            pw.println("");

            for (MMCBlockData data : mMMCBlockDataList) {
                pw.println(String.format("%-25s", data.date)
                        + String.format("%-10s", data.workLoad + "%")
                        + String.format("%-15s", data.totalReqCnt)
                        + String.format("%-15s", data.readReqCnt)
                        + String.format("%-15s", data.readTime)
                        + String.format("%-20s", data.readSize)
                        + String.format("%-20s", data.readThroughtput)
                        + String.format("%-20s", data.writeReqCnt)
                        + String.format("%-15s", data.writeTime)
                        + String.format("%-20s", data.writeSize)
                        + String.format("%-20s", data.writeThroughput));
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (pw != null)
                pw.close();
        }
    }

    @Override
    public void clearData() {

        Log.d(TAG, "clearData !!");

        getLMKDataList().clear();
        getMMCBlockDataList().clear();

        getLMKHtmlList().clear();
        getMMCInfoHtmlList().clear();

    }

    public static class LMKData {

        private String date;
        private String processName;
        private String pid;
        private String freemem;
        private String adjName;
        private int adjScore;

        private final StringBuffer sb = new StringBuffer();

        public static final String[] sTitle = { "Date", "Adj", "Process Name", "Free Memory" };

        static final int FOREGROUND_APP_ADJ_SCORE = 0;
        static final int VISIBLE_APP_ADJ_SCORE = 58;
        static final int PERCEPTIBLE_APP_ADJ_SCORE = 117;
        static final int BACKUP_APP_ADJ_SCORE = 176;
        static final int HEAVY_WEIGHT_APP_ADJ_SCORE = 235;
        static final int SERVICE_ADJ_SCORE = 294;
        static final int HOME_APP_ADJ_SCORE = 352;
        static final int PREVIOUS_APP_ADJ_SCORE = 411;
        static final int SERVICE_B_ADJ_SCORE = 470;
        static final int CACHED_APP_MIN_ADJ_SCORE = 529;
        static final int CACHED_APP_MAX_ADJ_SCORE = 1000;

        private final static HashMap<Integer, String> adjMap = new HashMap<Integer, String>() {
            {

                put(FOREGROUND_APP_ADJ_SCORE, "FOREGROUND");
                put(VISIBLE_APP_ADJ_SCORE, "VISIBLE");
                put(PERCEPTIBLE_APP_ADJ_SCORE, "PERCEPTIBLE");
                put(BACKUP_APP_ADJ_SCORE, "BACKUP");
                put(HEAVY_WEIGHT_APP_ADJ_SCORE, "HEAVY");
                put(SERVICE_ADJ_SCORE, "SERVICE A");
                put(HOME_APP_ADJ_SCORE, "HOME");
                put(PREVIOUS_APP_ADJ_SCORE, "PREVIOUS");
                put(SERVICE_B_ADJ_SCORE, "SERVICE B");
                put(CACHED_APP_MIN_ADJ_SCORE, "CACHED MIN");
                put(CACHED_APP_MAX_ADJ_SCORE, "CACHED MAX");

            }
        };

        public LMKData() {
            super();
        }

        public LMKData(String date, String processName, String pid,
                int adjScore, String freemem) {
            super();
            this.date = date;
            this.processName = processName;
            this.pid = pid;
            this.adjScore = adjScore;
            this.freemem = freemem;
            this.adjName = convertAdjName(adjScore);
        }

        private String convertAdjName(int adjScore) {

            String name = adjMap.get(adjScore);

            if (name == null) {
                if (adjScore > CACHED_APP_MIN_ADJ_SCORE)
                    return "CACHED APP";
                else {
                    return String.valueOf(adjScore);
                }
            } else {
                return name;
            }
        }

        public static int getLMKTotalCnt() {
            return getLMKDataList().size();
        }

        public String getDate() {
            return date;
        }

        public String getProcessName() {
            return processName;
        }

        public String getPid() {
            return pid;
        }

        public int getAdjScore() {
            return adjScore;
        }

        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return sb.append(date).append(DELIMETER).append(adjName).append("(").append(adjScore).append(")")
                    .append(DELIMETER).append(processName).append(DELIMETER).append(freemem).toString();

        }
    }

    public static class MMCBlockData {

        private String date = "";
        private Integer workLoad = 0;
        private String totalReqCnt = "";

        private String writeThroughput = "N/A";
        private String writeReqCnt = "N/A";
        private String writeSize = "N/A";
        private String writeTime = "N/A";

        private String readThroughtput = "N/A";
        private String readReqCnt = "N/A";
        private String readSize = "N/A";
        private String readTime = "N/A";
        private final StringBuffer sb = new StringBuffer();

        public static final String[] sTitle = { "Date", "WorkLoad",
                "Total ReqCnt", "ReadTime", "ReadSize", "ReadThroughput", "Read RqeCnt",
                "WriteTime", "WriteSize", "WriteThroughput", "Write ReqCnt" };

        public MMCBlockData() {
            super();
        }

        public MMCBlockData(String date, Integer workLoad, String totalReqCnt) {
            super();
            this.date = date;
            this.workLoad = workLoad;
            this.totalReqCnt = totalReqCnt;
        }

        public void setWriteData(String writeThroughput, String writeReqCnt,
                String writeSize, String writeTime) {

            this.writeThroughput = writeThroughput;
            this.writeReqCnt = writeReqCnt;
            this.writeSize = writeSize;
            this.writeTime = writeTime;
        }

        public void setReadData(String readThroughtput, String readReqCnt,
                String readSize, String readTime) {

            this.readThroughtput = readThroughtput;
            this.readReqCnt = readReqCnt;
            this.readSize = readSize;
            this.readTime = readTime;
        }

        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return  sb.append(date).append(DELIMETER).append(workLoad).append("%").append(DELIMETER)
                    .append(totalReqCnt).append(DELIMETER).append(readTime).append(DELIMETER)
                    .append(readSize).append(DELIMETER).append(readThroughtput).append(DELIMETER)
                    .append(readReqCnt).append(DELIMETER).append(writeTime).append(DELIMETER)
                    .append(writeSize).append(DELIMETER).append(writeThroughput).append(DELIMETER)
                    .append(writeReqCnt).toString();

        }

        public String getDate() {
            return date;
        }

    }

    static class MMCBlockDataComparator implements Comparator<MMCBlockData> {

        MMCDataSortType sortType;

        enum MMCDataSortType {
            WORKLOAD, WRITETIME, READTIME
        }

        public MMCBlockDataComparator(MMCDataSortType sortType) {
            this.sortType = sortType;
        }

        @Override
        public int compare(MMCBlockData data1, MMCBlockData data2) {

            int result = 0;

            switch (sortType) {
            case WORKLOAD:

                result = data2.workLoad.compareTo(data1.workLoad);
                break;

            default:
                Log.e("TAG", "can not compare the data!!!");
                // To-do
                // case WRITETIME :
                // case READTIME :
            }

            return result;
        }

    }

}
