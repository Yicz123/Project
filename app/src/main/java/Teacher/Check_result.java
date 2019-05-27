package Teacher;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facedemo.R;
import com.example.yi.myproject.MyProject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Check_result extends AppCompatActivity {
    TextView totalnum;
    TextView realnum;
    TextView notbenum;
    TextView percent;
    TextView course;
    Button submit;
    String url;
    String coursename;
    private GridView notbe;//未到名单
    String nocome;

    int total, real, nonum;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_result);
        //初始化控件
        init();

        getData();
        //
        setData(total, real);
        //设置按钮监听
        setButton();
    }

    private void setButton() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将最终结果提交
                showdialog();
            }
        });
    }

    private void init() {
        course = findViewById(R.id.coursename);
        totalnum = findViewById(R.id.re_shouldbe);
        realnum = findViewById(R.id.re_actualbe);
        notbe = findViewById(R.id.re_unbeList);
        notbenum = findViewById(R.id.re_didntbe);
        percent = findViewById(R.id.re_percent);
        submit = findViewById(R.id.submit);
    }

    private void getData() {
        Intent intent = getIntent();
        nocome = intent.getStringExtra("nocome");
        coursename = intent.getStringExtra("course");
        total = intent.getIntExtra("totalnum", 1);
        real = intent.getIntExtra("realnum", 1);
        if (real < 0) real = 0;
        nonum = total - real;
        course.setText(coursename);
    }

    private void setData(int total, int real) {
        int notbe = total - real;
        notbenum.setText(notbe + "人");//未到人数
        float per = (float) real / (float) total * 100;
        //保留两位小数
        DecimalFormat fnum = new DecimalFormat("##0.0");
        String p = fnum.format(per);
        percent.setText(p + "%");//到课率
        totalnum.setText(total + "人");
        realnum.setText(real + "人");
        setGridview();
    }

    //将未到名单显示在gridview上
    private void setGridview() {
        String[] names = {""};
        if (null != nocome) {
            Log.i("nocome", nocome);
            String[] str = nocome.split("/");
            names = new String[str.length];
            for (int i = 0; i < str.length; i++) {
                String s[] = str[i].split("-");
                names[i] = s[0];
                Log.i("name", names[i]);
            }
        }

        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < names.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", names[i]);
            data.add(map);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.course_item, new String[]{"name"},
                new int[]{R.id.textView}
        );
        notbe.setAdapter(simpleAdapter);
        //设置gridview的监听
        notbe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!("null").equals(nocome) && nocome.length() > 0) {
                    //Log.i("nocome", nocome.length() + "");
                    String str[] = nocome.split("/");
                    for (int i = 0; i < str.length; i++) {
                        if (id == i) {
                            showNormalDialog(str[i]);
                        }
                    }
                }
            }
        });
    }

    private void showNormalDialog(final String info) {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(Check_result.this);
        normalDialog.setIcon(R.drawable.dh);
        normalDialog.setTitle("Change");
        String[] s = info.split("-");
        normalDialog.setMessage("确定" + s[0] + "已到？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        nocome = nocome.replace(info + "/", "");
                        nocome = nocome.replace(info, "");
                        real += 1;
                        setData(total, real);
                    }
                });
        normalDialog.setNegativeButton("退出",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    private void showdialog() {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(Check_result.this);
        normalDialog.setIcon(R.drawable.dh);
        normalDialog.setTitle("确认");
        normalDialog.setMessage("确定提交最终结果？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        new link().start();

                    }
                });
        normalDialog.setNegativeButton("退出",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    private void showdialog1() {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(Check_result.this);
        normalDialog.setIcon(R.drawable.dh);
        normalDialog.setTitle("确认");
        normalDialog.setMessage("确定退出？结果还未提交");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        back_teacher();
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    class link extends Thread {
        @SuppressLint("WrongConstant")
        @Override
        public void run() {
            url = new basicInfo().submitinfoUrl();
            String tnum = totalnum.getText().toString();
            String rnum = realnum.getText().toString();
            if ("null".equals(nocome)) nocome = "";
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("coursrname",coursename);
                jsonObject.put("tnum",tnum);
                jsonObject.put("rnum",rnum);
                jsonObject.put("nocome",nocome);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String data=jsonObject.toString();
            Log.i("jsondata",data);
            String str = UserService.posttoServerforResult(url,data);
            if (null != str) {
                Looper.prepare();
                Toast.makeText(Check_result.this, "提交成功", 1).show();
                //返回老师的主页面
                back_teacher();
                Looper.loop();
            } else {
                Looper.prepare();
                Toast.makeText(Check_result.this, "连接服务器失败", 1).show();
                Looper.loop();
            }

            super.run();
        }
    }

    private void back_teacher() {
        Intent intent = new Intent(this, MyProject.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        showdialog1();
    }
}

