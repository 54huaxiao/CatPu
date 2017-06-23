package com.example.shick.stepcounter;

/**
 * Created by shick on 2016/12/1.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shick on 2016/11/27.
 */
public class Map_DB {
    private static final String DB_NAME = "MapDB";
    private static final String TABLE_NAME = "MapTable";
    private static final int DB_VERSION = 1;
    private static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
            +" (id INTEGER PRIMARY KEY,_order INTEGER,latitude DOUBLE,longitude DOUBLE)";
    private Context mcontext;
    public Map_DB(Context context, String name, SQLiteDatabase.CursorFactory cursorFactory, int v) {
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

    class NetSender extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            int order = Integer.parseInt(params[0]);
            double latitude = Double.parseDouble(params[1]), longitude = Double.parseDouble(params[2]);
            try {
                URL url = new URL("http://10.0.2.2:3002/api/map/insert");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setUseCaches(false);

                PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("_order", order);
                jsonObject.put("latitude", latitude);
                jsonObject.put("longitude", longitude);
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

    public void insertdb(int order, double la, double lo) {
        /*SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("_order", order);
        cv.put("latitude", la);
        cv.put("longitude", lo);
        db.insert(TABLE_NAME, null, cv);
        db.close();*/
        new NetSender().execute(String.valueOf(order), String.valueOf(la), String.valueOf(lo));

    }
//
//    public void deletedb(int id) {
//        SQLiteDatabase db = getWritableDatabase();
//        String whereClause = "id=?";
//        String[] whereArgs = {id+""};
//
//        db.delete(TABLE_NAME,whereClause, whereArgs);
//        db.close();
//
//    }

    /*public List<LatLng> selectdb(int order) {
        SQLiteDatabase db = getReadableDatabase();
        List<LatLng> polylines = new ArrayList<LatLng>();
        Cursor cursor = db.rawQuery("select * from MapTable", null);
        int judge;
        while(cursor.moveToNext()){
            judge = cursor.getInt(cursor.getColumnIndex("_order"));
            if (judge == order) {
                double la = cursor.getDouble(cursor.getColumnIndex("latitude"));
                double lo = cursor.getDouble(cursor.getColumnIndex("longitude"));
                LatLng l = new LatLng(la, lo);
                polylines.add(l);
            }
        }
        db.close();
        return polylines;
    }*/
}

