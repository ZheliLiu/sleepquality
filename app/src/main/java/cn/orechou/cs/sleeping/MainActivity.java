package cn.orechou.cs.sleeping;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Date;

import cn.orechou.cs.sleeping.Entity.SleepNode;
import cn.orechou.cs.sleeping.Entity.SleepRecord;
import cn.orechou.cs.sleeping.utils.FileReadAndWriteUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String LOG_TAG = "MainActivity";

    private boolean isRecording = false;
    private Button mSwitchBtn, mGoodBtn, mBadBtn;
    private TextView mTextView;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private GoogleApiClient mGoogleApiClient;

    private Date[] dateRecord = new Date[2];
    private ArrayList<SleepNode> mSleepNodes = new ArrayList<>();
    private ArrayList<SleepRecord> mSleepRecords = null;
    private FileReadAndWriteUtil mFileReadAndWriteUtil = new FileReadAndWriteUtil();
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        initData();
        initSensor();
        initView();
        initLocation();
    }

    private void initLocation() {
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onResume() {
        mSleepRecords = new ArrayList<>();
        mSleepNodes = new ArrayList<>();
        super.onResume();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void initData() {
        if (!mFileReadAndWriteUtil.checkDataFileExists()) {
            mSleepRecords = new ArrayList<>();
        } else {
            mSleepRecords = mFileReadAndWriteUtil.readData();
            Log.d(LOG_TAG, mSleepRecords.size() + "");
        }
    }

    private void initSensor() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(mSensorEventListener, mSensor, 1000000); // 1 秒钟记录一次
    }

    private void initView() {
        mSwitchBtn = (Button) findViewById(R.id.btn_switch);
        mGoodBtn = (Button) findViewById(R.id.btn_good);
        mBadBtn = (Button) findViewById(R.id.btn_bad);
        mTextView = (TextView) findViewById(R.id.tv_text);
        mGoodBtn.setVisibility(View.INVISIBLE);
        mBadBtn.setVisibility(View.INVISIBLE);
        mTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        SleepRecord sleepRecord = null;
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_switch:
                if (!isRecording) {
                    isRecording = true; // 开始记录
                    dateRecord[0] = new Date();
                    mSwitchBtn.setText("Stop");
                } else { // 结束记录
                    dateRecord[1] = new Date();
                    isRecording = false;
                    long time = dateRecord[1].getHours() * 60 + dateRecord[1].getMinutes() - dateRecord[0].getHours() * 60 - dateRecord[0].getMinutes();
                    Log.d("MainActivity", dateRecord[1].getTime() + " Time " + time);
                    if (time < 10) {
                        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Time recording less than ten minutes, are you sure go ahead?")
                                .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mGoodBtn.setVisibility(View.VISIBLE);
                                        mBadBtn.setVisibility(View.VISIBLE);
                                        mTextView.setVisibility(View.VISIBLE);
                                        mSwitchBtn.setVisibility(View.INVISIBLE);
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mSwitchBtn.setVisibility(View.VISIBLE);
                                        mSwitchBtn.setText("Start");
                                        isRecording = false;
                                        dialog.dismiss();
                                    }
                                }).create();
                        dialog.show();
                    } else {
                        mGoodBtn.setVisibility(View.VISIBLE);
                        mBadBtn.setVisibility(View.VISIBLE);
                        mTextView.setVisibility(View.VISIBLE);
                        mSwitchBtn.setVisibility(View.INVISIBLE);
                    }
                }
                break;
            case R.id.btn_good:
                intent = new Intent(this, SleepShowActivity.class);
                sleepRecord = new SleepRecord(mSleepNodes, true, mLastLocation.getLatitude(), mLastLocation.getLongitude());

                float sleepEfficiency = getMSleepEfficiency();
                if (sleepEfficiency < 60) {
                    sleepEfficiency = 60;
                    intent.putExtra("result", "More action");
                } else {
                    intent.putExtra("result", "Good Sleep");
                }

                sleepRecord.setSleepEfficiency(sleepEfficiency);

                mSleepRecords.add(sleepRecord);
                mFileReadAndWriteUtil.writeData(mSleepRecords);
                intent.putExtra("sleep_record", sleepRecord);

                startActivity(intent);
                finish();
                break;
            case R.id.btn_bad:
                intent = new Intent(this, SleepShowActivity.class);
                sleepRecord = new SleepRecord(mSleepNodes, true, mLastLocation.getLatitude(), mLastLocation.getLongitude());

                float sleepEfficiency2 = getMSleepEfficiency();
                if (sleepEfficiency2 > 60) {
                    sleepEfficiency2 = 50;
                    intent.putExtra("result", "Light sleep");
                } else {
                    intent.putExtra("result", "Bad Sleep");
                }

                sleepRecord.setSleepEfficiency(sleepEfficiency2);

                mSleepRecords.add(sleepRecord);
                mFileReadAndWriteUtil.writeData(mSleepRecords);
                intent.putExtra("sleep_record", sleepRecord);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }

    // 随眠效率的计算
    private float getMSleepEfficiency() {
        int length = mSleepNodes.size() < 100 ? mSleepNodes.size() : 100; // 模长
        float mSleepEfficiency = 100;
        int step = 1; // 步长
        if (mSleepNodes.size() > 100) {
            step = mSleepNodes.size() / length;
        }
        for (int i = 0; i < length; i++) {
            if (mSleepNodes.get(i * step).getCoordinateValue() > 1.5f) {
                mSleepEfficiency -= 1;
            }
        }
        return mSleepEfficiency;
    }


    SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (isRecording) {
                float x, y, z;
                x = event.values[0] > 0 ? event.values[0] : (0 - event.values[0]);
                y = event.values[1] > 0 ? event.values[1] : (0 - event.values[1]);
                z = event.values[2] > 0 ? event.values[2] : (0 - event.values[2]);
                float coordinateValue = x + y + z - 9.8f;
                coordinateValue = coordinateValue > 0 ? coordinateValue : (0 - coordinateValue);
                Log.d("coordinateValue", coordinateValue + "");
                SleepNode sleepNode = new SleepNode(coordinateValue, new Date());
                if (isRecording) {}
                mSleepNodes.add(sleepNode);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
             Toast.makeText(this, String.valueOf(mLastLocation.getLatitude()) + " " + String.valueOf(mLastLocation.getLongitude()), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
