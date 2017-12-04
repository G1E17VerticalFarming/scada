/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.domain;

import scada.persistence.ProductionBlock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DanielToft
 */
public interface ReadWriteProductionBlock {

    List<ProductionBlock> readPLCFile() throws IOException, ClassNotFoundException;

    void writePLCToFile(ArrayList<ProductionBlock> plcList) throws IOException;
    
}
