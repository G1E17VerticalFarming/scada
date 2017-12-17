/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.api;

import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.OkHttpClient;

/**
 * Class defining HTTP request functions (POST/GET)
 * @author DanielToft
 */
import java.io.IOException;

public class HttpOkhttpPostSend {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    /**
     * Method to perform a HTTP request POST call with a json encoded object
     * obj, this then waits for a response and prints it accordingly. 
     * @param <T> Generic representation of the object 
     * @param url Where to send the HTTP request
     * @param obj obj to json encode
     * @return HTTP response code based on the response from HTTP request
     * @throws IOException if response body is null or you recieve any http status code other than 200 (OK)
     */
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
    
    /**
     * Method to perform a HTTP request GET call with a json encoded object
     * obj, this then waits for a response and prints it accordingly. 
     * @param <T> Generic representation of the object 
     * @param url Where to send the HTTP request
     * @param classType Which object to convert the response into
     * @return A json decoded object of type classType.
     * @throws IOException if response body is null or you recieve any http status code other than 200 (OK)
     */
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
