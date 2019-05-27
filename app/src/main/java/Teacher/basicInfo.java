package Teacher;

public class basicInfo {
    String url = "47.102.205.118";
    String url1 = "219.219.220.172";
    String infourl = "http://" + url + ":8080/SeetaFaceJavaDemo/TeacherInfo?";
    String checkurl = "http://" + url + ":8080/SeetaFaceJavaDemo/ProjectServers?";
    String submiturl = "http://" + url + ":8080/SeetaFaceJavaDemo/Submit?";
    String recordurl = "http://" + url + ":8080/SeetaFaceJavaDemo/Record?";
    String recordurl1 = "http://" + url + ":8080/SeetaFaceJavaDemo/Sinfo?";
    String uploadImgUrl = "http://" + url + ":8080/SeetaFaceJavaDemo/getImageServlet";

    String checkurl1 = "http://" + url1 + ":8080/SeetaFaceJavaDemo/ProjectServers?";
    String feature ="http://" + url + ":8080/SeetaFaceJavaDemo/Returnfeature?";

    public String feature(){
        return feature;
    }

    public String getUploadImgUrl() {
        return uploadImgUrl;
    }

    public String getinfoUrl() {
        return infourl;
    }

    public String getcheckUrl1() {
        return checkurl1;
    }

    public String getcheckUrl() {
        return checkurl;
    }

    public String submitinfoUrl() {
        return submiturl;
    }

    public String getrecordUrl() {
        return recordurl;
    }

    public String getRecordurl1(){return recordurl1;}


}
