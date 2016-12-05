package com.lge.alt.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.lge.alt.data.dump.DumpDataCpuInfo;
import com.lge.alt.data.dump.DumpDataInfo;
import com.lge.alt.data.dump.IDumpInfo;
import com.lge.alt.data.dump.DumpDataProcStats;

public class DumpData implements IData, IDumpInfo {

    private static final String TAG = "DumpData";

    private static final LinkedHashMap<Integer, DumpDataInfo> mDataMap = new LinkedHashMap<Integer, DumpDataInfo>();

    public DumpData() {

        mDataMap.put(DUMP_PROCSTATS, new DumpDataProcStats());
        mDataMap.put(DUMP_CPUINFO, new DumpDataCpuInfo());

    }

    @Override
    public void clearData() {

        Iterator<Integer> it = mDataMap.keySet().iterator();

        while (it.hasNext()) {
            mDataMap.get(it.next()).clearData();
        }
    }

    @Override
    public void prepareHtmlDataList() {

        Iterator<Integer> it = mDataMap.keySet().iterator();

        while (it.hasNext()) {
            mDataMap.get(it.next()).prepareHtmlDataList();
        }
    }

    @Override
    public void addDataToList(Object data) throws DataTypeMismatchException {

        DumpDataInfo info = null;

        if (data instanceof DumpDataProcStats) {

           info = mDataMap.get(DUMP_PROCSTATS);

        } else if (data instanceof DumpDataCpuInfo) {

            info = mDataMap.get(DUMP_CPUINFO);

        } else {

            throw new DataTypeMismatchException(
                    "can't add data to list. due to not support data type");
        }

        info.getDataList().add((DumpDataInfo)data);

    }

    @Override
    public void makeOutPutData(File file) {

        prepareHtmlDataList();

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileOutputStream(file, true));

            Iterator<Integer> it = mDataMap.keySet().iterator();

            while (it.hasNext()) {
                mDataMap.get(it.next()).makeOutPut(pw);
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    public DumpDataInfo getDataObj(int info) {

        return mDataMap.get(info);
    }

}
