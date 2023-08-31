package com.example.farmaciaproject;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date; // Asegúrate de importar esta clase

import java.time.LocalDate;

public class ModificarProductoController {

    @FXML
    private TextField productoTextField;

    @FXML
    private TextField precioTextField;

    @FXML
    private TextField cantidadTextField;

    @FXML
    private DatePicker vencimientoDatePicker;

    private Stage modificarStage;
    private Producto producto; // Utiliza esta referencia al producto seleccionado

    public void initData(Producto producto) {
        this.producto = producto;
        productoTextField.setText(producto.getNombre());
        precioTextField.setText(String.valueOf(producto.getPrecio()));
        cantidadTextField.setText(String.valueOf(producto.getCantidad()));
        vencimientoDatePicker.setValue(producto.getFechaVencimiento().toLocalDate());
    }

    @FXML
    private void onModificarButtonClick() {
        String nuevoProducto = productoTextField.getText();
        String nuevoPrecioStr = precioTextField.getText();
        String nuevaCantidadStr = cantidadTextField.getText();
        LocalDate nuevaFechaVencimiento = vencimientoDatePicker.getValue();
        // Verificar si algún campo ha cambiado
        if (sonCamposIguales(nuevoProducto, nuevoPrecioStr, nuevaCantidadStr, nuevaFechaVencimiento)) {
            showAlert("Sin cambios", "Debes hacer algún cambio antes de modificar el producto.");
            return;
        }

        if (!nuevoProducto.isEmpty() && !nuevoPrecioStr.isEmpty() && !nuevaCantidadStr.isEmpty() && nuevaFechaVencimiento != null) {
            double nuevoPrecio = Double.parseDouble(nuevoPrecioStr);
            int nuevaCantidad = Integer.parseInt(nuevaCantidadStr);

            // Aquí realizamos la lógica para guardar los cambios en la base de datos
            String url = "jdbc:mysql://localhost:3306/bdnegocio";
            String username = "root";
            String password = "123456";

            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                String query = "UPDATE producto SET nombreProducto = ?, precioUnitario = ?, cantidadProducto = ?, fechaVencimiento = ? WHERE codigoProducto = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, nuevoProducto);
                    preparedStatement.setDouble(2, nuevoPrecio);
                    preparedStatement.setInt(3, nuevaCantidad);
                    preparedStatement.setDate(4, Date.valueOf(nuevaFechaVencimiento));
                    preparedStatement.setInt(5, producto.getId()); // Utiliza el ID del producto seleccionado

                    preparedStatement.executeUpdate();

                    showAlert("Éxito", "Producto modificado exitosamente.");
                    modificarStage.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Error", "Error al modificar el producto en la base de datos.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert("Error", "Error al conectar a la base de datos.");
            }
        } else {
            showAlert("Campos incompletos", "Por favor completa todos los campos antes de modificar el producto.");
        }
    }
    private boolean sonCamposIguales(String nuevoProducto, String nuevoPrecioStr, String nuevaCantidadStr, LocalDate nuevaFechaVencimiento) {
        return nuevoProducto.equals(producto.getNombre())
                && nuevoPrecioStr.equals(String.valueOf(producto.getPrecio()))
                && nuevaCantidadStr.equals(String.valueOf(producto.getCantidad()))
                && nuevaFechaVencimiento.equals(producto.getFechaVencimiento().toLocalDate());
    }
    public void setModificarStage(Stage modificarStage) {
        this.modificarStage = modificarStage;
    }

    private void showAlert(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
