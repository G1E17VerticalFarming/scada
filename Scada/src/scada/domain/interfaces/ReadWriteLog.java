/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.domain.interfaces;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import shared.Log;

/**
 *
 * @author DanielToft
 */
public interface ReadWriteLog {
    
    public abstract List<Log> readLogFile() throws IOException, ClassNotFoundException;
    
    public abstract void writeLogFile(List<Log> logList) throws IOException;
    
}
