package com.lge.alt.filter.node;

import com.lge.alt.ALTHelper;
import com.lge.alt.data.DataManager;
import com.lge.alt.data.DataTypeMismatchException;
import com.lge.alt.data.DataManager.DataType;
import com.lge.alt.data.node.DataZoneInfoHigh;
import com.lge.alt.data.node.DataZoneInfoNormal;


public class FilterZoneInfo extends NodeInfoFilter {

    private static final String TAG = "FilterZoneInfo";

    private final String[] mCommands = new String[] { "/proc/zoneinfo" };

    private final String[] mFilter = { "Normal", "HighMem" };

    private final int MAX_PARSE_LINE = 4;

    private boolean mParse = false;
    private int mFilterIndex = 0;
    private int mParseIndex = 0;

    private DataZoneInfoNormal mDataNormal;
    private DataZoneInfoHigh mDataHigh;

    @Override
    public String[] getCommands() {

        return mCommands;
    }

    @Override
    public void doFilter(String readline) {

        if (readline.contains(mFilter[mFilterIndex]) && !mParse) {
            mParse = true;
            return;
        }

        if (mParse) {
            parseLineAndSaveData(null, readline);
        }
    }

    @Override
    public void doParse(String parserType, String info) {

        String[] strArray;

        if (mDataNormal == null && mDataHigh == null) {

            String curTime = ALTHelper
                    .DateToString(ALTHelper.getCurDateExceptYear());

            mDataNormal = new DataZoneInfoNormal();
            mDataHigh = new DataZoneInfoHigh();
            mDataNormal.mInfo.add(curTime);
            mDataHigh.mInfo.add(curTime);
        }

        strArray = info.split(" +");

        if ("Normal".equals(mFilter[mFilterIndex])) {
            int size = Integer.parseInt(strArray[strArray.length - 1]) * 4;
            mDataNormal.mInfo.add(Integer.toString(size));
            mParseIndex++;
        }

        if ("HighMem".equals(mFilter[mFilterIndex])) {
            int size = Integer.parseInt(strArray[strArray.length - 1]) * 4;
            mDataHigh.mInfo.add(Integer.toString(size));
            mParseIndex++;
        }

        if (mParseIndex == MAX_PARSE_LINE) {
            mFilterIndex++;
            mParseIndex = 0;
            mParse = false;
        }

        if (mFilterIndex == mFilter.length) {
            try {
                DataManager.getInstance().addDataToMap(mDataNormal,
                        DataType.NODE);
                DataManager.getInstance()
                        .addDataToMap(mDataHigh, DataType.NODE);

                mDataNormal = null;
                mDataHigh = null;

                mFilterIndex = 0;
                mParseIndex = 0;
                mParse = false;

            } catch (DataTypeMismatchException e) {
                e.printStackTrace();
            }
        }
    }
}
