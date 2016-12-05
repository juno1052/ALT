package com.lge.alt.filter.node;

import com.lge.alt.ALTHelper;
import com.lge.alt.data.DataManager;
import com.lge.alt.data.DataTypeMismatchException;
import com.lge.alt.data.DataManager.DataType;
import com.lge.alt.data.node.DataFragInfo;

public class FilterFragInfo extends NodeInfoFilter {

    private static final String TAG = "FilterFragInfo";

    private final String[] mCommands = new String[] { "/d/extfrag/unusable_index" };

    private int mFilterIndex = 0;

    private DataFragInfo mData;

    @Override
    public String[] getCommands() {

        return mCommands;
    }

    @Override
    public void doParse(String parserType, String info) {

        if (mData == null) {
            mData = new DataFragInfo();
            mData.mTime = ALTHelper
                    .DateToString(ALTHelper.getCurDateExceptYear());
        }

        float avrFrag = 0.0f;
        String parse;
        String[] parseInfo = info.split(" +");

        StringBuffer sb = new StringBuffer();

        for (int idx = 3; idx < parseInfo.length; idx++) {

            parse = parseInfo[idx];

            if (idx >= 4) {
                float level = Float.parseFloat(parse) * 100;
                avrFrag += level;
                parse = Integer.toString((int)level) + " %";
            }

            sb.append(String.format("%-10s", parse));
        }

        mData.mInfo.add(sb.toString());

        parse = Integer.toString((int)(avrFrag / 11));

        if (mFilterIndex++ == 0) {
            mData.mAvrFragNormal = parse;
        } else {
            mData.mAvrFragHigh = parse;
        }

        if (mFilterIndex == 2) {
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
