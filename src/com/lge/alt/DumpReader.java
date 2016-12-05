package com.lge.alt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.lge.alt.filter.DumpFilter;
import com.lge.alt.filter.IFilter;
import com.lge.alt.filter.dump.FilterDumpInfo;

public class DumpReader implements Runnable {

    private static final String TAG = "LogReader";

    private final DumpFilter mFilter;

    public DumpReader() {

        mFilter = DumpFilter.getInstance();
    }

    @Override
    public void run() {

        for (FilterDumpInfo filter : mFilter.getFilters()) {

            Process p = null;
            BufferedReader br = null;

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
                    if (p != null) {
                        br.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        ALTService.getInstance().finished(ALTService.ALT_DUMP);
    }
}