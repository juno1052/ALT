package com.lge.alt.filter;

import java.util.ArrayList;
import java.util.HashMap;

import com.lge.alt.data.DataManager;
import com.lge.alt.data.DataTypeMismatchException;
import com.lge.alt.data.DataManager.DataType;
import com.lge.alt.data.node.DataMemInfo;
import com.lge.alt.data.node.NodeInfoData;
import com.lge.alt.filter.node.FilterBattInfo;
import com.lge.alt.filter.node.FilterCpuInfo;
import com.lge.alt.filter.node.FilterCpuLoad;
import com.lge.alt.filter.node.FilterCpuUsage;
import com.lge.alt.filter.node.FilterFragInfo;
import com.lge.alt.filter.node.FilterGpuInfo;
import com.lge.alt.filter.node.FilterMemInfo;
import com.lge.alt.filter.node.FilterPageInfo;
import com.lge.alt.filter.node.FilterZoneInfo;
import com.lge.alt.filter.node.NodeInfoFilter;

public class NodeFilter {

    private static final String TAG = "NodeFilter";

    private ArrayList<NodeInfoFilter> mFilterList = new ArrayList<NodeInfoFilter>();

    private static NodeFilter mInstance = null;

    public NodeFilter() {

        mFilterList.add(new FilterMemInfo());
        mFilterList.add(new FilterZoneInfo());
        mFilterList.add(new FilterPageInfo());
        mFilterList.add(new FilterFragInfo());
        mFilterList.add(new FilterCpuInfo());
        mFilterList.add(new FilterGpuInfo());
        mFilterList.add(new FilterCpuUsage());
        mFilterList.add(new FilterBattInfo());
        mFilterList.add(new FilterCpuLoad());
    }

    public static NodeFilter getInstance() {

        if (mInstance == null) {
            mInstance = new NodeFilter();
        }

        return mInstance;
    }

    public ArrayList<NodeInfoFilter> getFilters() {

        return mFilterList;
    }
}
