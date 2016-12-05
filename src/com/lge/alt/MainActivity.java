package com.lge.alt;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.AbstractThreadedSyncAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String TAG = "ALTMainActivity";

    private Button mATSButton = null;
    private Button mManualButton = null;
    private Button mReportButton = null;
    private Button mClearButton = null;
    private TextView mVersionText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d(TAG, "Main onCreate");

        setContentView(R.layout.activity_main);

        mVersionText = (TextView)findViewById(R.id.app_version);
        try {
            mVersionText
                    .setText(" v"
                            + getPackageManager().getPackageInfo(
                                    getPackageName(), 0).versionName);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // button
        mATSButton = (Button)findViewById(R.id.ats_button);
        mManualButton = (Button)findViewById(R.id.manual_button);
        mReportButton = (Button)findViewById(R.id.report_button);
        mClearButton = (Button)findViewById(R.id.clear_button);

        mATSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,
                        PackageListActivity.class);
                intent.putExtra("isATS", true);
                startActivity(intent);
            }
        });

        mManualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,
                        PackageListActivity.class);
                intent.putExtra("isATS", false);
                startActivity(intent);
            }
        });

        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();

                intent.setAction(Intent.ACTION_VIEW);
                intent.setClassName("com.android.chrome",
                        "com.google.android.apps.chrome.Main");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                File dir = getExternalFilesDirs(null)[0];
                String basePath = dir.getAbsolutePath() + ALTHelper.SAVEFOLDER;
                intent.setData(Uri.fromFile(new File(basePath)));
                startActivity(intent);
            }
        });

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("ClearSavedData")
                        .setMessage("will you delete all saved report files?")
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        // TODO Auto-generated method stub
                                        ALTHelper
                                                .removeAllSavedDatas(getExternalFilesDirs(null)[0]
                                                        .getAbsolutePath()
                                                        + ALTHelper.SAVEFOLDER);
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
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        setButtonEnable();
    }

    void setButtonEnable() {

        if (ALTService.isServiceStopped()) {
            mManualButton.setEnabled(true);
            mATSButton.setEnabled(true);
            mReportButton.setEnabled(true);
            mClearButton.setEnabled(true);
        } else {
            if (ALTService.isATS()) {
                mManualButton.setEnabled(false);
            } else {
                mATSButton.setEnabled(false);
            }
            mReportButton.setEnabled(false);
            mClearButton.setEnabled(false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "Main onDestroy");
    }

}
