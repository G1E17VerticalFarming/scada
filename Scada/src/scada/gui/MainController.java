/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.gui;

import PLCCommunication.Greenhouse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import scada.domain.Scada;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author chris
 */
public class MainController implements Initializable {

    @FXML
    private Label plc_ip, plc_port, plc_status;

    @FXML
    private ProgressIndicator progress_indicator;

    @FXML
    private Button plc_check;

    private boolean check = false;

    Greenhouse api = new Greenhouse();


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Scada scada = new Scada();
        api.SetGreenhouseStatus(200); // This means it's OK!
    }

    public synchronized void checkPLC(ActionEvent actionEvent) throws InterruptedException {
        check = true;


        progress_indicator.setVisible(true);
        plc_status.setText("Checking...");
        System.out.println("Greenhouse Status is: " + api.GetGreenhouseStatus());

        // TODO: 16-10-2017 MAKE IT POSSIBLE TO CHECK THE STATUS OF A PLC BY SIMPLE PING OR SOMETHING
        progress_indicator.setVisible(false);
        if (api.GetGreenhouseStatus() == 200) {
            plc_status.setText("OK!");
        } else {
            plc_status.setText("NOT OK");
        }

    }
}
