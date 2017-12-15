package scada.domain.interfaces;

import shared.Log;

import java.io.IOException;
import java.util.List;

/**
 * TODO WRITE ME
 */
public interface ReadWriteLog {

    List<Log> readLogFile() throws IOException, ClassNotFoundException;

    void writeLogFile(List<Log> logList) throws IOException;

}
