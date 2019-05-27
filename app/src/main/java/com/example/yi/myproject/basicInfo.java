package com.example.yi.myproject;

public class basicInfo {

    String url1= "219.219.220.172";
    String url = "47.102.205.118";
    String infourl = "http://" + url + ":8080/SeetaFaceJavaDemo/TeacherInfo?";

    private String infoUrl = "http://" + url + ":8080/SeetaFaceJavaDemo/getInfoServlet?";

    public String getinfoUrl() {
        return infoUrl;
    }//查询学生信息的url

    public String getinfoUrl1() {
        return infourl;
    }//查询老师信息的url

}
