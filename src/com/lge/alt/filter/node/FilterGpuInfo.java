package com.lge.alt.filter.node;

import com.lge.alt.ALTHelper;
import com.lge.alt.data.DataManager;
import com.lge.alt.data.DataTypeMismatchException;
import com.lge.alt.data.DataManager.DataType;
import com.lge.alt.data.node.DataGpuInfo;

public class FilterGpuInfo extends NodeInfoFilter {

    private static final String TAG = "FilterGpuInfo";

    private final String[] mCommands = new String[] { "/sys/class/kgsl/kgsl-3d0/gpuclk" };

    private DataGpuInfo mData;

    @Override
    public String[] getCommands() {

        return mCommands;
    }

    @Override
    public void doParse(String parserType, String info) {

        if (mData == null) {
            mData = new DataGpuInfo();
            mData.mInfo
                    .add(ALTHelper
                            .DateToString(ALTHelper
                                    .getCurDateExceptYear()));
        }

        mData.mInfo.add(info);

        try {
            DataManager.getInstance().addDataToMap(mData, DataType.NODE);

            mData = null;
        } catch (DataTypeMismatchException e) {
            e.printStackTrace();
        }
    }
}