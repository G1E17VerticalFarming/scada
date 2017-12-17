/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java;

import scada.api.ApiReceiveController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

/**
 * This is the class to recieve calls through the REST API.
 * Think of this as the methods that MES calls upon.
 * @author DanielToft
 */
@RestController
public class ApiFacade {
    
    ApiReceiveController apiReceiveController;
    
    /**
     * Constructor
     */
    public ApiFacade() {
        this.apiReceiveController = new ApiReceiveController();
    }
    
    /**
     * Method being called through REST API.
     * 
     * Ping method used to test if connection is still alive.
     * @return True or false if there is connection. 
     */
    @RequestMapping(value = "/ping/", method = RequestMethod.GET)
    public ResponseEntity<Boolean> postLog() {
        ResponseEntity<Boolean> returnResp = this.apiReceiveController.ping();
        return returnResp;
    }
}
