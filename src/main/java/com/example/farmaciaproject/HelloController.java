package com.example.farmaciaproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloController {
    @FXML
    private Button ButtonUser;

    private Stage mainStage; // Variable para la ventana principal

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;

        ButtonUser.setOnAction(event -> {
            loadMainMenu();
        });
    }

    private void loadMainMenu() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("mainmenu.fxml"));
            Parent root = fxmlLoader.load();

            MainMenuController controller = fxmlLoader.getController();
            controller.initialize(Main.getMainStage()); // Pasa la referencia de mainStage

            controller.setMainStage(mainStage); // Configura el mainStage en el controlador MainMenuController

            Scene scene = new Scene(root, mainStage.getWidth(), mainStage.getHeight());
            mainStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setMainMenuController(MainMenuController mainMenuController) {
    }
}
