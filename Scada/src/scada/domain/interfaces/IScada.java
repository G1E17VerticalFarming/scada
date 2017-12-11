package scada.domain.interfaces;

import API.IScadaFacade;
import shared.ProductionBlock;

import java.io.IOException;
import java.util.ArrayList;

//import scada.persistence.ProductionBlock;

public interface IScada {
    //ArrayList<ProductionBlock> readPLCList() throws IOException, ClassNotFoundException;
    ArrayList<ProductionBlock> getPLCList();
    
    ArrayList<ProductionBlock> getUpdatedPLCList();

    void savePLC(ProductionBlock plc);

    void removePLC(int plcToRemove);
}
