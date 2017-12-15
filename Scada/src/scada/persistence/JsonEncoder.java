package scada.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * TODO WRITE ME
 */
public class JsonEncoder {
    /**
     * Reads a json file and returns a freshly created object of the type T
     * passed into the parameter.
     *
     * @param <T>        returned type of the request
     * @param fileSource where to read the file from
     * @param classType  which type to return
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

    /**
     * Converts the input obj into a json string object and writes it to filesource
     * @param <T> Generic representation of the object
     * @param fileSource The file to write json object to
     * @param obj object to convert into json
     * @return Json encoded string object which is generic
     */
    public static <T> boolean stringifyObject(String fileSource, T obj) {
        ObjectMapper mapper = new ObjectMapper();

        try (FileWriter fileWriter = new FileWriter(fileSource)) {
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