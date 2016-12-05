package com.lge.alt.filter.node;

import android.util.Log;

import com.lge.alt.data.DataManager;
import com.lge.alt.data.DataTypeMismatchException;
import com.lge.alt.data.DataManager.DataType;
import com.lge.alt.data.node.DataCpuUsage;

public class FilterCpuUsage extends NodeInfoFilter {

    private static final String TAG = "FilterCpuUsage";

    private final String[] mCommands = new String[] { "/proc/stat" };

    private final String[] mFilter = { "cpu" };

    private final int MAX_PARSE_LINE = 5;

    private int mFilterIndex = 0;

    private DataCpuUsage mData;

    @Override
    public String[] getCommands() {

        return mCommands;
    }

    @Override
    public void doFilter(String readline) {

        if (readline.contains(mFilter[0])) {
            parseLineAndSaveData(null, readline);
        }
    }

    @Override
    public void doParse(String parserType, String info) {

        String[] strArray;

        if (mData == null) {
            mData = new DataCpuUsage();
        }

        strArray = info.split(" +");

        StringBuffer sb = new StringBuffer();

        if (mFilterIndex == 0) {
            sb.append(String.format("%-10s", mFilter[0]));
        } else {
            sb.append(String.format("%-10s", mFilter[0] + (mFilterIndex - 1)));
        }

        for (int idx = 1; idx < strArray.length - 3; idx++) {

            sb.append(String.format("%-10s", strArray[idx]));
        }

        mData.mInfo.add(sb.toString());

        if (++mFilterIndex == MAX_PARSE_LINE) {
            try {
                DataManager.getInstance().addDataToMap(mData, DataType.NODE);

                mData = null;
                mFilterIndex = 0;
            } catch (DataTypeMismatchException e) {
                e.printStackTrace();
            }
        }
    }
}