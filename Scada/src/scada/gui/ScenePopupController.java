/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scada.domain.Scada;
import shared.ProductionBlock;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import scada.domain.interfaces.IScada;

/**
 * FXML Controller class
 *
 * @author mads
 */
public class ScenePopupController implements Initializable {
    private IScada scada;

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
    private void handleAddButtonAction() throws IOException, ClassNotFoundException {
        int port = 0;
        String IP = IPTextField.getText().trim();
        String name = nameTextField.getText().trim();

        // GET PORT NUMBER
        try {
            port = Integer.parseInt(portTextField.getText().trim());

        } catch (NumberFormatException ex) {
            System.out.println("A number was not input as port.");
        }

        if (!IP.isEmpty() && !name.isEmpty() && port > 1024 && port < 65536) {
            ProductionBlock plc = new ProductionBlock();
            plc.setPort(port);
            plc.setIpaddress(IP);
            plc.setName(name);
            System.out.println("Added PLC " + plc.getName() + " - " + plc.getIpaddress() + ":" + plc.getPort());
            scada.savePLC(plc);

            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.close();
        } else {
            System.out.println("You done did something wrong!");
        }

    }

    @FXML
    private void handleCancelButtonAction() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onEnter() throws IOException, ClassNotFoundException {
        handleAddButtonAction();
    }

}
