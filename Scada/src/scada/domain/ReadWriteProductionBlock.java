/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.domain;

import shared.ProductionBlock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import scada.persistence.ProductionBlock;

/**
 * @author DanielToft
 */
public interface ReadWriteProductionBlock {

    List<ProductionBlock> readPLCFile() throws IOException, ClassNotFoundException;

    void savePLC(ArrayList<ProductionBlock> plcList) throws IOException; // Save multiple PLC's to file

    void savePLC(ProductionBlock plc) throws IOException, ClassNotFoundException; // Save single PLC to file

    void removePLC(int plcToRemove) throws IOException, ClassNotFoundException;

}
