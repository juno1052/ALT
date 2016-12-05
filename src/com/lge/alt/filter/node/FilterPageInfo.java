package com.lge.alt.filter.node;

import java.util.ArrayList;

import com.lge.alt.ALTHelper;
import com.lge.alt.data.DataManager;
import com.lge.alt.data.DataTypeMismatchException;
import com.lge.alt.data.DataManager.DataType;
import com.lge.alt.data.node.DataBlockInfo;
import com.lge.alt.data.node.DataPageInfo;

public class FilterPageInfo extends NodeInfoFilter {

    private static final String TAG = "FilterPageInfo";

    private final String[] mCommands = new String[] { "/proc/pagetypeinfo" };

    private final String[] mFilter = { "Page block order", "Pages per block",
            "Free pages count", "Number of blocks" };

    private final int MAX_PARSE_LINE = 3;

    private int mFilterIndex = 0;

    private boolean mParsePage = false;
    private boolean mParseBlock = false;
    private boolean mQuit = false;

    private DataPageInfo mDataPage;
    private DataBlockInfo mDataBlock;

    @Override
    public String[] getCommands() {

        return mCommands;
    }

    @Override
    public void doFilter(String readline) {

        parseLineAndSaveData(null, readline);
    }

    @Override
    public void parseLineAndSaveData(String key, String readline) {

        doParse(key, readline);
    }

    @Override
    public void doParse(String parserType, String info) {

        if (mDataPage == null && mDataBlock == null) {

            String curTime = ALTHelper
                    .DateToString(ALTHelper.getCurDateExceptYear());

            mDataPage = new DataPageInfo();
            mDataBlock = new DataBlockInfo();

            mDataPage.mTime = curTime;
            mDataBlock.mTime = curTime;
        }

        if (info.contains(mFilter[mFilterIndex])) {

            if (mFilterIndex == 0) {
                mDataPage.mBlockOrder = info;
                mFilterIndex++;
            } else if (mFilterIndex == 1) {
                mDataPage.mBlockPages = info;
                mFilterIndex++;
            } else if (mFilterIndex == 2) {
                mParsePage = true;
                mFilterIndex++;
                return;
            } else if (mFilterIndex == 3) {
                mParsePage = false;
                mParseBlock = true;
                mFilterIndex = 0;
                return;
            }
        }

        String[] parseInfo;

        if (mParsePage) {

            parseInfo = info.replace(",", "").split(" +");

            if (parseInfo.length == 1) {
                mParsePage = false;
                return;
            } else {
                StringBuffer sb = new StringBuffer();

                for (int idx = 3; idx < parseInfo.length; idx++) {

                    if (idx == 4) {
                        continue; // remove "type"
                    }

                    if (idx == 5) {
                        sb.append(String.format("%-15s", parseInfo[idx]));
                    } else {
                        sb.append(String.format("%-10s", parseInfo[idx]));
                    }

                }

                mDataPage.mInfo.add(sb.toString());
            }
        }

        if (mParseBlock) {

            parseInfo = info.replace(",", "").split(" +");

            StringBuffer sb = new StringBuffer();

            for (int idx = 3; idx < parseInfo.length; idx++) {

                sb.append(String.format("%-15s", parseInfo[idx]));
            }

            mDataBlock.mInfo.add(sb.toString());

            if (mDataBlock.mInfo.size() == MAX_PARSE_LINE) {
                mParseBlock = false;
                mQuit = true;
            }
        }

        if (mQuit) {
            try {
                DataManager.getInstance()
                        .addDataToMap(mDataPage, DataType.NODE);
                DataManager.getInstance().addDataToMap(mDataBlock,
                        DataType.NODE);

                mDataPage = null;
                mDataBlock = null;

                mParsePage = false;
                mParseBlock = false;
                mQuit = false;

                mFilterIndex = 0;
            } catch (DataTypeMismatchException e) {
                e.printStackTrace();
            }
        }
    }
}
