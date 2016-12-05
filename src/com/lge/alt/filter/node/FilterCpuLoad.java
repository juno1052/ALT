package com.lge.alt.filter.node;

import com.lge.alt.data.DataManager;
import com.lge.alt.data.DataTypeMismatchException;
import com.lge.alt.data.DataManager.DataType;
import com.lge.alt.data.node.DataCpuLoad;

public class FilterCpuLoad extends NodeInfoFilter {

    private static final String TAG = "FilterCpuLoad";

    private final String[] mCommands = new String[] { "/proc/loadavg" };

    private DataCpuLoad mData;

    @Override
    public String[] getCommands() {

        return mCommands;
    }

    @Override
    public void doParse(String parserType, String info) {

        String[] strArray;

        if (mData == null) {
            mData = new DataCpuLoad();
        }

        strArray = info.split(" +");

        mData.mInfo.add(strArray[0]);
        mData.mInfo.add(strArray[1]);
        mData.mInfo.add(strArray[2]);

        try {
            DataManager.getInstance().addDataToMap(mData, DataType.NODE);

            mData = null;
        } catch (DataTypeMismatchException e) {
            e.printStackTrace();
        }
    }
}