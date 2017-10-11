/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.domain;

import API.IScadaFacade;
import java.util.List;
import shared.ProductionBlock;

/**
 *
 * @author chris
 */
public class Scada implements IScadaFacade {

    @Override
    public boolean ping(String testData) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ProductionBlock> getProductionBlocks() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean setProduction(String productionBlock, String growthProfile) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
