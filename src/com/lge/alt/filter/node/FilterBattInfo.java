package com.lge.alt.filter.node;

import com.lge.alt.data.DataManager;
import com.lge.alt.data.DataTypeMismatchException;
import com.lge.alt.data.DataManager.DataType;
import com.lge.alt.data.node.DataBattInfo;

public class FilterBattInfo extends NodeInfoFilter {

    private static final String TAG = "FilterBattInfo";

    private final String[] mCommands = new String[] {
            "/sys/class/power_supply/battery/capacity",
            "/sys/class/power_supply/battery/temp" };

    private final int MAX_PARSE_LINE = 2;

    private int mFilterIndex = 0;

    private DataBattInfo mData;

    @Override
    public String[] getCommands() {

        return mCommands;
    }

    @Override
    public void doParse(String parserType, String info) {

        if (mData == null) {
            mData = new DataBattInfo();
        }

        if (mFilterIndex == 0) {
            mData.mInfo.add(info + " %");
        } else {
            float value = Float.parseFloat(info)/10.0f;
            mData.mInfo.add(Float.toString(value) + " 'C");
        }

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