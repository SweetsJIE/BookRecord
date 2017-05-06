package com.sweetsjie.bookrecord;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.idescout.sql.SqlScoutServer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView knowledgeView;
    private List<String> list = new ArrayList<String>();
    private ArrayAdapter<String> adapter = null;
    private Intent intent;
    private MyDataBaseHelper myDataBaseHelper;
    private int i;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //数据库在线调试软件初始化
        SqlScoutServer.create(this,getPackageName());



        //数据库类初始化
        myDataBaseHelper = new MyDataBaseHelper(MainActivity.this, "knowledge.db", null, 1);
        final SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();

        //控件初始化
        knowledgeView = (ListView) findViewById(R.id.knowledgeShow);
        FloatingActionButton addFileButton = (FloatingActionButton) findViewById(R.id.addFAB);
        FloatingActionButton searchButton = (FloatingActionButton) findViewById(R.id.mainSearchFAB);


        //适配器初始化
        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_expandable_list_item_1, list);
        knowledgeView.setAdapter(adapter);

        //设定按键监听事件
        addFileButton.setOnClickListener(new OpenFileAction());
        searchButton.setOnClickListener(new OpenFileAction());

        //listview监听事件
        knowledgeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) parent.getItemAtPosition(position);
                intent = new Intent(MainActivity.this, InfromationView.class);
                intent.putExtra("name",name);
                startActivityForResult(intent, 0);

            }
        });

        //listview长按监听事件
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
                                //获取要删除的item值
                                String nameStr = (String) parent.getItemAtPosition(position);
                                String ID = null;
                                //获取相应数据库表knowledge的id值
                                Cursor cursor = db.query("knowledge", null, null, null, null, null, null);
                                if (cursor.moveToFirst()) {
                                    do {
                                        String buf = cursor.getString((cursor.getColumnIndex("knowledge")));
                                        if (buf.equals(nameStr)){
                                            ID = cursor.getString((cursor.getColumnIndex("id")));
                                        }
                                    } while (cursor.moveToNext());
                                }
                                cursor.close();
                                //删除三表中相应的数据
                                db.delete("knowledge", "knowledge=?", new String[]{nameStr});
                                db.delete("page", "id=?", new String[]{ID});
                                db.delete("subject", "id=?", new String[]{ID});
                                //刷新listview
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
        //刷新listview数据
        refreshData();
        super.onResume();
    }

    //按钮动作:  窗口跳转 (Activity)  没有携带数据流的
    class OpenFileAction implements View.OnClickListener {
        public void onClick(View v) {
            switch (v.getId()){
                //搜索按键监听事件
                case R.id.mainSearchFAB:
                    intent = new Intent(MainActivity.this, SearchInformation.class);
                    startActivityForResult(intent, 0);
                    break;
                //添加知识点按键监听事件
                case R.id.addFAB:
                    Intent intentMain = new Intent(MainActivity.this, ListAllFileActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("purpose","knowledge");
                    //向ListAllFileActivity.class传递数据
                    intentMain.putExtra("information",bundle);
                    startActivityForResult(intentMain, 0);
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        SubMenu subFile = menu.addSubMenu(0, Menu.FIRST, 0, "删除所有知识点");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //数据库类初始化
        myDataBaseHelper = new MyDataBaseHelper(MainActivity.this, "knowledge.db", null, 1);
        final SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();
        switch(item.getItemId()) {
            case Menu.FIRST:
                // 设置Activity的Title
                db.execSQL("DELETE FROM knowledge;");
                db.execSQL("DELETE FROM page;");
                db.execSQL("DELETE FROM subject;");
                db.execSQL("DELETE FROM sqlite_sequence;");
                //刷新listview
                list.clear();
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this,"知识点删除成功",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    //刷新listview数据
    private void refreshData() {
        //数据库类初始化
        myDataBaseHelper = new MyDataBaseHelper(MainActivity.this, "knowledge.db", null, 1);
        myDataBaseHelper.getWritableDatabase();
        final SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();

        //把查询knowledge表中的数据存储到cursor
        Cursor cursor = db.query("knowledge", null, null, null, null, null, null);


        //遍历cursor 刷新listview数据
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
