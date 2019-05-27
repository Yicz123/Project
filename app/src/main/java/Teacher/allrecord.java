package Teacher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.example.facedemo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class allrecord extends Activity {
    String allrecord;
    GridView record;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allrecord);
        //初始化控件
        init();
        getData();
        setGridview();
    }
    private void init() {
        record=findViewById(R.id.record);
    }
    private void getData() {
        Intent intent = getIntent();
        allrecord = intent.getStringExtra("allrecord");
        Log.i("aaa",allrecord);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        finish();
        return true;
    }
    private void setGridview() {
        String[] names = {"", ""};
        if (!allrecord.equals("null")) {
            Log.i("aaa", allrecord);
            String[] str = allrecord.split(",");
            names = new String[str.length];
            for (int i = 0; i < str.length; i++) {
                names[i] = str[i];
            }
        }

        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < names.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            String str[]=names[i].split(";");
            if(str.length>1)
                names[i]=str[0]+","+str[1]+"次";
            Log.i("aaa",names[i]);
            map.put("name", names[i]);
            data.add(map);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.course_item, new String[]{"name"},
                new int[]{R.id.textView}
        );
        record.setAdapter(simpleAdapter);
        //设置gridview的监听
        record.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch ((int) id) {
                }
            }
        });
    }
}

