package com.example.farmaciaproject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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

    private Stage mainStage; // Variable para la ventana principal

    // Variable estática para almacenar la instancia de HelloController
    private static HelloController instance;

    public HelloController() {
        instance = this;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;

        ButtonUser.setOnAction(event -> login());

        // Configurar el evento para el botón "forgotPass"
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

            // Navegar a la pantalla del menú principal
            loadMainMenu();
        } else {
            // Mostrar un mensaje de error al usuario
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Si las credenciales son incorrectas o no se encuentra el usuario
    }

    public static void enableLogin(TextField usernameField, PasswordField passwordField, Button loginButton) {
        // Habilita la funcionalidad de inicio de sesión nuevamente
        usernameField.setDisable(false); // Habilita el campo de entrada de usuario
        passwordField.setDisable(false); // Habilita el campo de entrada de contraseña
        loginButton.setDisable(false); // Habilita el botón de inicio de sesión
    }

    private void loadMainMenu() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainmenu.fxml"));
            Parent root = fxmlLoader.load();

            MainMenuController mainMenuController = fxmlLoader.getController();
            mainMenuController.setMainStage(mainStage); // mainStage en el controlador MainMenuController

            // Verificar el tipo de usuario y deshabilitar el botón "Control de usuarios" si es "Empleado"
            Session session = Session.getInstance();
            String userType = session.getUserType();
            if ("empleado".equals(userType)) {
                mainMenuController.disableControlDeUsuarios(); // Método para deshabilitar el botón
            }

            Scene scene = new Scene(root, 1080, 720); // Establecer las dimensiones deseadas
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







