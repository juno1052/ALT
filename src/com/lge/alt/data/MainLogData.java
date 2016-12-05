package com.lge.alt.data;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.StringTokenizer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.lge.alt.ALTHelper;

import android.util.Log;

public class MainLogData implements IData {

    private static final String TAG = "MainData";

    private static final int GCDATALISTBUFFER = 100;

    static ArrayList<GCData> GCDataList = new ArrayList<GCData>();
    private int gclistIndex = 0;

    ArrayList<ArrayList<String>> mGCDataInfo = new ArrayList<>();
    ArrayList<ArrayList<String>> mLaunchDataInfo = new ArrayList<>();

    @Override
    public void prepareHtmlDataList() {
        // TODO Auto-generated method stub
        makeGCDataInfoOutputData();
        makeLaunchDataInfoOutputData();
    }

    void makeGCDataInfoOutputData() {

        ArrayList<String> GCInfo = new ArrayList<String>();

        String data = null;

        // Set Title
        for (int i = 0; i < GCData.sInfo.length; i++) {

            data = GCData.sInfo[i];

            GCInfo.add(data);
        }

        mGCDataInfo.add(GCInfo);

        ArrayList<GCData> dataList = getGCDataList();

        for (GCData gc : dataList) {
            // Set content
            GCInfo = new ArrayList<String>();

            GCInfo.add(ALTHelper.DateToString(gc.valutOfdate));
            GCInfo.add(gc.ProcessName + "(" + gc.PID + ")");
            GCInfo.add(gc.GC_cause);
            GCInfo.add(gc.freedObject);
            GCInfo.add(gc.freedByte);
            GCInfo.add(gc.freedLObject);
            GCInfo.add(gc.freedLByte);
            GCInfo.add(gc.percent_free);
            GCInfo.add(gc.current_heap_size);
            GCInfo.add(gc.total_memory);
            GCInfo.add(gc.pause_time);
            GCInfo.add(gc.Total_time);

            mGCDataInfo.add(GCInfo);
        }

    }

    void makeLaunchDataInfoOutputData() {

        String data = null;

        String[] sInfo = { "Sluggish App", "touched time", "started Time",
                "Launched Time", "Displayed Time", "Spec Time" };

        // Set Title
        for (int i = 0; i < sInfo.length; i++) {

            data = sInfo[i];

            ArrayList<String> LaunchInfo = new ArrayList<String>();
            LaunchInfo.add(data);

            // add data
            switch (data) {
            case "Sluggish App":
                LaunchInfo.add(ALTHelper.getLaunchedPackage());
                break;
            case "touched time":
                LaunchInfo
                        .add(ALTHelper
                                .DateToString(ALTHelper
                                        .getTimeActionUp()));
                break;
            case "started Time":
                LaunchInfo.add(ALTHelper
                        .DateToString(ALTHelper.getTimeStarted()));
                break;
            case "Launched Time":
                LaunchInfo
                        .add(ALTHelper
                                .DateToString(ALTHelper
                                        .getTimeLaunched()));
                break;
            case "Displayed Time":
                LaunchInfo.add(ALTHelper.getTimeDisplayed());
                break;
            case "Spec Time":
                LaunchInfo.add(ALTHelper.getTimeSpec());
                break;
            }

            mLaunchDataInfo.add(LaunchInfo);
        }

    }

    public ArrayList<ArrayList<String>> getGCInfo() {
        return mGCDataInfo;
    }

    public ArrayList<ArrayList<String>> getLaunchInfo() {
        return mLaunchDataInfo;
    }

    @Override
    public void addDataToList(Object data) throws DataTypeMismatchException {

        if (data instanceof GCData) {
            GCDataList.add(gclistIndex, (GCData)data);
            gclistIndex = (++gclistIndex) % GCDATALISTBUFFER;
        } else {
            throw new DataTypeMismatchException(
                    "can't add data to map. due to not support data type");
        }
    }

    static ArrayList<GCData> getGCDataList() {

        ArrayList<GCData> newList = new ArrayList<GCData>();
        for (GCData gc_d : GCDataList) {
            if (ALTHelper.getTimeStartedBefore(10000) != null) {
                if (gc_d.valutOfdate.after(ALTHelper
                        .getTimeStartedBefore(10000))) {
                    newList.add(gc_d);
                }
            }

        }

        Collections.sort(newList, new GCDataComparator(
                GCDataComparator.DataSortType.TIME));

        return newList;
    }

    @Override
    public void makeOutPutData(File file) {
        // TODO Auto-generated method stub

        prepareHtmlDataList();

        PrintWriter pw = null;
        int cnt = 0;

        try {
            pw = new PrintWriter(new FileOutputStream(file, true));

            pw.println("******************** MainData info ********************");
            pw.println("startLoggingTime : "
                    + ALTHelper
                            .DateToString(ALTHelper
                                    .getTimeStartedBefore(1000)));
            pw.println("endLoggingTime : "
                    + ALTHelper
                            .DateToString(ALTHelper
                                    .getTimeLaunched()));

            pw.println("");

            pw.println("******************** Launched info ********************");

            // should implement

            pw.println("");

            pw.println("******************** gc info (top5) sorted by total_time ********************");

            // -----------------------

            pw.println(String.format("%-20s", "time")
                    + String.format("%-10s", "PID")
                    + String.format("%-15s", "GC_cause")
                    + String.format("%-12s", "freedObject")
                    + String.format("%-10s", "freedByte")
                    + String.format("%-13s", "freedLObject")
                    + String.format("%-11s", "freedLByte")
                    + String.format("%-13s", "percent_free")
                    + String.format("%-18s", "current_heap_size")
                    + String.format("%-13s", "total_memory")
                    + String.format("%-11s", "pause_time")
                    + String.format("%-11s", "Total_time"));

            ArrayList<GCData> totalTimeList = (ArrayList<GCData>)getGCDataList()
                    .clone();
            Collections.sort(totalTimeList, new GCDataComparator(
                    GCDataComparator.DataSortType.TOTAL_TIME));

            for (GCData gc : totalTimeList) {

                if (cnt >= 5)
                    break;

                pw.println(String.format("%-20s",
                        ALTHelper.DateToString(gc.valutOfdate))
                        + String.format("%-10s", gc.PID)
                        + String.format("%-15s", gc.GC_cause)
                        + String.format("%-12s", gc.freedObject)
                        + String.format("%-10s", gc.freedByte)
                        + String.format("%-13s", gc.freedLObject)
                        + String.format("%-11s", gc.freedLByte)
                        + String.format("%-13s", gc.percent_free)
                        + String.format("%-18s", gc.current_heap_size)
                        + String.format("%-13s", gc.total_memory)
                        + String.format("%-11s", gc.pause_time)
                        + String.format("%-11s", gc.Total_time));
                cnt++;
            }

            pw.println("");

            // -----------------------

            pw.println("******************** gc info sorted by date ********************");

            pw.println(String.format("GC Total count : %5s", getGCDataList()
                    .size() + " times"));

            pw.println(String.format("%-20s", "time")
                    + String.format("%-10s", "processName")
                    + String.format("%-15s", "GC_cause")
                    + String.format("%-12s", "freedObject")
                    + String.format("%-10s", "freedByte")
                    + String.format("%-13s", "freedLObject")
                    + String.format("%-11s", "freedLByte")
                    + String.format("%-13s", "percent_free")
                    + String.format("%-18s", "current_heap_size")
                    + String.format("%-13s", "total_memory")
                    + String.format("%-11s", "pause_time")
                    + String.format("%-11s", "Total_time"));

            for (GCData gc : getGCDataList()) {
                pw.println(String.format("%-20s",
                        ALTHelper.DateToString(gc.valutOfdate))
                        + String.format("%-10s", gc.PID)
                        + String.format("%-15s", gc.GC_cause)
                        + String.format("%-12s", gc.freedObject)
                        + String.format("%-10s", gc.freedByte)
                        + String.format("%-13s", gc.freedLObject)
                        + String.format("%-11s", gc.freedLByte)
                        + String.format("%-13s", gc.percent_free)
                        + String.format("%-18s", gc.current_heap_size)
                        + String.format("%-13s", gc.total_memory)
                        + String.format("%-11s", gc.pause_time)
                        + String.format("%-11s", gc.Total_time));
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
        // GCDataList.clear();

        mGCDataInfo.clear();
        mLaunchDataInfo.clear();
    }

    public static class TriggeredData {

        public Date valutOfdate;
        public String packageName;
        public String className;
        public int launchingTime = -1;

        public boolean isSet() {
            if (packageName != null && launchingTime != -1)
                return true;
            else
                return false;
        }

        public TriggeredData() {
            // TODO Auto-generated constructor stub
        }

        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return packageName + " " + launchingTime;
        }

    }

    public static class GCData {

        // Explicit concurrent mark sweep GC freed 706(30KB) AllocSpace objects,
        // 0(0B) LOS objects, 53% free, 6MB/14MB, paused 371us total 20.851ms

        public static final String[] sInfo = { "time", "PID", "GC_cause",
                "freedObject", "freedByte", "freedLObject", "freedLByte",
                "percent_free", "current_heap_size", "total_memory",
                "pause_time", "Total_time" };

        public Date valutOfdate;
        public String PID;
        public String ProcessName;

        public String GC_cause; // Explicit, Background sticky
        // public String GC_type; //concurrent mark sweep GC freed

        public String freedObject; // 706
        public String freedByte; // 30KB

        public String freedLObject; // 0
        public String freedLByte; // 0B

        public String percent_free; // 53%

        public String current_heap_size; // 6MB
        public String total_memory; // 14MB

        public String pause_time; // 371us
        public String Total_time; // 20.851ms

        public boolean isSet() {
            if (GC_cause != null)
                return true;
            else
                return false;
        }

        public GCData() {
            // TODO Auto-generated constructor stub
        }

        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return GC_cause + " " + freedObject + " " + freedByte + " "
                    + freedLObject + " " + freedLByte + " " + percent_free
                    + " " + current_heap_size + " " + total_memory + " "
                    + pause_time + " " + Total_time;
        }

    }

    static class GCDataComparator implements Comparator<GCData> {

        DataSortType sortType;

        enum DataSortType {
            TIME, TOTAL_TIME
        }

        public GCDataComparator(DataSortType sortType) {
            this.sortType = sortType;
        }

        @Override
        public int compare(GCData data1, GCData data2) {

            int result = 0;

            switch (sortType) {
            case TOTAL_TIME:

                StringTokenizer st = new StringTokenizer(data2.Total_time, "ms");
                Double data2_double = Double.parseDouble(st.nextToken());
                st = new StringTokenizer(data1.Total_time, "ms");
                Double data1_double = Double.parseDouble(st.nextToken());

                result = data2_double.compareTo(data1_double);

                break;

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
