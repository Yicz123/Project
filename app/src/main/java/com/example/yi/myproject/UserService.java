package com.example.yi.myproject;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserService {

    public String loginOfGet(String ur, String username, String password, String type) {
        try {
            String data = "userName=" + username + "&passWord=" + password + "&type=" + type;
            String path = ur + data;
            Log.i("aaa", path);
            StringBuffer sb = new StringBuffer(path);
            URL url = new URL(sb.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            int responseCode = conn.getResponseCode();//获取响应吗
            if (responseCode == 200) {
                //访问成功
                InputStream is = conn.getInputStream();//得到InputStream输入流
                String state = getstateFromInputstream(is);
                return state;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }

    //请求服务器并得到返回值
    public static String posttoServerforResult(String ur,String data) {
        try {
            StringBuffer sb = new StringBuffer(ur);
            Log.i("aaa",sb.toString());
            URL url = new URL(sb.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("content-type", "text/html");//设置内容类型

            //用流将数据传到服务器
            OutputStream os = conn.getOutputStream();//获取输出流
            os.write(data.getBytes());//写入
            os.flush();

            int responseCode = conn.getResponseCode();//获取响应吗
            if (responseCode == 200) {
                //访问成功
                InputStream is = conn.getInputStream();//得到InputStream输入流
                String state = getstateFromInputstream(is);
                return state;
            } else {
                //访问失败

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }
    private static String getstateFromInputstream(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();//定义一个缓存流
        byte[] buffer = new byte[1024];//定义一个数组，用于读取is
        int len = -1;
        while ((len = is.read(buffer)) != -1) {//将字节写入缓存
            baos.write(buffer, 0, len);
        }
        is.close();//关闭输入流
        String state = baos.toString();//将缓存流中的数据转换成字符串
        baos.close();
        return state;
    }


}