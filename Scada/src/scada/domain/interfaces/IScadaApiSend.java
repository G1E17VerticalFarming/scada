/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.domain.interfaces;

import shared.GrowthProfile;
import shared.ProductionBlock;

/**
 *
 * @author Daniel
 */
public interface IScadaApiSend {
    
    public ProductionBlock[] getAllProductionBlocks();
    
    public ProductionBlock getSpecificProductionBlock(int id);
    
    public String saveProductionBlock(ProductionBlock pb);
    
    public GrowthProfile getSpecificGrowthProfile(int id);
    
    public boolean ping();
}
