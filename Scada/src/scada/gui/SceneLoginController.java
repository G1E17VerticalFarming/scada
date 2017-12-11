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

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 * Used for the login scene. Currently not operational
 */
public class SceneLoginController implements Initializable {

    @FXML
    private TextField usernameTextField, passwordTextField;
    @FXML
    private Button cancelButton, loginButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
