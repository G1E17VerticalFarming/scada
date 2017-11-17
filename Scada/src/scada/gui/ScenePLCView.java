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
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author mads
 */
public class ScenePLCView implements Initializable {

    @FXML
    private Button goBackButton;
    @FXML
    private TextField plcNameTextfield;
    @FXML
    private Spinner<?> temp1Spinner;
    @FXML
    private Spinner<?> temp2Spinner;
    @FXML
    private Spinner<?> FanspeedSpinner;
    @FXML
    private Spinner<?> moistureSpinner;
    @FXML
    private Spinner<?> lightRedSpinner;
    @FXML
    private Spinner<?> lightBlueSpinner;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void handleGoBackButtonAction(ActionEvent event) {
        Stage stage = (Stage) goBackButton.getScene().getWindow();
        stage.close();
    }
    
}
