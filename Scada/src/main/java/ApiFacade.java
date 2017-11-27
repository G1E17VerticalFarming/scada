/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java;

import shared.ProductionBlock;
import shared.GrowthProfile;
import shared.Log;
import scada.api.ApiReceiveController;

//import mes.api.Greeting;
import java.util.concurrent.atomic.AtomicLong;
//import mes.api.Json;
//import mes.api.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author DanielToft
 */
@RestController
public class ApiFacade {
    
    ApiReceiveController apiReceiveController;
    
    public ApiFacade() {
        this.apiReceiveController = new ApiReceiveController();
    }
    
    @RequestMapping(value = "/ping/", method = RequestMethod.GET)
    public ResponseEntity<Boolean> postLog() {
        ResponseEntity<Boolean> returnResp = this.apiReceiveController.ping();
        return returnResp;
    }
    
}
