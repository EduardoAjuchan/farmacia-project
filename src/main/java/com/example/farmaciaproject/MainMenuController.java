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
    @FXML
    private Button ButtonVentas;

    private Stage mainStage;

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @FXML
    protected void initialize() {
        ButtonUser.setOnAction(event -> {
            onControlUsuariosButtonClick();
        });
        ButtonControlProductos.setOnAction(event -> {
            onControlProductosButtonClick();
        });
        ButtonVentas.setOnAction(event -> {
            onVentasButtonClick();
        });
    }
    @FXML
    protected void onVentasButtonClick() {
        loadAndSetScene("ventas.fxml");
    }

    @FXML
    protected void onControlProductosButtonClick() {
        loadAndSetScene("producto-control.fxml");
    }

    @FXML
    protected void onControlUsuariosButtonClick() {
        loadAndSetScene("user-control.fxml");
    }

    private void loadAndSetScene(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlFileName));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1080, 720); // Establece las dimensiones deseadas
            mainStage.setScene(scene);

            if (fxmlFileName.equals("producto-control.fxml")) {
                ProductoControl productoControlController = loader.getController();
                productoControlController.setMainStage(mainStage);
                productoControlController.initialize();
                productoControlController.updateTableView();
            } else if (fxmlFileName.equals("user-control.fxml")) {

            } else if (fxmlFileName.equals("ventas.fxml")) {
                Ventas ventasController = loader.getController();
                ventasController.setMainStage(mainStage);
                ventasController.initialize();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

