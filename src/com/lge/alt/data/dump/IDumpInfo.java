package com.lge.alt.data.dump;

public interface IDumpInfo {

    public static final int DUMP_PROCSTATS = 0;
    public static final int DUMP_CPUINFO = DUMP_PROCSTATS + 1;

    public static final int CHART_CPU_INFO = 100;

    public static final int HTML_REPORT_DUMP_PROCSTAT  = 1000;
    public static final int HTML_REPORT_DUMP_CPUINFO   = HTML_REPORT_DUMP_PROCSTAT + 1;
}