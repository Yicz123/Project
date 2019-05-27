package Teacher;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import com.example.facedemo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class courseRecord extends AppCompatActivity {
    Button start;
    String[] arr;
    ArrayAdapter<String> myAdapter;
    GridView coursename;
    String course;
    ProgressBar pb;
    private GestureDetector gesture;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_record);
        //初始化控件
        init();

        setgesture();
        //把从服务器传回的课程写到gridview上
        setCourse();
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

    //将课程名显示在gridview上
    private void setGridview() {
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < arr.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", arr[i]);
            data.add(map);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.course_item, new String[]{"name"},
                new int[]{R.id.textView}
        );
        coursename.setAdapter(simpleAdapter);
        //设置gridview的监听
        coursename.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!("null").equals(course) && course.length() > 0) {
                    Log.i("nocome", course.length() + "");
                    String str[] = course.split(";");
                    for (int i = 0; i < str.length; i++) {
                        if (id == i) {
                            Log.i("aaa", str[i]);
                            toDetailedrecord(str[i]);
                        }
                    }
                }
            }
        });
    }

    private void toDetailedrecord(String course) {
        Intent intent = new Intent(this, Recorddetailed.class);
        intent.putExtra("course", course);
        startActivity(intent);
    }

    private void setCourse() {
        Intent intent = getIntent();
        course = intent.getStringExtra("course");
        String[] str = course.split(";");
        if (course != null) {
            Log.i("aaa", course);
            //将下拉框的个数与课程数量保持一致
            arr = new String[str.length];
            for (int i = 0; i < str.length; i++) {
                arr[i] = str[i];
            }
        } else {
            arr = new String[]{"1", "2"};
        }
        setGridview();
    }

    private void init() {
        //设置全屏幕，即系统可见ui，且actionbar设置为透明
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        if(Build.VERSION.SDK_INT >= 21){
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        pb = findViewById(R.id.progressBar);
        coursename = findViewById(R.id.coursename);
    }

}
