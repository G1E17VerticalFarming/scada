package scada.gui;

import PLCCommunication.PLC;
import PLCCommunication.UDPConnection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import scada.domain.Scada;
import shared.ProductionBlock;

import java.net.URL;
import java.util.ResourceBundle;
import scada.domain.interfaces.IScada;

/**
 * FXML Controller class
 * Used for the scene where a PLC can have setpoints set and the name, port and IP changed.
 */
public class ScenePLCView implements Initializable {
    public ProductionBlock plc;
    private IScada scada;
    @FXML
    private Button buttonBack, buttonUpdate, buttonSend;
    @FXML
    private Label plcID, status;
    @FXML
    private TextField plcName, plcIP, plcPort;
    @FXML
    private Spinner<Integer> temp1Spinner, waterSpinner, lightRedSpinner, lightBlueSpinner;
    @FXML
    private ChoiceBox fanSpeed;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.scada = Scada.getInstance();
    }

    /**
     * Button action used to close the scene.
     */
    @FXML
    private void handleButtonBackAction() {
        Stage stage = (Stage) buttonBack.getScene().getWindow();
        stage.close();
    }

    /**
     * Button action used to send SET commands to the chosen PLC.
     */
    @FXML
    private void handleButtonSetAction() {
        new Thread(() -> {
            buttonSend.setDisable(true);

            PLC plccomm = new PLC(new UDPConnection(plc.getPort(), plc.getIpaddress()));

            if (plccomm.SetTemperature(temp1Spinner.getValueFactory().getValue())) {
                if (lightBlueSpinner.getValueFactory().getValue() >= 0 && lightBlueSpinner.getValueFactory().getValue() <= 100) {
                    plccomm.SetBlueLight(lightBlueSpinner.getValueFactory().getValue());
                }
                if (lightRedSpinner.getValueFactory().getValue() >= 0 && lightRedSpinner.getValueFactory().getValue() <= 100) {
                    plccomm.SetRedLight(lightRedSpinner.getValueFactory().getValue());
                }
                plccomm.AddWater(waterSpinner.getValueFactory().getValue());
                //set fanspeed
                switch (fanSpeed.getSelectionModel().getSelectedItem().toString()) {
                    case "Off":
                        plccomm.SetFanSpeed(0);
                        break;
                    case "Low":
                        plccomm.SetFanSpeed(1);
                        break;
                    case "High":
                        plccomm.SetFanSpeed(2);
                        break;
                    default:
                        plccomm.SetFanSpeed(0);
                }
                Platform.runLater(() -> status.setText("SUCCESS: Setpoints blev sendt til PLC'en"));
                System.out.println("SUCCESS: Setpoints blev sendt til PLC'en");
            } else {
                Platform.runLater(() -> status.setText("FEJL: PLC kunne ikke kontaktes. Tjek indstillinger"));
                System.out.println("FEJL: PLC kunne ikke kontaktes. Tjek indstillinger");
            }
            buttonSend.setDisable(false);
        }).start();

    }

    /**
     * Method needed upon calling the scene. Populates all labels and spinners with current values
     * @param plc The production block that the scene should take information from
     */
    public void populatePLC(ProductionBlock plc) {
        this.plc = plc;
        plcID.setText("" + plc.getId());
        plcName.setText(plc.getName());
        plcIP.setText(plc.getIpaddress());
        plcPort.setText("" + plc.getPort());
        temp1Spinner.getValueFactory().setValue((int) plc.getTemp1());
    }

    /**
     * Method used for labels to make them able to listen to "Enter" button click. Calls updatePLC().
     */
    @FXML
    private void onEnter() {
        updatePLC();
    }

    /**
     * Mathod used to send changes to the current Production block object, based on content of labels in scene.
     */
    @FXML
    private void updatePLC() {
        plc.setName(plcName.getText());
        plc.setPort(Integer.parseInt(plcPort.getText()));
        plc.setIpaddress(plcIP.getText());
        this.scada.updatePLC(plc);

        Stage stage = (Stage) buttonUpdate.getScene().getWindow();
        stage.close();
    }


}
