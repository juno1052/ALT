package com.lge.alt.filter;

public interface IFilter {

    void doFilter(String readline); // check if readline is valid data

    void parseLineAndSaveData(String key, String readline); // parsing readline
                                                            // and save data

    public String[] getCommands();
}
