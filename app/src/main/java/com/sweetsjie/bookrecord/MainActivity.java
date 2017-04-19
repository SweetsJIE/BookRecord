package com.sweetsjie.bookrecord;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView knowledgeView;
    private List<String> list = new ArrayList<String>();
    private ArrayAdapter<String> adapter = null;
    private Intent intent;
    private MyDataBaseHelper mydatabaseHelper;
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //数据库类初始化
        mydatabaseHelper = new MyDataBaseHelper(MainActivity.this, "knowledgepoint.db", null, 1);
        mydatabaseHelper.getWritableDatabase();
        final SQLiteDatabase db = mydatabaseHelper.getWritableDatabase();


        knowledgeView = (ListView) findViewById(R.id.knowledgeShow);
        FloatingActionButton addFileButton = (FloatingActionButton) findViewById(R.id.addFAB);
        FloatingActionButton searchButton = (FloatingActionButton) findViewById(R.id.mainSearchFAB);


        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_expandable_list_item_1, list);
        knowledgeView.setAdapter(adapter);

        //设定按键监听事件
        addFileButton.setOnClickListener(new OpenFileAction());
        searchButton.setOnClickListener(new OpenFileAction());

        knowledgeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) parent.getItemAtPosition(position);
                intent = new Intent(MainActivity.this, InfromationView.class);
                intent.putExtra("name",name);
                startActivityForResult(intent, 0);

            }
        });

        knowledgeView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("警告")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setMessage("确定删除")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String nameStr = (String) parent.getItemAtPosition(position);
                                Log.d("item", nameStr);
                                db.delete("knowledgepoint", "knowledge=?", new String[]{nameStr});
                                refreshData();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    //按钮动作:  窗口跳转 (Activity)  没有携带数据流的
    class OpenFileAction implements View.OnClickListener {
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.mainSearchFAB:
                    intent = new Intent(MainActivity.this, SearchInformation.class);
                    startActivityForResult(intent, 0);
                    break;
                case R.id.addFAB:
                    intent = new Intent(MainActivity.this, KnowledgeAdd.class);
                    intent.putExtra("name","null");
                    startActivityForResult(intent, 0);
                    break;
            }
        }
    }


    //刷新listview数据
    private void refreshData() {
        mydatabaseHelper = new MyDataBaseHelper(MainActivity.this, "knowledgepoint.db", null, 1);
        mydatabaseHelper.getWritableDatabase();
        final SQLiteDatabase db = mydatabaseHelper.getWritableDatabase();
        Cursor cursor = db.query("knowledgepoint", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            list.clear();
            i = 0;
            do {
                String buf = cursor.getString((cursor.getColumnIndex("knowledge")));
                if (buf != null) {
                    list.add(i, buf);
                    i++;
                }
            } while (cursor.moveToNext());
            adapter.notifyDataSetChanged();
        }
        cursor.close();
    }
}
