package scada.domain;

import API.IScadaFacade;
import scada.persistence.ProductionBlock;

import java.io.IOException;
import java.util.ArrayList;

public interface IScada extends IScadaFacade {
    void writePLCFile(ArrayList<ProductionBlock> plcList) throws IOException;

    ArrayList readPLCFile() throws IOException, ClassNotFoundException;

    void removePLC(ProductionBlock plcToRemove) throws IOException, ClassNotFoundException;
}
