package com.example.shick.stepcounter;

/**
 * Created by shick on 2016/12/1.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shick on 2016/11/27.
 */
public class Map_DB extends SQLiteOpenHelper{
    private static final String DB_NAME = "MapDB";
    private static final String TABLE_NAME = "MapTable";
    private static final int DB_VERSION = 1;

    private static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
            +" (id INTEGER PRIMARY KEY,_order INTEGER,latitude DOUBLE,longitude DOUBLE)";
    private Context mcontext;
    public Map_DB(Context context, String name, SQLiteDatabase.CursorFactory cursorFactory, int v) {
        super(context, name, cursorFactory, DB_VERSION);
        mcontext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int il) {

    }

    public void insertdb(int order, double la, double lo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("_order", order);
        cv.put("latitude", la);
        cv.put("longitude", lo);
        db.insert(TABLE_NAME, null, cv);
        db.close();
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

    public List<LatLng> selectdb(int order) {
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
    }
}

