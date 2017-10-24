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
import javafx.scene.control.TableView;
import scada.domain.Scada;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author chris
 */
public class ScadaSceneController implements Initializable {

    @FXML
    private Button buttonCheckStatus, buttonOpenPLC, buttonAddPLC, buttonRemovePLC;

    @FXML
    private TableView tableviewPLC;

    PLCConnection con = new UDPConnection(5000, "localhost");
    IGreenhouse api = new PLC(con);


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Scada scada = new Scada();
    }

    public synchronized void checkStatus(ActionEvent actionEvent) {
        //api.ReadTemp1();
        System.out.println("You checked the status...");
    }

    public synchronized void openPLC(ActionEvent actionEvent) {
        //api.ReadTemp1();
        System.out.println("You opened a PLC...");
    }

    public synchronized void addPLC(ActionEvent actionEvent) {
        //api.ReadTemp1();
        System.out.println("You added a PLC...");
    }

    public synchronized void removePLC(ActionEvent actionEvent) {
        //api.ReadTemp1();
        System.out.println("You removed a PLC...");
    }
}
