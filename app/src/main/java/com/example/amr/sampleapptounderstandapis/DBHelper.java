package com.example.amr.sampleapptounderstandapis;

public class DBHelper extends CheckConnection {
    String URL = "http://192.168.1.107/phpinandroid/api.php";
    String url = "";
    String response = "";

    public String ShowAll() {
        try {
            url = URL + "?amr=view";
            System.out.println("URL Tampil helper: " + url);
            response = call(url);
        } catch (Exception e) {
        }
        return response;
    }

    public String insertData(String nama, String alamat) {

        try {
            url = URL + "?amr=insert&nama=" + nama + "&alamat=" + alamat;
            System.out.println("URL Insert helper : " + url);
            response = call(url);
        } catch (Exception e) {
        }
        return response;
    }

    public String getContactByid(int id) {
        try {
            url = URL + "?amr=get_biodata_by_id&id=" + id;
            System.out.println("URL Insert helper : " + url);
            response = call(url);
        } catch (Exception e) {
        }
        return response;
    }

    public String updateData(String id, String nama, String alamat) {

        try {
            url = URL + "?amr=update&id=" + id + "&nama=" + nama + "&alamat=" + alamat;
            System.out.println("URL Insert helper : " + url);
            response = call(url);
        } catch (Exception e) {
        }
        return response;
    }

    public String deleteData(int id) {
        try {
            url = URL + "?amr=delete&id=" + id;
            System.out.println("URL Insert helper : " + url);
            response = call(url);
        } catch (Exception e) {
        }
        return response;
    }

}
