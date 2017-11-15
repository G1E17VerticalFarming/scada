/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.api;

import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author DanielToft
 */
public class Json {
    /**
     * Reads a json file and returns a freshly created object of the type T
     * passed into the parameter.
     *
     * @param <T> returned type of the request
     * @param fileSource where to read the file from
     * @param classType which type to return
     * @return a new object of the type that was set with the parameter
     * classType
     */
    public static <T> T getJSON(String json, Class<T> classType) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, classType);
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }
    
    public static <T> String stringifyObject(T obj) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            // convert user object to json string and return it 
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
