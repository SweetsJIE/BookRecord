package com.sweetsjie.bookrecord;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by sweets on 17/4/18.
 */

public class MyDataBaseHelper extends SQLiteOpenHelper{

    private static final String CREATE_KNOWLEDGEPOINT = "CREATE TABLE IF NOT EXISTS knowledge (id INTEGER PRIMARY KEY AUTOINCREMENT,knowledge VARCHAR(255))";
    private static final String CREATE_PAGE = "CREATE TABLE IF NOT EXISTS page (id INTEGER PRIMARY KEY AUTOINCREMENT,page VARCHAR(255))";
    private static final String CREATE_SUBJECT = "CREATE TABLE IF NOT EXISTS subject (id INTEGER PRIMARY KEY AUTOINCREMENT,subject VARCHAR(255))";

    private Context mContext;
    public MyDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_KNOWLEDGEPOINT);
        db.execSQL(CREATE_PAGE);
        db.execSQL(CREATE_SUBJECT);
        //db.execSQL("INSERT INTO knowledgepoint (knowledge,page,subject) VALUES("+a+",15,"+b);
        //db.execSQL("INSERT INTO knowledgepoint(knowledge,page,subject) VALUES ('测试'，15，'测试')");
        Toast.makeText(mContext,"Create succeeded",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
