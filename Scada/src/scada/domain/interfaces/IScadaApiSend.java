/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.domain.interfaces;

import shared.GrowthProfile;
import shared.ProductionBlock;

/**
 * Interface defining REST API operations available to SCADA.
 * @author Daniel
 */
public interface IScadaApiSend {
    
    /**
     * API Method to fetch all Production Blocks
     * @return List of Production Block objects
     */
    public ProductionBlock[] getAllProductionBlocks();
    
    /**
     * API Method to get a specific production block based on id
     * @param id ID of production block to fetch 
     * @return Production Block object matching given ID
     */
    public ProductionBlock getSpecificProductionBlock(int id);
    
    /**
     * API Method to save a Production Block object
     * @param pb Production Block object to save 
     * @return True on succesful save, false otherwise
     */
    public String saveProductionBlock(ProductionBlock pb);
    
    /**
     * API Method to update a Production Block object
     * @param pb Production Block object to update
     * @return True on succesful update, false otherwise
     */
    public String updateProductionBlock(ProductionBlock pb);
    
    /**
     * API Method to delete a Production Block object
     * @param pb Production block object to delete
     * @return True on succesful deletion, false otherwise
     */
    public String deleteProductionBlock(ProductionBlock pb);
    
    /**
     * API Method to get a specific GrowthProfile object
     * @param id ID of the GrowthProfile object wished to fetch from DB
     * @return GrowthProfile object based on id
     */
    public GrowthProfile getSpecificGrowthProfile(int id);
    
    /**
     * API Ping method used to test if connection is still alive.
     * @return True or false if there is connection. 
     */
    public boolean ping();
}
