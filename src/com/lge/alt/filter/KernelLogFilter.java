package com.lge.alt.filter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import android.util.Log;

import com.lge.alt.ALTHelper;
import com.lge.alt.data.DataManager;
import com.lge.alt.data.DataTypeMismatchException;
import com.lge.alt.data.KernelLogData;
import com.lge.alt.data.DataManager.DataType;
import com.lge.alt.data.KernelLogData.MMCBlockData;

public class KernelLogFilter implements IFilter {

    private static final String TAG = "KernelData";
    private final String[] mCommands = new String[] { "sh", "-c", "dmesg" };
    private final KernelParser mParser = new KernelParser();

    private static KernelLogFilter mInstance = null;
    private static HashMap<String, String> sKernelFilterMap = new HashMap<String, String>();

    public static final String[] sKernelLogFilter = {
            "lowmemorykiller: Killing", "mmc0:mmc_blk_issue_rw_rq" };

    public static final String[] sKernelParseMethod = { "parseLMKInfo",
            "parseMMCBlockInfo", };

    static {
        for (int i = 0; i < sKernelLogFilter.length; i++) {
            sKernelFilterMap.put(sKernelLogFilter[i], sKernelParseMethod[i]);
        }
    }

    public static KernelLogFilter getInstance() {

        if (mInstance == null) {
            mInstance = new KernelLogFilter();
        }

        return mInstance;
    }

    @Override
    public String[] getCommands() {
        return mCommands;
    }

    @Override
    public void doFilter(String readline) {

        Date triggeredDate = ALTHelper.getTimeStartedBefore(10000);
        Iterator<String> it = sKernelFilterMap.keySet().iterator();

        if (readline == null)
            return;

        while (it.hasNext()) {
            String key = it.next();

            if (readline.contains(key)) {

                Date date = ALTHelper
                        .stringToDate(getDateFromKernelLog(readline));

                if (date == null) {
                    Log.e(TAG, "KernelFilter :: doFilter -> date is null");
                } else if (triggeredDate == null) {
                    Log.e(TAG,
                            "KernelFilter :: doFilter -> triggeredDate is null");
                } else if (date.after(triggeredDate)) {
                    parseLineAndSaveData(key, readline);
                }
            }
        }
    }

    @Override
    public void parseLineAndSaveData(String key, String readline) {

        String parserType = sKernelFilterMap.get(key);
        mParser.doParse(parserType, readline);
    }

    private static String getDateFromKernelLog(String info) {
        String date = "";
        String arr[] = null;

        arr = info.split("[0-9] +/");
        if (arr.length > 1)
            date = arr[1].split("]")[0].trim();

        return date;
    }

    private class KernelParser implements IParser {

        @Override
        public void doParse(String parserType, String info) {
            if (parserType.equals("parseLMKInfo"))
                parseLMKInfo(info);
            else if (parserType.equals("parseMMCBlockInfo")) {
                parseMMCBlockInfo(info);
            }
        }

        private void parseLMKInfo(String info) {
            // TODO need to implement the parsing code
            // parsing data
            // <6>[ 143.562367 / 05-13 19:18:43.268] lowmemorykiller: Killing
            // 'id.gms.wearable' (6453), adj 1000, Free mem 20572kB
            String date = "";
            String processName = "";
            String pid = "";
            int adjScore = 0;
            String freeMem = "";

            String[] arr = null;

            date = getDateFromKernelLog(info);

            arr = info.split("'");
            if (arr.length > 1)
                processName = arr[1].trim();

            arr = info.split("\\(|\\)");
            if (arr.length > 1)
                pid = arr[1].trim();

            arr = info.split("adj ");
            if (arr.length > 1)
                adjScore = Integer.parseInt(arr[1].split(",")[0].trim());

            arr = info.split("Free mem ");
            if (arr.length > 1)
                freeMem = arr[1].trim();

            // save LMKData
            try {
                DataManager.getInstance().addDataToMap(
                        new KernelLogData.LMKData(date, processName, pid,
                                adjScore, freeMem), DataType.KERNEL);
            } catch (DataTypeMismatchException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        private void parseMMCBlockInfo(String info) {
            // TODO
            // parsing data
            // <4>[ 5.155433 / 01-02 18:18:02.359] mmc0:mmc_blk_issue_rw_rq:
            // mmcqd:200 Workload=26%, duty 133159631, period 505080100,
            // req_cnt=297
            // <4>[ 5.155452 / 01-02 18:18:02.359] mmc0:mmc_blk_issue_rw_rq:
            // mmcqd:200 Write Throughput=4021 kB/s, req_cnd: 33, size: 164864
            // bytes, time:41 ms
            // <4>[ 5.155465 / 01-02 18:18:02.359] mmc0:mmc_blk_issue_rw_rq:
            // mmcqd:200 Read Throughput=74161 kB/s, req_cnd: 264, size: 6748672
            // bytes, time:91 ms

            KernelLogData.MMCBlockData mmcBlockData = null;
            String date = "";
            String workLoad = "";
            String totalReqCnt = "";

            if (info.contains("Workload=")) {
                date = getDateFromKernelLog(info);

                if (info.split("Workload=").length > 1)
                    workLoad = info.split("Workload=")[1].split("%")[0].trim();

                if (info.split("req_cnt=").length > 1)
                    totalReqCnt = info.split("req_cnt=")[1].trim();

                // save MMCBlockData
                try {
                    DataManager.getInstance().addDataToMap(
                            new KernelLogData.MMCBlockData(date,
                                    Integer.valueOf(workLoad), totalReqCnt),
                            DataType.KERNEL);
                } catch (DataTypeMismatchException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else if (info.contains("Write Throughput=")) {
                parseMMCBlockWriteInfo(info);
            } else if (info.contains("Read Throughput=")) {
                parseMMCBlockReadInfo(info);
            }

        }

        private void parseMMCBlockWriteInfo(String info) {

            String writeThroughput = "";
            String writeReqCnt = "";
            String writeSize = "";
            String writeTime = "";
            String date = "";
            ArrayList<MMCBlockData> kernelDataList = null;

            date = getDateFromKernelLog(info);
            if (info.split("Write Throughput=").length > 1)
                writeThroughput = info.split("Write Throughput=")[1].split(",")[0]
                        .trim();

            if (info.split("req_cnd: ").length > 1)
                writeReqCnt = info.split("req_cnd: ")[1].split(",")[0].trim();

            if (info.split("size: ").length > 1)
                writeSize = info.split("size: ")[1].split(",")[0].trim();

            if (info.split("time:").length > 1)
                writeTime = info.split("time:")[1].trim();

            kernelDataList = KernelLogData.getMMCBlockDataList();

            if (kernelDataList != null) {
                for (KernelLogData.MMCBlockData mmcBlockData : kernelDataList) {

                    if (mmcBlockData.getDate().equals(date)) {
                        mmcBlockData.setWriteData(writeThroughput, writeReqCnt,
                                writeSize, writeTime);
                        break;
                    }
                }
            }
        }

        private void parseMMCBlockReadInfo(String info) {

            String readThroughtput = "";
            String readReqCnt = "";
            String readSize = "";
            String readTime = "";
            String date = "";
            ArrayList<MMCBlockData> kernelDataList = null;

            date = getDateFromKernelLog(info);

            if (info.split("Read Throughput=").length > 1)
                readThroughtput = info.split("Read Throughput=")[1].split(",")[0]
                        .trim();

            if (info.split("req_cnd: ").length > 1)
                readReqCnt = info.split("req_cnd: ")[1].split(",")[0].trim();

            if (info.split("size: ").length > 1)
                readSize = info.split("size: ")[1].split(",")[0].trim();

            if (info.split("time:").length > 1)
                readTime = info.split("time:")[1].trim();

            kernelDataList = KernelLogData.getMMCBlockDataList();

            if (kernelDataList != null) {
                for (KernelLogData.MMCBlockData mmcBlockData : kernelDataList) {

                    if (mmcBlockData.getDate().equals(date)) {
                        mmcBlockData.setReadData(readThroughtput, readReqCnt,
                                readSize, readTime);
                        break;
                    }
                }
            }
        }
    }
}
