package com.sweetsjie.bookrecord;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by sweets on 17/4/18.
 */

public class KnowledgeAdd extends Activity{

    private Button changeInformationButton;
    private Intent intent;
    private EditText knowledgeAdd;
    private EditText pageAdd;
    private EditText subjectAdd;
    private MyDataBaseHelper myDataBaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.knowledge_add);

        changeInformationButton = (Button) findViewById(R.id.knowledgeKeepButton);
        knowledgeAdd = (EditText) findViewById(R.id.knowledgeAddET);
        pageAdd = (EditText) findViewById(R.id.pageAddEditText);
        subjectAdd = (EditText) findViewById(R.id.subjectAddEditText);
        setMultiLine(knowledgeAdd);
        setMultiLine(subjectAdd);

        myDataBaseHelper = new MyDataBaseHelper(KnowledgeAdd.this,"knowledgepoint.db",null,1);
        myDataBaseHelper.getWritableDatabase();
        final SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();


        intent =getIntent();
        final String name = intent.getStringExtra("name");


        refreshData();


        changeInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values =new ContentValues();
                if (name.equals("null")){
                    values.put("knowledge",knowledgeAdd.getText().toString());
                    values.put("page",pageAdd.getText().toString());
                    values.put("subject",subjectAdd.getText().toString());
                    db.insert("knowledgepoint",null,values);
                    values.clear();
                }else {
                    values.put("knowledge",knowledgeAdd.getText().toString());
                    values.put("page",pageAdd.getText().toString());
                    values.put("subject",subjectAdd.getText().toString());
                    db.update("knowledgepoint",values,"knowledge=?", new String[]{name});
                    values.clear();
                }
                finish();
            }
        });
    }

    private void setMultiLine(EditText editText){
        //设置EditText的显示方式为多行文本输入
        editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        //文本显示的位置在EditText的最上方
        editText.setGravity(Gravity.TOP);
        //改变默认的单行模式
        editText.setSingleLine(false);
        //水平滚动设置为False
        editText.setHorizontallyScrolling(false);
    }

    private void refreshData() {
        intent =getIntent();
        final String name = intent.getStringExtra("name");
        //数据库类初始化
        myDataBaseHelper = new MyDataBaseHelper(KnowledgeAdd.this, "knowledgepoint.db", null, 1);
        myDataBaseHelper.getWritableDatabase();
        final SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();
        Cursor cursor = db.query("knowledgepoint", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String buf = cursor.getString((cursor.getColumnIndex("knowledge")));
                if (buf.equals(name)){
                    knowledgeAdd.setText(buf);
                    buf = cursor.getString((cursor.getColumnIndex("page")));
                    pageAdd.setText(buf);
                    buf = cursor.getString((cursor.getColumnIndex("subject")));
                    subjectAdd.setText(buf);
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}
