package com.example.farmaciaproject;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class HelloController {
    @FXML
    private Button ButtonUser;
    @FXML
    private Button forgotPassButton;
    @FXML
    private TextField textFieldUsername;
    @FXML
    private PasswordField passwordField;

    private Stage mainStage;

    private static HelloController instance;

    public HelloController() {
        instance = this;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;

        ButtonUser.setOnAction(event -> login());

        forgotPassButton.setOnAction(event -> mostrarMensaje("Ponte en contacto con un administrador para restablecer tu contraseña"));

        enableLogin(textFieldUsername, passwordField, ButtonUser);
    }

    private void login() {
        String username = textFieldUsername.getText();
        String password = passwordField.getText();

        // Realizar la autenticación (comprobar credenciales y obtener el tipo de usuario)
        String userType = autenticarYObtenerTipoUsuario(username, password);

        if (userType != null) {
            Session session = Session.getInstance();
            session.setUser(username);
            session.setUserType(userType);

            loadMainMenu();
        } else {
            mostrarMensajeError("Credenciales incorrectas");
        }
    }

    private void mostrarMensajeError(String credencialesIncorrectas) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(credencialesIncorrectas);
        alert.showAndWait();
    }

    private String autenticarYObtenerTipoUsuario(String username, String password) {
        String url = "jdbc:mysql://localhost:3306/bdnegocio";
        String usuarioDB = "root";
        String contrasenaDB = "123456";

        try (Connection conn = DriverManager.getConnection(url, usuarioDB, contrasenaDB)) {
            String selectUsuarioSQL = "SELECT tipo_usuario FROM usuarios WHERE nombre_usuario = ? AND contrasena = ?";
            PreparedStatement pstmtUsuario = conn.prepareStatement(selectUsuarioSQL);
            pstmtUsuario.setString(1, username);
            pstmtUsuario.setString(2, password);

            ResultSet rsUsuario = pstmtUsuario.executeQuery();

            if (rsUsuario.next()) {
                return rsUsuario.getString("tipo_usuario");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void enableLogin(TextField usernameField, PasswordField passwordField, Button loginButton) {
        usernameField.setDisable(false);
        passwordField.setDisable(false);
        loginButton.setDisable(false);
    }

    private void loadMainMenu() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainmenu.fxml"));
            Parent root = fxmlLoader.load();

            MainMenuController mainMenuController = fxmlLoader.getController();
            mainMenuController.setMainStage(mainStage);

            Session session = Session.getInstance();
            String userType = session.getUserType();
            if ("empleado".equals(userType)) {
                mainMenuController.disableControlDeUsuarios();
            }

            Scene scene = new Scene(root, 1080, 720);
            mainStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarMensaje(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mensaje");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}








