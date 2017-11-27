/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.api;

//import scada.domain.IScada;
//import scada.domain.Scada;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author DanielToft
 */
public class ApiReceiveController {
    
    //private IScada scada;
    
    public ApiReceiveController() {
        //this.scada = Scada.getInstance();
    }
    
    public ResponseEntity<Boolean> ping() {
        return new ResponseEntity<Boolean>(false, HttpStatus.OK);
        //return new ResponseEntity<Boolean>(this.mes != null, HttpStatus.OK);
    }
    
}
