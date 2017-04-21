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

        changeInformationButton = (Button) findViewById(R.id.informationChangeButton);
        knowledgeTV = (TextView) findViewById(R.id.knowledgeTV);
        pageTV = (TextView) findViewById(R.id.pageTV);
        subjectTV = (TextView) findViewById(R.id.subjectTV);

        intent = getIntent();
        name = intent.getStringExtra("name");
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
        refreshData(name);
        super.onResume();
    }

    private void refreshData(String name) {
        //数据库类初始化
        myDataBaseHelper = new MyDataBaseHelper(InfromationView.this, "knowledgepoint.db", null, 1);
        myDataBaseHelper.getWritableDatabase();
        final SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();
        Cursor cursor = db.query("knowledgepoint", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String buf = cursor.getString((cursor.getColumnIndex("knowledge")));
                if (buf.equals(name)){
                    knowledgeTV.setText(buf);
                    buf = cursor.getString((cursor.getColumnIndex("page")));
                    pageTV.setText(buf);
                    buf = cursor.getString((cursor.getColumnIndex("subject")));
                    subjectTV.setText(buf);
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}
