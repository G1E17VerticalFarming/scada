/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import scada.persistence.ProductionBlock;

/**
 *
 * @author DanielToft
 */
public interface ReadWriteProductionBlock {
    
    public abstract List<ProductionBlock> readPLCFile() throws IOException, ClassNotFoundException;
    
    public abstract void writePLCFile(ArrayList<ProductionBlock> plcList) throws IOException;
    
}
