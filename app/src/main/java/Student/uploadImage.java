package Student;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.facedemo.R;
import com.example.facedemo.util.DetecteSeeta;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import Teacher.CheckinPage;
import seetaface.CMSeetaFace;
import seetaface.SeetaFace;

public class uploadImage extends AppCompatActivity {
    Button select, upload;
    String picPath = null;
    String Sid;
    ImageView image;
    Bitmap bt;
    File file;
    ProgressBar pb;
    Handler handler;
    private static String requestURL = "http://47.102.205.118:8080/SeetaFaceJavaDemo/getImageServlet?";
    // private String requestURL = new basicInfo().getUploadImgUrl();


    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_uploadimage);
        //动态申请读写权限
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }

        init();
        setbutton();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                Log.i("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
            }
        }
    }

    private void init() {
        //设置全屏幕，即系统可见ui，且actionbar设置为透明
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        if(Build.VERSION.SDK_INT >= 21){
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        handler=new Handler();
        select = findViewById(R.id.select);
        upload = findViewById(R.id.upload);
        image = findViewById(R.id.image);
        pb=findViewById(R.id.progressBar);
        Intent intent = getIntent();
        Sid = intent.getStringExtra("Sid");
    }

    private void setbutton() {
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectimage();
            }
        });
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectimage();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new link().start();
            }
        });
    }

    private void selectimage() {
        /*** * 这个是调用android内置的intent，来过滤图片文件 ，同时也可以过滤其他的 */
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            /** * 当选择的图片不为空的话，在获取到图片的途径 */
            Uri uri = data.getData();
            Log.e("tag", "uri = " + uri);
            try {
                String[] pojo = {MediaStore.Images.Media.DATA};
                Cursor cursor = managedQuery(uri, pojo, null, null, null);
                if (cursor != null) {
                    ContentResolver cr = this.getContentResolver();
                    int colunm_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String path = cursor.getString(colunm_index);
                    /***
                     * * 这里加这样一个判断主要是为了第三方的软件选择，比如：使用第三方的文件管理器的话，你选择的文件就不一定是图片了，
                     * * 这样的话，我们判断文件的后缀名 如果是图片格式的话，那么才可以
                     */
                    if (path.endsWith("jpg") || path.endsWith("png")) {
                        picPath = path;
                        bt = BitmapFactory.decodeStream(cr.openInputStream(uri));
                        image.setImageBitmap(bt);
                        image.setVisibility(View.VISIBLE);
                    } else {
                        alert();
                    }
                } else {
                    alert();
                }
            } catch (Exception e) {
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void alert() {
        Dialog dialog = new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("您选择的不是有效的图片")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        picPath = null;
                    }
                }).create();
        dialog.show();
    }

    class link extends Thread {
        @SuppressLint("WrongConstant")
        @Override
        public void run() {
            if(picPath==null){
                Looper.prepare();
                Toast.makeText(uploadImage.this, "请选择图片", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }else if (Sid != null&&picPath!=null) {
                DetecteSeeta mDetecteSeeta=new DetecteSeeta();
                CMSeetaFace face[] = mDetecteSeeta.DetectionFace(bt);
                if(face==null){
                    Looper.prepare();
                    Toast.makeText(uploadImage.this,"所选图片中未检测到人脸",Toast.LENGTH_SHORT).show();
                }else{
                    //获取上传图片的特征
                    float feature[]=face[0].features;
                    String a="";
                    for(int i=0;i<feature.length;i++){
                        if(i==feature.length-1)
                            a+=feature[i]+"";
                        else
                            a+=feature[i]+",";
                    }
                    String url=new basicInfo().getfeatureurl();
                    handler.post(runnable);

                    // 创建jsonobject类
                    JSONObject Select_jsonObj = new JSONObject();
                    //用于string转换
                    try {
                        Select_jsonObj.put("Sid",Sid);//  放入Sid
                        Select_jsonObj.put("feature",a);//  feature
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String data = Select_jsonObj.toString();//将jsonobject转换为String用于发送（比如http协议或是socket协议发送一般都是发送string类型所以此步骤是必须的）
                    Log.i("jsondata",data);

                    //上传特征
                    int re=UserService.postoServer(url,data);
                    //上传图片
                    int request = UserService.loginOfGet1(requestURL, Sid, picPath);
                    if (re==200&&request==200) {
                        Log.i("answer","feature");
                        handler.post(runnable1);
                        Looper.prepare();
                        Toast.makeText(uploadImage.this, "上传图片成功", Toast.LENGTH_SHORT).show();
                        finish();
                        Looper.loop();
                    }else{
                        Looper.prepare();
                        Toast.makeText(uploadImage.this, "上传失败", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            super.run();
        }
    }
    class link1 extends Thread {
        @SuppressLint("WrongConstant")
        @Override
        public void run() {
            Socket socket;
            try {
                socket = new Socket("219.219.220.172", 40000);
                //socket = new Socket("192.168.0.110", 8080);
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //读取图片到ByteArrayOutputStream
                bt.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] bytes = baos.toByteArray();
                out.write(bytes);
                Log.i("aaa", "aaa----------------------------------------------------");
                Log.i("aaa", bytes.length + "");
                out.close();
                socket.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            super.run();
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            pb.setVisibility(View.VISIBLE);
        }
    };
    Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            pb.setVisibility(View.INVISIBLE);
        }
    };
}
