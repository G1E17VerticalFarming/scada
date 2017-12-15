package scada.domain.interfaces;

import shared.ProductionBlock;

import java.util.ArrayList;


public interface IScada {
    ArrayList<ProductionBlock> getPLCList();
    
    ArrayList<ProductionBlock> getUpdatedPLCList();

    void savePLC(ProductionBlock plc);
    
    void updatePLC(ProductionBlock pb);

    void removePLC(ProductionBlock pb);
}
