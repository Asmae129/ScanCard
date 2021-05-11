package com.fsdm.wisd.scancard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    final static String LOG_TABLE = "log";
    final static String LOG_ID_COL = "id";
    final static String LOG_UID_COL = "uid";
    final static String LOG_NAME_COL = "name";
    final static String LOG_DATE_COL = "date";
    final static String LOG_STATE_COL = "state";

    final static String USERS_TABLE = "users";
    final static String USERS_ID_COL = "id";
    final static String USERS_UID_COL = "uid";
    final static String USERS_NAME_COL = "name";

    final static String DATABASE_NAME="scancardDB";
    Context context;


    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table if not exists " + LOG_TABLE + "( " + LOG_ID_COL + " INTEGER  primary key AUTOINCREMENT," + LOG_UID_COL + " text," + LOG_NAME_COL + " text ," + LOG_DATE_COL + " text," + LOG_STATE_COL + " INTEGER)");
        db.execSQL("create table if not exists " + USERS_TABLE + "( " + USERS_ID_COL + " INTEGER  primary key AUTOINCREMENT," + USERS_UID_COL + " text," + USERS_NAME_COL + " text )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }

    void addNewLog ( String uid, String name,String date,int state ) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOG_UID_COL, uid);
        values.put(LOG_NAME_COL, name);
        values.put(LOG_DATE_COL,date);
        values.put(LOG_STATE_COL,state);
        database.insert(LOG_TABLE, null, values);


    }

    ArrayList<UserLog> getUsersLog () {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery ("select * from " + LOG_TABLE , null);
        ArrayList<UserLog> usersLog=new ArrayList<>();
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(LOG_ID_COL));
            String uid = cursor.getString(cursor.getColumnIndex(LOG_UID_COL));
            String name=cursor.getString(cursor.getColumnIndex(LOG_NAME_COL));
            String date = cursor.getString(cursor.getColumnIndex(LOG_DATE_COL));
            int state = cursor.getInt(cursor.getColumnIndex(LOG_STATE_COL));

            UserLog userLog = new UserLog(id,state,uid,name,date);

            usersLog.add(userLog);
        }
        return usersLog;

    }



    void addNewUser(String uid, String name) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERS_UID_COL, uid);
        values.put(USERS_NAME_COL, name);

        database.insert(USERS_TABLE, null, values);

        Toast.makeText(context, "user has been added successfully", Toast.LENGTH_LONG);
    }

    User validUser(String uid) {
        User user=null;
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + USERS_TABLE + " where " + USERS_UID_COL + "=?", new String[]{uid});

        if (cursor != null) {
            boolean found = cursor.moveToFirst();
            if (found) {
                String name= cursor.getString(cursor.getColumnIndex(USERS_NAME_COL));
                user=new User(uid,name);
            }
        }
        return user;

    }



}
