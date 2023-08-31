package com.example.farmaciaproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class MainMenuController {
    @FXML
    private Button backButton;
    @FXML
    private Button ButtonUser;
    @FXML
    private Button ButtonControlProductos;

    private Stage mainStage;

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }


    @FXML
    protected void initialize(Stage mainStage) {
        this.mainStage = mainStage;
        ButtonUser.setOnAction(event -> {
            onControlUsuariosButtonClick();
        });
        ButtonControlProductos.setOnAction(event -> {
            onControlProductosButtonClick();
        });
    }


    @FXML
    protected void onControlProductosButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("producto-control.fxml"));
            Parent root = loader.load();

            ProductoControl productoControlController = loader.getController();
            productoControlController.setMainStage(mainStage);

            Scene scene = new Scene(root, mainStage.getWidth(), mainStage.getHeight());
            mainStage.setScene(scene);

            productoControlController.initialize(); // Inicializa la tabla de productos nuevamente
            productoControlController.updateTableView(); // Actualiza la tabla de productos
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    protected void onControlUsuariosButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("user-control.fxml"));
            Parent root = loader.load();

            // Si necesitas interactuar con el controlador de user-control.fxml
            // UserController userController = loader.getController();

            Scene scene = new Scene(root, mainStage.getWidth(), mainStage.getHeight());
            mainStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
