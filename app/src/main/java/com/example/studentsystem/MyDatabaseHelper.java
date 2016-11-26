package com.example.studentsystem;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by ljh on 2016/11/11.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_STUDENT = "create table Student" +
            "(  name text, " +
            "id text primary key," +
            "sex text," +
            "age integer, " +
            "score real)";

    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                            int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_STUDENT);
        Toast.makeText(mContext, "数据库初始化成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

    }
}
