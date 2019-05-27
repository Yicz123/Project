package Student;

public class basicInfo {

    String url1= "219.219.220.172";
    String url = "47.102.205.118";

    private String infoUrl = "http://" + url + ":8080/SeetaFaceJavaDemo/getInfoServlet?";
    private String recordInfo = "http://" + url + ":8080/SeetaFaceJavaDemo/getRecordServlet";
    private String image = "http://" + url + ":8080/SeetaFaceJavaDemo/Returnimage?";
    private String feature = "http://" + url + ":8080/SeetaFaceJavaDemo/Getfeature";

    public String getinfoUrl() {
        return infoUrl;
    }//查询学生信息的url

    public String getRecordInfo() {
        return recordInfo;
    }

    public String getfeatureurl() {
        return feature;
    }

    public String getiamgeurl() {
        return image;
    }

}
