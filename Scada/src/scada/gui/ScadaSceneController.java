/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.gui;

import PLCCommunication.PLC;
import PLCCommunication.PLCConnection;
import PLCCommunication.UDPConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author chris
 */
public class ScadaSceneController implements Initializable {

    @FXML
    private Button buttonCheckStatus, buttonOpenPLC, buttonAddPLC, buttonRemovePLC;

    @FXML
    private TableView tableviewPLC;

    ObjectOutputStream oos = null;
    FileOutputStream fout = null;

    PLCConnection con = new UDPConnection(5000, "localhost");
    PLC plc1 = new PLC(con);


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Scada scada = new Scada();
        readPLCFile();

    }

    private void writePLCFile() throws IOException {
        try {
            FileOutputStream fout = new FileOutputStream("Scada/src/resources/PLClist.dat", true);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            // TODO: 24-10-2017 Need to implement that a a list of PLC's can be saved in the PLCList.dat file
            //oos.writeObject(myClassList);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (oos != null) {
                oos.close();
            }
        }
    }

    private void readPLCFile() {
        // TODO: 24-10-2017 Need to implement that a file will be read, and a PLC list will be produced. If no file exists, then return an empty list
        File f = new File("Scada/src/resources/PLClist.dat");
        if (f.exists() && !f.isDirectory()) {
            System.out.println("The file exists...");
        } else {
            System.out.println("File doesn't exist...");
        }
    }

    public synchronized void checkStatus(ActionEvent actionEvent) {
        //api.ReadTemp1();
        System.out.println("You checked the status...");
    }

    public synchronized void openPLC(ActionEvent actionEvent) {
        //api.ReadTemp1();
        System.out.println("You opened a PLC...");
    }

    public synchronized void addPLC(ActionEvent actionEvent) {
        //api.ReadTemp1();
        System.out.println("You added a PLC...");
    }

    public synchronized void removePLC(ActionEvent actionEvent) {
        //api.ReadTemp1();
        System.out.println("You removed a PLC...");
    }
}
