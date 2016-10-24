package cn.orechou.cs.sleeping;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import cn.orechou.cs.sleeping.Entity.SleepRecord;
import cn.orechou.cs.sleeping.utils.FileReadAndWriteUtil;
import cn.orechou.cs.sleeping.utils.PermissionUtils;

public class MapShowActivity extends AppCompatActivity
        implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String LOG_TAG = "MapShowActivity";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private boolean mPermissionDenied = false;

    private GoogleMap mMap;

    private ArrayList<Integer> mIntegerArrayList;
    private ListView mListView;
    private ItemAdapter mItemAdapter;

    private FileReadAndWriteUtil mFileReadAndWriteUtil = new FileReadAndWriteUtil();
    private ArrayList<SleepRecord> mSleepRecords = null;
    private ArrayList<LatLng> MarkerPoints = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_show);

        initData();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mListView = (ListView) findViewById(R.id.listView);
        mItemAdapter = new ItemAdapter(this, R.layout.simple_item, mIntegerArrayList);
        mListView.setAdapter(mItemAdapter);
    }

    private void initData() {
        if (!mFileReadAndWriteUtil.checkDataFileExists()) {
            mSleepRecords = new ArrayList<>();
        } else {
            mSleepRecords = mFileReadAndWriteUtil.readData();
            Log.d(LOG_TAG, mSleepRecords.size() + "");
            for (int i = 0; i < mSleepRecords.size(); i++) {
                Log.d(LOG_TAG,mSleepRecords.get(i).getLatitude() + " " +  mSleepRecords.get(i).getLongitude());
                MarkerPoints.add(new LatLng(mSleepRecords.get(i).getLatitude(), mSleepRecords.get(i).getLongitude()));
            }
        }
        for (int i = 0; i < 10; i++) { //check icon
            mIntegerArrayList = new ArrayList<>();
            mIntegerArrayList.add(R.drawable.ic_location_64_10);
            mIntegerArrayList.add(R.drawable.ic_location_64_9);
            mIntegerArrayList.add(R.drawable.ic_location_64_8);
            mIntegerArrayList.add(R.drawable.ic_location_64_7);
            mIntegerArrayList.add(R.drawable.ic_location_64_6);
            mIntegerArrayList.add(R.drawable.ic_location_64_5);
            mIntegerArrayList.add(R.drawable.ic_location_64_4);
            mIntegerArrayList.add(R.drawable.ic_location_64_3);
            mIntegerArrayList.add(R.drawable.ic_location_64_2);
            mIntegerArrayList.add(R.drawable.ic_location_64_1);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
        Log.d(LOG_TAG, MarkerPoints.size() + "MarkerPoints");
        for (int i = 0; i < MarkerPoints.size(); i++) {
            MarkerOptions options = new MarkerOptions();
            options.position(MarkerPoints.get(i));
            if (mSleepRecords.get(i).getSleepEfficiency() >= 90) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_64_1));//prepare label
            } else if (mSleepRecords.get(i).getSleepEfficiency() >= 80 ) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_64_2));
            } else if (mSleepRecords.get(i).getSleepEfficiency() >= 70 ) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_64_3));
            } else if (mSleepRecords.get(i).getSleepEfficiency() >= 60 ) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_64_4));
            } else if (mSleepRecords.get(i).getSleepEfficiency() >= 50 ) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_64_5));
            } else if (mSleepRecords.get(i).getSleepEfficiency() >= 40 ) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_64_6));
            } else if (mSleepRecords.get(i).getSleepEfficiency() >= 30 ) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_64_7));
            } else if (mSleepRecords.get(i).getSleepEfficiency() >= 20 ) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_64_8));
            } else if (mSleepRecords.get(i).getSleepEfficiency() >= 20 ) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_64_9));
            } else {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_64_10));
            }
            mMap.addMarker(options);//add label
        }
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    public class ItemAdapter extends ArrayAdapter<Integer> {

        ArrayList<Integer> mIntegers;
        int resourceId;
        LayoutInflater mInflater;

        public ItemAdapter(Context context, int resource, ArrayList<Integer> objects) { //link with data
            super(context, resource, objects);
            mInflater = LayoutInflater.from(context);
            resourceId = resource;
            mIntegers = objects;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(resourceId, null);
                holder.mImageView = (ImageView) convertView.findViewById(R.id.iv);
                holder.mTextView = (TextView) convertView.findViewById(R.id.tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();//fresh and show
                holder.mImageView.setImageResource(mIntegers.get(position));
                holder.mTextView.setText("" + position * 10 + "~" + ((position + 1) * 10 - 1));
            }
            return convertView;
        }

        public class ViewHolder {
            TextView mTextView;
            ImageView mImageView;
        }
    }

}
