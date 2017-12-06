package scada.domain.interfaces;

import API.IScadaFacade;
//import scada.persistence.ProductionBlock;
import shared.ProductionBlock;

import java.io.IOException;
import java.util.ArrayList;

public interface IScada extends IScadaFacade {
    void writePLCFile(ArrayList<ProductionBlock> plcList) throws IOException;

    ArrayList readPLCFile() throws IOException, ClassNotFoundException;

    void removePLC(int plcToRemove) throws IOException, ClassNotFoundException;
}
