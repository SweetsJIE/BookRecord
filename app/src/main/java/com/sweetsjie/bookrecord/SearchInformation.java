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

        searchET = (EditText) findViewById(R.id.searchET);
        searchResult = (ListView) findViewById(R.id.searchResult);
        searchFAB = (FloatingActionButton) findViewById(R.id.searchFAB);

        myDataBaseHelper = new MyDataBaseHelper(SearchInformation.this, "knowledgepoint.db", null, 1);
        myDataBaseHelper.getWritableDatabase();
        final SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();

        adapter = new ArrayAdapter<String>(SearchInformation.this, android.R.layout.simple_expandable_list_item_1, list);
        searchResult.setAdapter(adapter);

        searchFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = db.query("knowledgepoint", null, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    list.clear();
                    i = 0;
                    do {
                        String buf = cursor.getString((cursor.getColumnIndex("knowledge")));
                        if (buf != null && buf.indexOf(searchET.getText().toString()) >= 0) {
                            list.add(i, buf);
                            i++;
                        }

                    } while (cursor.moveToNext());
                    Log.d("lsit", String.valueOf(list));
                    if (list.size()==0){
                        list.add(0,"未找到任何匹配信息");
                    }
                    adapter.notifyDataSetChanged();
                }
                cursor.close();
            }
        });


        searchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) parent.getItemAtPosition(position);
                intent = new Intent(SearchInformation.this, InfromationView.class);
                intent.putExtra("name",name);
                startActivityForResult(intent, 0);

            }
        });


    }
}
