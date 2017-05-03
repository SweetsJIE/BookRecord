package com.sweetsjie.bookrecord;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sweets on 17/4/19.
 */

public class SearchInformation extends Activity {

    private EditText searchET;
    private ListView searchResult;
    private FloatingActionButton searchFAB;
    private List<String> list = new ArrayList<String>();
    private ArrayAdapter<String> adapter = null;
    private MyDataBaseHelper myDataBaseHelper;
    private Intent intent;
    private int i = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_view);

        //控件初始化
        searchET = (EditText) findViewById(R.id.searchET);
        searchResult = (ListView) findViewById(R.id.searchResult);
        searchFAB = (FloatingActionButton) findViewById(R.id.searchFAB);

        //数据库类初始化
        myDataBaseHelper = new MyDataBaseHelper(SearchInformation.this, "knowledge.db", null, 1);
        final SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();

        //适配器初始化
        adapter = new ArrayAdapter<String>(SearchInformation.this, android.R.layout.simple_expandable_list_item_1, list);
        searchResult.setAdapter(adapter);


        //搜索按键监听事件
        searchFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取knowledge表数据
                Cursor cursor = db.query("knowledge", null, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    list.clear();
                    i = 0;
                    do {
                        String buf = cursor.getString((cursor.getColumnIndex("knowledge")));
                        //模糊查询
                        if (buf != null && buf.indexOf(searchET.getText().toString()) >= 0) {
                            list.add(i, buf);
                            i++;
                        }

                    } while (cursor.moveToNext());
                    Log.d("lsit", String.valueOf(list));
                    //没有数据就listview显示"未找到任何匹配信息"
                    if (list.size()==0){
                        list.add(0,"未找到任何匹配信息");
                    }
                    adapter.notifyDataSetChanged();
                }
                cursor.close();
            }
        });


        //listview按键监听事件
        searchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) parent.getItemAtPosition(position);
                intent = new Intent(SearchInformation.this, InfromationView.class);
                //向下一活动传递数据
                intent.putExtra("name",name);
                startActivityForResult(intent, 0);

            }
        });


    }
}
