/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.api;

import shared.ProductionBlock;

/*import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.OkHttpClient;*/

import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.OkHttpClient;

/**
 *
 * @author DanielToft
 */
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpOkhttpPostSend {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            //System.out.println(doPostRequest("http://skjoldtoft.dk/daniel/g1e17/config_switch.php", "{\"case\":\"getgeoaddresslist\"}"));
            /*System.out.println(doPostRequest("http://localhost:8080/user/", "{\n" +
"  	\"id\":2,\n" +
"    \"content\":\"wowowo\",\n" +
"    \"test\":{\n" +
"    	\"id\":1,\n" +
"          \"testString\":\"hunnar p makker, det virker\"\n" +
"    }\n" +
"}"));*/
            //ProductionBlock[] pbArr = doGetRequest("http://localhost:8080/production_block/", ProductionBlock[].class);
            //System.out.println(pbArr[0].getName());
            System.out.println(doGetRequest("http://localhost:8081/ping/", Boolean.class));
        } catch (IOException ex) {
            Logger.getLogger(HttpOkhttpPostSend.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*public static <T> String doPostRequest(String url, T obj) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, Json.stringifyObject(obj));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }*/
    
    public static <T> String doPostRequest(String url, T obj) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, Json.stringifyObject(obj));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        if(response.body() == null) {
            throw new IOException("Error[" + response.code() + "]: NullPointerException on response.body()");
        }
        if(response.code() == 200) {
            return response.code() + ": " + response.body().string();
        }
        throw new IOException("Error[" + response.code() + "]: " + response.body().string());
    }
    
    public static <T> T doGetRequest(String url, Class<T> classType) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        if(response.body() == null) {
            throw new IOException("Error[" + response.code() + "]: NullPointerException on response.body()");
        }
        if(response.code() == 200) {
            return Json.getJSON(response.body().string(), classType);
        }
        throw new IOException("Error[" + response.code() + "]: " + response.body().string());
    }

}
