/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author chris
 */
public class Main extends Application {

    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/resources/scene_scada.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();


/*        *//**
         *  This is to start a new test greenhouse. It will be deleted when an actual greenhouse is in place
         *//*
        Thread testCon = new Thread() {
            public void run() {
                //PLCConnection con = new UDPConnection(23456, "localhost");
                //PLC api = new PLC(con);
                //api.SetGreenhouseStatus(200); // This means it's OK!
                //System.out.println("PLC Status is: "+api.GetGreenhouseStatus());

            }
        };
        testCon.start(); // Starts the greenhouse through a seperate thread.
*/
    }
}
