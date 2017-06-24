package com.example.shick.stepcounter;

/**
 * Created by shick on 2016/12/1.
 */
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by shick on 2016/11/27.
 */
public class Run_DB {
    private static final String DB_NAME = "RunDB";
    private static final String TABLE_NAME = "RunTable";
    private static final int DB_VERSION = 1;
    private String username = null;
    private static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
            +" (id INTEGER PRIMARY KEY,date TEXT,time TEXT,distance TEXT,username TEXT,_order INTEGER)";
    private Context mcontext;

    private String prefix = "http://10.0.2.2:3002/api/run/";
    private static final String INSERT = "insert";
    private static final String DELETE = "delete";
    private static final String SELECT = "select";

    public Run_DB(Context context, String name, SQLiteDatabase.CursorFactory cursorFactory, int v) {
        //super(context, name, cursorFactory, DB_VERSION);
        mcontext = context;
    }
    /*@Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int il) {

    }*/

    public void insertDB(String date, String time, String distance, String username,String order) {
        /*SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("date",date);
        cv.put("time", time);
        cv.put("distance", distance);
        cv.put("username", username);
        cv.put("_order", order);
        db.insert(TABLE_NAME, null, cv);
        db.close();*/

        NetSender netSender = new NetSender();
        netSender.execute(INSERT, date, time, distance, username, order);
    }


    public void deleteDB(String date) {
        /*SQLiteDatabase db = getWritableDatabase();
        String whereClause = "date=?";
        String[] whereArgs = {date};

        db.delete(TABLE_NAME,whereClause, whereArgs);
        db.close();*/
        NetSender netSender = new NetSender();
        netSender.execute(DELETE, date);
    }

    /*public boolean selectDB(final String date) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{"date","time", "distance", "username","_order"}, "date = ?",
                new String[]{date}, null, null, null, null);

        JSONObject test = null;
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                JSONObject result = (JSONObject) msg.obj;
                test = result;
                super.handleMessage(msg);
            }
        };
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String path = prefix + SELECT;
                try {
                    URL url = new URL(path);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setUseCaches(false);

                    PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("date", date);

                    printWriter.write(jsonObject.toString());
                    printWriter.flush();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String line = bufferedReader.readLine();
                    Message msg = new Message();
                    msg.obj = new JSONObject(line);
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String judge = null;

        while(cursor.moveToNext()){
            judge = cursor.getString(cursor.getColumnIndex("date"));
        }
        db.close();

        return judge == null? false : true;
    }*/

    class NetSender extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            String type = params[0];
            String date = params[1], time = params[2], distance = params[3], username = params[4], order = params[5];
            String path = prefix + type;

            try {
                URL url = new URL(path);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setUseCaches(false);

                PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("date", date);

                if (type.equals(INSERT)) {
                    jsonObject.put("time", time);
                    jsonObject.put("distance", distance);
                    jsonObject.put("username", username);
                    jsonObject.put("_order", order);
                }

                printWriter.write(jsonObject.toString());
                printWriter.flush();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String line = bufferedReader.readLine();
                return new JSONObject(line);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new JSONObject();
        }
    }
}

