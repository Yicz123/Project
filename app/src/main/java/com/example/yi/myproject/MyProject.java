/*
 **2018.12.15
 * 易成昭
 */
package com.example.yi.myproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.facedemo.R;
import com.example.facedemo.util.AssetsToSd;
import com.example.facedemo.util.FileUtil;
import com.example.facedemo.util.MyConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import Student.Student;
import Teacher.Teacher;
public class MyProject extends AppCompatActivity {
    ImageView Image;
    EditText NameText, PasswordText;
    RadioGroup radioGroup;
    Button Login;
    String name;
    String pass;
    String type = "0";//访问的类型,0表示只验证，不返回信息
    String exist;//判定是否存在输入的账号
    String url;
    int State = 0;//登陆的状态，1表示已登录，0表示未登录
    private SharedPreferences sp;
    SharedPreferences.Editor editor;
    int T_S=0; //标识选择老师或学生，1为老师，2为学生
    String TAG = "permission";
    private ProgressDialog pd;
    private static final int MSG_COPE = 0;
    private static final int MSG_COPE_FAIL = 2;
    ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化控件
        init();
        initData();
        //给按钮设置监听
        ButtonListener();
        initPermission();
    }

    private void initPermission() {
        if (Build.VERSION.SDK_INT < 23) {
            // 如果系统版本低于23，直接跑应用的逻辑
            runApp();
        }else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            } else {
                runApp();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                runApp();
            } else {
                Toast.makeText(this, "获取权限被拒绝后的操作", Toast.LENGTH_SHORT).show();
                Log.i("TAG","获取权限被拒绝后的操作");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void runApp() {
        FileUtil.createDir(MyConfig.FACE_LIBRARY);
        FileUtil.createDir(MyConfig.ROOT_CACHE+ File.separator+MyConfig.FACE_DIR);
        FileUtil.createDir(MyConfig.FACE_CACHE);

        /* 显示ProgressDialog */
        if (new File(MyConfig.model1).exists()&&new File(MyConfig.model2).exists()&&new File(MyConfig.model3).exists()){
            //startToMain();
            return;
        }
        pd = pd.show(this,"复制必要文件", "复制中，请稍后……");
        /* 开启一个新线程，在新线程里执行耗时的方法 */
        new Thread(new Runnable() {
            @Override
            public void run() {
                AssetsToSd.getInstance(MyProject.this).copyAssetsToSD("Model",MyConfig.modelPath).setFileOperateCallback(new AssetsToSd.FileOperateCallback() {
                    @Override
                    public void onSuccess() {
                        // TODO: 文件复制成功时，主线程回调
                        handler.sendEmptyMessage(MSG_COPE);
                    }

                    @Override
                    public void onFailed(String error) {
                        // TODO: 文件复制失败时，主线程回调
                        handler.sendEmptyMessage(MSG_COPE_FAIL);
                    }
                });
            }
        }).start();
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int mCode = msg.what;
            switch (mCode){
                case MSG_COPE:
                    Log.d(TAG,"复制完成！");
                    Toast.makeText(MyProject.this,"复制完成！",Toast.LENGTH_SHORT).show();
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                    //startToMain();
                    break;
                case MSG_COPE_FAIL:
                    Log.d(TAG,"复制失败！");
                    Toast.makeText(MyProject.this,"复制失败！",Toast.LENGTH_SHORT).show();
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                    finish();
                    break;
            }
        }
    };

    private void init() {
        //设置全屏幕，即系统可见ui，且actionbar设置为透明
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        if(Build.VERSION.SDK_INT >= 21){
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        Image = findViewById(R.id.image);
        NameText = findViewById(R.id.NameText);//用户名
        PasswordText = findViewById(R.id.PasswordText);//密码
        radioGroup = findViewById(R.id.RadioGroup);
        Login = findViewById(R.id.Login);
        pb=findViewById(R.id.progressBar);
    }

    private void initData() {
        //从sharedpreference中读取信息
        sp = getSharedPreferences("data", MODE_PRIVATE);
        if (sp.getString("name", "") != "") {
            NameText.setText(sp.getString("name", "00"));
            PasswordText.setText(sp.getString("password", ""));
            name = NameText.getText().toString();
            pass = PasswordText.getText().toString();
            //登录状态为1时，直接进入下一个页面
            if ((sp.getInt("State", 0)) == 1) {
                if(name.contains("T"))
                new link().start();
                else if(name.contains("S"))
                    new link1().start();
            }
        }
    }

    private void ButtonListener() {
        //给radiogroup设置监听
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.Teacher) {
                    T_S = 1;
                } else {
                    T_S = 2;
                }
            }
        });

        //给Login设置监听
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                if (T_S==1) {
                    //进入老师的操作界面
                    name = NameText.getText().toString();
                    pass = PasswordText.getText().toString();
                    //先判断输入的内容是否为空
                    if (name.equals("") || pass.equals("")) {
                        if (name.equals("")) {
                            //Toast.makeText(MainActivity.this, "Name不能为空", Toast.LENGTH_SHORT).show();
                            NameText.setError("用户名不能为空");
                            NameText.requestFocus();
                        } else {
                            //Toast.makeText(MainActivity.this, "Password不能为空", Toast.LENGTH_SHORT).show();
                            PasswordText.setError("密码不能为空");
                            PasswordText.requestFocus();
                        }
                    } else {
                        new link().start();
                    }
                } else if(T_S==2){
                    //进入学生操作的界面
                    name = NameText.getText().toString();
                    pass = PasswordText.getText().toString();
                    //先判断输入的内容是否为空
                    if (name.equals("") || pass.equals("")) {
                        if (name.equals("")) {
                            //Toast.makeText(MainActivity.this, "Name不能为空", Toast.LENGTH_SHORT).show();
                            NameText.setError("用户名不能为空");
                            NameText.requestFocus();
                        } else {
                            //Toast.makeText(MainActivity.this, "Password不能为空", Toast.LENGTH_SHORT).show();
                            PasswordText.setError("密码不能为空");
                            PasswordText.requestFocus();
                        }
                    } else {
                        //登录
                        new link1().start();
                    }
                }else{
                    pb.setVisibility(View.INVISIBLE);
                    Toast.makeText(MyProject.this, "请选择登录身份", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //进入老师操作的界面
    private void startTeacher() {
        Intent intent = new Intent(this, Teacher.class);
        intent.putExtra("name", name);
        intent.putExtra("pass", pass);
        startActivity(intent);

    }

    //进入学生操作的界面
    private void startStudent() {
        Intent intent = new Intent(this, Student.class);
        intent.putExtra("name", name);
        intent.putExtra("pass", pass);
        startActivity(intent);
    }

    class link extends Thread {
        @SuppressLint("WrongConstant")
        @Override
        public void run() {

            url = new basicInfo().getinfoUrl1();
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("name",name);
                jsonObject.put("pass",pass);
                jsonObject.put("type",type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String data=jsonObject.toString();
            exist = UserService.posttoServerforResult(url,data);
            pb.setVisibility(View.INVISIBLE);
            if (null == exist) {
                Looper.prepare();
                Toast.makeText(MyProject.this, "连接服务器失败", 1).show();
                Looper.loop();
            } else {
                String s = "0";//用户不存在
                String s1 = "00";//密码错误
                //用户不存在
                if (s.equals(exist)) {
                    //在线程中使用Toast要用Looper
                    Looper.prepare();
                    Toast.makeText(MyProject.this, "用户不存在", 1).show();
                    Looper.loop();

                }
                //密码错误
                else if (s1.equals(exist)) {
                    Looper.prepare();
                    Toast.makeText(MyProject.this, "密码错误", 1).show();
                    Looper.loop();
                }
                //用户名和密码均正确
                else {
                    //将信息存在sharedpreference中
                    saveInfo(name, pass);
                    //进入老师操作的界面
                    startTeacher();
                }
            }
            super.run();
        }
    }
    class link1 extends Thread {
        @SuppressLint("WrongConstant")
        @Override
        public void run() {
            url = new basicInfo().getinfoUrl();
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("name",name);
                jsonObject.put("pass",pass);
                jsonObject.put("type",type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String data=jsonObject.toString();
            exist = UserService.posttoServerforResult(url,data);
            pb.setVisibility(View.INVISIBLE);
            if (null == exist) {
                Looper.prepare();
                Toast.makeText(MyProject.this, "连接服务器失败", 1).show();
                Looper.loop();
            } else {
                String s = "0";//用户不存在
                String s1 = "00";//密码错误
                //用户不存在
                if (s.equals(exist)) {
                    //在线程中使用Toast要用Looper
                    Looper.prepare();
                    Toast.makeText(MyProject.this, "用户不存在", 1).show();
                    Looper.loop();

                }
                //密码错误
                else if (s1.equals(exist)) {
                    Looper.prepare();
                    Toast.makeText(MyProject.this, "密码错误", 1).show();
                    Looper.loop();
                }
                //用户名和密码均正确
                else {
                    //将信息存在sharedpreference中
                    saveInfo(name, pass);
                    //进入学生操作的界面
                    startStudent();
                }
            }
            super.run();
        }
    }

    //使用SharedPreferences保存登录信息
    private void saveInfo(String et_name, String et_password) {
        sp = getSharedPreferences("data", MODE_PRIVATE);//data为保存的SharedPreferences文件名
        editor = sp.edit();
        editor.putString("name", et_name);
        editor.putString("password", et_password);
        State = 1;
        editor.putInt("State", State);
        editor.commit();
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
            Toast.makeText(MyProject.this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            Intent intent = new Intent();
            intent.setClass(MyProject.this, ExitApplication.class);
            //其中FinishActivity 是个空的Activity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
