/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.api;

import java.io.IOException;

import shared.ProductionBlock;
import shared.GrowthProfile;
//import shared.Log;

/**
 *
 * @author DanielToft
 */
public class ApiSendController {
    
    
    
    public ProductionBlock[] getAllProductionBlocks() {
        ProductionBlock[] pbArr;
        try {
            pbArr = HttpOkhttpPostSend.doGetRequest("http://localhost:8080/production_block/", ProductionBlock[].class);
        } catch (IOException ex) {
            System.out.println("Something brok");
            return null;
        }
        return pbArr;
    }
    
    public ProductionBlock getSpecificProductionBlock(int id) {
        ProductionBlock pb;
        try {
            pb = HttpOkhttpPostSend.doGetRequest("http://localhost:8080/production_block/" + id, ProductionBlock.class);
        } catch (IOException ex) {
            System.out.println("Something brok");
            return null;
        }
        return pb;
    }
    
    public String saveProductionBlock(ProductionBlock pb) {
        String returnStr;
        try {
            returnStr = HttpOkhttpPostSend.doPostRequest("http://localhost:8080/production_block/", pb);
        } catch (IOException ex) {
            System.out.println("Something brok");
            return "not success";
        }
        return returnStr;
    }
    
    public GrowthProfile getSpecificGrowthProfile(int id) {
        GrowthProfile gp;
        try {
            gp = HttpOkhttpPostSend.doGetRequest("http://localhost:8080/growth_profile/" + id, GrowthProfile.class);
        } catch (IOException ex) {
            System.out.println("Something brok");
            return null;
        }
        return gp;
    }
    
    /*public String saveLog(Log log) {
        String returnStr;
        try {
            returnStr = HttpOkhttpPostSend.doPostRequest("http://localhost:8080/production_block/", log);
        } catch (IOException ex) {
            System.out.println("Something brok");
            return "not success";
        }
        return returnStr;
    }*/
}
