package scada.domain.interfaces;

import API.IScadaFacade;
import shared.ProductionBlock;

import java.io.IOException;
import java.util.ArrayList;

public interface IScada extends IScadaFacade {
    /**
     * This method reads a list of Production blocks from disk
     *
     * @return ArrayList of Production blocks found on disk
     */
    ArrayList readPLCList() throws IOException, ClassNotFoundException;

    /**
     * This method is used to save a whole list of arrays to disk.
     * @param plcList Requires an ArrayList of Production Blocks to save to disk
     */
    void savePLC(ArrayList<ProductionBlock> plcList) throws IOException;

    /**
     * This method saves a single production block to disk. Is usually used to save an edited PLC, but can also save a
     * new object if the current one isn't found. Operates based on object ID's.
     * @param plc Takes a single production block object to save.
     */
    void savePLC(ProductionBlock plc) throws IOException, ClassNotFoundException;

    /**
     * This method removes a single PLC based on the ID of the object.
     * @param plcToRemove Takes the ID of the PLC as input
     */
    void removePLC(int plcToRemove) throws IOException, ClassNotFoundException;
}
