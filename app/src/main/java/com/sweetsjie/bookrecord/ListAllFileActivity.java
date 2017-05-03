package com.sweetsjie.bookrecord;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
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
    private Intent intentGet;
    private Bundle bundle;
    private String nameString;
    private MyDataBaseHelper myDataBaseHelper;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取上一活动传过来的数据
        intentGet = getIntent();
        bundle =  intentGet.getBundleExtra("information");

        //初始化progressDialog控件
        progressDialog = new ProgressDialog(ListAllFileActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在识别·········");

        //获取文件path
        File path = android.os.Environment.getExternalStorageDirectory();
        File[] f = path.listFiles();

        fill(f);
    }

    //获取file名字添加到listview
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

    //判断是否为txt后缀文件
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

    //获取文件名字Array
    private String[] fileToStrArr(List<File> fl) {
        ArrayList<String> fnList = new ArrayList<String>();
        for (int i = 0; i < fl.size(); i++) {
            nameString = fl.get(i).getName();
            fnList.add(nameString);
        }
        return fnList.toArray(new String[0]);
    }



    //lsitview按键监听事件
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        //获取文件path
        File file = fileList.get(position);
        if (file.isDirectory()) {
            File[] f = file.listFiles();
            fill(f);
        } else {
            //进度圆显示
            progressDialog.show();

            //获取文件实例
            final File file1 = new File(file.getAbsolutePath());

            //获取线程
            new Thread(new Runnable(){
                public void run() {
                    //数据库类初始化
                    myDataBaseHelper = new MyDataBaseHelper(ListAllFileActivity.this, "knowledge.db", null, 1);
                    SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();

                    ContentValues values0 = new ContentValues();
                    ContentValues values1 = new ContentValues();
                    ContentValues values2 = new ContentValues();

                    //获取上一活动传递过来的数据
                    String purpose = bundle.getString("purpose");
                    String knowledgeName = bundle.getString("knowledgeName");

                    if (purpose.equals("knowledge")){
                        try {
                            //分词子函数操作   传输file实例和前x个高频词
                            countLetter(file1, 8);
                            intentGet.setClass(ListAllFileActivity.this, MainActivity.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //异步数据操作
                        Message message = new Message();
                        message.what = 0;
                        handler.sendMessage(message);

                        startActivity(intentGet);
                    }
                    else {
                        //获取导入题库txt内容
                        values2.put("subject",getStringFromFile(file1.getAbsolutePath(), "GB2312"));

                        String ID = null;
                        //获取相应的id值
                        Cursor cursor = db.query("knowledge", null, null, null, null, null, null);
                        if (cursor.moveToFirst()) {
                            do {
                                String buf = cursor.getString((cursor.getColumnIndex("knowledge")));
                                if (buf.equals(knowledgeName)){
                                    ID = cursor.getString((cursor.getColumnIndex("id")));
                                }
                            } while (cursor.moveToNext());
                        }
                        cursor.close();

                        //把获取的题库更新数据库表subject
                        db.update("subject",values2,"id=?", new String[]{ID});
                        intentGet.setClass(ListAllFileActivity.this, KnowledgeAdd.class);
                        //设置回传数据
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

    //分词子函数
    public void countLetter(File file, int frequency) throws Exception {

        //数据库类初始化
        myDataBaseHelper = new MyDataBaseHelper(ListAllFileActivity.this, "knowledge.db", null, 1);
        final SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();

        ContentValues values0 =new ContentValues();
        ContentValues values1 =new ContentValues();
        ContentValues values2 =new ContentValues();



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
                //两字和三字存进list
                listSelecting.add(string);
            } else if (string.length() > 3) {
                //把四字词直接添加数据库
                values0.put("knowledge", string);
                values1.put("page","null");
                values2.put("subject","null");
                db.insert("knowledge", null, values0);
                db.insert("page",null,values1);
                db.insert("subject",null,values2);
                values0.clear();
                values1.clear();
                values2.clear();
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

                //把高频两字和三字词存入数据库
                values0.put("knowledge", bean.getKey());
                values1.put("page", "null");
                values2.put("subject", "null");

                db.insert("knowledge", null, values0);
                db.insert("page", null, values1);
                db.insert("subject", null, values2);

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


