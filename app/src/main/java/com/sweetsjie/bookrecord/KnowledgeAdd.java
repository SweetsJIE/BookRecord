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
import android.widget.Toast;

/**
 * Created by sweets on 17/4/18.
 */

public class KnowledgeAdd extends Activity{

    private Button changeInformationButton;
    private Button subjectInput;
    private Intent intent;
//    private Intent inputIntent;
    private Bundle bundle;
    private EditText knowledgeAdd;
    private EditText pageAdd;
    private EditText subjectAdd;
    private MyDataBaseHelper myDataBaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.knowledge_add);

        changeInformationButton = (Button) findViewById(R.id.knowledgeKeepButton);
        subjectInput = (Button) findViewById(R.id.subjectInput);
        knowledgeAdd = (EditText) findViewById(R.id.knowledgeAddET);
        pageAdd = (EditText) findViewById(R.id.pageAddEditText);
        subjectAdd = (EditText) findViewById(R.id.subjectAddEditText);
        setMultiLine(knowledgeAdd);
        setMultiLine(subjectAdd);

        myDataBaseHelper = new MyDataBaseHelper(KnowledgeAdd.this,"knowledgepoint.db",null,1);
        myDataBaseHelper.getWritableDatabase();
        final SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();

        intent =getIntent();
        bundle =  intent.getBundleExtra("name");
        final String name = bundle.getString("name");


        refreshData(name);


        subjectInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(KnowledgeAdd.this,ListAllFileActivity.class);
                Bundle bundle2 =new Bundle();
                bundle2.putString("purpose","subject");
                bundle2.putString("knowledgeName","null");
                bundle2.putString("knowledgeName",name);
                //bundle2.putString("knowledgeName",knowledgeAdd.getText().toString());
                //bundle.putString("name",knowledgeTV.getText().toString());
                //intent.putExtra("name",bundle);

                //Intent inputIntent = new Intent(KnowledgeAdd.this, ListAllFileActivity.class);
                intent1.putExtra("information",bundle2);
//                intent.setClass(KnowledgeAdd.this,ListAllFileActivity.class);
                startActivityForResult(intent1, 1);
            }
        });


        changeInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values =new ContentValues();
                if (name.equals("null") && checkInData(knowledgeAdd.getText().toString())){
                    values.put("knowledge",knowledgeAdd.getText().toString());
                    values.put("page",pageAdd.getText().toString());
                    values.put("subject",subjectAdd.getText().toString());
                    db.insert("knowledgepoint",null,values);
                    values.clear();

                    intent.setClass(KnowledgeAdd.this, InfromationView.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("nameBack", knowledgeAdd.getText().toString());
                    intent.putExtra("nameBack", bundle1);
                    setResult(0, intent);
                    finish();
                }else if (!name.equals("null")){
                    values.put("knowledge",knowledgeAdd.getText().toString());
                    values.put("page",pageAdd.getText().toString());
                    values.put("subject",subjectAdd.getText().toString());
                    db.update("knowledgepoint",values,"knowledge=?", new String[]{name});
                    values.clear();

                    intent.setClass(KnowledgeAdd.this, InfromationView.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("name", knowledgeAdd.getText().toString());
                    intent.putExtra("name", bundle1);
                    setResult(0, intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case 1:
                Bundle b=data.getBundleExtra("information");  //data为B中回传的Intent
                String str=b.getString("knowledgeName");//str即为回传的值
                refreshData(str);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        intent.setClass(KnowledgeAdd.this, InfromationView.class);
        setResult(0, intent);
        super.onBackPressed();
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


    private void refreshData(String name) {
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

    private boolean checkInData(String knowledge){
        myDataBaseHelper = new MyDataBaseHelper(KnowledgeAdd.this, "knowledgepoint.db", null, 1);
        myDataBaseHelper.getWritableDatabase();
        final SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();
        Cursor cursor = db.query("knowledgepoint", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String buf = cursor.getString((cursor.getColumnIndex("knowledge")));
                if (buf.equals(knowledge)){
                    Toast.makeText(KnowledgeAdd.this,"知识点已存在",Toast.LENGTH_SHORT).show();
                    return false;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return true;
    }
}
