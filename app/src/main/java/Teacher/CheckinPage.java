package Teacher;

import android.Manifest;
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
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facedemo.R;
import com.example.facedemo.util.DetecteSeeta;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.util.ArrayList;

import seetaface.CMSeetaFace;
import seetaface.SeetaFace;

import static android.view.View.inflate;


public class CheckinPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Button start, chooseimg;
    CMSeetaFace[] face;
    ArrayList<CMSeetaFace> allface=new ArrayList<>();
    ArrayList<Bitmap> allbitmap=new ArrayList<>();
    int imagenum=-1;
    Bitmap bitmap, bitresult, bitget;
    Button getresult;
    ImageView image;
    String picPath;
    String mImagePath, mImageName;
    String[] arr;
    ArrayAdapter<String> myAdapter;
    Spinner sp;
    String course;
    String courseName;//选择的课程名称
    String cname;
    String sid;
    int totalnum;//选择课程的总人数
    int realnum = 0;//实际人数
    int nonum = 0;
    String nocome;//未到名单
    ProgressBar pb;
    Handler handler;
    DetecteSeeta mDetecteSeeta;
    private float mPosX;
    private float mPosY;
    private float mCurrentPosX;
    private float mCurrentPosY;
    private GestureDetector gesture;

    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_page);
        //动态申请读写权限
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }
        //申请相机权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

        } else {
            //否则去请求相机权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        staticLoadCVLibraries();
        //设置滑动的监听
        setgesture();
        //把从服务器传回的课程写到下拉框上
        setCourse();
        //初始化下拉框
        initspinner();
        //初始化控件
        initData();
        //设置按钮监听
        setButton();
    }

    //OpenCV库静态加载并初始化
    private void staticLoadCVLibraries() {
        boolean load = OpenCVLoader.initDebug();
        if (load) {
            Log.i("CV", "Open CV Libraries loaded...");
        }
    }

    private void setgesture() {
        gesture = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            public boolean onFling(MotionEvent e1, MotionEvent e2, float X, float Y) {
                if (Math.abs(e2.getY() - e1.getY()) > 100) {
                    return true;
                }

                if (e1.getRawX() - e2.getRawX() > 100) {
                    return true;
                }

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

    private void setCourse() {
        Intent intent = getIntent();
        arr = new String[]{""};
        course = intent.getStringExtra("course");
        if (!("").equals(course)) {
            String[] str = course.split(";");
            //将下拉框的个数与课程数量保持一致
            arr = new String[str.length];
            for (int i = 0; i < str.length; i++) {
                arr[i] = str[i];
            }
        }

    }

    //下拉框的初始化
    private void initspinner() {
        myAdapter = new ArrayAdapter<String>(this, R.layout.check_textview, arr) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = inflate(getContext(), R.layout.list_item, null);
                TextView label = (TextView) view.findViewById(R.id.tv_name);
                label.setTextColor(Color.BLACK);
                label.setText(arr[position]);
                return view;
            }
        };
        myAdapter.setDropDownViewResource(R.layout.list_item);
    }


    private void initData() {
        handler = new Handler();
        getresult = findViewById(R.id.getresult);
        start = findViewById(R.id.startCheckIn);
        chooseimg = findViewById(R.id.choosePicture);
        image = findViewById(R.id.imageView);
        sp = findViewById(R.id.chooseClass);
        pb = findViewById(R.id.progressBar);
        sp.setAdapter(myAdapter);
    }

    private void setButton() {
        //给image设置监听，切换已选择的图片
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setimage();
            }
        });
        //给image设置长按监听，删除图片
        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showListlongClickDialog();
                Log.i("longclick","longclick");
                return true;
            }
        });
        getresult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getresult();
            }
        });
        //选择图片
        chooseimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListDialog();
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(courseName)) {
                    pb.setVisibility(View.INVISIBLE);
                    Toast.makeText(CheckinPage.this, "您未选择课程", Toast.LENGTH_SHORT).show();
                } else if (bitmap != null) {
                    pb.setVisibility(View.VISIBLE);
                    mDetecteSeeta = new DetecteSeeta();
                    //检测出每张图片中的人脸，并放在allface中
                    for(int i=0;i<allbitmap.size();i++){
                        bitmap=allbitmap.get(i);
                        //检查人脸时将图片放大
                        bitmap = ResizeBitmap(bitmap, 1000);
                        face=mDetecteSeeta.DetectionFace(bitmap);
                        if(face!=null){
                            //把人脸标记出来
                            for(int k=0;k<face.length;k++){
                                bitmap=mDetecteSeeta.DetectionBitmap(bitmap, face[k].left, face[k].top, face[k].right, face[k].bottom, "red");
                            }
                            allbitmap.set(i,bitmap);

                            for(int j=0;j<face.length;j++){
                                allface.add(face[j]);
                            }
                        }

                    }
                    Log.i("allface",allface.size()+"");
                    //把所有图片中检测到的人脸放到face中
                    face=new CMSeetaFace[allface.size()];
                    for(int i=0;i<allface.size();i++){
                        face[i]=allface.get(i);
                    }
                    Log.i("facelength",face.length+"");
                    bitresult = bitmap;

                    if (allface.size()!=0) {
                        pb.setVisibility(View.VISIBLE);
                        //获取信息进行人脸识别
                        nocome=null;
                        nonum=0;
                        realnum=0;
                        new compare().start();
                    } else {
                        handler.post(runnable2);
                        Toast.makeText(CheckinPage.this, "选择的图片中未检测到人脸", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CheckinPage.this, "请选择图片", Toast.LENGTH_SHORT).show();
                }

            }
        });
        sp.setAdapter(myAdapter);
        sp.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String content = parent.getItemAtPosition(position).toString();
        courseName = content;
        //当回到当前页面改变了课程名时使查看按钮不可见
        if (cname != null) {
            if (!cname.equals(courseName)) {
                handler.post(runnable3);
            }
        }
        cname = courseName;
        //Toast.makeText(CheckinPage.this, "点击了:" + content, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setimage(){
        if(imagenum<allbitmap.size()-1){
            image.setImageBitmap(allbitmap.get(imagenum+1));
            imagenum++;
        }else{
            image.setImageBitmap(allbitmap.get(0));
            imagenum=0;
        }
    }


    private void getresult() {
        Intent intent = new Intent(this, Check_result.class);
        intent.putExtra("totalnum", totalnum);
        intent.putExtra("realnum", realnum);
        intent.putExtra("nocome", nocome);
        intent.putExtra("course", courseName);
        //getresult.setVisibility(View.INVISIBLE);
        pb.setVisibility(View.INVISIBLE);
        startActivity(intent);
    }

    private void selectimage() {
        /*** * 这个是调用android内置的intent，来过滤图片文件 ，同时也可以过滤其他的 */
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);//启动相册
        startActivityForResult(intent, 1);
    }

    private void takeImage() {
        createFile();
        mImageName = "" + System.currentTimeMillis() + ".jpg";
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//调用android自带的照相机
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mImagePath + mImageName)));
        startActivityForResult(intent, 2);
    }

    //保存路径
    public void createFile() {
        mImagePath = Environment.getExternalStorageDirectory() + "/mypp/";//指定保存路径
        File f = new File(mImagePath);
        if (!f.exists()) {
            f.mkdir();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
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
                            bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                            allbitmap.add(bitmap);//把选择的图片添加到allbitmap中
                            getresult.setVisibility(View.INVISIBLE);
                            imagenum++;
                            image.setImageBitmap(bitmap);
                            chooseimg.setText("继续添加图片");
                            image.setVisibility(View.VISIBLE);
                        } else {
                            alert();
                        }
                    } else {
                        alert();
                    }
                } catch (Exception e) {
                }
            } else if(requestCode==2){
                //拍照的处理
                Bitmap bm = BitmapFactory.decodeFile(mImagePath + mImageName);
                bitmap = ResizeBitmap(bm, 1000);
                allbitmap.add(bitmap);//把拍摄的照片添加到allbitmap中
                getresult.setVisibility(View.INVISIBLE);
                image.setImageBitmap(bitmap);
                chooseimg.setText("继续添加图片");
                bm.recycle();//太大记得回收
                image.setVisibility(View.VISIBLE);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static Bitmap ResizeBitmap(Bitmap bitmap, int newWidth) {//拍照的图片太大，设置格式大小
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float temp = ((float) height) / ((float) width);
        int newHeight = (int) ((newWidth) * temp);
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        bitmap.recycle();
        return resizedBitmap;
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

    class compare extends Thread {
        @Override
        public void run() {
            String url = new basicInfo().getcheckUrl();
            //获取到选择该课程的所有学生信息
            String allinfo = UserService.getallinfo(url, courseName);
            if (allinfo != null) {
                String info[] = allinfo.split("/");
                totalnum = info.length;
                for (int i = 0; i < totalnum; i++) {
                    String s[] = info[i].split("-");
                    String name = s[0];
                    sid = s[1];
                    String ur = new basicInfo().feature();
                    //根据id获取feature

                    String feature =UserService.getfeature(ur,sid);
                    if(feature!=null){
                        Log.i("aaa",feature);
                        Log.i("aaa",feature.length()+"");
                    }

                    float fea[]=new float[2048];
                    String f[]=feature.split(",");
                    for(int k=0;k<2048;k++){
                        fea[k]=Float.parseFloat(f[k]);
                    }

                    int flag = 0;
                    //获取到的feature与face进行比较
                    if (fea != null) {
                        for (int j = 0; j < face.length; j++) {
                            SeetaFace jni=new SeetaFace();;
                            float m[]=face[j].features;
                            float tSim =jni.CalcSimilarity(m,fea);
                            if (tSim > 0.6) {
                                //用蓝色矩形标记出选择此门课程已到的人脸
                                //bitresult = mDetecteSeeta.DetectionBitmap(bitresult, face[j].left, face[j].top, face[j].right, face[j].bottom, "blue");
                                flag = 1;//表示当前这个人已到
                                realnum++;
                                break;
                            }
                        }
                    }
                    if (flag == 0) {
                        nonum++;
                        if (nocome == null) {
                            nocome = name + "-" + sid;
                        } else {
                            nocome = nocome + "/" + name + "-" + sid;
                        }
                    }
                }
            }
            handler.post(runnable);
            if(nocome==null){
                nocome="";
            }
            else{
                Log.i("nocome", nocome);
            }


        }
    }

    private void showListDialog() {
        final String[] items = {"拍照", "从相册选择"};
        AlertDialog.Builder listDialog = new AlertDialog.Builder(CheckinPage.this);
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // which 下标从0开始
                // ...To-do
                switch (which) {
                    case 0:
                        takeImage();
                        break;
                    case 1:
                        selectimage();
                        break;
                }
            }
        });
        listDialog.show();
    }
    private void showListlongClickDialog(){
        final String[] items = {"移除"};
        AlertDialog.Builder listDialog = new AlertDialog.Builder(CheckinPage.this);
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // which 下标从0开始
                // ...To-do
                switch (which) {
                    case 0:
                        Log.i("imagenum",imagenum+"");
                        allbitmap.remove(allbitmap.get(imagenum));
                        if(allbitmap.size()==0){
                            imagenum=-1;
                            bitmap=null;
                            image.setVisibility(View.INVISIBLE);
                        }
                        else if(imagenum!=0){
                            imagenum--;
                            setimage();
                        }else if(imagenum==0){
                            setimage();
                        }
                        break;
                }
            }
        });
        listDialog.show();
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.i("inrunnable", "inrunnable");
            image.setImageBitmap(bitresult);
            pb.setVisibility(View.INVISIBLE);
            getresult.setVisibility(View.VISIBLE);
        }
    };

    Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            Log.i("inrunnable", "inrunnable");
            pb.setVisibility(View.VISIBLE);
        }
    };

    Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            Log.i("inrunnable", "inrunnable");
            pb.setVisibility(View.INVISIBLE);
        }
    };
    Runnable runnable3 = new Runnable() {
        @Override
        public void run() {
            Log.i("inrunnable", "inrunnable");
            getresult.setVisibility(View.INVISIBLE);
        }
    };

}
