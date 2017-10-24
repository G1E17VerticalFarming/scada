/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.gui;

import API.IGreenhouse;
import PLCCommunication.PLC;
import PLCCommunication.PLCConnection;
import PLCCommunication.UDPConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import scada.domain.Scada;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author chris
 */
public class ScadaSceneController implements Initializable {

    @FXML
    private Label plc_ip, plc_port, plc_status;

    @FXML
    private Button plc_check;

    private boolean check = false;

    PLCConnection con = new UDPConnection(5000, "localhost");
    IGreenhouse api = new PLC(con);


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Scada scada = new Scada();

    }

    public synchronized void checkPLC(ActionEvent actionEvent) throws InterruptedException {
        check = true;
        api.ReadTemp1();
        //api.ReadTemp2();
        //api.GetStatus();
        //api.ReadTemp2();

        //plc_status.setText("Checking...");
        //System.out.println("PLC Status is: " + api.GetGreenhouseStatus());

        // TODO: 16-10-2017 MAKE IT POSSIBLE TO CHECK THE STATUS OF A PLC BY SIMPLE PING OR SOMETHING

        /*if (api.GetGreenhouseStatus() == 200) {
            plc_status.setText("OK!");
        } else {
            plc_status.setText("NOT OK");
        }*/

    }
}
