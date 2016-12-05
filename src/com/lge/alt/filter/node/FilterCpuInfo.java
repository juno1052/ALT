package com.lge.alt.filter.node;

import com.lge.alt.ALTHelper;
import com.lge.alt.data.DataManager;
import com.lge.alt.data.DataTypeMismatchException;
import com.lge.alt.data.DataManager.DataType;
import com.lge.alt.data.node.DataCpuInfo;

public class FilterCpuInfo extends NodeInfoFilter {

    private static final String TAG = "FilterCpuInfo";

    private final String[] mCommands = new String[] {
            "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq",
            "/sys/devices/system/cpu/cpu1/cpufreq/scaling_cur_freq",
            "/sys/devices/system/cpu/cpu2/cpufreq/scaling_cur_freq",
            "/sys/devices/system/cpu/cpu3/cpufreq/scaling_cur_freq" };

    private final int MAX_PARSE_LINE = 4;

    private int mFilterIndex = 0;

    private DataCpuInfo mData;

    @Override
    public String[] getCommands() {

        return mCommands;
    }

    @Override
    public void doParse(String parserType, String info) {

        if (mData == null) {
            mData = new DataCpuInfo();
            mData.mInfo
                    .add(ALTHelper
                            .DateToString(ALTHelper
                                    .getCurDateExceptYear()));
        }

        mData.mInfo.add(info);

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