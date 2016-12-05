package com.lge.alt.filter.node;

import com.lge.alt.ALTHelper;
import com.lge.alt.data.DataManager;
import com.lge.alt.data.DataTypeMismatchException;
import com.lge.alt.data.DataManager.DataType;
import com.lge.alt.data.node.DataMemInfo;

public class FilterMemInfo extends NodeInfoFilter {

    private static final String TAG = "FilterMemInfo";

    private final String[] mCommands = new String[] { "/proc/meminfo" };

    private final String[] mFilter = { "MemTotal", "MemFree", "Cached",
            "SwapTotal", "SwapFree", "Slab" };

    private int mFilterIndex = 0;

    private DataMemInfo mData;

    @Override
    public String[] getCommands() {

        return mCommands;
    }

    @Override
    public void doFilter(String readline) {

        if (readline.contains(mFilter[mFilterIndex])) {
            parseLineAndSaveData(null, readline);
        }
    }

    @Override
    public void doParse(String parserType, String info) {

        String[] strArray;

        if (mData == null) {

            mData = new DataMemInfo();
            mData.mInfo
                    .add(ALTHelper
                            .DateToString(ALTHelper
                                    .getCurDateExceptYear()));
        }

        strArray = info.split(" +");

        mData.mInfo.add(strArray[strArray.length - 2]);

        if (++mFilterIndex == mFilter.length) {
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
