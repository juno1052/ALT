package com.lge.alt.filter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.lge.alt.ALTHelper;
import com.lge.alt.ALTService;
import com.lge.alt.data.DataManager;
import com.lge.alt.data.DataTypeMismatchException;
import com.lge.alt.data.SystemLogData;
import com.lge.alt.data.DataManager.DataType;

public class SystemLogFilter implements IFilter {

    private static final String TAG = "SystemLogFilter";

    private static SystemLogFilter mInstance = null;
    private static HashMap<String, String> sSystemLogFilterMap = new HashMap<String, String>();

    private final String[] mCommands = new String[] { "sh", "-c",
            "logcat -v time -b system" };
    /*
     * private final String[] mCommands = new String[] { "sh", "-c",
     * "logcat -v time -b system ActivityManager *:S" };
     */
    private final SystemParser mParser = new SystemParser();

    public static final String[] sSystemLogFilter = {
            "Scheduling restart of crashed service", "Start proc", "has died",
            "START u0" };

    public static final String[] sSystemParseMethod = { "parseProcRestartInfo",
            "parseProcStartInfo", "parseProcDiedInfo", "parseStartU0Info" };

    static {
        for (int i = 0; i < sSystemLogFilter.length; i++) {
            sSystemLogFilterMap.put(sSystemLogFilter[i], sSystemParseMethod[i]);
        }
    }

    public static SystemLogFilter getInstance() {

        if (mInstance == null) {
            mInstance = new SystemLogFilter();
        }

        return mInstance;
    }

    @Override
    public String[] getCommands() {
        return mCommands;
    }

    @Override
    public void doFilter(String readline) {

        Iterator<String> it = sSystemLogFilterMap.keySet().iterator();

        if (readline == null)
            return;

        while (it.hasNext()) {
            String key = it.next();

            if (readline.contains(key)) {
                parseLineAndSaveData(key, readline);
                return;
            }
        }
    }

    @Override
    public void parseLineAndSaveData(String key, String readline) {

        String parserType = sSystemLogFilterMap.get(key);
        mParser.doParse(parserType, readline);
    }

    private class SystemParser extends LogcatParser {

        @Override
        public void doParse(String parserType, String info) {

            LogcatData data = mParser.toLogcatData(info);

            if (parserType.equals(sSystemParseMethod[0])) {
                parseProcRestartInfo(data);
            } else if (parserType.equals(sSystemParseMethod[1])) {
                parseProcStartInfo(data);
            } else if (parserType.equals(sSystemParseMethod[2])) {
                parseProcDiedInfo(data);
            } else if (parserType.equals(sSystemParseMethod[3])) {
                parseStartU0Info(data);
            }
        }

        private void parseProcRestartInfo(LogcatData data) {
            // TODO need to implement the parsing code

            // new procRestartData()
            SystemLogData.ProcRestartData procRestartData = new SystemLogData.ProcRestartData();

            procRestartData.valutOfdate = data.getDate();

            // Scheduling restart of crashed service
            // com.android.gallery3d/com.lge.gallery.smartshare.push.SmartshareNotificationService
            // in 10992ms
            StringTokenizer st = new StringTokenizer(data.getContent(), " ");

            if (st.countTokens() == 8) {
                st.nextToken(); // Scheduling
                st.nextToken(); // restart
                st.nextToken(); // of
                st.nextToken(); // crashed
                st.nextToken(); // service

                // 5 servicename
                procRestartData.serviceName = st.nextToken();

                st.nextToken();

                // 7 duration
                procRestartData.duration = st.nextToken();
            }

            if (procRestartData.isSet()) {
                try {
                    DataManager.getInstance().addDataToMap(procRestartData,
                            DataType.SYSTEM);
                } catch (DataTypeMismatchException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

        private void parseProcStartInfo(LogcatData data) {
            // TODO
            SystemLogData.ProcStartData procStartData = new SystemLogData.ProcStartData();

            procStartData.valutOfdate = data.getDate();

            procStartData.PID = data.getPid();

            StringTokenizer st = new StringTokenizer(data.getContent(), ":");

            // processName, component
            StringTokenizer process_st = new StringTokenizer(st.nextToken(),
                    " ");

            int count = process_st.countTokens();

            if (count == 6 || count == 7) {
                process_st.nextToken(); // start
                process_st.nextToken(); // proc

                procStartData.start_processName = process_st.nextToken();

                process_st.nextToken(); // for

                if (count == 6) {
                    procStartData.component = process_st.nextToken();
                } else if (count == 7) {
                    procStartData.component = process_st.nextToken() + " "
                            + process_st.nextToken();
                } else {
                    // Error!!
                }

                procStartData.component_processName = process_st.nextToken();
            }

            if (procStartData.isSet()) {
                try {
                    DataManager.getInstance().addDataToMap(procStartData,
                            DataType.SYSTEM);
                } catch (DataTypeMismatchException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

        private void parseProcDiedInfo(LogcatData data) {
            // TODO
            SystemLogData.ProcDiedData procDiedData = new SystemLogData.ProcDiedData();

            procDiedData.valutOfdate = data.getDate();

            // Process com.google.android.gms:car (pid 10212) has died
            String content = data.getContent();

            StringTokenizer st = new StringTokenizer(content, " ()");

            if (st.countTokens() == 6) {
                st.nextToken(); // Process
                procDiedData.processName = st.nextToken(); // com.google.android.gms:car
                st.nextToken(); // pid
                procDiedData.PID = st.nextToken(); // 10212
            }

            if (procDiedData.isSet()) {
                try {
                    DataManager.getInstance().addDataToMap(procDiedData,
                            DataType.SYSTEM);
                } catch (DataTypeMismatchException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

        private void parseStartU0Info(LogcatData data) {

            String temp[] = data.getContent().split("cmp=");
            StringTokenizer st = new StringTokenizer(temp[1], " }");
            String activityName = st.nextToken();

            String package_temp[] = activityName.split("/");
            String packageName = package_temp[0];

            String className = ALTHelper.removeSlash(activityName);

            if (ALTService.getInstance().packages.containsKey(className)) {
                Integer ItimeLimit = ALTService.getInstance().packages
                        .get(className);
                ALTHelper.setStart(className, packageName,
                        ItimeLimit.toString(), data.getDate());

                ALTService.getInstance().started();
            }

        }
    }
}
