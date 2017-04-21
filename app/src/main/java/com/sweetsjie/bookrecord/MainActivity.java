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

import com.idescout.sql.SqlScoutServer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView knowledgeView;
    private List<String> list = new ArrayList<String>();
    private ArrayAdapter<String> adapter = null;
    private Intent intent;
    private MyDataBaseHelper mydatabaseHelper;
    private int i;


    private String text = "劫富济贫武松乘着酒兴，只管走上冈子来。走不到半里多路，见一个败落的山神庙。行到庙前，见这庙门上贴着一张印信榜文，武松住了脚读时，上面写道：\n" +
            "　　阳谷县示：为这景阳冈上新有一只大虫，近来伤害人命，见今杖（zhàng）限各乡里正并猎户人等，打捕未获。如有过往客商人等，可于巳（sì）、午、未三个时辰，结伴过冈。其余时分及单身客人，白日不许过冈，恐被伤害性命不便。各宜知悉。\n" +
            "　　武松读了印信榜文，方知端的有虎。欲待转身再回酒店里来，寻思道：“我回去时，须吃他耻（chǐ）笑，不是好汉，难以转去。”存想了一回，说道：“怕甚么！且只顾上去，看怎地！”武松正走，看看酒涌上来，便把毡笠儿背在脊梁（jǐliang）上，将梢棒绾（wǎn）在肋（lèi）下，一步步上那冈子来。回头看这日色时，渐渐地坠下去了。此时正是十月间天气，日短夜长，容易得晚。武松自言自说道：“哪得甚么大虫！人自怕了，不敢上山。”\n" +
            "　　武松走了一阵，酒力发作，焦热起来，一只手提着梢棒，一只手把胸膛前袒（tǎn）开，踉踉跄跄，直奔乱树林来。见一块光挞挞大青石，把那梢棒倚在一边，放翻身体，却待要睡，只见发起一阵狂风来。那一阵风过处，只听得乱树背后扑地一声响，跳出一只吊睛白额大虫来。\n" +
            "　　武松见了，叫声：“呵呀！”从青石上翻将下来，便拿那条梢棒在手里，闪在青石边。那个大虫又饥又渴，把两只爪在地下略按一按，和身望上一扑，从半空里撺（cuān）将下来。武松被那一惊，酒都做冷汗出了。说时迟，那时快，武松见大虫扑来，只一闪，闪在大虫背后。那大虫背后看人最难，便把前爪搭在地下，把腰胯（kuà）一掀，掀将起来。武松只一闪，闪在一边。大虫见掀他不着，吼一声，却似半天里起个霹雳（pīlì），震得那山冈也动；把这铁棒也似虎尾倒竖起来，只一剪，武松却又闪在一边。原来那大虫拿人，只是一扑，一掀，一剪，三般提不着时，气性先自没了一半。那大虫又剪不着，再吼了一声，一兜兜将回来。\n" +
            "　　武松见那大虫复翻身回来，双手轮起梢棒，尽平生气力，只一棒，从半空劈将下来。只听得一声响，簌（sù）簌地将那树连枝带叶劈脸打将下来。定睛看时，一棒劈不着大虫。原来慌了，正打在枯树上，把那条梢棒折做两截，只拿得一半在手里。那大虫咆哮，性发起来，翻身又只一扑，扑将来。武松又只一跳，却退了十步远，那大虫却好把两只前爪搭在武松面前。武松将半截棒丢在一边，两只手就势把大虫顶花皮胳月荅（gēda）地①揪住，一按按将下来。那只大虫急要挣扎，被武松尽气力纳定，哪里肯放半点儿松宽？武松把只脚望大虫面门上、眼睛里，只顾乱踢。那大虫咆哮起来，把身底下扒起两堆黄泥，做了一个土坑。武松把那大虫嘴直按下黄泥坑里去。那大虫吃武松奈何得没了些气力。武松把左手紧紧地揪住顶花皮，偷出右手来，提起铁锤般大小拳头，尽平生之力，只顾打。打得五七十拳，那大虫眼里、口里、鼻子里、耳朵里都迸（bèng）出鲜血来，更动弹不得，只剩口里兀（wù）自气喘。武松放了手，来松树边寻那打折的棒橛（jué），拿在手里；只怕大虫不死，把棒橛又打了一回。那大虫气都没了。武松再寻思道：“我就地拖得这死大虫下冈子去。”就血泊里双手来提时，哪里提得动！原来使尽了气力，手脚都酥（sū）软了，动掸不得。";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        SqlScoutServer.create(this,getPackageName());



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
                    Intent intentMain = new Intent(MainActivity.this, ListAllFileActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("purpose","knowledge");
                    intentMain.putExtra("information",bundle);
                    startActivityForResult(intentMain, 0);
//                    intent = new Intent(MainActivity.this, KnowledgeAdd.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("name","null");
//                    intent.putExtra("name",bundle);
//                    startActivityForResult(intent, 0);
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
