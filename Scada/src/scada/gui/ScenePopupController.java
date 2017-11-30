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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scada.domain.Scada;
import scada.persistence.ProductionBlock;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author mads
 */
public class ScenePopupController implements Initializable {
    private Scada scada;

    @FXML
    private TextField nameTextField, IPTextField, portTextField;
    @FXML
    private Button addButton, cancelButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.scada = Scada.getInstance();
    }

    @FXML
    private void handleAddButtonAction(ActionEvent event) throws IOException, ClassNotFoundException {
        Stage stage = (Stage) addButton.getScene().getWindow();
        ArrayList plcList = scada.readPLCFile();
        int newestPLCID;
        int port = 0;
        String IP = IPTextField.getText().trim();
        String name = nameTextField.getText().trim();

        // FIND HIGHEST ID OF PLC'S
        if (plcList.size() == 0) {
            newestPLCID = 1;
        } else {
            ProductionBlock newestPLC = (ProductionBlock) plcList.get(plcList.size() - 1);
            newestPLCID = newestPLC.getId() + 1;
        }

        // GET PORT NUMBER
        try {
            port = Integer.parseInt(portTextField.getText().trim());
        } catch (NumberFormatException ex) {
            System.out.println("A number was not input as port.");
        }

        if (!IP.isEmpty() && !name.isEmpty() && port > 1024 && port < 65536) {
            ProductionBlock plc = new ProductionBlock(newestPLCID, IP, port, name);
            System.out.println("Added PLC - " + plc.toString());
            plcList.add(plc);
            scada.writePLCFile(plcList);
            stage.close();
        } else {
            System.out.println("You done did something wrong!");
        }

    }

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

}
