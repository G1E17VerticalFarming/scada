/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.persistence;

import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileWriter;

/**
 *
 * @author DanielToft
 */
public class JsonEncoder {
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
    public static <T> T getJSON(String fileSource, Class<T> classType) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(new File(fileSource), classType);
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }
    
    public static <T> boolean stringifyObject(String fileSource, T obj) {
        ObjectMapper mapper = new ObjectMapper();

        try(FileWriter fileWriter = new FileWriter(fileSource)) {
            // convert user object to json string and return it 
            fileWriter.write(mapper.writeValueAsString(obj));
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }
    }
}