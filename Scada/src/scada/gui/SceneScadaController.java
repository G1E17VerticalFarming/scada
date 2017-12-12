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

/**
 * FXML Controller class
 * Primary scene of the SCADA program. Used for getting overview of all production blocks.
 */
public class SceneScadaController implements Initializable {
    private Scada scada = Scada.getInstance();
    private ObservableList<ProductionBlock> PLCTable;
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

    /**
     * Method used for shutting down all threads when program closes.
     * If removed, will cause threads to remain open upon exiting program.
     * DO NOT REMOVE
     */
    public static void shutdown() {
        System.exit(0);
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.scada = Scada.getInstance();
        try {
            populateListView(); //Populate list of Production blocks with info from disk
            startTimer(); //Start a countdown for automatic checking

        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used for populating the tableview of the scene. Takes the values stored about Production blocks and shows
     * them inside the corresponding column.
     */
    private void populateListView() throws ClassNotFoundException, IOException {
        //Read production blocks from disk
        ArrayList plcList = scada.readPLCList();

        //Set column values to values from objects found
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

        //Get currently selected item, and retain selection after updating tableview
        PLCTable = FXCollections.observableArrayList(plcList);
        int currentSelectionIndex = tableviewPLC.getSelectionModel().getFocusedIndex();
        tableviewPLC.setItems(PLCTable);
        tableviewPLC.getSelectionModel().select(currentSelectionIndex);

        System.out.println(plcList.size() + " PLC(s) found and loaded.");
    }

    /**
     * Method used to open ScenePLCView, where the user can change settings of the production block
     */
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
        stageAddPLC.initStyle(StageStyle.UTILITY); //Set borders and buttons of stage to minimum
        stageAddPLC.showAndWait(); //Continue executing after stage has been closed (indicating a change to PLC)

        populateListView(); //Update tableview
    }

    /**
     * Method used to open ScenePopupController. Used for creating a new production block object.
     */
    public synchronized void addPLC() throws IOException, ClassNotFoundException {
        Stage stageAddPLC = new Stage();
        stageAddPLC.initModality(Modality.APPLICATION_MODAL);
        Parent root = FXMLLoader.load(getClass().getResource("/resources/scene_popup.fxml"));
        Scene scene = new Scene(root);

        stageAddPLC.setScene(scene);
        stageAddPLC.initStyle(StageStyle.UTILITY); //Set borders and buttons of stage to minimum
        stageAddPLC.showAndWait();//Continue executing after stage has been closed (indicating a new PLC was added)

        populateListView(); // Update tableview
    }

    /**
     * Method called when checking a production blocks status and values.
     */
    public synchronized void checkStatus() throws IOException {
        ArrayList<ProductionBlock> plcs = new ArrayList<>(tableviewPLC.getItems());
        scada.checkStatus(plcs);
        tableviewPLC.refresh();
    }

    /**
     * Method called for removing a production block.
     */
    public synchronized void removePLC() throws IOException, ClassNotFoundException {
        if (tableviewPLC.getSelectionModel().getSelectedItem() != null) {
            ProductionBlock selectedPLC = tableviewPLC.getSelectionModel().getSelectedItem();
            scada.removePLC(selectedPLC.getId());
            populateListView();
        }
    }

    /**
     * Method used for updating the timer label. When end is reached, it calls checkStatus() and resets the counter
     */
    private void startTimer() throws InterruptedException {
        new Thread(() -> {
            for (int i = countDownTime; i >= 0; i--) {
                try {
                    labelTimer.setText("Næste tjek: " + i + " sek"); //Text to set inside timer label
                    Thread.sleep(1000); //time between label updates
                    if (i == 0) {
                        checkStatus(); // Call to update status of all production blocks in the tablevie
                        i = countDownTime; //Reset timer back to selected time interval
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
