package com.example.yyy.fingerprint.LoginRegister;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.yyy.fingerprint.FolderManage.Authority;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/8/4.
 */
public class ClientDatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "ClientDatabaseHelper";
    private static final String DB_NAME = "client.db";
    private static final int DB_VERSION = 1;
    private static final String DB_CREATE_TABLE_KEYS = "create table keys (caption text primary key, value text)";
//    private static final String DB_CREATE_TABLE_AUTHORITY = "create table authority " +
//            "(cpu_id text, " +
//            "file_path text, " +
//            "authority_number text, " +
//            "primary key(cpu_id, file_path, authority_number))";


    private static ClientDatabaseHelper mInstance;

    protected ClientDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public synchronized static ClientDatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ClientDatabaseHelper(context);
        }
        return mInstance;
    }

    public synchronized static void destoryInstance() {
        if (mInstance != null) {
            mInstance.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE_TABLE_KEYS);
        //db.execSQL(DB_CREATE_TABLE_AUTHORITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }

    public synchronized void insertKeys(String caption, String value) {
        Log.d(TAG, "insertKeys");
        ContentValues values = new ContentValues(2);
        values.put("caption", caption);
        values.put("value", value);
        getWritableDatabase().insert("keys", null, values);
    }

//    public synchronized void insertAuthority(Authority authority) {
//        Log.d(TAG, "insertAuthority");
//        if (!isFindAuthority(authority)) {
//            ContentValues values = new ContentValues(3);
//            values.put("cpu_id", authority.getCpu_id());
//            values.put("file_path", authority.getFile_path());
//            values.put("authority_number", authority.getAuthority_number());
//            getWritableDatabase().insert("authority", null, values);
//        }
//    }

    public synchronized boolean isFindKeys(String caption) {
        Log.d(TAG, "isFindKeys");
        String mCaption = null;
        Cursor cursor = getWritableDatabase().query("keys", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            mCaption = cursor.getString(cursor.getColumnIndex("caption"));
            if (caption.equals(mCaption)) {
                String value = cursor.getString(cursor.getColumnIndex("value"));
                if (value != "")
                    return true;
            }
        }
        cursor.close();
        return false;
    }

//    public synchronized boolean isFindAuthority(Authority authority) {
//        Log.d(TAG, "isFindAuthority");
//        String mcpu_id = null, mfile_path = null, mauthority_number = null;
//        Cursor cursor = getWritableDatabase().query("authority", null, null, null, null, null, null);
//        while (cursor.moveToNext()) {
//            mcpu_id = cursor.getString(cursor.getColumnIndex("cpu_id"));
//            mfile_path = cursor.getString(cursor.getColumnIndex("file_path"));
//            mauthority_number = cursor.getString(cursor.getColumnIndex("authority_number"));
//            if (authority.getCpu_id().equals(mcpu_id) && authority.getFile_path().equals(mfile_path) && authority.getAuthority_number().equals(mauthority_number)) {
//                return true;
//            }
//        }
//        cursor.close();
//        return false;
//    }
//
//    public synchronized List<Authority> getAuthority() {
//        List<Authority> authorities = new ArrayList<>();
//        Cursor cursor = getWritableDatabase().query("authority", null, null, null, null, null, null);
//
//        try {
//            cursor.moveToFirst();
//            while (!cursor.isAfterLast()) {
//                String cpu_id = cursor.getString(cursor.getColumnIndex("cpu_id"));
//                String file_path = cursor.getString(cursor.getColumnIndex("file_path"));
//                String authority_number = cursor.getString(cursor.getColumnIndex("authority_number"));
//                Authority authority = new Authority(cpu_id, file_path, authority_number);
//                authorities.add(authority);
//                cursor.moveToNext();
//            }
//        } finally {
//            cursor.close();
//        }
//        return authorities;
//    }

    public synchronized String getValue(String caption) {
        Log.d(TAG, "getValue");
        String mCaption = null, value = null;
        Cursor cursor = getWritableDatabase().query("keys", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            mCaption = cursor.getString(cursor.getColumnIndex("caption"));
            if (caption.equals(mCaption)) {
                value = cursor.getString(cursor.getColumnIndex("value"));
                Log.d("value=", value);
                return value;
            }
        }
        cursor.close();
        return "";
    }

}

