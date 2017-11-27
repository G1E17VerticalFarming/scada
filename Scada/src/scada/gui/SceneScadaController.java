/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import scada.domain.Scada;
import scada.persistence.ProductionBlock;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class SceneScadaController implements Initializable {
    private Scada scada = new Scada();
    private ObservableList<ProductionBlock> PLCTable;

    @FXML
    private Button buttonAddPLC, buttonRemovePLC, buttonOpenPLC, buttonCheckStatus;
    @FXML
    private TableView tableviewPLC;
    @FXML
    private TableColumn<ProductionBlock, Integer> PLC_ID, PLC_port, PLC_temp1, PLC_temp2, PLC_fanspeed;
    @FXML
    private TableColumn<ProductionBlock, String> PLC_IP, PLC_status, PLC_moisture, PLC_ETA, PLC_lastOK, PLC_lastCheck, PLC_name;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            ArrayList<ProductionBlock> list = new ArrayList<>();
            ProductionBlock plc1 = new ProductionBlock(1, "10.10.0.1", 5000);
            ProductionBlock plc2 = new ProductionBlock(2, "localhost", 5001);
            list.add(plc1);
            list.add(plc2);
            scada.writePLCFile(list);
            populateListView();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void populateListView() throws ClassNotFoundException, IOException {
        ArrayList plcList = scada.readPLCFile();
        PLCTable = FXCollections.observableArrayList(plcList);

        PLC_ID.setCellValueFactory(new PropertyValueFactory<>("id"));
        PLC_IP.setCellValueFactory(new PropertyValueFactory<>("ipaddress"));
        PLC_port.setCellValueFactory(new PropertyValueFactory<>("port"));

        tableviewPLC.setItems(null);
        tableviewPLC.setItems(PLCTable);
        System.out.println("PLC List loaded.");
    }

    public synchronized void checkStatus(ActionEvent actionEvent) {
        //plc5000.ReadTemp1();
        // TODO: 08-11-2017 Add functionality
        System.out.println("You checked the status of PLC ");
    }

    public synchronized void openPLC(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        Stage stageAddPLC = new Stage();
        stageAddPLC.initModality(Modality.APPLICATION_MODAL);
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("scene_plcview.fxml"));
        Scene scene = new Scene(root);

        stageAddPLC.setScene(scene);
        stageAddPLC.initStyle(StageStyle.UTILITY);
        stageAddPLC.showAndWait();

        populateListView();
    }

    public synchronized void addPLC(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        Stage stageAddPLC = new Stage();
        stageAddPLC.initModality(Modality.APPLICATION_MODAL);
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("scene_popup.fxml"));
        Scene scene = new Scene(root);

        stageAddPLC.setScene(scene);
        stageAddPLC.initStyle(StageStyle.UTILITY);
        stageAddPLC.showAndWait();

        populateListView();
    }

    public synchronized void removePLC(ActionEvent actionEvent) {
        //api.ReadTemp1();
        // TODO: 08-11-2017 Add functionality
        System.out.println("You removed a PLC...");
    }
}
