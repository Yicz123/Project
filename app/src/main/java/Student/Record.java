package Student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facedemo.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Record extends AppCompatActivity {
    GridView gridView;
    TextView title;
    private TableLayout tableLayout, tableTitle;
    TableRow tableRow;
    int row, col = 2;
    Handler handler;
    String Sid;
    String record[];
    Button all;
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_detailed);
        init();
        new link().start();
    }

    public void init(){
        //设置全屏幕，即系统可见ui，且actionbar设置为透明
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        if(Build.VERSION.SDK_INT >= 21){
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        handler=new Handler();
        Intent intent = getIntent();
        Sid = intent.getStringExtra("Sid");
        title = findViewById(R.id.course);
        all=findViewById(R.id.all);
        all.setVisibility(View.INVISIBLE);
    }
    class link extends Thread {
        @SuppressLint("WrongConstant")
        @Override
        public void run() {
            String url = new basicInfo().getRecordInfo();

            // 创建jsonobject类
            JSONObject Select_jsonObj = new JSONObject();
            String data = null;
            //用于string转换
            try {
                Select_jsonObj.put("Sid",Sid);//  放入Sid
                Select_jsonObj.put("type",1);//  放入type,1表示获取所有记录
            } catch (JSONException e) {
                e.printStackTrace();
            }
            data = Select_jsonObj.toString();//将jsonobject转换为String用于发送（比如http协议或是socket协议发送一般都是发送string类型所以此步骤是必须的）
            Log.i("jsondata",data);

            String str = UserService.posttoServerforResult(url, data);
            if (null != str) {
                if ("".equals(str)) {
                    Looper.prepare();
                    Toast.makeText(Record.this, "没有查询到记录", 1).show();
                    Looper.loop();
                } else {
                    Log.i("aaa", str);
                    record = str.split(",");
                    //表格的行数
                    row = record.length;
                    handler.post(runnable);
                }

            } else {
                Looper.prepare();
                Toast.makeText(Record.this, "服务器连接失败", 1).show();
                Looper.loop();

            }
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            createtable(record);
        }
    };
    private void createtable(String s[]) {

        //获取控件tableLayout
        tableLayout = (TableLayout) findViewById(R.id.table1);
        tableTitle = findViewById(R.id.table);
        //清除表格所有行
        tableLayout.removeAllViews();
        tableTitle.removeAllViews();
        //全部列自动填充空白处
        tableLayout.setStretchAllColumns(true);
        tableTitle.setStretchAllColumns(true);
        //生成X行，Y列的表格
        //生成表头，不随scrollrow滑动
        tableRow = new TableRow(Record.this);
        for (int j = 1; j <= col; j++) {
            TextView tv = new TextView(Record.this);
            tv.setTextSize(20);
            tv.setGravity(Gravity.CENTER);
            switch (j) {
                case 1:
                    tv.setText("课程名");
                    break;
                case 2:
                    tv.setText("缺课次数");
                    break;
            }
            tableRow.addView(tv);
        }
        tableTitle.addView(tableRow, new TableLayout.LayoutParams(MP, WC, 1));


        //内容随scrollrow滑动
        for (int i = 1; i <= row; i++) {
            tableRow = new TableRow(Record.this);
            String str[] = s[i - 1].split(";");
            for (int j = 1; j <= col; j++) {
                    TextView tv = new TextView(Record.this);
                    tv.setTextSize(20);
                    Resources resources = getBaseContext().getResources();
                    tv.setTextColor(Color.parseColor("#336633"));//深绿色
                    tv.setGravity(Gravity.CENTER);
                    tv.setText(str[j - 1]);
                    //1 在res/font下寻找字体文件
                    Typeface typeface = ResourcesCompat.getFont(this, R.font.test);
                    //2 在assets下寻找字体文件
                    //Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/helvetica.ttf");
                    tv.setTypeface(typeface);
                    tableRow.addView(tv);
            }
            //新建的TableRow添加到TableLayout
            tableLayout.addView(tableRow, new TableLayout.LayoutParams(MP, WC, 1));
        }
    }

}
