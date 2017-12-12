package scada.domain.interfaces;

import shared.ProductionBlock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO WRITE ME
 */
public interface ReadWriteProductionBlock {

    List<ProductionBlock> readPLCFile() throws IOException, ClassNotFoundException;

    void savePLC(ArrayList<ProductionBlock> plcList) throws IOException; // Save multiple PLC's to file

    void savePLC(ProductionBlock plc) throws IOException, ClassNotFoundException; // Save single PLC to file

    void removePLC(int plcToRemove) throws IOException, ClassNotFoundException;

}
