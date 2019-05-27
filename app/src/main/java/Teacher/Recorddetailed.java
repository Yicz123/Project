package Teacher;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facedemo.R;

public class Recorddetailed extends AppCompatActivity {
    String course;
    String url;
    Button all;
    TextView title;
    String arr[];
    String arc[];
    GestureDetector gesture;
    //要更新UI，不能再子线程中进行，用handler进行线程间通信
    Handler handler = null;
    int row, col = 4;
    private TableLayout tableLayout, tableTitle;
    TableRow tableRow;
    String record[];
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int MP = ViewGroup.LayoutParams.MATCH_PARENT;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_detailed);
        init();
        setbutton();
        setgesture();
        setdata();
        //连接服务器查找课程记录
        linkserver();
    }

    private void setgesture() {
        gesture = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            public boolean onFling(MotionEvent e1, MotionEvent e2, float X, float Y) {
                if (Math.abs(e2.getY() - e1.getY()) > 100) {
                    //Toast.makeText(courseRecord.this, "ERROR OPERATION", Toast.LENGTH_SHORT).show();
                    return true;
                }
                //向右滑
                if (e1.getRawX() - e2.getRawX() > 100) {
                    return true;
                }

                //向左滑
                if (e2.getRawX() - e1.getRawX() > 100) {
                    finish();
                    return true;
                }
                return super.onFling(e1, e2, X, Y);
            }
        });
    }

    public boolean onTouchEvent(MotionEvent event) {
        gesture.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void linkserver() {
        new link().start();
    }

    private void setdata() {
        Intent intent = getIntent();
        course = intent.getStringExtra("course");
        title.setText(course);
        title.setTextColor(Color.DKGRAY);
        Typeface tf = ResourcesCompat.getFont(this,R.font.test);
        title.setTypeface(tf);
        Log.i("aaa", course);
    }

    private void init() {
        //设置全屏幕，即系统可见ui，且actionbar设置为透明
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        if(Build.VERSION.SDK_INT >= 21){
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        handler = new Handler();
        all=findViewById(R.id.all);
        title = findViewById(R.id.course);
    }
    private void setbutton(){
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new link1().start();
            }
        });
    }

    class link extends Thread {
        @SuppressLint("WrongConstant")
        @Override
        public void run() {
            url = new basicInfo().getrecordUrl();
            String str = UserService.Record(url, course);
            if (null != str) {

                if ("".equals(str)) {
                    Looper.prepare();
                    Toast.makeText(Recorddetailed.this, "没有查询到记录", 1).show();
                    Looper.loop();
                } else {
                    Log.i("aaa", str);
                    record = str.split(",");
                    //表格的行数
                    row = record.length;
                    //createtable(record);
                    handler.post(runnable);
                }

            } else {
                Looper.prepare();
                Toast.makeText(Recorddetailed.this, "服务器连接失败", 1).show();
                Looper.loop();

            }
        }
    }

    class link1 extends Thread{
        public void run(){
            url=new basicInfo().getRecordurl1();
            String allrecord=UserService.getallrecord(url,course);
            Log.i("allrecord",allrecord);
            show(allrecord);
        }
    }

    public void show(String allrecord){
        Intent intent =new Intent(this,allrecord.class);
        intent.putExtra("allrecord",allrecord);
        startActivity(intent);
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
        tableRow = new TableRow(Recorddetailed.this);
        for (int j = 1; j <= col; j++) {
            TextView tv = new TextView(Recorddetailed.this);
            tv.setTextSize(20);
            tv.setGravity(Gravity.CENTER);
            switch (j) {
                case 1:
                    tv.setText("日期");
                    break;
                case 2:
                    tv.setText("应到人数");
                    break;
                case 3:
                    tv.setText("未到人数");
                    break;
                case 4:
                    tv.setText("未到名单");
                    break;
            }
            Typeface typeface = ResourcesCompat.getFont(this,R.font.test);
            tv.setTypeface(typeface);
            tv.setTextColor(Color.DKGRAY);
            tableRow.addView(tv);
        }
        tableTitle.addView(tableRow, new TableLayout.LayoutParams(MP, WC, 1));


        //内容随scrollrow滑动
        for (int i = 1; i <= row; i++) {
            tableRow = new TableRow(Recorddetailed.this);
            String str[] = s[i - 1].split(";");
            for (int j = 1; j <= col; j++) {
                if (j < 4) {
                    TextView tv = new TextView(Recorddetailed.this);
                    tv.setTextSize(20);
                    Resources resources = getBaseContext().getResources();
                    tv.setTextColor(resources.getColor(R.color.blue));
                    tv.setGravity(Gravity.CENTER);
                    if(j==3){
                        //计算未到人数
                        str[2] = Integer.parseInt(str[1]) - Integer.parseInt(str[2]) + "";
                    }
                    tv.setText(str[j - 1]);
                    Typeface tf= ResourcesCompat.getFont(this, R.font.test);
                    tv.setTypeface(tf);
                    tv.setTextColor(Color.DKGRAY);
                    tableRow.addView(tv);
                } else {
                    Button button = new Button(Recorddetailed.this);
                    Resources resources = getBaseContext().getResources();
                    button.setTextColor(resources.getColor(R.color.blue));
                    button.setBackgroundColor(Color.parseColor("#00000000"));
                    Typeface tf = ResourcesCompat.getFont(this,R.font.test);
                    button.setTypeface(tf);
                    button.setText("查看未到名单");
                    button.setTextColor(Color.DKGRAY);
                    button.getPaint().setFakeBoldText(true);//设置加粗
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (str.length >= 4) {
                                if (!("null").equals(str[3])) {
                                    String s[] = str[3].split("/");
                                    arr = new String[s.length];
                                    for (int m = 0; m < s.length; m++) {
                                        arr[m] = s[m];
                                    }
                                } else {
                                    arr = new String[]{""};
                                }
                            } else {
                                arr = new String[]{""};
                            }
                            showListDialog(arr);
                        }
                    });
                    tableRow.addView(button);
                }
            }
            //新建的TableRow添加到TableLayout
            tableLayout.addView(tableRow, new TableLayout.LayoutParams(MP, WC, 1));
        }
    }

    private void showListDialog(String s[]) {
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(Recorddetailed.this);
        if (s != null) {
            listDialog.setItems(s, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // which 下标从0开始
                    // ...To-do
                    switch (which) {

                    }
                }
            });
        }
        listDialog.show();
        Log.i("aaa","testdialog");
    }
}

