package com.lge.alt.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.lge.alt.ALTService;
import com.lge.alt.data.node.DataBattInfo;
import com.lge.alt.data.node.DataBlockInfo;
import com.lge.alt.data.node.DataCpuInfo;
import com.lge.alt.data.node.DataCpuLoad;
import com.lge.alt.data.node.DataCpuUsage;
import com.lge.alt.data.node.DataFragInfo;
import com.lge.alt.data.node.DataGpuInfo;
import com.lge.alt.data.node.DataMemInfo;
import com.lge.alt.data.node.DataPageInfo;
import com.lge.alt.data.node.DataZoneInfoHigh;
import com.lge.alt.data.node.DataZoneInfoNormal;
import com.lge.alt.data.node.INodeInfo;
import com.lge.alt.data.node.NodeInfoData;

import android.util.Log;

public class NodeData implements IData, INodeInfo {

    private static final String TAG = "NodeData";
    private static final int BUFFER_TIME = 30*1000;

    private LinkedHashMap<Integer, NodeInfoData> mDataMap = new LinkedHashMap<Integer, NodeInfoData>();

    public NodeData() {

        mDataMap.put(MEMINFO, new DataMemInfo());
        mDataMap.put(ZONEINFO_NORMAL, new DataZoneInfoNormal());
        mDataMap.put(ZONEINFO_HIGH, new DataZoneInfoHigh());
        mDataMap.put(PAGEINFO, new DataPageInfo());
        mDataMap.put(BLOCKINFO, new DataBlockInfo());
        mDataMap.put(FRAGINFO, new DataFragInfo());
        mDataMap.put(CPUINFO, new DataCpuInfo());
        mDataMap.put(GPUINFO, new DataGpuInfo());
        mDataMap.put(CPUUSAGE, new DataCpuUsage());
        mDataMap.put(BATTINFO, new DataBattInfo());
        mDataMap.put(CPULOAD, new DataCpuLoad());
    }

    private int getBufferCnt() {

        // max logging period : 30s
        return BUFFER_TIME / ALTService.NODE_PERIOD;
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
            mDataMap.get(it.next()).prepareDataList();
        }
    }

    @Override
    public void addDataToList(Object data) throws DataTypeMismatchException {

        NodeInfoData info = null;

        if (data instanceof DataMemInfo) {
            info = mDataMap.get(MEMINFO);
        } else if (data instanceof DataZoneInfoNormal) {
            info = mDataMap.get(ZONEINFO_NORMAL);
        } else if (data instanceof DataZoneInfoHigh) {
            info = mDataMap.get(ZONEINFO_HIGH);
        } else if (data instanceof DataPageInfo) {
            info = mDataMap.get(PAGEINFO);
        } else if (data instanceof DataBlockInfo) {
            info = mDataMap.get(BLOCKINFO);
        } else if (data instanceof DataFragInfo) {
            info = mDataMap.get(FRAGINFO);
        } else if (data instanceof DataCpuInfo) {
            info = mDataMap.get(CPUINFO);
        } else if (data instanceof DataGpuInfo) {
            info = mDataMap.get(GPUINFO);
        } else if (data instanceof DataCpuUsage) {
            info = mDataMap.get(CPUUSAGE);
        } else if (data instanceof DataBattInfo) {
            info = mDataMap.get(BATTINFO);
        } else if (data instanceof DataCpuLoad) {
            info = mDataMap.get(CPULOAD);
        } else {
            throw new DataTypeMismatchException(
                    "can't add data to list. due to not support data type");
        }

        info.getDataRaw().add((NodeInfoData)data);

        if (info.getDataRaw().size() > getBufferCnt()) {
            info.getDataRaw().remove(0);
        }
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

    public NodeInfoData getDataObj(int info) {

        return mDataMap.get(info);
    }
}