package com.example.farmaciaproject;
import com.example.farmaciaproject.MainMenuController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.farmaciaproject.Main.mainStage;

public class UserControl {

    @FXML
    private Label bienvenidoLabel;

    @FXML
    private TextField usuarioTextField;

    @FXML
    private TextField contrasenaTextField;

    @FXML
    private ComboBox<String> tipoUsuarioComboBox;

    @FXML
    private TableView<Usuario> usuariosTableView;

    @FXML
    private TableColumn<Usuario, Integer> idColumn;

    @FXML
    private TableColumn<Usuario, String> usuarioColumn;

    @FXML
    private TableColumn<Usuario, String> contrasenaColumn;

    @FXML
    private TableColumn<Usuario, String> tipoUsuarioColumn;

    private DatabaseConnector databaseConnector;

    public void initialize() {
        databaseConnector = new DatabaseConnector();

        tipoUsuarioComboBox.getItems().addAll("empleado", "administrador");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usuarioColumn.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        contrasenaColumn.setCellValueFactory(new PropertyValueFactory<>("contrasena"));
        tipoUsuarioColumn.setCellValueFactory(new PropertyValueFactory<>("tipoUsuario"));

        updateTableView();

        // Obtener el nombre de usuario actual desde la sesión
        Session session = Session.getInstance();
        String nombreUsuario = session.getUser();
        bienvenidoLabel.setText("¡Bienvenido, " + nombreUsuario + "!");
    }

    @FXML
    private void onGuardarButtonClick(ActionEvent event) {
        String usuario = usuarioTextField.getText().trim();
        String contrasena = contrasenaTextField.getText().trim();
        String tipoUsuario = tipoUsuarioComboBox.getValue();

        if (!usuario.isEmpty() && !contrasena.isEmpty() && tipoUsuario != null) {
            databaseConnector.insertUser(usuario, contrasena, tipoUsuario);

            usuarioTextField.clear();
            contrasenaTextField.clear();
            tipoUsuarioComboBox.getSelectionModel().clearSelection();

            updateTableView();
        }
    }

    @FXML
    private void onModificarButtonClick(ActionEvent event) {
        Usuario selectedUsuario = usuariosTableView.getSelectionModel().getSelectedItem();

        if (selectedUsuario != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("modificar-user.fxml"));
                Parent root = loader.load();

                ModificarUser modificarUserController = loader.getController();
                modificarUserController.initData(selectedUsuario);
                modificarUserController.setUserControl(this);

                Stage stage = new Stage();
                stage.setTitle("Modificar Usuario");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Selección requerida", "Selecciona un usuario para modificar.");
        }
    }

    @FXML
    private void onEliminarButtonClick(ActionEvent event) {
        Usuario selectedUsuario = usuariosTableView.getSelectionModel().getSelectedItem();

        if (selectedUsuario != null) {
            boolean confirmacion = showConfirmationDialog("Confirmación", "¿Estás seguro de eliminar este usuario?");
            if (confirmacion) {
                databaseConnector.deleteUser(selectedUsuario.getId());
                updateTableView();
            }
        } else {
            showAlert("Selección requerida", "Selecciona un usuario para eliminar.");
        }
    }


    @FXML
    private void onLimpiarButtonClick(ActionEvent event) {
        usuarioTextField.clear();
        contrasenaTextField.clear();
        tipoUsuarioComboBox.getSelectionModel().clearSelection();
    }

    private void updateTableView() {
        List<Usuario> usuarios = databaseConnector.getAllUsuarios();
        usuariosTableView.getItems().clear();
        usuariosTableView.getItems().addAll(usuarios);
    }

    @FXML
    private void onVolverButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mainmenu.fxml"));
            Parent root = loader.load();

            MainMenuController mainMenuController = loader.getController();
            mainMenuController.setMainStage(mainStage); // Establece el mainStage en MainMenuController

            Scene scene = new Scene(root, 1080, 720); // Establece las dimensiones deseadas
            mainStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void showAlert(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    public void usuarioModificado(int userId) {
        // Actualiza los datos en la tabla de la interfaz gráfica
        updateTableView();
    }

    private boolean showConfirmationDialog(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
