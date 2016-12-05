package com.lge.alt.filter;

import java.util.ArrayList;

import com.lge.alt.filter.dump.FilterDumpCpuInfo;
import com.lge.alt.filter.dump.FilterDumpInfo;
import com.lge.alt.filter.dump.FilterDumpProcStats;

public class DumpFilter {

    private final ArrayList<FilterDumpInfo> mFilterList = new ArrayList<FilterDumpInfo>();

    private static DumpFilter mInstance = null;

    private DumpFilter() {

        mFilterList.add(new FilterDumpProcStats());
        mFilterList.add(new FilterDumpCpuInfo());
    }

    public static DumpFilter getInstance() {

        if (mInstance == null) {
            mInstance = new DumpFilter();
        }

        return mInstance;
    }

    public ArrayList<FilterDumpInfo> getFilters() {

        return mFilterList;
    }
}
