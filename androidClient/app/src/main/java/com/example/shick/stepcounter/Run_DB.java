package com.example.shick.stepcounter;

/**
 * Created by shick on 2016/12/1.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by shick on 2016/11/27.
 */
public class Run_DB extends SQLiteOpenHelper{
    private static final String DB_NAME = "RunDB";
    private static final String TABLE_NAME = "RunTable";
    private static final int DB_VERSION = 1;
    private String username = null;
    private static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
            +" (id INTEGER PRIMARY KEY,date TEXT,time TEXT,distance TEXT,username TEXT,_order INTEGER)";
    private Context mcontext;
    public Run_DB(Context context, String name, SQLiteDatabase.CursorFactory cursorFactory, int v) {
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

    public void insertDB(String date, String time, String distance, String username,String order) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("date",date);
        cv.put("time", time);
        cv.put("distance", distance);
        cv.put("username", username);
        cv.put("_order", order);
        db.insert(TABLE_NAME, null, cv);
        db.close();
    }


    public void deleteDB(String date) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "date=?";
        String[] whereArgs = {date};

        db.delete(TABLE_NAME,whereClause, whereArgs);
        db.close();

    }

    public boolean selectDB(String date) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{"date","time", "distance", "username","_order"}, "date = ?",
                new String[]{date}, null, null, null, null);
        String judge = null;
        while(cursor.moveToNext()){
            judge = cursor.getString(cursor.getColumnIndex("date"));
        }
        db.close();
        return judge == null? false : true;
    }
}

