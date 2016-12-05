package com.lge.alt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import android.content.res.AssetManager;
import com.lge.alt.PackageListActivity.MyPackageListInfo;
import com.lge.alt.data.MainLogData.TriggeredData;

public class ALTHelper {

    public final static String ATSPACKAGELIST_FILENAME = "MonitorATSPackageList";
    public final static String PMPACKAGELIST_FILENAME = "MonitorPMPackageList";
    public final static String SAVEFOLDER = "/saved";

    // action up
    private static Date dTimeActionUp;

    // start
    private static Date dTimeStarted;
    private static String sLaunchedPackage;
    private static String sLaunchedActivity;
    private static String sTimeSpec;

    // displayed
    private static Date dTimeLaunched;
    private static String sTimeDisplayed;
    public static void clearHelperData() {

        dTimeActionUp = null;
        dTimeStarted = null;
        dTimeLaunched = null;
        sLaunchedActivity = null;
        sLaunchedPackage = null;
        sTimeDisplayed = null;
        sTimeSpec = null;
    }

    public static boolean isStarted() {
        return (sTimeSpec != null && dTimeStarted != null
                && sLaunchedActivity != null && sLaunchedPackage != null);
    }

    public static void setStart(String className, String packageName,
            String timeSpec, Date timeStarted) {
        // class, package, spectime, startTime,
        sLaunchedActivity = className;
        sLaunchedPackage = packageName;
        sTimeSpec = timeSpec;
        dTimeStarted = timeStarted;
    }

    public static void setTimeActionUp(Date date) {
        dTimeActionUp = date;
    }

    public static Date getTimeActionUp() {
        return dTimeActionUp;
    }

    public static Date getTimeStarted() {
        return dTimeStarted;
    }

    public static String getLaunchedActivity() {
        return sLaunchedActivity;
    }

    public static String getLaunchedPackage() {
        return sLaunchedPackage;
    }

    public static void setLaunch(Date timeLaunched, String timeDisplayed) {
        dTimeLaunched = timeLaunched;
        sTimeDisplayed = timeDisplayed;
    }

    public static Date getTimeLaunched() {
        return dTimeLaunched;
    }

    public static String getTimeDisplayed() {
        return sTimeDisplayed;
    }

    public static String getTimeSpec() {
        return sTimeSpec;
    }

    public static Date getTimeStartedBefore(int msTime) {

        if (dTimeStarted == null) {
            return null;
        }

        long time = dTimeStarted.getTime() - msTime;

        return new Date(time);
    }

    public static Date getTimeLaunchedBefore(int msTime) {

        if (dTimeLaunched == null) {
            return null;
        }

        long time = dTimeLaunched.getTime() - msTime;

        return new Date(time);
    }

    public static String getProcessNameByPID(String pID) {
        Process p = null;
        BufferedReader br = null;
        String processName = "";

        String cmd = "/proc/" + pID + "/cmdline";

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(
                    cmd)));

            String line;
            while ((line = br.readLine()) != null) {
                processName = line.trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return processName;

    }

    public static Date stringToDate(String str) {

        SimpleDateFormat dayTime = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");

        Date date = null;
        try {
            date = dayTime.parse(str);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return date;
    }

    public static String DateToString(Date date) {

        SimpleDateFormat dayTime = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");

        if (date == null) {
            return null;
        }

        return dayTime.format(date);
    }

    public static Date getCurDateExceptYear() {

        long time = System.currentTimeMillis();
        Date date = new Date(time);

        String str = DateToString(date);
        return stringToDate(str);
    }

    public static void savePackageListFile(File dir, String fileName,
            ArrayList<MyPackageListInfo> list) {

        deletePackageListFile(dir, fileName);

        File file = new File(dir, fileName);

        PrintWriter pw = null;

        try {
            pw = new PrintWriter(new FileOutputStream(file, true));

            for (MyPackageListInfo p : list) {
                if (p.isChecked) {
                    pw.println(p.className + "," + p.packageName + ","
                            + p.limitedTime);
                }
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (pw != null)
                pw.close();
        }
    }

    public static boolean loadPackageListFromFile(String dir, String fileName,
            HashMap<String, Integer> class_spec,
            HashMap<String, String> package_class) {
        try {
            File file = new File(dir, fileName);
            FileInputStream in = new FileInputStream(file);
            BufferedReader b = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = b.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, ",");

                String className = st.nextToken();
                String packageName = st.nextToken();
                String limitationTime = st.nextToken();

                package_class.put(packageName, className);
                class_spec.put(className, Integer.parseInt(limitationTime));

            }

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        if (class_spec.size() > 0) {
            return false;
        } else {
            return true;
        }
    }

    public static void deletePackageListFile(File dir, String fileName) {
        File file = new File(dir, fileName);

        file.delete();

    }

    public static void loadPackageListFromATS(AssetManager asset,
            HashMap<String, Integer> class_spec,
            HashMap<String, String> package_class) {
        // TODO Auto-generated method stub
        try {
            InputStream in = asset.open("ATSPackageList.txt");
            BufferedReader b = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = b.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, ",");
                String className = st.nextToken();
                String packageName = st.nextToken();
                Integer limitationTime = Integer.parseInt(st.nextToken());

                package_class.put(packageName, className);
                class_spec.put(className, limitationTime);

            }

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

    public static boolean isPackageListExist(String dir, String fileName) {
        // TODO Auto-generated method stub

        File file = new File(dir, fileName);
        return file.isFile();

    }

    public static void loadExceptionPackageList(AssetManager asset,
            ArrayList<String> packages) {
        // TODO Auto-generated method stub
        try {
            InputStream in = asset.open("ExceptionPackageList.txt");
            BufferedReader b = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = b.readLine()) != null) {

                String className = line;
                packages.add(className);
            }

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

    public static String removeSlash(String name) {

        name = name.replace("/.", ".");
        name = name.replace("/", ".");

        return name;
    }

    public static boolean removeAllSavedDatas(String dir) {
        // File file = new File(dir+SAVEFOLDER);
        File file = new File(dir);

        String path = dir;
        String subDirs[] = null;

        if (file.isDirectory()) {
            subDirs = file.list();
            int subDirsLength = subDirs.length;
            for (int i = 0; i < subDirsLength; i++) {
                removeAllSavedDatas(path + "/" + subDirs[i]);
            }
        }

        return file.delete();
    }

}
