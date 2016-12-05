package com.lge.alt.filter.dump;

import android.util.Log;

import com.lge.alt.data.DataManager;
import com.lge.alt.data.DataManager.DataType;
import com.lge.alt.data.DataTypeMismatchException;
import com.lge.alt.data.DumpData;
import com.lge.alt.data.dump.DumpDataCpuInfo;
import com.lge.alt.data.dump.DumpDataInfo;
import com.lge.alt.data.dump.IDumpInfo;


public class FilterDumpCpuInfo extends FilterDumpInfo implements IDumpInfo {

    private final double MINMUM_CPU_LOAD = 1.0;

    private final String[] mCommands = new String[] { "sh", "-c",
            "dumpsys cpuinfo" };

    public static final String[] sDumpCpuInfoFilter = {
        "CPU usage from", "TOTAL:" };

    private enum CpuDataType {
        UNKNOWN,
        TIME_LINE,
        MAIN_PROC,
        SUB_PROC,
        SKIP_PROC,
        END_DATA
    }

        @Override
        public String[] getCommands() {
            return mCommands;
        }

        @Override
        public void doFilter(String readline) {

            CpuDataType type = checkDataType(readline);
            parseLineAndSaveData(String.valueOf(type), readline);

        }

        @Override
        public void parseLineAndSaveData(String type, String readline) {

            doParse(type, readline);
        }

        @Override
        public void doParse(String type, String info) {

            DumpDataCpuInfo dump = null;

            String name = "";
            String load = "";

            DumpDataCpuInfo.mDumpRawList.add(info); //for raw dump data

            switch(type) {

                case "TIME_LINE" :
                    new DumpDataCpuInfo().setTimeInfo(info.trim());
                    break;

                case "MAIN_PROC" :

                    load = info.trim().split("%")[0];
                    name = info.trim().split("/")[1].split(":")[0];


                    if(Double.parseDouble(load) >= MINMUM_CPU_LOAD) {
                        dump = new DumpDataCpuInfo(name, load, true);
                    }

                    break;

                case "SUB_PROC" :

                    load = info.trim().split("%")[0];
                    name = info.trim().split("/")[1].split(":")[0];

                    if(Double.parseDouble(load) >= MINMUM_CPU_LOAD) {
                        dump = new DumpDataCpuInfo(name, load, false);
                    }

                    break;

//                case "SKIP_PROC" :
//
//                    load = info.trim().split("[+]")[1].split("%")[0];
//                    name = info.trim().split("/")[1].split(":")[0];
//
//                    dump = new DumpDataCpuInfo(name, load, false);

//                  break;

                case "END_DATA" :
                    dump = new DumpDataCpuInfo("", "", false, true);
                    break;
                default :

            }

            try {
                if(dump != null) {
                    DataManager.getInstance().addDataToMap(
                            dump, DataType.DUMP);
                }
            } catch (DataTypeMismatchException e) {
                e.printStackTrace();
            }
        }

        private CpuDataType checkDataType(String line) {
            CpuDataType type = CpuDataType.UNKNOWN;

            if(line.contains(sDumpCpuInfoFilter[0])) {

                type = CpuDataType.TIME_LINE;

            } else if(line.contains(sDumpCpuInfoFilter[1])) {

                type = CpuDataType.END_DATA;

            } else if(String.valueOf(line.charAt(3)).equals(" ")) {

                type = CpuDataType.SUB_PROC;

            } else if(String.valueOf(line.charAt(3)).equals("+")) {

                type = CpuDataType.SKIP_PROC;

            } else if(String.valueOf(line.charAt(1)).equals(" ")) {

                type = CpuDataType.MAIN_PROC;
            } else {
                Log.e(TAG, "Nothing to match");
            }

            return type;
        }

    }
