package com.sweetsjie.bookrecord;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by sweets on 17/4/18.
 */

public class InfromationView extends Activity{

    private Button changeInformationButton;
    private TextView knowledgeTV;
    private TextView pageTV;
    private TextView subjectTV;
    private Intent intent;
    private MyDataBaseHelper myDataBaseHelper;
    private String name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_view);

        //控件初始化
        changeInformationButton = (Button) findViewById(R.id.informationChangeButton);
        knowledgeTV = (TextView) findViewById(R.id.knowledgeTV);
        pageTV = (TextView) findViewById(R.id.pageTV);
        subjectTV = (TextView) findViewById(R.id.subjectTV);

        //获取上一个活动传过来的数据
        intent = getIntent();
        name = intent.getStringExtra("name");

        //刷新三个TextView数据
        refreshData(name);


        //更改内容按键监听事件
        changeInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(InfromationView.this, KnowledgeAdd.class);
                Bundle bundle =new Bundle();
                bundle.putString("name",knowledgeTV.getText().toString());
                intent.putExtra("name",bundle);
                startActivityForResult(intent, 0);
            }
        });
    }


    //接收KnowledgeAdd回传值
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case 0:
                Bundle b=data.getBundleExtra("name");  //data为B中回传的Intent
                String str=b.getString("name");//str即为回传的值
                refreshData(str);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        //刷新三个TextView数据
        refreshData(name);
        super.onResume();
    }

    //刷新三个TextView数据  传入形参listview item键值
    private void refreshData(String name) {
        //数据库类初始化
        myDataBaseHelper = new MyDataBaseHelper(InfromationView.this, "knowledge.db", null, 1);
        final SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();
        //读取三个表中的数据
        Cursor cursor0 = db.query("knowledge", null, null, null, null, null, null);
        Cursor cursor1 = db.query("page", null, null, null, null, null, null);
        Cursor cursor2 = db.query("subject", null, null, null, null, null, null);

        //遍历到对应的knowledge取出相应page和subject
        if (cursor0.moveToFirst() && cursor1.moveToFirst() && cursor2.moveToFirst()) {
            do {
                String buf = cursor0.getString((cursor0.getColumnIndex("knowledge")));
                if (buf.equals(name)){
                    knowledgeTV.setText(buf);
                    buf = cursor1.getString((cursor1.getColumnIndex("page")));
                    pageTV.setText(buf);
                    buf = cursor2.getString((cursor2.getColumnIndex("subject")));
                    subjectTV.setText(buf);
                    break;
                }
            } while (cursor0.moveToNext() && cursor1.moveToNext() && cursor2.moveToNext());
        }
        cursor0.close();
        cursor1.close();
        cursor2.close();
    }
}
