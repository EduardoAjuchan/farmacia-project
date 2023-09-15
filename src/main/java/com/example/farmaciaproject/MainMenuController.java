package com.example.farmaciaproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    @FXML
    private Label bienvenidoLabel;
    @FXML
    private Button logoutButton;
    private Stage mainStage;
    private static boolean isControlUsuariosEnabled = true; // Controla el estado habilitado del botón "Control de Usuarios"

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
        logoutButton.setOnAction(event -> {
            onLogoutButtonClick();
        });

        // Actualiza el texto del Label con el nombre de usuario
        Session session = Session.getInstance();
        String username = session.getUser();
        bienvenidoLabel.setText("¡Bienvenido, " + username + "!");
        // Verifica el tipo de usuario y deshabilita el botón "Control de Usuarios" si es "Empleado"
        String userType = session.getUserType();
        if ("Empleado".equalsIgnoreCase(userType)) {
            isControlUsuariosEnabled = false;
        }
        ButtonUser.setDisable(!isControlUsuariosEnabled);
    }

    public static void disableControlDeUsuarios() {
        isControlUsuariosEnabled = false;
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

    @FXML
    private void onLogoutButtonClick() {
        // Elimina los datos de sesión
        Session.getInstance().clearSession();
        // Cierra la aplicación
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.close();
    }
    private void loadAndSetScene(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
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
