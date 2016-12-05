package com.lge.alt.filter.dump;

import com.lge.alt.data.DataManager;
import com.lge.alt.data.DataTypeMismatchException;
import com.lge.alt.data.DataManager.DataType;
import com.lge.alt.data.dump.DumpDataProcStats;


public class FilterDumpProcStats extends FilterDumpInfo {

        private final String[] mCommands = new String[] { "sh", "-c",
                "dumpsys procstats --hours 1" };

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

            try {
                DataManager.getInstance().addDataToMap(
                        new DumpDataProcStats(info), DataType.DUMP);
            } catch (DataTypeMismatchException e) {
                e.printStackTrace();
            }
        }
    }
