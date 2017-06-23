package com.example.shick.stepcounter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;;

/**
 * Created by shick on 2016/12/1.
 */

public class DB_Activity extends AppCompatActivity {
    private Run_DB database;
    private List<Run> runlist = new ArrayList<Run>();
    //private int times,hour,min,second,total_distance;
    private TextView total_times, totaldistances, totaltime;
    private String username = null;
    private static final String TABLE_NAME = "RunTable";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db_layout);

        Intent intent = getIntent();
        username = intent.getStringExtra("User");
        database = new Run_DB(this, "RunDB", null, 1);
        initRun(username);
        final Run_Adapter adapter = new Run_Adapter(DB_Activity.this, R.layout.run_item, runlist);
        final ListView listView = (ListView) findViewById(R.id.runlist);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int p, long l) {
                final Run temp = runlist.get(p);
                Dialog delete = new AlertDialog.Builder(DB_Activity.this).
                        setTitle("").
                        setMessage("是否删除？").
                        setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                database.deleteDB(temp.getDate());
                                Toast.makeText(DB_Activity.this, "删除成功！", Toast.LENGTH_SHORT).show();

                                adapter.clear();
                                initRun(username);
                                listView.setAdapter(adapter);
                            }
                        }).
                        show();
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int p, long l) {
                final Run temp = runlist.get(p);
                int a = Integer.parseInt(temp.getOrder());
                Intent intent = new Intent();
                intent.setClass(DB_Activity.this, MessageActivity.class);
                intent.putExtra("time", temp.getTime());
                intent.putExtra("step", temp.getDistance());
                intent.putExtra("orderr", a);
                startActivity(intent);
            }
        });

    }
    private void initRun(String username) {
        int flag = 0;

        /*times = 0;
        total_distance = 0;
        hour = min = second = 0;*/
        /*SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{"date","time", "distance", "username","_order"}, "username = ?",
                new String[]{username}, null, null, null, null);
        String date = null;
        String time = null;
        String distance = null;
        while(cursor.moveToNext()){
            times++;
            date = cursor.getString(cursor.getColumnIndex("date"));
            time = cursor.getString(cursor.getColumnIndex("time"));
            distance = cursor.getString(cursor.getColumnIndex("distance"));
            String a = cursor.getString(cursor.getColumnIndex("_order"));
//            Toast.makeText(DB_Activity.this, a+"", Toast.LENGTH_SHORT).show();
            String h = time.substring(0,2);
            int hi = Integer.parseInt(h);
            String m = time.substring(3,5);
            int mi = Integer.parseInt(m);
            String s = time.substring(6,8);
            int si = Integer.parseInt(s);

            second += si;
            if (second >= 60) {
                second -= 60;
                min++;
            }
            min += mi;
            if (min >= 60) {
                min -= 60;
                hour++;
            }
            hour += hi;

            int i = Integer.parseInt(distance);
            total_distance += i;
            Run temp = new Run(date, time, distance, a+"");
            runlist.add(temp);
        }
        total_times = (TextView)findViewById(R.id.cishu);
        total_times.setText("总次数："+times+"");

        totaldistances = (TextView)findViewById(R.id.licheng);
        totaldistances.setText("总步数："+ total_distance);

        totaltime = (TextView)findViewById(R.id.shichang);

        String shour = hour < 10 ? "0"+hour:hour+"";
        String smin = min < 10 ? "0"+min:min+"";
        String ssecond = second < 10 ?"0"+second:second+"";
        totaltime.setText("总时长: "+shour+":"+smin+":"+ssecond);
        db.close();*/
        class NetSender extends AsyncTask<String, Void, JSONArray> {
            @Override
            protected JSONArray doInBackground(String... params) {
                String username = params[0];
                try {
                    URL url = new URL("http://10.0.2.2:3002/api/run/select");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setUseCaches(false);
                    PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("username", username);
                    printWriter.write(jsonObject.toString());
                    printWriter.flush();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String line = bufferedReader.readLine();
                    return  new JSONArray(line);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new JSONArray();
            }
            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                try {
                    int times = jsonArray.length(), hour = 0,min = 0,second = 0,total_distance = 0;
                    String date = null, time = null, distance = null;
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        date = jsonObject.getString("date");
                        time = jsonObject.getString("time");
                        distance = jsonObject.getString("distance");
                        String a = jsonObject.getString("_order");
                        int hi = Integer.parseInt(time.substring(0,2));
                        int mi = Integer.parseInt(time.substring(3,5));
                        int si = Integer.parseInt(time.substring(6,8));
                        second += si;
                        if (second >= 60) {
                            second -= 60;
                            min++;
                        }
                        min += mi;
                        if (min >= 60) {
                            min -= 60;
                            hour++;
                        }
                        hour += hi;
                        int dis = Integer.parseInt(distance);
                        total_distance += dis;
                        Run temp = new Run(date, time, distance, a+"");
                        runlist.add(temp);
                    }
                    total_times = (TextView)findViewById(R.id.cishu);
                    total_times.setText("总次数："+times+"");

                    totaldistances = (TextView)findViewById(R.id.licheng);
                    totaldistances.setText("总步数："+ total_distance);

                    totaltime = (TextView)findViewById(R.id.shichang);

                    String shour = hour < 10 ? "0"+hour:hour+"";
                    String smin = min < 10 ? "0"+min:min+"";
                    String ssecond = second < 10 ?"0"+second:second+"";
                    totaltime.setText("总时长: "+shour+":"+smin+":"+ssecond);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onPostExecute(jsonArray);
            }
        }
    }

}
