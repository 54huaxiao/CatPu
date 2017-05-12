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
import android.graphics.Canvas;
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
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.Settings;
import android.renderscript.Script;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ZoomControls;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    //map
    private TextureMapView mMapView;
    private ToggleButton mToggleButton;
    // sensor manager and sensors
    private SensorManager mSensorManager = null;
    private Sensor mMagneticSensor = null;
    private Sensor mAccelerometerSensor = null;
    // location manager
    private LocationManager mLocationManager;
    private Location currentLocation = null;
    private String providerName = null;

    private CoordinateConverter mConverter = null;
    private float rotateDegree = 0;

    List<LatLng> polylines = new ArrayList<LatLng>();
    private int order;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private Overlay startOverlay = null;
    private Overlay endOverlay = null;
    private ArrayList<Overlay> pathOverlay = new ArrayList<>();

    //DB
    private Run_DB database;
    private Map_DB map_database;
    private List<Run> runlist = new ArrayList<Run>();
    SimpleDateFormat sDateFormat;
    String date;
    // UI Views
    private TextView timer, todaydate;
    private TextView textViewStep;
    private Button buttonStartAndPause;
    private Button buttonStop;

    // private variables to store time and step count
    private long currentTime;
    private long stepCount;
    private long actualStep;
    private long stepStamp;

    // to store the state of the timer
    private int chronometerState;

    // thread
    private Thread clockThread;

    // sensor manager and sensor
    private SensorManager sensorManager;
    private Sensor stepCounter;

    // music player
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private MusicService ms;
    private ArrayList<String> musicList = new ArrayList<>();
    private int musicNum = 0;

    private ImageView music_;
    private ImageView search_;
    private ImageView DB_;
    private TextView music;


    private static final int STATE_START = 0;
    private static final int STATE_PAUSE = 1;
    private static final int STATE_STOP = 2;


    final Handler handler_ = new Handler();
    final Runnable updateThread = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null) {
                handler_.postDelayed(updateThread, 1000);
            }
        }
    };

    public void updateTop() {
        todaydate = (TextView)findViewById(R.id.today);
        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = DateFormat.format(new java.util.Date());
        todaydate.setText(today);
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
            ms = ((MusicService.MyBinder)service).getService();
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

    Handler handler1 = new Handler();
    Runnable runnable1 = new Runnable() {
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
            handler1.postDelayed(this, 250);
        }
    };

    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case STATE_START:
                    currentTime++;
                    break;
                case STATE_STOP:
                    currentTime = 0;
                    stepCount = 0;
                    stepStamp = 0;
                    break;
            }
            setTime(currentTime);
            setStep(stepCount);
            super.handleMessage(message);
        }
    };
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (chronometerState == STATE_START) {
                    try {
                        Thread.sleep(10);
                        Message message = new Message();
                        message.what = STATE_START;
                        handler.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (chronometerState == STATE_STOP) {
                    Message message = new Message();
                    message.what = STATE_STOP;
                    handler.sendMessage(message);
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        database = new Run_DB(this, "RunDB", null, 1);
        map_database = new Map_DB(this, "MapDB", null, 1);
        updateTop();
        handler1.postDelayed(runnable1, 250);

        preferences = getSharedPreferences("demo", Context.MODE_PRIVATE);
        order = preferences.getInt("order", 0);

        mMapView = (TextureMapView) findViewById(R.id.bmapView);
        mToggleButton = (ToggleButton) findViewById(R.id.mapToggleButton);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mConverter = new CoordinateConverter();
        mConverter.from(CoordinateConverter.CoordType.GPS);

        // 确保Provider可以使用
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(this, "Location Provider Enabled.", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            startActivityForResult(intent, 0);
        }

        providerName = getProviderName();
        //如果没有则设置为GPS
        if (providerName == null) {
            providerName = LocationManager.GPS_PROVIDER;
        }

        Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.pointer), 100, 100, true);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        mMapView.getMap().setMyLocationEnabled(true);
        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, bitmapDescriptor);
        mMapView.getMap().setMyLocationConfigeration(config);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            currentLocation = mLocationManager.getLastKnownLocation(providerName);
        }
        if (currentLocation == null && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            providerName = LocationManager.NETWORK_PROVIDER;
            currentLocation = mLocationManager.getLastKnownLocation(providerName);
        }

        Location location=new Location(LocationManager.GPS_PROVIDER);
        if (isBetterLocation(location, currentLocation)) {
            updateLocation(location);
            providerName=location.getProvider();
            currentLocation = location;
        } else {
            updateLocation(currentLocation);
            providerName=currentLocation.getProvider();
        }
        updateLocation(currentLocation);
        centerCurrentLocationOnScreen();

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

        // cancel the zoom button
        mMapView.showZoomControls(false);

        timer = (TextView) findViewById(R.id.chronometer);
        textViewStep = (TextView) findViewById(R.id.step);
        buttonStartAndPause = (Button) findViewById(R.id.startAndPause);
        buttonStop = (Button) findViewById(R.id.stop);
        // init time
        currentTime = 0;
        stepCount = 0;
        actualStep = 0;
        stepStamp = 0;
        setTime(currentTime);
        setStep(stepCount);

        // init chronometerState
        chronometerState = STATE_STOP;
        // init sensor manager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        // 音乐播放，数据库查询，本机文件管理器控件接口
        music_ = (ImageView) findViewById(R.id.music_);
        DB_ = (ImageView) findViewById(R.id.database_);
        search_ = (ImageView) findViewById(R.id.search_);
        music = (TextView) findViewById(R.id.music);

        // 调用github中安卓开源库进行文件管理器的打开
        search_.setOnClickListener(new View.OnClickListener() {
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
        DB_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DB_Activity.class);
                startActivity(intent);
            }
        });

        // 音乐播放，暂停按钮
        music_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    music.setText("暂停音乐");
                    handler_.post(updateThread);
                } else {
                    mediaPlayer.pause();
                    music.setText("播放音乐");
                    handler_.removeCallbacks(updateThread);
                }
            }
        });

        //监听音频播放完的代码，实现音频的自动循环播放
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                try {
                    mediaPlayer.reset();
                    int temp = (int) (Math.random() * 7);
                    AssetFileDescriptor media = getAssets().openFd(musicList.get(temp%7));
                    mediaPlayer.setDataSource(media.getFileDescriptor(), media.getStartOffset(), media.getLength());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    handler_.post(updateThread);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        buttonStartAndPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTop();
                if (chronometerState == STATE_STOP) {
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
                    stepStamp = actualStep;
                    if (clockThread == null) {
                        sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        date = sDateFormat.format(new java.util.Date());
                        clockThread = new Thread(runnable);
                        clockThread.start();
                        //start time
                    }
                    chronometerState = STATE_START;
                    buttonStartAndPause.setText("暂停");
                } else if (chronometerState == STATE_START) {
                    if (clockThread == null) {
                        clockThread = new Thread(runnable);
                    }
                    chronometerState = STATE_PAUSE;
                    buttonStartAndPause.setText("开始");
                    LatLng desLatLng1 = new LatLng(0, 0);
                    map_database.insertdb(order, desLatLng1.latitude, desLatLng1.longitude);
                } else if (chronometerState == STATE_PAUSE) {
                    stepStamp = actualStep - stepCount;
                    if (clockThread == null) {
                        clockThread = new Thread(runnable);
                    }
                    chronometerState = STATE_START;
                    buttonStartAndPause.setText("暂停");
                }
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chronometerState == STATE_START || chronometerState == STATE_PAUSE) {
                    chronometerState = STATE_STOP;
                    // set end Icon
                    LatLng limit = convertLocationToLatLng(currentLocation);
                    endOverlay = mMapView.getMap().addOverlay(new MarkerOptions().position(limit)
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.end)));
                    if (clockThread != null) {
                        clockThread = null;
                    }
                    buttonStartAndPause.setText("开始");

                    order++;
                    preferences=getSharedPreferences("demo", Context.MODE_PRIVATE);
                    editor=preferences.edit();
                    editor.putInt("order", order);
                    editor.commit();
                    // delete the thread
                    if (!database.selectDB(date)) {
                        database.insertDB(date,timer.getText().toString(), String.valueOf(stepCount), order-1+"");
                        updateTop();
                    }
                    // press the stop button, save the data in the database and start a new Activity
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, MessageActivity.class);
                    intent.putExtra("time", timer.getText().toString());
                    intent.putExtra("step", String.valueOf(stepCount));
                    intent.putExtra("orderr", order-1);
                    startActivity(intent);
                }

            }
        });
        initMediaPlayer();
    }

    public void setTime(long time) {
        long hour = time/360000;
        long minute = (time - hour* 360000)/ 6000;
        long second = (time - hour * 360000- minute*6000) / 100;
        String h = hour < 10 ? "0" + String.valueOf(hour) : String.valueOf(hour);
        String m = minute < 10 ? "0" + String.valueOf(minute) : String.valueOf(minute);
        String s = second < 10 ? "0" + String.valueOf(second) : String.valueOf(second);
        timer.setText(h + ":" + m + ":" + s);
    }

    private void setStep(long step) {
        textViewStep.setText(String.valueOf(step));
    }

    @Override
    protected void onStart() {
        if (chronometerState == STATE_STOP) {
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
        actualStep = (long) event.values[0];
        if (chronometerState == STATE_START) {
            stepCount = actualStep - stepStamp;
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        if (id == R.id.music) {
//            new MaterialFilePicker()
//                    .withActivity(this)
//                    .withRequestCode(1)
//                    .withHiddenFiles(true) // Show hidden files and folders
//                    .start();
//        }
//        if (id == R.id.DB) {
//            Intent intent = new Intent(MainActivity.this, DB_Activity.class);
//            startActivity(intent);
//        }
//
//        return super.onOptionsItemSelected(item);
//        return true;
//    }

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
                if (mediaPlayer.isPlaying()) {
                    music.setText("播放音乐");
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(filePath);
                    mediaPlayer.prepare();
                } else {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(filePath);
                    mediaPlayer.prepare();
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
            mediaPlayer.setDataSource(media.getFileDescriptor(), media.getStartOffset(), media.getLength());
            mediaPlayer.prepare();
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
            mLocationManager.requestLocationUpdates(providerName, 0, 0, mLocationListener);
        }
        super.onResume();
        if (stepCounter != null) {
            sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_SHORT).show();
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
                updateLocation(currentLocation);
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    private LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateLocation(location);
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {
            updateLocation(null);
        }
    };

    public void updateLocation(Location location) {
        if (location != null) {
            currentLocation = location;
            MyLocationData.Builder data = new MyLocationData.Builder();
            LatLng desLatLng = convertLocationToLatLng(currentLocation);
            data.latitude(desLatLng.latitude);
            data.longitude(desLatLng.longitude);
            data.direction(rotateDegree);
            mMapView.getMap().setMyLocationData(data.build());
        }
    }


    LatLng convertLocationToLatLng(Location location) {
        mConverter.from(CoordinateConverter.CoordType.GPS);
        mConverter.coord(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
        return mConverter.convert();
    }

    boolean isPhoneShaking(float [] values) {
        return (Math.abs(values[0]) > 17 || Math.abs(values[1]) > 17 || Math.abs(values[2]) > 17);
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


