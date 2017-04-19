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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_view);

        changeInformationButton = (Button) findViewById(R.id.informationChangeButton);
        knowledgeTV = (TextView) findViewById(R.id.knowledgeTV);
        pageTV = (TextView) findViewById(R.id.pageTV);
        subjectTV = (TextView) findViewById(R.id.subjectTV);





        //更改内容按键监听事件
        changeInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(InfromationView.this, KnowledgeAdd.class);
                intent.putExtra("name",knowledgeTV.getText().toString());
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onResume() {
        refreshData();
        super.onResume();
    }

    private void refreshData() {
        intent = getIntent();
        String name = intent.getStringExtra("name");
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
