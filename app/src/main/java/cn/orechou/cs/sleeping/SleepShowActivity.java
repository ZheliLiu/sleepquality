package cn.orechou.cs.sleeping;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cn.orechou.cs.sleeping.Entity.SleepNode;
import cn.orechou.cs.sleeping.Entity.SleepRecord;
import cn.orechou.cs.sleeping.views.SimpleLineChart;

public class SleepShowActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = "SleepShowActivity";

    private TextView mTvSleepEfficiency, mTvLocation, mTvResult;

    private SleepRecord mSleepRecord;
    private SimpleLineChart mSimpleLineChart;
    private ArrayList<SleepNode> mSleepNodes;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_show);
        mSleepRecord = (SleepRecord) getIntent().getSerializableExtra("sleep_record");
        mSleepNodes = mSleepRecord.getSleepNodes();
        result = getIntent().getStringExtra("result");
        initView();
        initData();

    }

    private void initData() { //get 10 points from record and show on the line chart
        ArrayList<String> xItemList = new ArrayList<>();

        int length = mSleepNodes.size() < 10 ? mSleepNodes.size() : 10; // 模长
        int step = 1; // 步长
        if (mSleepNodes.size() > 10) {
            step = mSleepNodes.size() / length;
        }
        for (int i = 0; i < length; i++) {
            Date date = mSleepNodes.get(i * step).getSaveDate();
            xItemList.add(date.getHours() + ":" + date.getMinutes());
        }

        String[] xItem = new String[length]; // time
        xItemList.toArray(xItem);
        String[] yItem = {"Awake", "Sleep", "Deep"};
        if(mSimpleLineChart == null)
            Log.e("lzl","null!!!!");
        mSimpleLineChart.setXItem(xItem);
        mSimpleLineChart.setYItem(yItem);
        HashMap<Integer,Integer> pointMap = new HashMap();
        for(int i = 0;i < mSleepNodes.size();i++){
            if (mSleepNodes.get(i).getCoordinateValue() < 0.5f) {
                pointMap.put(i, 0);
            } else if (mSleepNodes.get(i).getCoordinateValue() >= 1.5f) {
                pointMap.put(i, 1);
            } else {
                pointMap.put(i, 2);
            }
        }
        mSimpleLineChart.setData(pointMap);

        mTvSleepEfficiency.setText("Sleep Efficiency: " + mSleepRecord.getSleepEfficiency() + "%");
        mTvLocation.setVisibility(View.INVISIBLE);
        mTvResult.setText("Result:" + result);
    }

    private void initView() {
        mTvSleepEfficiency = (TextView) findViewById(R.id.et_sleep_efficiency);
        mTvLocation = (TextView) findViewById(R.id.et_location);
        mTvResult = (TextView) findViewById(R.id.et_result);
        mSimpleLineChart = (SimpleLineChart) findViewById(R.id.simpleLineChart);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_sleep:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_record:

                break;
            case R.id.btn_Map:
                intent = new Intent(this, MapShowActivity.class);
                startActivity(intent);
                break;
        }
    }
}
