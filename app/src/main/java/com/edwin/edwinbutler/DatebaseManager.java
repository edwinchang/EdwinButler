package com.edwin.edwinbutler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by edwinchang on 2016-5-26.
 */

//注：其他代码参见：http://www.runoob.com/w3cnote/android-tutorial-contentprovider.html

public class DatebaseManager extends SQLiteOpenHelper {
    final String CREATE_SQL = "CREATE TABLE test(_id INTEGER PRIMARY KEY AUTOINCREMENT,name)";

    public DatebaseManager(Context context, String name, SQLiteDatabase.CursorFactory factory,
                           int version) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
