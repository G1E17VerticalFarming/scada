package scada.domain.interfaces;

import shared.ProductionBlock;

import java.util.ArrayList;

/**
 * 
 * @author chris
 */
public interface IScada {
    
    /**
     * 
     * @return 
     */
    public abstract ArrayList<ProductionBlock> getPLCList();
    
    /**
     * 
     * @return 
     */
    public abstract ArrayList<ProductionBlock> getUpdatedPLCList();

    /**
     * 
     * @param plc 
     */
    public abstract void savePLC(ProductionBlock plc);
    
    /**
     * 
     * @param pb 
     */
    public abstract void updatePLC(ProductionBlock pb);

    /**
     * 
     * @param pb 
     */
    public abstract void removePLC(ProductionBlock pb);
}
