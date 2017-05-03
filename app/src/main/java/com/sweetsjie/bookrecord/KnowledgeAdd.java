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

        //控件初始化
        changeInformationButton = (Button) findViewById(R.id.knowledgeKeepButton);
        subjectInput = (Button) findViewById(R.id.subjectInput);
        knowledgeAdd = (EditText) findViewById(R.id.knowledgeAddET);
        pageAdd = (EditText) findViewById(R.id.pageAddEditText);
        subjectAdd = (EditText) findViewById(R.id.subjectAddEditText);
        //设置控件TextView支持多行输入
        setMultiLine(knowledgeAdd);
        setMultiLine(subjectAdd);

        //数据库类初始化
        myDataBaseHelper = new MyDataBaseHelper(KnowledgeAdd.this,"knowledge.db",null,1);
        final SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();

        //获取上一个活动传过来的数据
        intent =getIntent();
        bundle =  intent.getBundleExtra("name");
        final String name = bundle.getString("name");


        //刷新数据
        refreshData(name);


        //导入题库按键监听事件
        subjectInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(KnowledgeAdd.this,ListAllFileActivity.class);
                Bundle bundle2 =new Bundle();
                bundle2.putString("purpose","subject");
                bundle2.putString("knowledgeName","null");
                bundle2.putString("knowledgeName",name);
                //向下一代活动传递数据
                intent1.putExtra("information",bundle2);
                startActivityForResult(intent1, 1);
            }
        });


        //保存按键监听事件
        changeInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values0 =new ContentValues();
                ContentValues values1 =new ContentValues();
                ContentValues values2 =new ContentValues();


//                if (name.equals("null") && checkInData(knowledgeAdd.getText().toString())){
//
//                    values0.put("knowledge",knowledgeAdd.getText().toString());
//                    values1.put("page",pageAdd.getText().toString());
//                    values2.put("subject",subjectAdd.getText().toString());
//                    db.insert("knowledge",null,values0);
//                    db.insert("page",null,values1);
//                    db.insert("subject",null,values2);
//                    values0.clear();
//                    values1.clear();
//                    values2.clear();
//
//                    intent.setClass(KnowledgeAdd.this, InfromationView.class);
//                    Bundle bundle1 = new Bundle();
//                    bundle1.putString("nameBack", knowledgeAdd.getText().toString());
//                    intent.putExtra("nameBack", bundle1);
//                    setResult(0, intent);
//                    finish();
                //判断上一活动是否为InformationView.class
                if (!name.equals("null")){
                    values0.put("knowledge",knowledgeAdd.getText().toString());
                    values1.put("page",pageAdd.getText().toString());
                    values2.put("subject",subjectAdd.getText().toString());

                    String ID = null;
                    //获取相应的id值
                    Cursor cursor = db.query("knowledge", null, null, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        do {
                            String buf = cursor.getString((cursor.getColumnIndex("knowledge")));
                            if (buf.equals(name)){
                                ID = cursor.getString((cursor.getColumnIndex("id")));
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                    //更新三个表中的数据
                    db.update("knowledge",values0,"knowledge=?", new String[]{name});
                    db.update("page",values1,"id=?", new String[]{ID});
                    db.update("subject",values2,"id=?", new String[]{ID});
                    values0.clear();
                    values1.clear();
                    values2.clear();

                    //向InfromationView.class回传数据
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

    //接收回传值
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

    //返回键返回上一活动
    @Override
    public void onBackPressed() {
        intent.setClass(KnowledgeAdd.this, InfromationView.class);
        setResult(0, intent);
        super.onBackPressed();
    }

    

    //设置TextView多行输入
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


    //刷新三个TextView数据
    private void refreshData(String name) {
        //数据库类初始化
        myDataBaseHelper = new MyDataBaseHelper(KnowledgeAdd.this, "knowledge.db", null, 1);
        final SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();

        //获取三个表数据
        Cursor cursor0 = db.query("knowledge", null, null, null, null, null, null);
        Cursor cursor1 = db.query("page", null, null, null, null, null, null);
        Cursor cursor2 = db.query("subject", null, null, null, null, null, null);

        //遍历找出对应知识点相关的page和subject
        if (cursor0.moveToFirst() && cursor1.moveToFirst() && cursor2.moveToFirst()) {
            do {
                String buf = cursor0.getString((cursor0.getColumnIndex("knowledge")));
                if (buf.equals(name)){
                    knowledgeAdd.setText(buf);
                    buf = cursor1.getString((cursor1.getColumnIndex("page")));
                    pageAdd.setText(buf);
                    buf = cursor2.getString((cursor2.getColumnIndex("subject")));
                    subjectAdd.setText(buf);
                    break;
                }
            } while (cursor0.moveToNext() && cursor1.moveToNext() && cursor2.moveToNext());
        }
        cursor0.close();
        cursor1.close();
        cursor2.close();

    }

    //知识点名字查重
    private boolean checkInData(String knowledge){
        myDataBaseHelper = new MyDataBaseHelper(KnowledgeAdd.this, "knowledge.db", null, 1);
        final SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();
        Cursor cursor = db.query("knowledge", null, null, null, null, null, null);
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
