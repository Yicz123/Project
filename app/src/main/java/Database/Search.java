package Database;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Search {
    //获取课程总人数
    public int  searchnum(String courseName) throws SQLException, ClassNotFoundException {
        Connection con = new DBConnect().Connect();
        String sql = "select COUNT(*) as ct from learn where C_name='" + courseName + "'";
        PreparedStatement ps;
        ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt("ct");
    }

    public ResultSet  searchSid(String courseName) throws SQLException, ClassNotFoundException {
        Connection con = new DBConnect().Connect();
        String sql = "select * from learn where C_name='" + courseName + "'";
        Log.i("coursename",sql);
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        return rs;
    }

    public ResultSet  searchStudent(String Sid) throws SQLException, ClassNotFoundException {
        Connection con = new DBConnect().Connect();
        String sql = "select * from student where S_id='"+Sid+"'";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        return rs;
    }

}
