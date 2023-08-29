package com.example.farmaciaproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        Parent root = fxmlLoader.load();
        HelloController controller = fxmlLoader.getController(); // Obtén el controlador

        // Configura el controlador para que pueda cambiar la escena
        controller.setMainStage(stage);

        // Deshabilita el botón de maximizar
        stage.setResizable(false);

        Scene scene = new Scene(root, 1080, 720);
        stage.setTitle("Farmacia EL DIFUNTO");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}