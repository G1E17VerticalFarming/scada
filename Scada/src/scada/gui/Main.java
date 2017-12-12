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

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        // Set initial stage of program. Can be changed into a login scene instead if needed.
        Parent root = FXMLLoader.load(getClass().getResource("/resources/scene_scada.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("/resources/icon.png"))); //Program icon path
        stage.setTitle("Vertical Farming SCADA"); // Set title of program
        stage.setResizable(false); //Make user unable to resize program
        stage.show();
        stage.setOnHidden(e -> SceneScadaController.shutdown()); //Create listener to close all threads upon exiting
    }
}
