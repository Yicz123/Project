package Student;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facedemo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

;import Teacher.Recorddetailed;

public class Student_Info extends AppCompatActivity {
    TextView number, name; //学号和姓名，认为学号即用户名
    ImageView image;
    ListView listView;
    String num[];//缺课次数
    String coursename;
    String userName;  //用户名
    String data;//要传送的数据
    String url;
    String sname, pass, info;
    String course, S_name;
    int coursenum;
    String type = "1";
    Handler handler;
    Bitmap bitmap;
    List<String> courses = new ArrayList<String>(); //课程信息
    private static String userState = "1"; //用户登录状态, 1 表示已登录
    private static final String TAG = "lookForInfo";
    private static final int TIME_OUT = 10 * 1000; // 超时时间
    private static final String CHARSET = "utf-8"; // 设置编码

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_info);
        //初始化
        init();
        //设置基本信息
        getinfo();
        setbutton();
        //设置listview
        //setlistview();
    }

    private void setbutton() {
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toupload();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                for(int i=0;i<coursenum;i++) {
                    if (position == i) {
                        TextView tv = (TextView) view.findViewById(R.id.textView);
                        Log.i("aaa",userName);
                        coursename=tv.getText().toString();
                        // 创建jsonobject类
                        JSONObject Select_jsonObj = new JSONObject();
                        //用于string转换
                        try {
                            Select_jsonObj.put("Sid",userName);//  放入Sid
                            Select_jsonObj.put("coursename",coursename);//  放入coursename
                            Select_jsonObj.put("type",0);//  放入type,0表示获取当前课程的记录
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        data = Select_jsonObj.toString();//将jsonobject转换为String用于发送（比如http协议或是socket协议发送一般都是发送string类型所以此步骤是必须的）
                        Log.i("jsondata",data);
                        new getrecord().start();
                    }
                }
            }
        });
    }

    public void toupload(){
        Intent intent=new Intent(this,uploadImage.class);
        intent.putExtra("Sid",userName);
        startActivity(intent);
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
        number = findViewById(R.id.num);
        name = findViewById(R.id.name);
        image = findViewById(R.id.image);
        listView = findViewById(R.id.listview);
        userName = getIntent().getStringExtra("name");
    }

    //设置基本信息
    private void getinfo() {
        Intent intent = getIntent();
        sname = intent.getStringExtra("name");
        pass = intent.getStringExtra("pass");
        number.setText(userName);
        new link().start();
    }

    //设置listview
    private void setlistview() {
        BaseAdapter adapter = new BaseAdapter() {

            @Override
            public int getCount() {
                return courses.size();
            }

            @Override
            public Object getItem(int position) {
                return courses.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = Student_Info.this.getLayoutInflater();
                View view;
                if (convertView == null) {
                    view = inflater.inflate(R.layout.student_item, null);
                } else {
                    view = convertView;
                    Log.i("info", "有缓存不需要重新生成" + position);
                }
                TextView tv = (TextView) view.findViewById(R.id.textView);
                tv.setText(courses.get(position));
                return view;
            }
        };
        listView.setAdapter(adapter);
    }


    class link extends Thread {
        @SuppressLint("WrongConstant")
        @Override
        public void run() {
            url = new basicInfo().getinfoUrl();
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("name",sname);
                jsonObject.put("pass",pass);
                jsonObject.put("type",type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String data=jsonObject.toString();
            String info = UserService.posttoServerforResult(url,data);
            if (info != null) {
                Log.i("aaa",info);
                String str[] = info.split(",");
                for (int i = 0; i < str.length; i++) {
                    if (i == 0) course = str[i];
                    if (i == 1) S_name = str[i];
                }
            }
            //显示名字
            if (null != S_name)
                name.setText(S_name);
            //显示课程
            String s[] = course.split(";");
            coursenum=s.length;
            for (int i = 0; i < s.length; i++) {
                courses.add(s[i]);
            }
            //获取图片
            new link1().start();
            handler.post(runnable);
            super.run();
        }
    }

    class link1 extends Thread {
        @SuppressLint("WrongConstant")
        @Override
        public void run() {
            url = new basicInfo().getiamgeurl();
            InputStream image = new UserService().loginOfGetimage(url, sname);
            bitmap = BitmapFactory.decodeStream(image);
            handler.post(runnable1);
        }
    }

    class getrecord extends Thread {
        @SuppressLint("WrongConstant")
        @Override
        public void run() {
            url=new basicInfo().getRecordInfo();
            String str = UserService.posttoServerforResult(url, data);
            str=coursename+"：缺课"+str+"次";
            num=new String[1];
            num[0]=str;
            handler.post(runnable2);
            //showListDialog(num);
            Log.i("aaanum",num[0]);
        }
    }

    private void showListDialog(String s[]) {
        AlertDialog.Builder listDialog = new AlertDialog.Builder(Student_Info.this);
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
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            setlistview();
        }
    };

    Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            if(bitmap!=null)
            image.setImageBitmap(bitmap);
        }
    };

    Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            showListDialog(num);
        }
    };

}
