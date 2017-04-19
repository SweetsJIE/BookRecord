package com.sweetsjie.bookrecord;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by sweets on 17/4/18.
 */

public class MyDataBaseHelper extends SQLiteOpenHelper{

    private static final String CREATE_KNOWLEDGEPOINT = "CREATE TABLE IF NOT EXISTS knowledgepoint (knowledge VARCHAR(255),page VARCHAR(255),subject VARCHAR(255))";
//    private static final String CREATE_PAGE = "CREATE TABLE IF NOT EXISTS collect (bookname VARCHAR(255),bookurl VARCHAR(255))";
//    private static final String CREATE_ = "CREATE TABLE IF NOT EXISTS user (account VARCHAR(255),password VARCHAR(255))";
    private String a = "Android是一种基于Linux的自由及开放源代码的操作系统，主要使用于移动设备，如智如智能手机和平板电脑，由Google公司和开放手机联盟领导及开发。统一中文名称，中国大陆地区较多人使用“安卓”或“安致”。Android操作系统最初由AndyRubin开发，主要支持手机。2005年8月由Google收购注资。2007年11月，Google与84家硬件制造商、软件开发商及电信营运商组建开放手机联盟共同研发改良Android系统。";
    private String b = "随后Google以Apache开源许可证的授权方式，发布了Android的源代码。第一部Android智能手机发布于2008年10月。Android逐渐扩展到平板电脑及其他领域上，如电视、数码相机、游戏机等。2011年第一季度，Android在全球的市场份额首次超过塞班系统，跃居全球第一。2013年的第四季度，Android平台手机的全球市场份额已经达到78%。2013年09月24日谷歌开发的操作系统Android在迎来了5岁生日，全世界采用这款系统的设备数量已经达到10亿台。";
    private Context mContext;
    public MyDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_KNOWLEDGEPOINT);
        //db.execSQL("INSERT INTO knowledgepoint (knowledge,page,subject) VALUES("+a+",15,"+b);
        //db.execSQL("INSERT INTO knowledgepoint(knowledge,page,subject) VALUES ('测试'，15，'测试')");
        Toast.makeText(mContext,"Create succeeded",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
