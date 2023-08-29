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
            // Carga la escena mainmenu.fxml
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("mainmenu.fxml"));
            Parent root = fxmlLoader.load();

            // Obtén el controlador de la escena mainmenu.fxml
            MainMenuController controller = fxmlLoader.getController();

            // Establece el controlador para que pueda cambiar la escena
            controller.setMainStage(mainStage);

            // Cambia a la escena mainmenu.fxml
            mainStage.setScene(new Scene(root, 1080, 720));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
