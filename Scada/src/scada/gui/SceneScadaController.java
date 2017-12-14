/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.gui;

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
import shared.ProductionBlock;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import scada.domain.interfaces.IScada;

public class SceneScadaController implements Initializable {

    private IScada scada = Scada.getInstance();
    private final ObservableList<ProductionBlock> PLCTable = FXCollections.observableArrayList();
    private final Integer countDownTime = 30; //SET AMOUNT OF SECONDS FOR SELFCHECK OF PLCS

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

        populateListView();
        startTimer(); //Start the startTimer for countdown

        //PLCTable = FXCollections.observableArrayList(plcList);
        //tableviewPLC.setItems(PLCTable);
    }

    private void populateListView() {
        ArrayList<ProductionBlock> plcList = scada.getPLCList();
        System.out.println("PLC SIZE:" + plcList.size());
        synchronized(PLCTable) {
            PLCTable.clear();
            PLCTable.addAll(plcList);
        }

        tableviewPLC.refresh();
        //int currentSelectionIndex = tableviewPLC.getSelectionModel().getFocusedIndex();
        //tableviewPLC.getSelectionModel().select(currentSelectionIndex);

        System.out.println("PLC's loaded (if any)");
    }

    public synchronized void openPLC() throws IOException, ClassNotFoundException {
        Stage stageAddPLC = new Stage();
        stageAddPLC.initModality(Modality.APPLICATION_MODAL);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("scene_plcview.fxml"));
        Parent root = fxmlLoader.load();
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
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("scene_popup.fxml"));
        Scene scene = new Scene(root);

        stageAddPLC.setScene(scene);
        stageAddPLC.initStyle(StageStyle.UTILITY);
        stageAddPLC.showAndWait();

        populateListView();
    }

    public synchronized void checkStatus() {
        ArrayList<ProductionBlock> plcList = new ArrayList<>();
        new Thread(() -> {
        plcList.addAll(scada.getUpdatedPLCList());
        
        synchronized(PLCTable) {
            PLCTable.clear();
            PLCTable.addAll(plcList);
        }
        }).start();

        tableviewPLC.refresh();
        //int currentSelectionIndex = tableviewPLC.getSelectionModel().getFocusedIndex();
        //tableviewPLC.getSelectionModel().select(currentSelectionIndex);

        System.out.println("Updated PLC's loaded (if any)");
        //ArrayList<ProductionBlock> plcs = new ArrayList<>(tableviewPLC.getItems());
        //scada.checkStatus(plcs);
        //tableviewPLC.refresh();
    }

    public synchronized void removePLC() throws IOException, ClassNotFoundException {
        if (tableviewPLC.getSelectionModel().getSelectedItem() != null) {
            ProductionBlock selectedPLC = tableviewPLC.getSelectionModel().getSelectedItem();
            scada.removePLC(selectedPLC);
            populateListView();
        }
    }

    private void startTimer() {
        new Thread(() -> {
            for (int i = countDownTime; i >= 0; i--) {
                try {
                    labelTimer.setText("Næste tjek: " + i + " sek");
                    Thread.sleep(1000);
                    if (i <= 0) {
                        checkStatus();
                        i = countDownTime;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
