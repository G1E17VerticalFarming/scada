package scada.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main class of program, used to start a JavaFX scene
 */
public class Main extends Application {

    public static void mainMethod(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("scene_scada.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Vertical Farming SCADA"); // Set title of program
        stage.setResizable(false); //Make user unable to resize program
        stage.show();
        stage.setOnHidden(e -> SceneScadaController.shutdown()); //Create listener to close all threads upon exiting
    }
}
