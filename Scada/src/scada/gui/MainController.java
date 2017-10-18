/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.gui;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Scada scada = new Scada();
    }

    public synchronized void checkPLC(ActionEvent actionEvent) throws InterruptedException {
        check = true;

// TODO: 16-10-2017 MAKE IT POSSIBLE TO CHECK THE STATUS OF A PLC BY SIMPLE PING OR SOMETHING 
        progress_indicator.setVisible(true);
        plc_status.setText("Checking...");

        //progress_indicator.setVisible(false);
        //plc_status.setText("NOT OK");

    }
}
