/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.gui;

import PLCCommunication.PLC;
import PLCCommunication.UDPConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import scada.domain.Scada;
import scada.persistence.ProductionBlock;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;


public class SceneScadaController implements Initializable {
    private Scada scada = Scada.getInstance();
    private ObservableList<ProductionBlock> PLCTable;
    private final Integer countDownTime = 30; //SET AMOUNT OF SECONDS FOR SELFCHECK OF PLCS
    private SimpleDateFormat ft = new SimpleDateFormat("dd/MM-yyyy HH:mm:ss");


    @FXML
    private Button buttonAddPLC, buttonRemovePLC, buttonOpenPLC, buttonCheckStatus;
    @FXML
    private TableView<ProductionBlock> tableviewPLC;
    @FXML
    private TableColumn<ProductionBlock, Integer> PLC_ID, PLC_port;
    @FXML
    private TableColumn PLC_temp1, PLC_temp2, PLC_moisture;
    @FXML
    private TableColumn<ProductionBlock, String> PLC_IP, PLC_status, PLC_ETA, PLC_lastOK, PLC_lastCheck, PLC_name;
    @FXML
    private TextField labelTimer;

    public static void shutdown() {
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.scada = Scada.getInstance();
        try {
            populateListView();
            startTimer(); //Start the startTimer for countdown

        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void populateListView() throws ClassNotFoundException, IOException {
        ArrayList plcList = scada.readPLCFile();

        PLCTable = FXCollections.observableArrayList(plcList);
        int currentSelectionIndex = tableviewPLC.getSelectionModel().getFocusedIndex();

        PLC_ID.setCellValueFactory(new PropertyValueFactory<>("id"));
        PLC_IP.setCellValueFactory(new PropertyValueFactory<>("ipaddress"));
        PLC_port.setCellValueFactory(new PropertyValueFactory<>("port"));
        PLC_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        PLC_temp1.setCellValueFactory(new PropertyValueFactory("temp1"));
        PLC_temp2.setCellValueFactory(new PropertyValueFactory("temp2"));
        PLC_moisture.setCellValueFactory(new PropertyValueFactory("moisture"));
        PLC_status.setCellValueFactory(new PropertyValueFactory("status"));
        PLC_lastCheck.setCellValueFactory(new PropertyValueFactory("lastCheck"));
        PLC_lastOK.setCellValueFactory(new PropertyValueFactory("lastOK"));
        PLC_ETA.setCellValueFactory(new PropertyValueFactory("estimatedDone"));

        tableviewPLC.setItems(PLCTable);
        tableviewPLC.getSelectionModel().select(currentSelectionIndex);
        System.out.println("PLC's loaded (if any)");
    }

    public synchronized void openPLC() throws IOException, ClassNotFoundException {
        Stage stageAddPLC = new Stage();
        stageAddPLC.initModality(Modality.APPLICATION_MODAL);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/scene_plcview.fxml"));
        final Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

        ScenePLCView controller = fxmlLoader.getController();
        System.out.println("Passing object: " + tableviewPLC.getSelectionModel().getSelectedItem().getName());
        ProductionBlock plc = tableviewPLC.getSelectionModel().getSelectedItem();
        controller.populatePLC(plc);

        stageAddPLC.setScene(scene);
        stageAddPLC.initStyle(StageStyle.UTILITY);
        stageAddPLC.showAndWait();

        populateListView();
    }

    public synchronized void addPLC() throws IOException, ClassNotFoundException {
        Stage stageAddPLC = new Stage();
        stageAddPLC.initModality(Modality.APPLICATION_MODAL);
        Parent root = FXMLLoader.load(getClass().getResource("/resources/scene_popup.fxml"));
        Scene scene = new Scene(root);

        stageAddPLC.setScene(scene);
        stageAddPLC.initStyle(StageStyle.UTILITY);
        stageAddPLC.showAndWait();

        populateListView();
    }

    public synchronized void checkStatus() throws IOException {
        new Thread(() -> {
            ArrayList<ProductionBlock> newStatusList = new ArrayList<>();

            for (Object plc : tableviewPLC.getItems()) {
                Date dNow = new Date();
                ProductionBlock currentPLC = (ProductionBlock) plc;
                System.out.println("Checking status of " + currentPLC.getIpaddress() + ":" + currentPLC.getPort());

                PLC plccomm = new PLC(new UDPConnection(currentPLC.getPort(), currentPLC.getIpaddress()));

                // Check status of PLC
                double temp1 = plccomm.ReadTemp1();

                if (temp1 == -1) { // No connection to PLC
                    currentPLC.setTemp1(-1);
                    currentPLC.setTemp2(-1);
                    currentPLC.setMoisture(-1);
                    currentPLC.setStatus("No connection");
                    currentPLC.setFanspeed(-1);
                } else if (temp1 == -2) { // Error in returned data
                    currentPLC.setTemp1(-2);
                    currentPLC.setTemp2(-2);
                    currentPLC.setMoisture(-2);
                    currentPLC.setStatus("Data error");
                    currentPLC.setFanspeed(-2);
                } else {
                    currentPLC.setTemp1(temp1);
                    currentPLC.setTemp2(plccomm.ReadTemp2());
                    currentPLC.setMoisture(plccomm.ReadMoist());
                    currentPLC.setStatus("OK");
                    currentPLC.setLastOK("" + ft.format(dNow));
                }
                currentPLC.setLastCheck("" + ft.format(dNow));


                newStatusList.add(currentPLC);
            }
            try {
                scada.writePLCFile(newStatusList);
            } catch (IOException e) {
                e.printStackTrace();
            }
            tableviewPLC.refresh();
            System.out.println("You checked the status of the PLC's ");
        }).start();
    }

    public synchronized void removePLC() throws IOException, ClassNotFoundException {
        if (tableviewPLC.getSelectionModel().getSelectedItem() != null) {
            ProductionBlock selectedPLC = tableviewPLC.getSelectionModel().getSelectedItem();
            scada.removePLC(selectedPLC.getId());
            populateListView();
        }
    }

    private void startTimer() throws InterruptedException {
        new Thread(() -> {
            for (int i = countDownTime; i >= 0; i--) {
                try {
                    labelTimer.setText("Næste tjek: " + i + " sek");
                    Thread.sleep(1000);
                    if (i == 0) {
                        checkStatus();
                        i = countDownTime;
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
