package scada.domain.interfaces;

import shared.ProductionBlock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface defining operations regarding a Production Block
 */
public interface ReadWriteProductionBlock {

    /**
     * Method to read the PLC list file and return a list of constructed 
     * Production Block objects
     * @return A list of all Production Blocks found in the PLC list
     * @throws IOException If an error occurs during file read
     */
    public abstract List<ProductionBlock> readPLCFile() throws IOException;

    /**
     * Method to save multiple PLC's to a dat file
     * @param plcList List of PLC's to write to PLC list
     * @throws IOException If an error occurs during file write
     */
    public abstract void savePLC(ArrayList<ProductionBlock> plcList) throws IOException;
    
    /**
     * Method to write multiple PLC's to a json file.
     * This method is called IF there is no connection to the MES system.
     * Even though the MES system is offline, it is still possible to save
     * which PLC objects need to be updated, once the MES system comes online.
     * @param pbList List of PLC's to write to PLC list
     * @throws IOException If an error occurs during file write
     */
    public abstract void saveUpdatePLCList(ArrayList<ProductionBlock> pbList) throws IOException;
    
    /**
     * Method to write multiple PLC's to a json file.
     * This method is called IF there is no connection to the MES system.
     * Even though the MES system is offline, it is still possible to save
     * which PLC objects need to be delete, once the MES system comes online.
     * @param pbList List of PLC's to write to PLC list
     * @throws IOException If an error occurs during file write
     */
    public abstract void saveDeletePLCList(ArrayList<ProductionBlock> pbList) throws IOException;

    /**
     * Method to remove a single PLC from PLC dat file
     * @param plcToRemove ID of the PLC to remove
     * @throws IOException If an error occurs during file write
     */
    public abstract void removePLC(int plcToRemove) throws IOException;

}
