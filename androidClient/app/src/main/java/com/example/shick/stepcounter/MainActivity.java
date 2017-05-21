package com.example.shick.stepcounter;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
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
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.shick.stepcounter.TimeRecorder.STATE_START;
import static com.example.shick.stepcounter.TimeRecorder.STATE_PAUSE;
import static com.example.shick.stepcounter.TimeRecorder.STATE_STOP;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // Views
    private TextureMapView mMapView = null;
    private ToggleButton mToggleButton = null;
    private TextView mTextViewTimer = null;
    private TextView mTextViewToday = null;
    private TextView mTextViewStep = null;
    private Button mButtonStartAndPause = null;
    private Button mButtonStop = null;
    private ImageView mImageViewMusic = null;
    private ImageView mImageViewSearch = null;
    private ImageView mImageViewDB = null;
    private TextView mTextViewMusic = null;

    // sensor manager and sensors
    private SensorManager mSensorManager = null;
    private Sensor mMagneticSensor = null;
    private Sensor mAccelerometerSensor = null;
    private Sensor mStepCounter = null;
    // location manager and location
    private LocationManager mLocationManager = null;
    private Location currentLocation = null;

    private CoordinateConverter mConverter = null;

    private float rotateDegree = 0;

    // music player
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private MusicService mMusicService;
    private ArrayList<String> musicList = new ArrayList<>();



    // store date format
    SimpleDateFormat sDateFormat;
    String date;


    // store?
    List<LatLng> polylines = new ArrayList<LatLng>();
    private int order;
    private SharedPreferences preferences;

    private SharedPreferences.Editor editor;
    // store cordinate changes
    private Overlay startOverlay = null;
    private Overlay endOverlay = null;

    private ArrayList<Overlay> pathOverlay = new ArrayList<>();
    //DB
    private Run_DB database;
    private Map_DB map_database;

    private StepRecorder stepRecorder = null;

    private TimeRecorder timeRecorder = null;

    // thread
    private Thread clockThread;


    final Handler handler_ = new Handler();
    final Runnable updateThread = new Runnable() {
        @Override
        public void run() {
            if (mMediaPlayer != null) {
                handler_.postDelayed(updateThread, 1000);
            }
        }
    };

    public void updateTop() {
        mTextViewToday = (TextView)findViewById(R.id.today);
        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = DateFormat.format(new java.util.Date());
        mTextViewToday.setText(today);
        int times = 0;
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from RunTable", null);
        while(cursor.moveToNext()){
            times++;
        }
        TextView total_times = (TextView)findViewById(R.id.ts);
        total_times.setText(times+"");
        db.close();
    }

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMusicService = ((MusicService.MyBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            unbindService(sc);
            try {
                MainActivity.this.finish();
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Handler handlerUpdateDB = new Handler();
    Runnable runnableUpdateDB = new Runnable() {
        @Override
        public void run() {
            final Button B = (Button) findViewById(R.id.startAndPause);
            if (B.getText().toString().equals("暂停")) {
                if (polylines.size() == 0) {
                        LatLng desLatLng = convertLocationToLatLng(currentLocation);
                    polylines.add(desLatLng);
                }
                List<LatLng> temp = new ArrayList<LatLng>();
                temp.add(polylines.get(polylines.size()-1));
                LatLng desLatLng = convertLocationToLatLng(currentLocation);
                temp.add(desLatLng);
                map_database.insertdb(order, polylines.get(polylines.size()-1).latitude, polylines.get(polylines.size()-1).longitude);

                polylines.add(desLatLng);
                OverlayOptions ooPolyline = new PolylineOptions().width(5)
                        .color(Color.RED).points(temp);
                Overlay point = mMapView.getMap().addOverlay(ooPolyline);
                pathOverlay.add(point);
            } else {
                polylines.clear();
            }
            if (mToggleButton.isChecked()) {
                centerCurrentLocationOnScreen();
            }
            handlerUpdateDB.postDelayed(this, 250);
        }
    };

    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case STATE_START:
                    timeRecorder.setTime(timeRecorder.getTime() + 1);
                    break;
                case STATE_STOP:
                    timeRecorder.reset();
                    stepRecorder.reset();
                    break;
            }
            mTextViewTimer.setText(timeRecorder.getFormattedTime());
            mTextViewStep.setText(String.valueOf(stepRecorder.getStepCount()));
            super.handleMessage(message);
        }
    };
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (true) {

                if (timeRecorder.isStart()) {
                    try {
                        Thread.sleep(10);
                        Message message = new Message();
                        message.what = STATE_START;
                        handler.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (timeRecorder.isStop()) {
                    Message message = new Message();
                    message.what = STATE_STOP;
                    handler.sendMessage(message);
                    break;
                }
            }
        }
    };

    private void initViews() {
        mMapView = (TextureMapView) findViewById(R.id.bmapView);
        mToggleButton = (ToggleButton) findViewById(R.id.mapToggleButton);
        mTextViewTimer = (TextView) findViewById(R.id.chronometer);
        mTextViewStep = (TextView) findViewById(R.id.step);
        mButtonStartAndPause = (Button) findViewById(R.id.startAndPause);
        mButtonStop = (Button) findViewById(R.id.stop);
        mImageViewMusic = (ImageView) findViewById(R.id.music_);
        mImageViewDB = (ImageView) findViewById(R.id.database_);
        mImageViewSearch = (ImageView) findViewById(R.id.search_);
        mTextViewMusic = (TextView) findViewById(R.id.music);

        Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.pointer), 100, 100, true);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        mMapView.getMap().setMyLocationEnabled(true);
        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, bitmapDescriptor);
        mMapView.getMap().setMyLocationConfigeration(config);
        // cancel the zoom button
        mMapView.showZoomControls(false);
    }

    private void initUtils() {
        stepRecorder = new StepRecorder();
        timeRecorder = new TimeRecorder();
    }

    private void initDB() {
        database = new Run_DB(this, "RunDB", null, 1);
        map_database = new Map_DB(this, "MapDB", null, 1);
        updateTop();
        handlerUpdateDB.postDelayed(runnableUpdateDB, 250);

        preferences = getSharedPreferences("demo", Context.MODE_PRIVATE);
        order = preferences.getInt("order", 0);
    }

    private void initManagersAndSensors() {
        // init sensor manager
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mConverter = new CoordinateConverter();
        mConverter.from(CoordinateConverter.CoordType.GPS);
    }

    private void initCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 确保Provider可以使用
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    || mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                Toast.makeText(this, "Location Provider Enabled.", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                startActivityForResult(intent, 0);
            }
            Location gpsLocation = new Location(LocationManager.GPS_PROVIDER);
            Location networkLocation = new Location(LocationManager.GPS_PROVIDER);
            currentLocation = isBetterLocation(gpsLocation, networkLocation) ? gpsLocation : networkLocation;
            updateLocationOnMap();
            centerCurrentLocationOnScreen();
        }
    }

    private void setListeners() {
        mMapView.getMap().setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        mToggleButton.setChecked(false);
                        break;
                    default:
                        break;
                }
            }
        });

        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    centerCurrentLocationOnScreen();
                }
            }
        });

        // 调用github中安卓开源库进行文件管理器的打开
        mImageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialFilePicker()
                        .withActivity(MainActivity.this)
                        .withRequestCode(1)
                        .withHiddenFiles(true) // Show hidden files and folders
                        .start();
            }
        });

        //跳转界面，从主界面跳转到历史记录界面
        mImageViewDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DB_Activity.class);
                startActivity(intent);
            }
        });

        // 音乐播放，暂停按钮
        mImageViewMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mMediaPlayer.isPlaying()) {
                    mMediaPlayer.start();
                    mTextViewMusic.setText("暂停音乐");
                    handler_.post(updateThread);
                } else {
                    mMediaPlayer.pause();
                    mTextViewMusic.setText("播放音乐");
                    handler_.removeCallbacks(updateThread);
                }
            }
        });

        //监听音频播放完的代码，实现音频的自动循环播放
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                try {
                    mMediaPlayer.reset();
                    int temp = (int) (Math.random() * 7);
                    AssetFileDescriptor media = getAssets().openFd(musicList.get(temp%7));
                    mMediaPlayer.setDataSource(media.getFileDescriptor(), media.getStartOffset(), media.getLength());
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                    handler_.post(updateThread);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mButtonStartAndPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTop();
                if (timeRecorder.isStop()) {
                    mToggleButton.setChecked(true);
                    // init startIcon
                    LatLng limit = convertLocationToLatLng(currentLocation);
                    startOverlay = mMapView.getMap().addOverlay(new MarkerOptions().position(limit)
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.start)));
                    // clear path
                    for (Overlay overlay : pathOverlay) {
                        overlay.remove();
                    }
                    pathOverlay.clear();
                    stepRecorder.updateStepStamp(STATE_STOP);
                    if (clockThread == null) {
                        sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        date = sDateFormat.format(new java.util.Date());
                        clockThread = new Thread(runnable);
                        clockThread.start();
                        //start time
                    }
                    timeRecorder.start();
                    mButtonStartAndPause.setText("暂停");
                } else if (timeRecorder.isStart()) {
                    if (clockThread == null) {
                        clockThread = new Thread(runnable);
                    }
                    timeRecorder.pause();
                    mButtonStartAndPause.setText("开始");
                    LatLng desLatLng1 = new LatLng(0, 0);
                    map_database.insertdb(order, desLatLng1.latitude, desLatLng1.longitude);
                } else if (timeRecorder.isPause()) {
                    stepRecorder.updateStepStamp(STATE_PAUSE);
                    if (clockThread == null) {
                        clockThread = new Thread(runnable);
                    }
                    timeRecorder.start();
                    mButtonStartAndPause.setText("暂停");
                }
            }
        });

        mButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!timeRecorder.isStop()) {
                    timeRecorder.stop();
                    // set end Icon
                    LatLng limit = convertLocationToLatLng(currentLocation);
                    endOverlay = mMapView.getMap().addOverlay(new MarkerOptions().position(limit)
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.end)));
                    if (clockThread != null) {
                        clockThread = null;
                    }
                    mButtonStartAndPause.setText("开始");

                    order++;
                    preferences=getSharedPreferences("demo", Context.MODE_PRIVATE);
                    editor=preferences.edit();
                    editor.putInt("order", order);
                    editor.commit();
                    // delete the thread
                    if (!database.selectDB(date)) {
                        database.insertDB(date,mTextViewTimer.getText().toString(), String.valueOf(stepRecorder.getStepCount()), order-1+"");
                        updateTop();
                    }
                    // press the stop button, save the data in the database and start a new Activity
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, MessageActivity.class);
                    intent.putExtra("time", mTextViewTimer.getText().toString());
                    intent.putExtra("step", String.valueOf(stepRecorder.getStepCount()));
                    intent.putExtra("orderr", order-1);
                    startActivity(intent);
                }

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        initUtils();
        initViews();
        initDB();
        initManagersAndSensors();
        initCurrentLocation();
        setListeners();

        mTextViewTimer.setText(timeRecorder.getFormattedTime());
        mTextViewStep.setText(String.valueOf(stepRecorder.getStepCount()));

        initMediaPlayer();
    }

    public void setTime(long time) {
        long hour = time/360000;
        long minute = (time - hour* 360000)/ 6000;
        long second = (time - hour * 360000- minute*6000) / 100;
        String h = hour < 10 ? "0" + String.valueOf(hour) : String.valueOf(hour);
        String m = minute < 10 ? "0" + String.valueOf(minute) : String.valueOf(minute);
        String s = second < 10 ? "0" + String.valueOf(second) : String.valueOf(second);
        mTextViewTimer.setText(h + ":" + m + ":" + s);
    }

    private void setStep(long step) {
        mTextViewStep.setText(String.valueOf(step));
    }

    @Override
    protected void onStart() {
        if (timeRecorder.isStop()) {
            // delete existing overlay
            mToggleButton.setChecked(false);
            if (startOverlay != null) {
                startOverlay.remove();
            }
            if (endOverlay != null) {
                endOverlay.remove();
            }
            if (pathOverlay != null) {
                // clear path
                for (Overlay overlay : pathOverlay) {
                    overlay.remove();
                }
                pathOverlay.clear();
            }
        }
        super.onStart();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i("test accuracy", sensor.getName() + " sensor is activated. the Accuracy is " + accuracy);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        stepRecorder.updateActualStep((long) event.values[0]);
        if (timeRecorder.isStart()) {
            stepRecorder.updateStepCount();
        }
    }

    // 打开文件管理器后选择音乐文件加载进入mediaPlayer
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 文件存在并且成功选择
        if (requestCode == 1 && resultCode == RESULT_OK) {
            try {
                String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show();
                // Do anything with file
                if (mMediaPlayer.isPlaying()) {
                    mTextViewMusic.setText("播放音乐");
                    mMediaPlayer.reset();
                    mMediaPlayer.setDataSource(filePath);
                    mMediaPlayer.prepare();
                } else {
                    mMediaPlayer.reset();
                    mMediaPlayer.setDataSource(filePath);
                    mMediaPlayer.prepare();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 当不选择文件管理器中的音乐文件时，软件内置7首音乐文件，支持循环播放
    private void initMediaPlayer() {
        try {
            for (int i = 0; i < 7; i++) {
                musicList.add("music_" + i + ".mp3");
            }
            int temp = (int) (Math.random() * 7);
            AssetFileDescriptor media = getAssets().openFd(musicList.get(temp%7));
            mMediaPlayer.setDataSource(media.getFileDescriptor(), media.getStartOffset(), media.getLength());
            mMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //下面都是地图部分
    public void centerCurrentLocationOnScreen() {
        LatLng desLatLng = convertLocationToLatLng(currentLocation);
        MapStatus mMapStatus = new MapStatus.Builder().target(desLatLng).zoom(19).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mMapView.getMap().setMapStatus(mMapStatusUpdate);
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

    @Override
    protected void onDestroy() {
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        // register magnetic and accelerometer sensor into sensor manager (onResume
        mSensorManager.registerListener(mSensorEventListener, mMagneticSensor,
                SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(mSensorEventListener, mAccelerometerSensor,
                SensorManager.SENSOR_DELAY_GAME);
        // register location update listener
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(getProviderName(), 0, 0, mLocationListener);
        }
        super.onResume();
        if (mStepCounter != null) {
            mSensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "StepCountSensor not available!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
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

    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        float[] accValues = null;
        float[] magValues = null;
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
                updateLocationOnMap();
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    private LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            currentLocation = location;
            updateLocationOnMap();
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {
            currentLocation = null;
            updateLocationOnMap();
        }
    };

    public void updateLocationOnMap() {
        MyLocationData.Builder data = new MyLocationData.Builder();
        LatLng desLatLng = convertLocationToLatLng(currentLocation);
        data.latitude(desLatLng.latitude);
        data.longitude(desLatLng.longitude);
        data.direction(rotateDegree);
        mMapView.getMap().setMyLocationData(data.build());
    }


    LatLng convertLocationToLatLng(Location location) {
        mConverter.from(CoordinateConverter.CoordType.GPS);
        mConverter.coord(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
        return mConverter.convert();
    }

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    protected boolean isBetterLocation(Location location,
                                       Location currentBestLocation) {
        if (currentBestLocation == null && location != null) {
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


