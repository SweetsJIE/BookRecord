package com.sweetsjie.bookrecord;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeSet;


public class ListAllFileActivity extends ListActivity {


    private List<File> fileList;
    private List<String> listBuf = new ArrayList<String>();
    private List<String> listSelecting = new ArrayList<String>();
    private List<String> listSelected = new ArrayList<String>();
    private Intent intentGet;
    private Bundle bundle;
    private String fileNameKey = "fileName";
    private String nameString;
    private MyDataBaseHelper mydatabaseHelper;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intentGet = getIntent();
        bundle =  intentGet.getBundleExtra("information");

        progressDialog = new ProgressDialog(ListAllFileActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在识别·········");

        File path = android.os.Environment.getExternalStorageDirectory();
        File[] f = path.listFiles();

        fill(f);
    }

    private void fill(File[] files) {
        fileList = new ArrayList<File>();

        for (File file : files) {
            if (isValidFileOrDir(file)) {
                fileList.add(file);
            }
        }
        ArrayAdapter<String> fileNameList = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                fileToStrArr(fileList));

        setListAdapter(fileNameList);
    }

    private boolean isValidFileOrDir(File fileIn) {
        if (fileIn.isDirectory()) {
            return true;
        } else {
            String fileNameLow = fileIn.getName().toLowerCase();
            if (fileNameLow.endsWith(".txt")) {
                return true;
            }
        }
        return false;
    }

    private String[] fileToStrArr(List<File> fl) {
        ArrayList<String> fnList = new ArrayList<String>();
        for (int i = 0; i < fl.size(); i++) {
            nameString = fl.get(i).getName();
            //Log.d("TAG",nameString);
            fnList.add(nameString);
        }
        return fnList.toArray(new String[0]);
    }



    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {


        File file = fileList.get(position);
        if (file.isDirectory()) {
            File[] f = file.listFiles();
            fill(f);
        } else {
            progressDialog.show();

            final File file1 = new File(file.getAbsolutePath());

            new Thread(new Runnable(){
                public void run() {
                    mydatabaseHelper = new MyDataBaseHelper(ListAllFileActivity.this, "knowledgepoint.db", null, 1);
                    SQLiteDatabase db = mydatabaseHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    String purpose = bundle.getString("purpose");
                    String knowledgeName = bundle.getString("knowledgeName");

                    if (purpose.equals("knowledge")){
                        try {
                            countLetter(file1, 8);
                            intentGet.setClass(ListAllFileActivity.this, MainActivity.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Message message = new Message();
                        message.what = 0;
                        handler.sendMessage(message);

                        startActivity(intentGet);
                    }
                    else {
                        values.put("subject",getStringFromFile(file1.getAbsolutePath(), "GB2312"));
                        db.update("knowledgepoint",values,"knowledge=?", new String[]{knowledgeName});

                        intentGet.setClass(ListAllFileActivity.this, KnowledgeAdd.class);
                        setResult(1, intentGet);
                        finish();
                    }

                }
            }).start();
        }
    }

    //异步消息处理
    public android.os.Handler handler = new android.os.Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (progressDialog.isShowing())progressDialog.dismiss();
                    break;
                default:
                    break;
            }
        }

    };

    public void countLetter(File file, int frequency) throws Exception {

        mydatabaseHelper = new MyDataBaseHelper(this, "knowledgepoint.db", null, 1);
        SQLiteDatabase db = mydatabaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        StringReader sr = new StringReader(getStringFromFile(file.getAbsolutePath(), "GB2312"));
        IKSegmentation ik = new IKSegmentation(sr, true);
        Lexeme lex = null;
        try {
            while ((lex = ik.next()) != null) {
                listBuf.add(lex.getLexemeText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String string : listBuf) {
            if (string.length() == 2 || string.length() == 3) {
                listSelecting.add(string);
            } else if (string.length() > 3) {
                values.put("knowledge", string);
                db.insert("knowledgepoint", null, values);
                //listSelected.add(string);
            }
        }


        String string = String.valueOf(listSelecting);

        //字符转换为字节
        byte[] by = string.getBytes();
        //构造一个字节输入流
        InputStream is = new ByteArrayInputStream(by);
        //通过字节流g构成缓存流
        InputStreamReader inputStreamReader = new InputStreamReader(is);

        BufferedReader br = new BufferedReader(inputStreamReader);
        Map<String, Integer> map = new HashMap<String, Integer>();
        try {
            String line = null;
            //遍历文本讲字符-次数添加到map中去
            while ((line = br.readLine()) != null) {
                StringTokenizer stoken = new StringTokenizer(line, ",.!  ");
                while (stoken.hasMoreElements()) {
                    int count;
                    String letter = stoken.nextToken();
                    if (!map.containsKey(letter)) {
                        count = 1;
                    } else {
                        count = map.get(letter) + 1;
                    }
                    map.put(letter, count);
                }
            }
        } finally {
            br.close();
        }
        TreeSet<WordBean> set = new TreeSet<WordBean>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String key = entry.getKey();
            Integer count = map.get(key);
            set.add(new WordBean(key, count));
        }
        //输出set中的数据
        Iterator ite = set.iterator();
        int count = 0;
        while (ite.hasNext()) {
            if (count++ < frequency) {
                WordBean bean = (WordBean) ite.next();

                values.put("knowledge", bean.getKey());
                db.insert("knowledgepoint", null, values);

                Log.d("data", bean.getKey() + ":" + bean.getCount());
            } else {
                break;
            }
        }

    }


    //把txt文本信息转换成String
    public String getStringFromFile(String fileNameString, String code) {
        try {
            StringBuffer sBuffer = new StringBuffer();
            FileInputStream fInputStream = new FileInputStream(fileNameString);

            InputStreamReader inputStreamReader = new InputStreamReader(fInputStream, code);
            BufferedReader in = new BufferedReader(inputStreamReader);
            if (!new File(fileNameString).exists()) {
                return null;
            }
            while (in.ready()) {
                sBuffer.append(in.readLine() + "\n");
            }
            in.close();
            return sBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

class WordBean implements Comparable<WordBean> {
    String key;
    Integer count;

    public WordBean(String key, Integer count) {
        super();
        this.key = key;
        this.count = count;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    //WordBean按照count排序
    @Override
    public int compareTo(WordBean o) {
        int temp = this.count - o.count;
        return temp == 0 ? this.key.compareTo(o.key) : -temp; //逆序负号
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((count == null) ? 0 : count.hashCode());
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WordBean other = (WordBean) obj;
        if (count == null) {
            if (other.count != null)
                return false;
        } else if (!count.equals(other.count))
            return false;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        return true;
    }

}


