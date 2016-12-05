package com.lge.alt.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import com.lge.alt.ALTHelper;

import android.util.Log;

public class SystemLogData implements IData {

    private static final String TAG = "SystemData";

    private static final int PRDDATALISTBUFFER = 50;
    private static final int PSDDATALISTBUFFER = 50;
    private static final int PDDDATALISTBUFFER = 50;

    static ArrayList<ProcRestartData> procRestartDataList = new ArrayList<ProcRestartData>();
    private int prdlistIndex = 0;
    static ArrayList<ProcStartData> procStartDataList = new ArrayList<ProcStartData>();
    private int psdlistIndex = 0;
    static ArrayList<ProcDiedData> procDiedDataList = new ArrayList<ProcDiedData>();
    private int pddlistIndex = 0;

    ArrayList<ArrayList<String>> mProcRestartDataInfo = new ArrayList<>();
    ArrayList<ArrayList<String>> mProcStartDataInfo = new ArrayList<>();
    ArrayList<ArrayList<String>> mProcDiedDataInfo = new ArrayList<>();
    ArrayList<ArrayList<String>> mProcDataInfo = new ArrayList<>();

    @Override
    public void prepareHtmlDataList() {
        makeProcDataInfoOutputData();
        // makeProcRestartDataInfoOutputData();
        // makeProcStartDataInfoOutputData();
        // makeProcDiedDataInfoOutputData();
    }

    void makeProcDataInfoOutputData() {

        ArrayList<String> procInfo = new ArrayList<String>();

        String data = null;

        // Set Title
        for (int i = 0; i < ProcData.sInfo.length; i++) {

            data = ProcData.sInfo[i];

            procInfo.add(data);
        }

        mProcDataInfo.add(procInfo);

        ArrayList<ProcData> dataList = getProcDataList();

        for (ProcData pd : dataList) {
            // Set content
            procInfo = new ArrayList<String>();
            procInfo.add(ALTHelper.DateToString(pd.valutOfdate));
            procInfo.add(pd.status);
            procInfo.add(pd.packageName);
            procInfo.add(pd.message);

            mProcDataInfo.add(procInfo);
        }

    }

    void makeProcRestartDataInfoOutputData() {

        ArrayList<String> procRestartInfo = new ArrayList<String>();

        String data = null;

        // Set Title
        for (int i = 0; i < ProcRestartData.sInfo.length; i++) {

            data = ProcRestartData.sInfo[i];

            procRestartInfo.add(data);
        }

        mProcRestartDataInfo.add(procRestartInfo);

        ArrayList<ProcRestartData> dataList = getProcRestartDataList();

        for (ProcRestartData prd : dataList) {
            // Set content
            procRestartInfo = new ArrayList<String>();
            procRestartInfo.add(ALTHelper
                    .DateToString(prd.valutOfdate));
            procRestartInfo.add(prd.serviceName + " in" + prd.duration);

            mProcRestartDataInfo.add(procRestartInfo);
        }

    }

    void makeProcStartDataInfoOutputData() {

        ArrayList<String> procStartInfo = new ArrayList<String>();

        String data = null;

        // Set Title
        for (int i = 0; i < ProcStartData.sInfo.length; i++) {

            data = ProcStartData.sInfo[i];

            procStartInfo.add(data);
        }

        mProcStartDataInfo.add(procStartInfo);

        ArrayList<ProcStartData> dataList = getProcStartDataList();

        for (ProcStartData psd : dataList) {
            // Set content
            procStartInfo = new ArrayList<String>();
            procStartInfo.add(ALTHelper
                    .DateToString(psd.valutOfdate));
            procStartInfo.add(psd.start_processName);
            procStartInfo.add(psd.component);
            procStartInfo.add(psd.component_processName);

            mProcStartDataInfo.add(procStartInfo);
        }

    }

    void makeProcDiedDataInfoOutputData() {

        ArrayList<String> procDiedInfo = new ArrayList<String>();

        String data = null;

        // Set Title
        for (int i = 0; i < ProcDiedData.sInfo.length; i++) {

            data = ProcDiedData.sInfo[i];

            procDiedInfo.add(data);
        }

        mProcDiedDataInfo.add(procDiedInfo);

        ArrayList<ProcDiedData> dataList = getProcDiedDataList();

        for (ProcDiedData pdd : dataList) {
            // Set content
            procDiedInfo = new ArrayList<String>();
            procDiedInfo.add(ALTHelper
                    .DateToString(pdd.valutOfdate));
            procDiedInfo.add(pdd.processName + " (pid:" + pdd.PID + ")");

            mProcDiedDataInfo.add(procDiedInfo);
        }

    }

    public ArrayList<ArrayList<String>> getRestartProcInfo() {
        return mProcRestartDataInfo;
    }

    public ArrayList<ArrayList<String>> getStartProcInfo() {
        return mProcStartDataInfo;
    }

    public ArrayList<ArrayList<String>> getDiedProcInfo() {
        return mProcDiedDataInfo;
    }

    public ArrayList<ArrayList<String>> getProcInfo() {
        return mProcDataInfo;
    }

    @Override
    public void addDataToList(Object data) throws DataTypeMismatchException {

        if (data instanceof ProcRestartData) {
            procRestartDataList.add((ProcRestartData)data);
            prdlistIndex = (++prdlistIndex) % PRDDATALISTBUFFER;
        } else if (data instanceof ProcStartData) {
            procStartDataList.add((ProcStartData)data);
            psdlistIndex = (++psdlistIndex) % PSDDATALISTBUFFER;
        } else if (data instanceof ProcDiedData) {
            procDiedDataList.add((ProcDiedData)data);
            pddlistIndex = (++pddlistIndex) % PDDDATALISTBUFFER;
        } else {
            throw new DataTypeMismatchException(
                    "can't add data to map. due to not support data type");
        }

    }

    static ArrayList<ProcRestartData> getProcRestartDataList() {
        ArrayList<ProcRestartData> newList = new ArrayList<ProcRestartData>();
        for (ProcRestartData prd : procRestartDataList) {
            if (ALTHelper.getTimeStartedBefore(10000) != null) {
                if (prd.valutOfdate.after(ALTHelper
                        .getTimeStartedBefore(10000))) {
                    newList.add(prd);
                }
            }

        }

        Collections.sort(newList, new ProcRestartDataComparator(
                ProcRestartDataComparator.DataSortType.TIME));

        return newList;
    }

    static ArrayList<ProcStartData> getProcStartDataList() {
        ArrayList<ProcStartData> newList = new ArrayList<ProcStartData>();
        for (ProcStartData pd : procStartDataList) {
            if (ALTHelper.getTimeStartedBefore(10000) != null) {
                if (pd.valutOfdate.after(ALTHelper
                        .getTimeStartedBefore(10000))) {
                    newList.add(pd);
                }
            }
        }

        Collections.sort(newList, new ProcStartDataComparator(
                ProcStartDataComparator.DataSortType.TIME));

        return newList;
    }

    static ArrayList<ProcDiedData> getProcDiedDataList() {
        ArrayList<ProcDiedData> newList = new ArrayList<ProcDiedData>();
        for (ProcDiedData pdd : procDiedDataList) {
            if (ALTHelper.getTimeStartedBefore(10000) != null) {
                if (pdd.valutOfdate.after(ALTHelper
                        .getTimeStartedBefore(10000))) {
                    newList.add(pdd);
                }
            }
        }

        Collections.sort(newList, new ProcDiedDataComparator(
                ProcDiedDataComparator.DataSortType.TIME));

        return newList;
    }

    static ArrayList<ProcData> getProcDataList() {
        ArrayList<ProcData> newList = new ArrayList<ProcData>();

        // step1. startData
        ArrayList<ProcStartData> procStartDataList = getProcStartDataList();
        for (ProcStartData psd : procStartDataList) {
            newList.add(new ProcData(psd.valutOfdate, "START",
                    psd.start_processName, "for " + psd.component + " "
                            + psd.component_processName));
        }
        // step2. restartData
        ArrayList<ProcRestartData> procRestartDataList = getProcRestartDataList();
        for (ProcRestartData prd : procRestartDataList) {
            newList.add(new ProcData(prd.valutOfdate, "RESTART",
                    prd.serviceName,
                    "Scheduling restart of crashed service in " + prd.duration
                            + "ms"));
        }

        // step3. diedData
        ArrayList<ProcDiedData> procDiedDataList = getProcDiedDataList();
        for (ProcDiedData pdd : procDiedDataList) {
            newList.add(new ProcData(pdd.valutOfdate, "DIED", pdd.processName,
                    "pid:" + pdd.PID + " has died"));
        }

        // order by time
        Collections.sort(newList, new ProcDataComparator(
                ProcDataComparator.DataSortType.TIME));

        return newList;
    }

    @Override
    public void makeOutPutData(File file) {
        // TODO Auto-generated method stub
        prepareHtmlDataList();

        PrintWriter pw = null;

        try {
            pw = new PrintWriter(new FileOutputStream(file, true));

            pw.println("******************** systemData info ********************");
            pw.println("startLoggingTime : "
                    + ALTHelper
                            .DateToString(ALTHelper
                                    .getTimeStartedBefore(1000)));
            pw.println("endLoggingTime : "
                    + ALTHelper
                            .DateToString(ALTHelper
                                    .getTimeLaunched()));

            pw.println("******************** ProcRestart info ********************");
            pw.println(String.format("%-20s", "time")
                    + String.format("%-10s", "duration")
                    + String.format("%-70s", "restart serviceName"));
            for (ProcRestartData prd : getProcRestartDataList()) {
                pw.println(String.format("%-20s",
                        ALTHelper.DateToString(prd.valutOfdate))
                        + String.format("%-10s", prd.duration)
                        + String.format("%-70s", prd.serviceName));
            }
            pw.println("");

            pw.println("******************** ProcStart info ********************");

            pw.println(String.format("ProcStart count : %5s",
                    getProcStartDataList().size() + " times"));

            pw.println(String.format("%-20s", "time")
                    + String.format("%-40s", "start_processName")
                    + String.format("%-20s", "component")
                    + String.format("%-70s", "component_processName"));
            for (ProcStartData psd : getProcStartDataList()) {
                pw.println(String.format("%-20s",
                        ALTHelper.DateToString(psd.valutOfdate))
                        + String.format("%-40s", psd.start_processName)
                        + String.format("%-20s", psd.component)
                        + String.format("%-70s", psd.component_processName));
            }
            pw.println("");

            pw.println("******************** ProcDied info ********************");
            pw.println(String.format("%-20s", "time")
                    + String.format("%-40s", "processName")
                    + String.format("%-10s", "pid"));
            for (ProcDiedData pdd : getProcDiedDataList()) {
                pw.println(String.format("%-20s",
                        ALTHelper.DateToString(pdd.valutOfdate))
                        + String.format("%-40s", pdd.processName)
                        + String.format("%-10s", pdd.PID));
            }
            pw.println("");

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

        // procRestartDataList.clear();
        // procStartDataList.clear();
        // procDiedDataList.clear();

        mProcRestartDataInfo.clear();
        mProcStartDataInfo.clear();
        mProcDiedDataInfo.clear();
        mProcDataInfo.clear();
    }

    public static class ProcRestartData {

        public static final String[] sInfo = { "time", "restart service info" };

        public Date valutOfdate;
        public String serviceName;
        public String duration;

        public boolean isSet() {
            if (serviceName != null && duration != null)
                return true;
            else
                return false;
        }

        public ProcRestartData() {
            // TODO Auto-generated constructor stub

        }

        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return serviceName + " " + duration;
        }

    }

    public static class ProcStartData {

        // Start proc com.android.contacts for activity
        // com.android.contacts/.activities.PeopleActivity: pid=17362 uid=10016
        // gids={50016, 9997, 3003, 1028, 1015, 1023, 4002} abi=armeabi-v7a

        public static final String[] sInfo = { "time", "packageName",
                "component", "className" };

        public Date valutOfdate;
        public String PID;

        public String start_processName;

        public String component;
        public String component_processName;

        // pid, uid, gids

        public boolean isSet() {
            if (start_processName != null && component != null
                    && component_processName != null)
                return true;
            else
                return false;
        }

        public ProcStartData() {
            // TODO Auto-generated constructor stub
        }

        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return start_processName + " " + component + " "
                    + component_processName;
        }

    }

    public static class ProcDiedData {

        // 06-29 17:20:38.865 1014 1935 I ActivityManager: Process
        // com.dailymotion.dailymotion (pid 20942) has died

        public static final String[] sInfo = { "time", "processName" };

        public Date valutOfdate;

        public String PID;
        public String processName;

        // pid, uid, gids

        public boolean isSet() {
            if (processName != null && PID != null)
                return true;
            else
                return false;
        }

        public ProcDiedData() {
            // TODO Auto-generated constructor stub
        }

        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return processName + " " + PID;
        }

    }

    public static class ProcData {

        public static final String[] sInfo = { "time", "status", "packageName",
                "message" };

        public Date valutOfdate;

        public String status;
        public String packageName;
        public String message;

        public boolean isSet() {
            if (valutOfdate != null)
                return true;
            else
                return false;
        }

        public ProcData(Date date, String status, String packageName,
                String message) {
            // TODO Auto-generated constructor stub
            valutOfdate = date;
            this.status = status;
            this.packageName = packageName;
            this.message = message;
        }

        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return packageName + " " + message;
        }

    }

    static class ProcRestartDataComparator implements
            Comparator<ProcRestartData> {

        DataSortType sortType;

        enum DataSortType {
            TIME
        }

        public ProcRestartDataComparator(DataSortType sortType) {
            this.sortType = sortType;
        }

        @Override
        public int compare(ProcRestartData data1, ProcRestartData data2) {

            int result = 0;

            switch (sortType) {
            case TIME:
                if (data2.valutOfdate.after(data1.valutOfdate)) {
                    result = -1;
                } else {
                    result = 1;
                }

                break;

            default:
                Log.e("yongho", "can not compare the data!!!");
                // To-do
            }

            return result;
        }

    }

    static class ProcStartDataComparator implements Comparator<ProcStartData> {

        DataSortType sortType;

        enum DataSortType {
            TIME
        }

        public ProcStartDataComparator(DataSortType sortType) {
            this.sortType = sortType;
        }

        @Override
        public int compare(ProcStartData data1, ProcStartData data2) {

            int result = 0;

            switch (sortType) {
            case TIME:
                if (data2.valutOfdate.after(data1.valutOfdate)) {
                    result = -1;
                } else {
                    result = 1;
                }

                break;

            default:
                Log.e("yongho", "can not compare the data!!!");
                // To-do
            }

            return result;
        }

    }

    static class ProcDiedDataComparator implements Comparator<ProcDiedData> {

        DataSortType sortType;

        enum DataSortType {
            TIME
        }

        public ProcDiedDataComparator(DataSortType sortType) {
            this.sortType = sortType;
        }

        @Override
        public int compare(ProcDiedData data1, ProcDiedData data2) {

            int result = 0;

            switch (sortType) {
            case TIME:
                if (data2.valutOfdate.after(data1.valutOfdate)) {
                    result = -1;
                } else {
                    result = 1;
                }

                break;

            default:
                Log.e("yongho", "can not compare the data!!!");
                // To-do
            }

            return result;
        }

    }

    static class ProcDataComparator implements Comparator<ProcData> {

        DataSortType sortType;

        enum DataSortType {
            TIME
        }

        public ProcDataComparator(DataSortType sortType) {
            this.sortType = sortType;
        }

        @Override
        public int compare(ProcData data1, ProcData data2) {

            int result = 0;

            switch (sortType) {
            case TIME:
                if (data2.valutOfdate.after(data1.valutOfdate)) {
                    result = -1;
                } else {
                    result = 1;
                }

                break;

            default:
                Log.e("yongho", "can not compare the data!!!");
                // To-do
            }

            return result;
        }

    }

}
