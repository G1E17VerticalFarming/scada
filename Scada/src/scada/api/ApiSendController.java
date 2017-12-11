/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.api;

import java.io.IOException;
import scada.domain.interfaces.IScadaApiSend;

import shared.ProductionBlock;
import shared.GrowthProfile;
//import shared.Log;

/**
 *
 * @author DanielToft
 */
public class ApiSendController implements IScadaApiSend {
    
    private static ApiSendController instance = null;

    public static ApiSendController getInstance() {
        if (instance == null) {
            instance = new ApiSendController();
        }
        return instance;
    }
    
    private String address = "http://localhost:8081";
    private String myIp = "127.0.0.1";
    private int myPort = 6660;
    
    private ApiSendController() {
        
    }
    
    @Override
    public ProductionBlock[] getAllProductionBlocks() {
        ProductionBlock[] pbArr;
        try {
            pbArr = HttpOkhttpPostSend.doGetRequest(this.address + "/" + this.myIp + "/" + myPort + "/production_block/", ProductionBlock[].class);
        } catch (IOException ex) {
            System.out.println("Something brok");
            return null;
        }
        return pbArr;
    }
    
    @Override
    public ProductionBlock getSpecificProductionBlock(int id) {
        ProductionBlock pb;
        try {
            pb = HttpOkhttpPostSend.doGetRequest(this.address + "/" + this.myIp + "/" + myPort + "/production_block/" + id, ProductionBlock.class);
        } catch (IOException ex) {
            System.out.println("Something brok");
            return null;
        }
        return pb;
    }
    
    @Override
    public String saveProductionBlock(ProductionBlock pb) {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        String returnStr;
        try {
            returnStr = HttpOkhttpPostSend.doPostRequest(this.address + "/" + this.myIp + "/" + myPort + "/production_block/", pb);
        } catch (IOException ex) {
            System.out.println("Something brok");
            return "not success";
        }
        System.out.println(returnStr);
        return returnStr;
    }

    @Override
    public String deleteProductionBlock(ProductionBlock pb) {
        String returnStr;
        try {
            returnStr = HttpOkhttpPostSend.doPostRequest(this.address + "/production_block/delete/", pb);
        } catch (IOException ex) {
            System.out.println("Something brok");
            return "not success";
        }
        System.out.println(returnStr);
        return returnStr;
    }
    
    @Override
    public GrowthProfile getSpecificGrowthProfile(int id) {
        GrowthProfile gp;
        try {
            gp = HttpOkhttpPostSend.doGetRequest(this.address + "/growth_profile/" + id, GrowthProfile.class);
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

    @Override
    public boolean ping() {
        boolean bool;
        try {
            bool = HttpOkhttpPostSend.doGetRequest(this.address + "/ping/", boolean.class);
        } catch (IOException ex) {
            System.out.println("Something brok");
            return false;
        }
        return bool;
    }
}
