package scada.domain.interfaces;

import shared.Log;

import java.io.IOException;
import java.util.List;

/**
 * Interface defining operations regarding Log
 */
public interface ReadWriteLog {
    
    /**
     * Method to read Logs json file
     * @return List of Log objects found in the json file
     * @throws IOException If an error occurs during file write
     */
    public abstract List<Log> readLogFile() throws IOException;

    /**
     * Method to write Log objects to the Logs json file
     * @param logList List of Log objects to write to the json file
     * @throws IOException If an error occurs during file write
     */
    public abstract void writeLogFile(List<Log> logList) throws IOException;

}
