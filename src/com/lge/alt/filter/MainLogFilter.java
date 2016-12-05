package com.lge.alt.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import android.util.Log;

import com.lge.alt.ALTHelper;
import com.lge.alt.ALTService;
import com.lge.alt.data.DataManager;
import com.lge.alt.data.DataTypeMismatchException;
import com.lge.alt.data.MainLogData;
import com.lge.alt.data.DataManager.DataType;

public class MainLogFilter implements IFilter {

    private static final String TAG = "MainLogFilter";

    private final String[] mCommands = new String[] { "sh", "-c",
            "logcat -v time -b main" };
    /*
     * private final String[] mCommands = new String[] { "sh", "-c",
     * "logcat -v time -b main ActivityManager *:S art *:S InputTransport *:S"
     * };
     */
    private final MainParser mParser = new MainParser();

    private static MainLogFilter mInstance = null;
    private static HashMap<String, String> sMainFilterMap = new HashMap<String, String>();

    public static final String[] sMainLogFilter = { "Displayed",
            "concurrent mark sweep GC", "notifyMotion - action=ACTION_UP" };

    public static final String[] sMainParseMethod = { "parseTriggerInfo",
            "parseGCInfo", "parseActionUpInfo" };

    static {
        for (int i = 0; i < sMainLogFilter.length; i++) {
            sMainFilterMap.put(sMainLogFilter[i], sMainParseMethod[i]);
        }
    }

    public static MainLogFilter getInstance() {

        if (mInstance == null) {
            mInstance = new MainLogFilter();
        }

        return mInstance;
    }

    @Override
    public String[] getCommands() {
        return mCommands;
    }

    @Override
    public void doFilter(String readline) {

        Iterator<String> it = sMainFilterMap.keySet().iterator();

        if (readline == null)
            return;

        while (it.hasNext()) {
            String key = it.next();

            if (readline.contains(key)) {
                // Log.e("juno", "isValid ok " + key);

                parseLineAndSaveData(key, readline);
                return;
            }
        }
    }

    @Override
    public void parseLineAndSaveData(String key, String readline) {
        String parserType = sMainFilterMap.get(key);
        mParser.doParse(parserType, readline);
    }

    public class MainParser extends LogcatParser {

        @Override
        public void doParse(String parserType, String info) {
            // TODO Auto-generated method stub

            LogcatData data = mParser.toLogcatData(info);

            if (parserType.equals(sMainParseMethod[0])) {
                parseTriggerInfo(data);
            } else if (parserType.equals(sMainParseMethod[1])) {
                parseGCInfo(data);
            } else if (parserType.equals(sMainParseMethod[2])) {
                parseActionUpInfo(data);
            }

        }

        private int stringToInt(String duration) {

            // Log.v("yongho","duration:"+duration);

            StringBuffer num = new StringBuffer();

            int value = 0;

            for (int i = 0; i < duration.length(); i++) {
                char c = duration.charAt(i);

                if (Character.isDigit(c)) {
                    num.append(c);
                } else if (c == 's') {
                    value = Integer.parseInt(num.toString()) * 1000;
                    num = new StringBuffer();
                } else if (c == 'm') {
                    value += Integer.parseInt(num.toString());
                    num = new StringBuffer();
                    break;
                }
            }

            // Log.v("yongho","value:"+value);

            return value;
        }

        private void parseTriggerInfo(LogcatData data) {
            // TODO need to implement the parsing code

            // new TriggeredData()
            MainLogData.TriggeredData triggeredData = new MainLogData.TriggeredData();

            triggeredData.valutOfdate = data.getDate();

            StringTokenizer st = new StringTokenizer(data.getContent(), ":+");

            if (st.countTokens() > 2) {
                String temp_packageName = st.nextToken();
                String[] name = temp_packageName.split(" ");
                if( name.length > 2 ){
                String[] pName = name[2].split("/");
                triggeredData.packageName = pName[0];
                triggeredData.className = ALTHelper.removeSlash(name[2]);
                st.nextToken();
                String time = st.nextToken(); // with ms or ns...
                triggeredData.launchingTime = stringToInt(time); // temp...
                }
            }

            if (triggeredData.isSet()) {
                if (ALTHelper.isStarted()
                        && triggeredData.packageName.equals(ALTHelper
                                .getLaunchedPackage())) {
                    // check package & time over
                    int timeLimit = Integer.parseInt(ALTHelper.getTimeSpec());

                    if (triggeredData.launchingTime >= timeLimit) {
                        Log.v(TAG, "Triggered!! : " + triggeredData.packageName
                                + " time:" + triggeredData.launchingTime);

                        ALTHelper.setLaunch(triggeredData.valutOfdate,
                                triggeredData.launchingTime + "");

                        // do trigger
                        ALTService.getInstance().triggered();

                    }

                } else {
                    Log.e(TAG,
                            "can't triggered! startTime:"
                                    + ALTHelper.getTimeStarted() + " class:"
                                    + ALTHelper.getLaunchedActivity());
                    ALTHelper.clearHelperData();
                }

            }

        }

        private void parseGCInfo(LogcatData data) {
            // TODO

            // new GCData()
            MainLogData.GCData gcData = new MainLogData.GCData();
            gcData.valutOfdate = data.getDate();

            gcData.PID = data.getPid();
            gcData.ProcessName = ALTHelper.getProcessNameByPID(gcData.PID
                    .trim());

            int index = 0;
            String[] temp = data.getContent().split(" ");

            if (temp.length == 19 || temp.length == 20) {
                if (temp.length == 19) {
                    gcData.GC_cause = temp[index++];
                } else if (temp.length == 20) {
                    gcData.GC_cause = temp[index++] + " " + temp[index++];
                }
                // set others
                index += 5;
                StringTokenizer st = new StringTokenizer(temp[index++], "()");// 706(30KB)
                if (st.countTokens() == 2) {
                    gcData.freedObject = st.nextToken();
                    gcData.freedByte = st.nextToken();
                }
                index += 2;
                st = new StringTokenizer(temp[index++], "()");// 0(0B)
                if (st.countTokens() == 2) {
                    gcData.freedLObject = st.nextToken();
                    gcData.freedLByte = st.nextToken();
                }
                index += 2;
                gcData.percent_free = temp[index++]; // 53%
                index++;
                st = new StringTokenizer(temp[index++], "/"); // 6MB/14MB
                if (st.countTokens() == 2) {
                    gcData.current_heap_size = st.nextToken();
                    gcData.total_memory = st.nextToken();
                }
                index++;
                gcData.pause_time = temp[index++]; // 371us
                index++;
                // System.out.println(temp[index++]); // total
                gcData.Total_time = temp[index++]; // 20.851ms

            } else {
                // error
            }

            if (gcData.isSet()) {
                // Log.v("juno","GCData!! : "+gcData.toString());
                try {
                    DataManager.getInstance().addDataToMap(gcData,
                            DataType.MAIN);
                } catch (DataTypeMismatchException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

        private void parseActionUpInfo(LogcatData data) {
            // TODO
            // channel '1cffcb78 com.lge.launcher2/com.lge.launcher2.Launcher
            // (server)' : action=ACTION_UP, x=651.096, y=1145.105,
            // downTime=31521176380000, eventTime=31521227488000, pointerCount=1

            // notifyMotion - action=ACTION_UP, x=333.537, y=1278.002,
            // eventTime=12420686129000, downTime=12420642937000

            ALTHelper.setTimeActionUp(data.getDate());
        }

    }
}
