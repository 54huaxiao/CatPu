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
public class User_DB extends SQLiteOpenHelper{
    private static final String DB_NAME = "UserDB";
    private static final String TABLE_NAME = "UserTable";
    private static final int DB_VERSION = 1;

    private static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
            +" (id INTEGER PRIMARY KEY,username TEXT,password TEXT)";
    private Context mcontext;
    public User_DB(Context context, String name, SQLiteDatabase.CursorFactory cursorFactory, int v) {
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

    public void insertDB(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username",username);
        cv.put("password", password);
        db.insert(TABLE_NAME, null, cv);
        db.close();
    }


    public void deleteDB(String username) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "username=?";
        String[] whereArgs = {username};

        db.delete(TABLE_NAME,whereClause, whereArgs);
        db.close();

    }

    public boolean selectDB(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{"username","password"}, "username = ?",
                new String[]{username}, null, null, null, null);
        String judge = null;
        while(cursor.moveToNext()){
            judge = cursor.getString(cursor.getColumnIndex("username"));
        }
        db.close();
        return judge == null? false : true;
    }

    public String getPassword(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{"username","password"}, "username = ?",
                new String[]{username}, null, null, null, null);
        String password = null;
        while(cursor.moveToNext()){
            password = cursor.getString(cursor.getColumnIndex("password"));
        }
        db.close();
        return password;
    }
}

