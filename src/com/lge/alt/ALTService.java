package com.lge.alt;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.lge.alt.data.DataManager;
import com.lge.alt.data.DataManager.DataType;
import com.lge.alt.data.DataTypeMismatchException;
import com.lge.alt.html.HTMLDataManager;

public class ALTService extends Service {

    private static final String TAG = "ALTService";
    private static final int SERVICE_ID = 8009;

    public static final int ALT_MAIN = 0x1;
    public static final int ALT_SYSTEM = 0x2;
    public static final int ALT_KERNEL = 0x4;
    public static final int ALT_NODE = 0x8;
    public static final int ALT_DUMP = 0x10;

    private static final int STATE_RUN = 0;
    private static final int STATE_STOP = 1;
    private static final int STATE_TRIG = 2;

    public static final int NODE_PERIOD = 500;

    private int mFinished = 0;

    private static Context mContext = null;
    private static ALTService mInstance = null;

    public HashMap<String, Integer> packages;
    public HashMap<String, String> package_class;

    private ServiceMainHandler mServiceMainHandler;
    private final HashMap<Integer, Handler> mThreads = new HashMap<Integer, Handler>();
    private final HomeProcess hProc = new HomeProcess();

    private static int mServiceState = STATE_STOP;
    private static boolean isATS = false;

    public static boolean Status = false;

    public ALTService() {
        super();
        mInstance = this;
        Log.d(TAG, "ALTService");
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Service onCreate");

        mContext = getApplicationContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "Service onDestroy");

        setServiceState(STATE_STOP);
        destoryThread();
        stopForeground(true);
        Runtime.getRuntime().gc();
    }

    @Override
    public void onStart(Intent intent, int startId) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.d(TAG, "Service onStartCommand");

        if (mServiceState != STATE_STOP) {
            Log.e(TAG, "alreay running");
            return -1;
        }

        startForeground(SERVICE_ID, createNotification());

        packages = new HashMap<String, Integer>();
        package_class = new HashMap<String, String>();

        isATS = intent.getBooleanExtra("isATS", false);
        String savedFileName = null;

        if (isATS) {
            savedFileName = ALTHelper.ATSPACKAGELIST_FILENAME;

        } else {
            savedFileName = ALTHelper.PMPACKAGELIST_FILENAME;
        }

        ALTHelper.loadPackageListFromFile(getFilesDir().getAbsolutePath(),
                savedFileName, packages, package_class);

        if (packages.size() == 0) {
            Log.e(TAG, "packageList 0!!");
            return -1;
        }

        // load process list
        setHomeProcess();

        mServiceMainHandler = new ServiceMainHandler();

        initThreads();
        runThreads(ALT_MAIN | ALT_SYSTEM | ALT_NODE, 0);

        setServiceState(STATE_RUN);

        return START_NOT_STICKY;

    }

    public static Context getContext() {
        return mContext;
    }

    public static ALTService getInstance() {
        return mInstance;
    }

    private Notification createNotification() {

        Intent intent = new Intent(getContext(), com.lge.alt.MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.noti_content))
                .setSmallIcon(R.drawable.icon_alt)
                .setCategory(Notification.CATEGORY_SYSTEM)
                .setContentIntent(contentIntent).setOngoing(true);

        return builder.build();
    }

    private void initThreads() {
        HandlerThread thread;

        // main_log
        thread = new HandlerThread("main_log");
        thread.start();
        mThreads.put(ALT_MAIN, new MainThreadHandler(thread.getLooper()));

        // system_log
        thread = new HandlerThread("system_log");
        thread.start();
        mThreads.put(ALT_SYSTEM, new SystemThreadHandler(thread.getLooper()));

        // kernel_log
        thread = new HandlerThread("kernel_log");
        thread.start();
        mThreads.put(ALT_KERNEL, new KernelThreadHandler(thread.getLooper()));

        // node_log
        thread = new HandlerThread("node_log");
        thread.start();
        mThreads.put(ALT_NODE, new NodeThreadHandler(thread.getLooper()));

        // dumpsys_log
        thread = new HandlerThread("dump_log");
        thread.start();
        mThreads.put(ALT_DUMP, new DumpThreadHandler(thread.getLooper()));
    }

    private void runThreads(int thread, long delayMillis) {

        if ((thread & ALT_MAIN) != 0) {
            mThreads.get(ALT_MAIN).postDelayed(new LogReader(ALT_MAIN),
                    delayMillis);
        }

        if ((thread & ALT_SYSTEM) != 0) {
            mThreads.get(ALT_SYSTEM).postDelayed(new LogReader(ALT_SYSTEM),
                    delayMillis);
        }

        if ((thread & ALT_KERNEL) != 0) {
            mThreads.get(ALT_KERNEL).postDelayed(new LogReader(ALT_KERNEL),
                    delayMillis);
        }

        if ((thread & ALT_NODE) != 0) {
            mThreads.get(ALT_NODE).postDelayed(new NodeReader(), delayMillis);
        }

        if ((thread & ALT_DUMP) != 0) {
            mThreads.get(ALT_DUMP).postDelayed(new DumpReader(), delayMillis);
        }
    }

    private void destoryThread() {

        Iterator<Integer> iterator = mThreads.keySet().iterator();

        while (iterator.hasNext()) {
            mThreads.get(iterator.next()).getLooper().quit();
        }

    }

    private void setServiceState(int state) {
        mServiceState = state;
        Log.i(TAG, "Service State : " + state);
    }

    public static boolean isServiceStopped() {
        return (mServiceState == STATE_STOP);
    }

    public static boolean isATS() {
        return isATS;
    }

    private class ServiceMainHandler extends Handler {

        public static final int STARTED = 1;
        public static final int TRIGGERED = 2;
        public static final int FINISHED = 3;

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);

            if (isServiceStopped()) {
                return;
            }

            switch (msg.what) {

            case STARTED:
                Log.i(TAG, "ServiceMainHandler : STARTED");

                if (mServiceState == STATE_TRIG) {
                    started();
                    Log.w(TAG, "Triggered processing is not completed !!");
                    return;
                }

                clearData(DataType.NODE, false);
                break;

            case TRIGGERED:
                Log.i(TAG, "ServiceMainHandler : TRIGGERED");

                setServiceState(STATE_TRIG);

                runThreads(ALT_KERNEL, 0);
                runThreads(ALT_DUMP, 0);
                break;

            case FINISHED:
                Log.d(TAG,
                        "ServiceMainHandler : FINISHED (State : "
                                + mServiceState + ", LogType : "
                                + Integer.toHexString(msg.arg1) + ")");

                if (msg.arg1 != ALT_KERNEL && msg.arg1 != ALT_NODE
                        && msg.arg1 != ALT_DUMP) {
                    return;
                }

                if (mServiceState == STATE_TRIG) {

                    mFinished |= msg.arg1;

                    if (mFinished == (ALT_KERNEL | ALT_NODE | ALT_DUMP)) {

                        makeOutputData();

                        // ALTHelper.clearHelperData();
                        mFinished = 0;
                        setServiceState(STATE_RUN);
                        runThreads(ALT_NODE, 0);
                    }
                } else {
                    if (msg.arg1 == ALT_NODE) {
                        runThreads(ALT_NODE, NODE_PERIOD);
                    }
                }
                break;

            default:
                break;
            }
        }
    }

    private class MainThreadHandler extends Handler {

        public MainThreadHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);

            switch (msg.what) {
            default:
                break;
            }
        }
    }

    private class SystemThreadHandler extends Handler {

        public SystemThreadHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);

            switch (msg.what) {
            default:
                break;
            }
        }
    }

    private class KernelThreadHandler extends Handler {

        public KernelThreadHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);

            switch (msg.what) {
            default:
                break;
            }
        }
    }

    private class NodeThreadHandler extends Handler {

        public NodeThreadHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);

            switch (msg.what) {
            default:
                break;
            }
        }
    }

    private class DumpThreadHandler extends Handler {

        public DumpThreadHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);

            switch (msg.what) {
            default:
                break;
            }
        }
    }

    public void started() {

        Message msg = mServiceMainHandler.obtainMessage();
        msg.what = ServiceMainHandler.STARTED;
        mServiceMainHandler.sendMessage(msg);
    }

    public void triggered() {

        Message msg = mServiceMainHandler.obtainMessage();
        msg.what = ServiceMainHandler.TRIGGERED;
        mServiceMainHandler.sendMessage(msg);
    }

    public void finished(int logtype) {

        Message msg = mServiceMainHandler.obtainMessage();
        msg.what = ServiceMainHandler.FINISHED;
        msg.arg1 = logtype;
        mServiceMainHandler.sendMessage(msg);
    }

    private File getOutputDirectory() {

        String className = ALTHelper.getLaunchedActivity();
        if (className == null) {
            Log.e(TAG, "getOutputDirectory : package name is null !!");
            return null;
        }

        Date date = new Date(System.currentTimeMillis());

        String curTime = ALTHelper.DateToString(date);
        StringTokenizer st = new StringTokenizer(curTime, "-: ");

        String dirPackage = className;
        String dirTime = st.nextToken() + "-" + st.nextToken() + "_"
                + st.nextToken() + "h_" + st.nextToken() + "m_"
                + st.nextToken() + "s";

        File dir = mContext.getExternalFilesDirs(null)[0];
        String basePath = dir.getAbsolutePath() + ALTHelper.SAVEFOLDER;

        File outDir = new File(basePath, dirPackage + "/" + dirTime);
        if (outDir.exists() == false) {
            outDir.mkdirs();
        }

        return outDir;
    }

    private void makeOutputData() {

        File dir = getOutputDirectory();

        if (dir == null) {
            Log.e(TAG, "makeOutputData : output directory is null !!");
            return;
        }

        DataManager.getInstance().makeOutputDataText(dir);
        HTMLDataManager.getInstance().makeOutputDataHtml(dir);

        Toast.makeText(getApplicationContext(),
                String.format(getString(R.string.svc_stat_triggered), dir),
                Toast.LENGTH_LONG).show();

        clearData(null, true);
    }

    private void clearData(DataType dataType, boolean all) {

        try {
            DataManager.getInstance().clearData(dataType, all);
        } catch (DataTypeMismatchException e) {
            e.printStackTrace();
        }
    }

    public String getHomeProcessName() {
        return hProc.getName();
    }

    public boolean isHomeProcess(int uID) {
        return hProc.uID == uID;
    }

    public void setHomeProcess() {

        ActivityManager am = (ActivityManager)getSystemService(Activity.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> l = am.getRunningAppProcesses();
        Iterator i = l.iterator();

        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i
                    .next());

            if (info.processName.equals(hProc.getName())) {
                hProc.set(info.pid, info.uid);
            }
        }

    }

    class HomeProcess {
        private int pID;
        private int uID;
        private final String NAME = "com.lge.launcher2";

        public void set(int pID, int uID) { // TODO Auto-generated
            this.pID = pID;
            this.uID = uID;
        }

        public String getName() {
            return NAME;
        }

        public int getUID() {
            return uID;
        }

    }

}
