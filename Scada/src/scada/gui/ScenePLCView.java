/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.gui;

import PLCCommunication.PLC;
import PLCCommunication.UDPConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import scada.domain.Scada;
import scada.persistence.ProductionBlock;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author mads
 */
public class ScenePLCView implements Initializable {
    public ProductionBlock plc;
    private Scada scada = Scada.getInstance();
    @FXML
    private Button buttonBack, buttonSet;
    @FXML
    private Label plcID;
    @FXML
    private TextField plcName;
    @FXML
    private Spinner<Integer> temp1Spinner, moistureSpinner, lightRedSpinner, lightBlueSpinner;
    @FXML
    private ToggleGroup fanspeed;
    @FXML
    private RadioButton radioOff, radioLow, radioHigh;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void handleButtonBackAction() {
        Stage stage = (Stage) buttonBack.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleButtonSetAction() {
        plc.setName(plcName.getText());
        PLC plccomm = new PLC(new UDPConnection(plc.getPort(), plc.getIpaddress()));
        plccomm.SetTemperature(temp1Spinner.getValueFactory().getValue());
        if (lightBlueSpinner.getValueFactory().getValue() >= 0 && lightBlueSpinner.getValueFactory().getValue() <= 100) {
            plccomm.SetBlueLight(lightBlueSpinner.getValueFactory().getValue());
        }
        if (lightRedSpinner.getValueFactory().getValue() >= 0 && lightRedSpinner.getValueFactory().getValue() <= 100) {
            plccomm.SetRedLight(lightRedSpinner.getValueFactory().getValue());
        }
        plccomm.SetMoisture(moistureSpinner.getValueFactory().getValue());
        if (radioOff.isSelected()) {
            plccomm.SetFanSpeed(0);
        } else if (radioLow.isSelected()) {
            plccomm.SetFanSpeed(1);
        } else if (radioHigh.isSelected()) {
            plccomm.SetFanSpeed(2);
        }

        System.out.println("*****Changing PLC settings*****");
        Stage stage = (Stage) buttonSet.getScene().getWindow();
        stage.close();
    }

    public void populatePLC(ProductionBlock plc) {
        this.plc = plc;
        plcID.setText("" + plc.getId());
        plcName.setText(plc.getName());
        temp1Spinner.getValueFactory().setValue((int) plc.getTemp1());
        moistureSpinner.getValueFactory().setValue((int) plc.getMoisture());
    }


}
