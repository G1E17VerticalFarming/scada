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
import javafx.scene.image.Image;
import javafx.stage.Stage;
import scada.domain.Scada;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Scada scada = new Scada();
        Parent root = FXMLLoader.load(getClass().getResource("/resources/scene_scada.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("/resources/icon.png")));
        stage.setTitle("Vertical Farming SCADA");
        stage.setResizable(false);
        stage.show();
    }
}
