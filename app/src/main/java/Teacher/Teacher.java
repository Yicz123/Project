package Teacher;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.facedemo.R;
import com.example.yi.myproject.ExitApplication;
import com.example.yi.myproject.MyProject;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.Mat;

public class Teacher extends AppCompatActivity {
    String name;
    String pass;
    String course;
    String info;
    String type = "1";//类型2表示获取课程
    int State = 0;
    Button checkinpage, lookinfo, change_pass, abort, courserecord;
    String url;

    ProgressBar pb;
    private SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_menu);
        //获取主页面传来的用户名和密码
        getNameandPass();
        //初始化控件
        init();
        //设置按钮监听
        setbutton();
    }

    private void init() {
        checkinpage = findViewById(R.id.gotoCheckInPage);
        //courserecord = findViewById(R.id.History_record);
        lookinfo = findViewById(R.id.lookforMyInfo);
        change_pass = findViewById(R.id.changePassword);
        abort = findViewById(R.id.abort);
        pb = findViewById(R.id.progressBar);
    }

    private void setbutton() {
        checkinpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                //获取课程，然后在子线程中进入下一个页面
                new link().start();
            }
        });
        /*courserecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                new link1().start();
            }
        });*/

        lookinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                new link2().start();
            }
        });

        change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changepass();
            }
        });

        abort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back_main();
            }
        });
    }

    private void checkinpage() {
        Intent intent = new Intent(this, CheckinPage.class);
        //把课程传到下一个页面
        intent.putExtra("course", course);
        startActivity(intent);
    }

    private void lookrecord() {
        Intent intent = new Intent(this, courseRecord.class);
        intent.putExtra("course", course);
        startActivity(intent);
    }

    private void lookforinfo() {
        Intent intent = new Intent(this, Teacher_Info.class);
        intent.putExtra("name", name);
        intent.putExtra("pass", pass);
        intent.putExtra("info", info);
        startActivity(intent);
    }

    private void changepass() {
        Intent intent = new Intent(this, Password_change.class);
        intent.putExtra("pass", pass);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    private void back_main() {
        Intent intent = new Intent(this, MyProject.class);
        //点击退出登录时，设置State为0并保存
        State = 0;
        saveInfo(State);
        startActivity(intent);
    }

    private void saveInfo(int state) {
        sp = getSharedPreferences("data", MODE_PRIVATE);//data为保存的SharedPreferences文件名
        editor = sp.edit();
        editor.putInt("State", state);
        editor.commit();
    }

    private void getNameandPass() {
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        pass = intent.getStringExtra("pass");
    }

    class link extends Thread {
        @SuppressLint("WrongConstant")
        @Override
        public void run() {
            url = new basicInfo().getinfoUrl();
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("name",name);
                jsonObject.put("pass",pass);
                type="2";//获取课程
                jsonObject.put("type",type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String data=jsonObject.toString();
            course = UserService.posttoServerforResult(url,data);
            if (null != course) {
                pb.setVisibility(View.INVISIBLE);
                //进入考勤页面
                checkinpage();
            } else {
                pb.setVisibility(View.INVISIBLE);
                Looper.prepare();
                Toast.makeText(Teacher.this, "连接服务器失败", 1).show();
                Looper.loop();
            }

            super.run();
        }
    }

    class link2 extends Thread {
        @SuppressLint("WrongConstant")
        @Override
        public void run() {
            url = new basicInfo().getinfoUrl();
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("name",name);
                jsonObject.put("pass",pass);
                type="1";//获取老师的信息
                jsonObject.put("type",type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String data=jsonObject.toString();

            info=UserService.posttoServerforResult(url,data);
            //Log.i("returndata",info);
            if (info != null) {
                pb.setVisibility(View.INVISIBLE);
                //进入查看信息界面
                lookforinfo();
            } else {
                pb.setVisibility(View.INVISIBLE);
                Looper.prepare();
                Toast.makeText(Teacher.this, "连接服务器失败", 1).show();
                Looper.loop();
            }
            super.run();
        }
    }

    //退出时的时间
    private long mExitTime;

    //对返回键进行监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(Teacher.this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            Intent intent = new Intent();
            intent.setClass(Teacher.this, ExitApplication.class);
            //其中FinishActivity 是个空的Activity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}


