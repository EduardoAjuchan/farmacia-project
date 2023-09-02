package com.example.farmaciaproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class Ventas {
    @FXML
    private Button ButtonVolver;

    private Stage mainStage;

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @FXML
    protected void initialize() {
        ButtonVolver.setOnAction(event -> {
            onVolverButtonClick();
        });
    }

    @FXML
    protected void onVolverButtonClick() {
        loadAndSetScene("mainmenu.fxml");
    }

    private void loadAndSetScene(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlFileName));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1080, 720); // Establece las dimensiones deseadas
            mainStage.setScene(scene);

            if (fxmlFileName.equals("mainmenu.fxml")) {
                MainMenuController mainMenuController = loader.getController();
                mainMenuController.setMainStage(mainStage);
                mainMenuController.initialize();
                // Otras acciones que desees realizar en el controlador de MainMenu
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

