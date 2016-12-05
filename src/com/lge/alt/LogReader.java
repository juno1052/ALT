package com.lge.alt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.lge.alt.filter.IFilter;
import com.lge.alt.filter.KernelLogFilter;
import com.lge.alt.filter.MainLogFilter;
import com.lge.alt.filter.SystemLogFilter;

public class LogReader implements Runnable {

    private static final String TAG = "LogReader";

    public ArrayList<IFilter> mFilters;
    private int mLogType = -1;

    public LogReader(int logtype) {

        mFilters = new ArrayList<IFilter>();

        this.mLogType = logtype;

        switch (logtype) {
        case ALTService.ALT_MAIN:
            mFilters.add(MainLogFilter.getInstance());
            break;
        case ALTService.ALT_SYSTEM:
            mFilters.add(SystemLogFilter.getInstance());
            break;
        case ALTService.ALT_KERNEL:
            mFilters.add(KernelLogFilter.getInstance());
            break;

        default:
            break;
        }
    }

    private void preExecute(int logType) throws IOException {

        switch (logType) {

        case ALTService.ALT_MAIN:
        case ALTService.ALT_SYSTEM:
            Runtime.getRuntime().exec("logcat -c");
            break;

        // case PerfLogCollectorService.PERFLOG_KERNEL:
        // command = new String[] { "sh", "-c",
        // "echo 7 > /sys/module/mmc_core/parameters/debug_level" };
        // Runtime.getRuntime().exec(command);
        // break;

        default:
            // Log.d(TAG, "Nothing to do");
        }
    }

    @Override
    public void run() {

        for (int i = 0; i < mFilters.size(); i++) {

            Process p = null;
            BufferedReader br = null;
            IFilter filter = mFilters.get(i);

            try {
                preExecute(mLogType);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                p = Runtime.getRuntime().exec(filter.getCommands());

                br = new BufferedReader(new InputStreamReader(
                        p.getInputStream()));

                String line;
                while ((line = br.readLine()) != null) {
                    filter.doFilter(line);

                    if (ALTService.isServiceStopped()) {
                        break;
                    }

                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                if (p != null) {
                    p.destroy();
                }
                try {
                    if (br != null) {
                        br.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        ALTService.getInstance().finished(mLogType);
    }
}