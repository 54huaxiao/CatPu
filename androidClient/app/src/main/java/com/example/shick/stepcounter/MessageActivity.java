package com.example.shick.stepcounter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

import java.security.Provider;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent on 2016/12/11.
 */

public class MessageActivity extends Activity {

    public static final String STATICACTION = "com.example.shick.stepcounter.MyBroadcast";
    private Map_DB map_database;
    private TextureMapView mMapView2;
    private Button back_btn;
    private TextView distance;
    private TextView time;
    private TextView velocity;
    private TextView calorie;
    private static final double step_distance = 0.8;
    // Sensor Manager
    private SensorManager mSensorManager = null;
    private Sensor mMagneticSensor = null;
    private Sensor mAccelerometerSensor = null;

    // location manager
    private LocationManager mLocationManager;
    private Location currentLocation = null;
    private String providerName = null;

    private LatLng startLocation = null;
    private LatLng endLocation = null;

    private CoordinateConverter mConverter = null;
    private float rotateDegree = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.message_layout);

        map_database = new Map_DB(this, "MapDB", null, 1);

        mMapView2 = (TextureMapView) findViewById(R.id.bmapView2);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mConverter = new CoordinateConverter();
        mConverter.from(CoordinateConverter.CoordType.GPS);

        providerName = getProviderName();
        if (providerName == null) {
            providerName = LocationManager.GPS_PROVIDER;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            currentLocation = mLocationManager.getLastKnownLocation(providerName);
        }
        if (currentLocation == null && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            providerName = LocationManager.NETWORK_PROVIDER;
            currentLocation = mLocationManager.getLastKnownLocation(providerName);
        }

        Location location=new Location(LocationManager.GPS_PROVIDER);
        if (isBetterLocation(location, currentLocation)) {
            updateLocation(location, rotateDegree);
            providerName=location.getProvider();
            currentLocation = location;
        } else {
            updateLocation(currentLocation, rotateDegree);
            providerName=currentLocation.getProvider();
        }

        int a = getIntent().getIntExtra("orderr", 0);
        List<LatLng> guiji = map_database.selectdb(a);

        if (guiji.size() >= 2) {
            startLocation = guiji.get(0);
            endLocation = guiji.get(guiji.size() - 1);
            // draw lines
            int i = 0;
            List<LatLng> temp1 = new ArrayList<LatLng>();
            LatLng pause = new LatLng(0,0);
            while (i < guiji.size()) {
                if (guiji.get(i).toString().equals(pause.toString())&&temp1.size() >= 2) {
                    OverlayOptions ooPolyline = new PolylineOptions().width(5)
                            .color(Color.RED).points(temp1);
                    mMapView2.getMap().addOverlay(ooPolyline);
                    temp1.clear();
                    i++;
                    continue;
                }
                if (guiji.get(i).toString().equals(pause.toString())) {
                    temp1.clear();
                    i++;
                    continue;
                }
                if (i == guiji.size()-1 && temp1.size() >= 2) {
                    OverlayOptions ooPolyline = new PolylineOptions().width(5)
                            .color(Color.RED).points(temp1);
                    mMapView2.getMap().addOverlay(ooPolyline);
                    temp1.clear();
                    i++;
                    continue;
                }
                temp1.add(guiji.get(i));
                i++;
            }
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocation = convertLocationToLatLng(mLocationManager.getLastKnownLocation(providerName));
            endLocation = startLocation;
        }

        // mark start icon
        mMapView2.getMap().addOverlay(new MarkerOptions().position(startLocation)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.start)));
        // mark end icon
        mMapView2.getMap().addOverlay(new MarkerOptions().position(endLocation)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.end)));

        Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.pointer), 100, 100, true);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        mMapView2.getMap().setMyLocationEnabled(true);
        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, bitmapDescriptor);
        mMapView2.getMap().setMyLocationConfigeration(config);

        LatLng centerLocation = new LatLng((startLocation.latitude + endLocation.latitude) / 2,
                (startLocation.longitude + endLocation.longitude) / 2);
        MapStatus mMapStatus = new MapStatus.Builder().target(centerLocation).zoom(19).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mMapView2.getMap().setMapStatus(mMapStatusUpdate);
        mMapView2.showZoomControls(false);
        back_btn = (Button) findViewById(R.id.return_back);

        // 测量数据转化控件
        distance = (TextView) findViewById(R.id.distance_);
        time = (TextView) findViewById(R.id.time);
        velocity = (TextView) findViewById(R.id.velocity);
        calorie = (TextView) findViewById(R.id.calorie);

        String time_ = getIntent().getStringExtra("time");
        String []temp = time_.split(":");
        int mins = (Integer.parseInt(temp[0])) * 3600
                + Integer.parseInt(temp[1]) * 60
                + (Integer.parseInt(temp[2]));
        String step_ = getIntent().getStringExtra("step");

        // 以60公斤1m7身高的正常人，通过步数测量距离
        double distance_ = Integer.parseInt(step_) * step_distance / 1000;
        time.setText(time_);

        // 测量数据保持两位小数的形式显示
        velocity.setText(String.format("%.2f", mins/distance_/60));
        distance.setText(String.format("%.2f", distance_));
        calorie.setText(String.format("%.2f", Math.ceil(distance_*0.06)));
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageActivity.this.finish();
            }
        });

        // Static Broadcast
        Intent intent = new Intent(STATICACTION);
        Bundle bundle = new Bundle();
        bundle.putString("step", String.valueOf(step_));
        bundle.putString("time", time_);
        bundle.putString("calorie", String.format("%.2f", Math.ceil(distance_*0.06)));
        intent.putExtras(bundle);
    }



    @Override
    protected void onDestroy() {
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView2.onDestroy();
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView2.onResume();
        // register magnetic and accelerometer sensor into sensor manager (onResume
        mSensorManager.registerListener(mSensorEventListener, mMagneticSensor,
                SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(mSensorEventListener, mAccelerometerSensor,
                SensorManager.SENSOR_DELAY_GAME);
        // register location update listener
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(providerName, 0, 0, mLocationListener);
        }
        super.onResume();
    }


    @Override
    protected void onPause() {
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView2.onPause();
        // unregister sensors (onPause
        mSensorManager.unregisterListener(mSensorEventListener);
        // unregister update listener
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.removeUpdates(mLocationListener);
        }
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }return super.onKeyDown(keyCode, event);
    }

    // 获取 Location Provider
    private String getProviderName() {
        // 构建位置查询条件
        Criteria criteria = new Criteria();
        // 查询精度：高
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 是否查询海拨：否
        criteria.setAltitudeRequired(false);
        // 是否查询方位角 : 否
        criteria.setBearingRequired(false);
        // 是否允许付费：是
        criteria.setCostAllowed(true);
        // 电量要求：低
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        // 返回最合适的符合条件的 provider ，第 2 个参数为 true 说明 , 如果只有一个 provider 是有效的 , 则返回当前
        // provider
        return mLocationManager.getBestProvider(criteria, true);
    }

    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        float[] accValues = null;
        float[] magValues = null;
        long lastShakeTime = 0;
        @Override
        public void onSensorChanged(SensorEvent event) {
            float [] R = new float[9];
            float [] values = new float[3];
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    accValues = event.values;
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    magValues = event.values;
                    break;
                default:
                    break;
            }
            if (accValues != null && magValues != null) {
                SensorManager.getRotationMatrix(R, null, accValues, magValues);
                SensorManager.getOrientation(R, values);
                rotateDegree = (float) Math.toDegrees(values[0]);
                updateDirection(rotateDegree);
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    private LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateLocation(location, rotateDegree);
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };

    public void updateLocation(Location location, float degree) {
        if (location != null) {
            currentLocation = location;
            MyLocationData.Builder data = new MyLocationData.Builder();
            LatLng desLatLng = convertLocationToLatLng(location);
            data.latitude(desLatLng.latitude);
            data.longitude(desLatLng.longitude);
            data.direction(degree);
            mMapView2.getMap().setMyLocationData(data.build());
        }
    }

    public void updateDirection(float degree) {
        MyLocationData.Builder data = new MyLocationData.Builder();
        LatLng desLatLng = convertLocationToLatLng(currentLocation);
        data.latitude(desLatLng.latitude);
        data.longitude(desLatLng.longitude);
        data.direction(degree);
        mMapView2.getMap().setMyLocationData(data.build());
    }

    LatLng convertLocationToLatLng(Location location) {
        mConverter.from(CoordinateConverter.CoordType.GPS);
        mConverter.coord(new LatLng(location.getLatitude(), location.getLongitude()));
        return mConverter.convert();
    }



    private static final int TWO_MINUTES = 1000 * 60 * 2;

    protected boolean isBetterLocation(Location location,
                                       Location currentBestLocation) {
        if (currentBestLocation == null) {
            return true;
        }

        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;


        if (isSignificantlyNewer) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        }

        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
                .getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate
                && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}