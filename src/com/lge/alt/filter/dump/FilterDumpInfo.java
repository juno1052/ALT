package com.lge.alt.filter.dump;

import com.lge.alt.filter.IFilter;
import com.lge.alt.filter.IParser;

public abstract class FilterDumpInfo implements IFilter, IParser {

    public static final String TAG = "FilterDumpInfo";

    @Override
    public abstract String[] getCommands();

    @Override
    public abstract void doParse(String parserType, String readline);

    @Override
    public void doFilter(String readline) {

        parseLineAndSaveData(null, readline);
    }

    @Override
    public void parseLineAndSaveData(String key, String readline) {

        doParse(key, readline);
    }
}