package Student;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.facedemo.R;
import com.example.yi.myproject.ExitApplication;
import com.example.yi.myproject.MyProject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student extends AppCompatActivity {
    GridView gridview;
    String name;
    String pass;
    int State=0;
    private SharedPreferences sp;
    SharedPreferences.Editor editor;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        View decorView = getWindow().getDecorView();

        //设置全屏
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        if(Build.VERSION.SDK_INT >= 21){
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        //获取登录界面传来的信息
        getNameAndPass();
        //初始化控件
        gridview=findViewById(R.id.Gridview);
        //gridview的设置
        setGridview();

    }

    public void getNameAndPass(){
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        pass = intent.getStringExtra("pass");
    }

    //gridview设置
    private void setGridview(){
       int[] ids = new int[]{
                R.drawable.dh, R.drawable.dh,
                R.drawable.dh, R.drawable.dh,
                R.drawable.dh
        };
       String[] names = new String[]{
                "上传图片", "我的信息","考勤记录", "修改密码","退出登录"
        };
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < 5; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name",names[i]);
            map.put("id", ids[i]);
            data.add(map);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.student_item, new String[]{"name"},
                new int[]{R.id.textView}
        );
        gridview.setAdapter(simpleAdapter);
        //设置gridview的监听
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch ((int) id){
                    case 0:
                        uploadImage();
                        break;
                    case 1: stu_info();
                        break;
                    case 2: record();
                        break;
                    case 3:changepassword();
                        break;
                    case 4:back_main();
                        break;
                }
            }
        });
    }

    private void stu_info(){
        Intent intent=new Intent(this, Student_Info.class);
        intent.putExtra("name", name);
        intent.putExtra("pass", pass);
        startActivity(intent);
    }

    private void changepassword(){
        Intent intent=new Intent(this, Password_change.class);
        intent.putExtra("name", name);
        intent.putExtra("pass", pass);
        startActivity(intent);
    }

    private void back_main(){
        Intent intent=new Intent(this,MyProject.class);
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
    private void uploadImage(){
        Intent intent=new Intent(this, uploadImage.class);
        intent.putExtra("Sid",name);
        startActivity(intent);
    }
    private void record(){
        Intent intent = new Intent(this, Record.class);
        intent.putExtra("Sid", name);
        startActivity(intent);
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
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            Intent intent = new Intent();
            intent.setClass(this, ExitApplication.class);
            //其中FinishActivity 是个空的Activity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
