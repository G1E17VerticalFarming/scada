/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Class to handle HTTP requests from MES.
 * @author DanielToft
 */
public class ApiReceiveController {
    
    public ApiReceiveController() {
    }
    
    /**
     * Ping method used to test if connection is still alive.
     * @return True or false if there is connection. 
     */
    public ResponseEntity<Boolean> ping() {
        return new ResponseEntity<Boolean>(false, HttpStatus.OK);
    }
    
}
