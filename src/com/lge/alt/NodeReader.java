package com.lge.alt;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.lge.alt.filter.NodeFilter;
import com.lge.alt.filter.node.NodeInfoFilter;

public class NodeReader implements Runnable {

    private static final String TAG = "NodeReader";

    private final NodeFilter mFilter;

    public NodeReader() {

        mFilter = NodeFilter.getInstance();
    }

    @Override
    public void run() {

        for (NodeInfoFilter filter : mFilter.getFilters()) {

            String line = null;
            BufferedReader reader = null;

            String[] cmd = filter.getCommands();

            for (int idx = 0; idx < cmd.length; idx++) {

                try {
                    reader = new BufferedReader(new InputStreamReader(
                            new FileInputStream(cmd[idx])));

                    while ((line = reader.readLine()) != null) {
                        filter.doFilter(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if(reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        ALTService.getInstance().finished(
                ALTService.ALT_NODE);
    }
}