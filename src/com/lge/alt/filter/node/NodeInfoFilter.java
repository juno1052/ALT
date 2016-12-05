package com.lge.alt.filter.node;

import com.lge.alt.filter.IFilter;
import com.lge.alt.filter.IParser;

public abstract class NodeInfoFilter implements IFilter, IParser {

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