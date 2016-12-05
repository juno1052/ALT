package com.lge.alt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import android.app.Activity;

import android.app.AlertDialog;
import android.app.Instrumentation;
import android.app.AlertDialog.Builder;
import android.content.AsyncTaskLoader;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class PackageListActivity extends Activity {

    private static final String TAG = "ATSListActivity";

    private Intent mServiceIntent = null;
    private Button mStartButton = null;
    private Button mDefaultButton = null;
    private TextView mSelectedPackageText = null;

    private int mSelectedPackageCount = 0;

    private ArrayList<MyPackageListInfo> packageLists = null;

    private HashMap<String, Boolean> ATSPackageLists = null;
    private HashMap<String, Integer> savedPackageLists = null;
    public HashMap<String, String> package_class = null;

    MyPackageListInfoAdapter adapter = null;
    private String savedFileName = null;

    boolean isATS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.package_select);

        mServiceIntent = new Intent("com.lge.alt.service");
        mServiceIntent.setPackage("com.lge.alt");

        isATS = getIntent().getBooleanExtra("isATS", false);

        init(isATS);
        setPackageList(isATS);

        // listView
        adapter = new MyPackageListInfoAdapter(this, R.layout.item);

        ListView list;
        list = (ListView)findViewById(R.id.packageListView);
        list.setAdapter(adapter);

        // selected packages count
        mSelectedPackageText = (TextView)findViewById(R.id.selected_package);

        // button
        mStartButton = (Button)findViewById(R.id.start_button);
        mDefaultButton = (Button)findViewById(R.id.default_button);

        refreshNotFoundPackageList();

        if (ALTService.isServiceStopped()) {
            setButtonEnable(true);
            adapter.setEnable(true);
        } else {
            setButtonEnable(false);
            adapter.setEnable(false);
        }

        if (ALTService.isServiceStopped()) {
            mStartButton.setText(R.string.start);
        } else {
            mStartButton.setText(R.string.stop);
        }

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.start_button) {
                    if (ALTService.isServiceStopped()) {

                        if (mSelectedPackageCount == 0) {
                            Toast.makeText(getApplicationContext(),
                                    R.string.svc_noti_select,
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            // save monitoring package
                            ALTHelper.savePackageListFile(getFilesDir()
                                    .getAbsoluteFile(), savedFileName,
                                    packageLists);

                            mServiceIntent.putExtra("isATS", isATS);

                            startService(mServiceIntent);

                            Toast.makeText(getApplicationContext(),
                                    R.string.svc_stat_start, Toast.LENGTH_SHORT)
                                    .show();

                            mStartButton.setText(R.string.stop);
                            updateView(false);
                            moveTaskToBack(true);
                        }

                    } else {

                        stopService(mServiceIntent);
                        Toast.makeText(getApplicationContext(),
                                R.string.svc_stat_stop, Toast.LENGTH_SHORT)
                                .show();

                        mStartButton.setText(R.string.start);
                        updateView(true);

                    }
                }
            }
        });

        mDefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(
                        PackageListActivity.this)
                        .setTitle("defualtSet")
                        .setMessage("will you set default packages?")
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        // TODO Auto-generated method stub
                                        ALTHelper
                                                .deletePackageListFile(
                                                        getFilesDir()
                                                                .getAbsoluteFile(),
                                                        savedFileName);
                                        init(isATS);
                                        setPackageList(isATS);
                                        updateView(true);
                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        // TODO Auto-generated method stub

                                    }
                                }).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        menu.add(0, 0, 0, "checkAll");
        menu.add(0, 1, 0, "uncheckAll");
        // getMenuInflater().inflate(R.menu.settings_packagelist, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (ALTService.isServiceStopped()) {
            switch (item.getItemId()) {
            case 0:
                checkAll();
                break;
            case 1:
                uncheckAll();
                break;

            default:
                break;
            }
        } else {
            // toast
            Toast.makeText(getApplicationContext(), R.string.svc_stat_start,
                    Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void checkAll() {
        mSelectedPackageCount = 0;
        for (MyPackageListInfo p : packageLists) {
            if (p.packageName != null) {
                p.isChecked = true;
                mSelectedPackageCount++;
            }
        }

        updateView(true);
    }

    private void uncheckAll() {
        for (MyPackageListInfo p : packageLists) {
            p.isChecked = false;
        }
        mSelectedPackageCount = 0;
        updateView(true);
    }

    boolean isNotFoundPackage(String name) {
        boolean val = false;

        if (ATSPackageLists.containsKey(name)) {
            val = true;
        }

        return val;
    }

    void setNotFoundPackage(String name, boolean val) {
        if (ATSPackageLists.containsKey(name)) {
            ATSPackageLists.put(name, val);
        }
    }

    void refreshNotFoundPackageList() {
        Iterator<String> i = ATSPackageLists.keySet().iterator();

        mSelectedPackageText.setText("" + packageLists.size() + " ("
                + mSelectedPackageCount + ")");

    }

    void updateView(boolean val) {

        refreshNotFoundPackageList();

        setButtonEnable(val);
        adapter.setEnable(val);
        adapter.notifyDataSetChanged();
    }

    void init(boolean isATS) {
        packageLists = new ArrayList<MyPackageListInfo>();
        ATSPackageLists = new HashMap<String, Boolean>();
        savedPackageLists = new HashMap<String, Integer>();
        package_class = new HashMap<String, String>();
        mSelectedPackageCount = 0;

        if (isATS) {
            savedFileName = ALTHelper.ATSPACKAGELIST_FILENAME;
        } else {
            savedFileName = ALTHelper.PMPACKAGELIST_FILENAME;
        }
    }

    void setPackageList(boolean isATS) {

        boolean isFirst = true;

        // load from saveFile
        if (ALTHelper.isPackageListExist(getFilesDir().getAbsolutePath(),
                savedFileName)) {
            isFirst = ALTHelper.loadPackageListFromFile(getFilesDir()
                    .getAbsolutePath(), savedFileName, savedPackageLists,
                    package_class);
        }

        //
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm
                .getInstalledApplications(PackageManager.GET_META_DATA);

        if (isATS) { // ATS
            HashMap<String, Integer> ATSList = new HashMap<String, Integer>();

            // load from ATSFile
            ALTHelper.loadPackageListFromATS(getAssets(), ATSList,
                    package_class);

            // set packageList
            Iterator<String> it = ATSList.keySet().iterator();

            while (it.hasNext()) {

                String className = it.next();
                String packageName = null;
                int specTime = ATSList.get(className);

                // check notinstalledPackage
                for (ApplicationInfo packageInfo : packages) {
                    String pName = packageInfo.packageName;
                    StringTokenizer st = new StringTokenizer(pName, ".");

                    if (st.countTokens() >= 3 && className.contains(pName)) {
                        packageName = pName;
                    }

                }

                if (isFirst) {
                    if (packageName != null) {
                        packageLists.add(new MyPackageListInfo(true,
                                packageName, className, ATSList.get(className)
                                        .toString()));
                        mSelectedPackageCount++;
                    } else {
                        packageLists.add(new MyPackageListInfo(false,
                                packageName, className, ATSList.get(className)
                                        .toString()));
                    }
                } else {
                    if (packageName != null) {
                        if (savedPackageLists.containsKey(className)) {
                            packageLists.add(new MyPackageListInfo(true,
                                    packageName, className, savedPackageLists
                                            .get(className).toString()));
                            mSelectedPackageCount++;
                        } else {
                            packageLists.add(new MyPackageListInfo(false,
                                    packageName, className, ATSList.get(
                                            className).toString()));
                        }
                    } else {
                        packageLists.add(new MyPackageListInfo(false,
                                packageName, className, ATSList.get(className)
                                        .toString()));
                    }

                }

            }
        } else { // pm
            getPackageList();
        }

    }

    void setButtonEnable(boolean val) {

        // button
        // mStartButton;
        mDefaultButton.setEnabled(val);

        // list

    }

    void getPackageList() {

        PackageManager pm = getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> packages = pm.queryIntentActivities(intent,
                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);

        // find package using packageManager
        for (int i = 0; i < packages.size(); i++) {
            ResolveInfo firstInfo = packages.get(i);

            String packageName = firstInfo.activityInfo.packageName;
            if (firstInfo.activityInfo.name != null) {
                StringBuffer class_temp = new StringBuffer(
                        firstInfo.activityInfo.name);
                String className = class_temp.toString();

                Log.v("yongho", "string:" + className);

                if (savedPackageLists.containsKey(className)) {
                    packageLists.add(new MyPackageListInfo(true, packageName,
                            className, savedPackageLists.get(className)
                                    .toString()));
                    mSelectedPackageCount++;
                } else {

                    packageLists.add(new MyPackageListInfo(false, packageName,
                            className, "-1"));
                }
            }

        }

    }

    class MyPackageListInfo {
        boolean isChecked;
        // Drawable Icon;
        String packageName;
        String className;
        String limitedTime;

        public MyPackageListInfo(boolean isChecked, String packageName,
                String className, String limitedTime) {
            // TODO Auto-generated constructor stub
            this.isChecked = isChecked;
            // this.Icon = Icon;
            this.className = className;
            this.packageName = packageName;
            this.limitedTime = limitedTime;
        }
    }

    class MyPackageListInfoAdapter extends BaseAdapter {
        Context con;
        LayoutInflater inflacter;
        int layout;
        boolean enable;

        public MyPackageListInfoAdapter(Context con, int aLayout) {
            // TODO Auto-generated constructor stub
            this.con = con;
            inflacter = (LayoutInflater)con
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.layout = aLayout;
            enable = true;
        }

        public void setEnable(boolean val) {
            enable = val;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return packageLists.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return packageLists.get(position).className;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if (convertView == null) {
                convertView = inflacter.inflate(layout, parent, false);
            }

            CheckBox cBox = (CheckBox)convertView.findViewById(R.id.checkItem);
            TextView txt = (TextView)convertView.findViewById(R.id.packageName);
            EditText etx = (EditText)convertView.findViewById(R.id.limitedTime);

            if (packageLists.get(position).packageName == null) {
                cBox.setEnabled(false);
                txt.setEnabled(false);
                txt.setTextColor(Color.RED);
                etx.setEnabled(false);
            } else {
                cBox.setEnabled(enable);
                txt.setEnabled(enable);
                txt.setTextColor(Color.BLACK);
                etx.setEnabled(enable);
            }

            cBox.setTag(position);
            cBox.setChecked(packageLists.get(position).isChecked);
            cBox.setFocusable(false);
            cBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                        boolean isChecked) {
                    // TODO Auto-generated method stub

                    if (isChecked != packageLists.get((int)buttonView.getTag()).isChecked) {
                        if (isChecked) {
                            mSelectedPackageCount++;
                            setNotFoundPackage(
                                    packageLists.get((int)buttonView.getTag()).className,
                                    true);
                            refreshNotFoundPackageList();
                        } else {
                            mSelectedPackageCount--;
                            setNotFoundPackage(
                                    packageLists.get((int)buttonView.getTag()).className,
                                    false);
                            refreshNotFoundPackageList();
                        }

                        packageLists.get((int)buttonView.getTag()).isChecked = isChecked;
                    }

                }
            });

            // ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            // icon.setImageDrawable(lists.get(position).Icon);

            txt.setText(packageLists.get(position).className);

            etx.setText(packageLists.get(position).limitedTime);
            etx.setTag(position);
            etx.setOnFocusChangeListener(new OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // TODO Auto-generated method stub
                    EditText view = (EditText)v;
                    String spec = view.getText().toString();
                    
                    try {
                        Integer.parseInt(spec);
                    } catch (Exception e) {
                        // TODO: handle exception
                        view.setText("-1");
                        spec = "-1";
                    }

                    packageLists.get((int)v.getTag()).limitedTime = spec;
                }
            });

            return convertView;
        }
    }

}
