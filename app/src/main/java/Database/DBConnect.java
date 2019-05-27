package Database;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnect {

    public Connection Connect() throws ClassNotFoundException, SQLException {
        Connection conn = null;
        Class.forName("com.mysql.jdbc.Driver"); //加载驱动
        String ip = "47.102.205.118";
        //设置UTF8格式，不然无法识别中文
        String url="jdbc:mysql://47.102.205.118:3306/myproject?useUnicode=true&characterEncoding=UTF-8";
        conn=DriverManager.getConnection(url,"root","root");
       /* conn = DriverManager.getConnection(
                "jdbc:mysql://" + ip + ":3306/" + "myproject",
                "root", "root");*/
        Log.i("connnecttosql", "success");
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("select * from learn where S_id = 'S201901'");
        return conn;
    }


}
