package Student;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

public class UserService {

    //连接服务器并获取返回的结果
    public static String loginOfGet(String ur, String username, String password, String type) {
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
            } else {
                //访问失败

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }


    public static InputStream loginOfGetimage(String ur, String username) {
        try {
            String data = "userName=" + username;
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
                return is;
            } else {
                //访问失败

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }


    //上传图片
    public static int loginOfGet1(String ur, String studentname, String picPath) {
        File file = new File(picPath);
        int res = 0;
        String result = null;
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        // Log.i("aaa",RequestURL);
        try {
            String data = "Sid=" + studentname;
            String path = ur + data;
            Log.i("aaa", path);
            StringBuffer sb1 = new StringBuffer(path);
            URL url = new URL(sb1.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setReadTimeout(10 * 1000);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存

            conn.setRequestProperty("Charset", "utf-8"); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            if (file != null) {
                Log.i("aaa", "file");
                /**
                 * 当文件不为空时执行上传
                 */
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件,key要写成img
                 * filename是文件的名字，包含后缀名
                 */
                sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""
                        + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset="
                        + "utf-8" + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                FileInputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;

                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                int responseCode = conn.getResponseCode();//获取响应吗
                if (responseCode == 200) {
                    //访问成功
                    InputStream input = conn.getInputStream();//得到InputStream输入流
                    String state = getstateFromInputstream(input);
                    return responseCode;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
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

    //请求服务器（无返回值）
    public static int  postoServer(String ur,String data) {
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
                return responseCode;
            } else {
                //访问失败

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return 0;
    }

    //获取从服务器返回的数据转化为字符串
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