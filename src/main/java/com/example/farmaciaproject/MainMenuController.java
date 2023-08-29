package com.example.farmaciaproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;

public class MainMenuController {
    @FXML
    private Button backButton; // Referencia al botón para regresar a la pantalla anterior

    private Stage mainStage; // Variable para la ventana principal

    public void setMainStage(Stage mainStage) {
        mainStage.setScene(new Scene(new AnchorPane(), 1080, 720));
        this.mainStage = mainStage;
    }

    @FXML
    protected void initialize() {
        // Método que se llama cuando se carga la pantalla
        // Puedes realizar inicializaciones aquí si es necesario
    }

    @FXML
    protected void onBackButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("mainmenu.fxml"));
            Parent root = loader.load();

            MainMenuController mainMenuController = loader.getController();
            mainMenuController.setMainStage(mainStage);

            Scene scene = new Scene(root, mainStage.getWidth(), mainStage.getHeight()); // Ajustar al tamaño de la ventana principal
            mainStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
