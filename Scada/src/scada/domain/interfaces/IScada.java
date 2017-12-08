package scada.domain.interfaces;

import API.IScadaFacade;
import shared.ProductionBlock;

import java.io.IOException;
import java.util.ArrayList;

//import scada.persistence.ProductionBlock;

public interface IScada extends IScadaFacade {
    ArrayList readPLCList() throws IOException, ClassNotFoundException;

    void savePLC(ArrayList<ProductionBlock> plcList) throws IOException;

    void savePLC(ProductionBlock plc) throws IOException, ClassNotFoundException;

    void removePLC(int plcToRemove) throws IOException, ClassNotFoundException;
}
