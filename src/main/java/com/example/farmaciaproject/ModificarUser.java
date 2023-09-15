package com.example.farmaciaproject;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ModificarUser {

    @FXML
    private TextField usuarioTextField;

    @FXML
    private TextField contrasenaTextField;

    @FXML
    private JFXComboBox<String> tipoUsuarioComboBox;

    @FXML
    private JFXButton modificarButton;

    private Usuario usuario;
    private UserControl userControl;

    public void initialize() {
        tipoUsuarioComboBox.getItems().addAll("empleado", "administrador");
        modificarButton.setOnAction(event -> modificarUsuario());
    }

    public void initData(Usuario usuario) {
        this.usuario = usuario; // Almacena el usuario actual
        usuarioTextField.setText(usuario.getNombreUsuario());
        contrasenaTextField.setText(usuario.getContrasena());
        tipoUsuarioComboBox.setValue(usuario.getTipoUsuario());
    }

    @FXML
    void modificarUsuario() {
        if (usuario != null) {
            String nuevoUsuario = usuarioTextField.getText();
            String nuevaContrasena = contrasenaTextField.getText();
            String nuevoTipoUsuario = tipoUsuarioComboBox.getValue();
            String url = "jdbc:mysql://localhost:3306/bdnegocio";
            String username = "root";
            String password = "123456";
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                String query = "UPDATE usuarios SET nombre_usuario = ?, contrasena = ?, tipo_usuario = ? WHERE id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, nuevoUsuario);
                    preparedStatement.setString(2, nuevaContrasena);
                    preparedStatement.setString(3, nuevoTipoUsuario);
                    preparedStatement.setInt(4, usuario.getId());
                    preparedStatement.executeUpdate();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Éxito");
                    alert.setHeaderText(null);
                    alert.setContentText("Usuario modificado exitosamente.");
                    alert.showAndWait();

                    if (userControl != null) {
                        userControl.usuarioModificado(usuario.getId());
                    }
                    modificarButton.getScene().getWindow().hide();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Error al modificar el usuario en la base de datos.");
                    alert.showAndWait();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                // Muestra un mensaje de error si hay problemas con la conexión a la base de datos
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Error al conectar a la base de datos.");
                alert.showAndWait();
            }
        } else {
            // Muestra un mensaje de error si no hay usuario seleccionado
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo modificar el usuario. No se ha seleccionado ningún usuario.");
            alert.showAndWait();
        }
    }

    // Método para establecer una referencia al controlador UserControl
    public void setUserControl(UserControl userControl) {
        this.userControl = userControl;
    }
}





