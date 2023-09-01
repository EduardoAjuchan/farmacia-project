package com.example.farmaciaproject;

import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductoControl {
    private Stage mainStage;
    @FXML
    private TextField productoTextField;

    @FXML
    private TextField precioTextField;

    @FXML
    private TextField cantidadTextField;

    @FXML
    private DatePicker vencimientoDatePicker;

    @FXML
    private TableView<Producto> productosTableView;

    @FXML
    private TableColumn<Producto, Integer> idColumn;

    @FXML
    private TableColumn<Producto, String> productoColumn;

    @FXML
    private TableColumn<Producto, Integer> cantidadColumn;

    @FXML
    private TableColumn<Producto, Double> precioColumn;

    @FXML
    private TableColumn<Producto, Date> vencimientoColumn;

    @FXML
    private JFXButton guardarButton;

    @FXML
    private JFXButton modificarButton;

    @FXML
    private JFXButton eliminarButton;

    @FXML
    private JFXButton limpiarButton;

    private ObservableList<Producto> productosList = FXCollections.observableArrayList();

    private Producto selectedProducto;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/bdnegocio";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";

    public void initialize() {
        precioTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                precioTextField.setText(newValue.replaceAll("[^\\d\\.]", ""));
            }
        });

        cantidadTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                cantidadTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        productoColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        cantidadColumn.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        precioColumn.setCellValueFactory(new PropertyValueFactory<>("precio"));

        // Para la columna de fechaVencimiento, necesitas usar un Callback personalizado
        vencimientoColumn.setCellValueFactory(cellData -> {
            SimpleObjectProperty<Date> property = new SimpleObjectProperty<>(cellData.getValue().getFechaVencimiento());
            return property;
        });
        productosTableView.setItems(productosList);
        // Configuración de los manejadores de eventos de los botones
        guardarButton.setOnAction(this::onGuardarButtonClick);
        modificarButton.setOnAction(this::onModificarButtonClick);
        eliminarButton.setOnAction(this::onEliminarButtonClick);
        limpiarButton.setOnAction(this::onLimpiarButtonClick);
        updateTableView();
    }

    @FXML
    private void onGuardarButtonClick(ActionEvent event) {
        String producto = productoTextField.getText().trim();
        String precioStr = precioTextField.getText().trim();
        String cantidadStr = cantidadTextField.getText().trim();
        LocalDate vencimiento = vencimientoDatePicker.getValue();

        if (!producto.isEmpty() && !precioStr.isEmpty() && !cantidadStr.isEmpty() && vencimiento != null) {
            double precio = Double.parseDouble(precioStr);
            int cantidad = Integer.parseInt(cantidadStr);

            Producto nuevoProducto = new Producto(producto, cantidad, precio, Date.valueOf(vencimiento));

            insertProducto(nuevoProducto);

            clearFields();
            updateTableView();
        }
    }

    @FXML
    private void onModificarButtonClick(ActionEvent event) {
        if (selectedProducto != null) {
            openModificarProductoWindow(selectedProducto);
        } else {
            showAlert("Selección requerida", "Selecciona un producto para modificar.");
        }
    }

    private void openModificarProductoWindow(Producto producto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("modificar-producto.fxml"));
            Parent root = loader.load();

            ModificarProductoController modificarProductoController = loader.getController();
            modificarProductoController.initData(producto); // Pasa los datos del producto

            Stage modificarStage = new Stage();
            modificarStage.setTitle("Modificar Producto");
            modificarStage.setScene(new Scene(root));
            modificarProductoController.setModificarStage(modificarStage); // Pasa la referencia de la ventana

            modificarStage.showAndWait();

            // Actualiza los datos después de la modificación
            updateTableView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void onEliminarButtonClick(ActionEvent event) {
        if (selectedProducto != null) {
            boolean confirmacion = showConfirmationDialog("Confirmación", "¿Estás seguro de eliminar este producto?");
            if (confirmacion) {
                deleteProducto(selectedProducto.getId());
                updateTableView();
            }
        } else {
            showAlert("Selección requerida", "Selecciona un producto para eliminar.");
        }
    }

    @FXML
    private void onLimpiarButtonClick(ActionEvent event) {
        clearFields();
    }

    @FXML
    private void onProductoSelected(MouseEvent event) {
        selectedProducto = productosTableView.getSelectionModel().getSelectedItem();
        if (selectedProducto != null) {
            // Rellena los campos con los datos del producto seleccionado
            productoTextField.setText(selectedProducto.getNombre());
            precioTextField.setText(String.valueOf(selectedProducto.getPrecio()));
            cantidadTextField.setText(String.valueOf(selectedProducto.getCantidad()));
            vencimientoDatePicker.setValue(selectedProducto.getFechaVencimiento().toLocalDate());
        }
    }

    void updateTableView() {
        productosList.clear();
        productosList.addAll(getAllProductos());
    }

    private void clearFields() {
        productoTextField.clear();
        precioTextField.clear();
        cantidadTextField.clear();
        vencimientoDatePicker.setValue(null);
        selectedProducto = null;
    }

    private void insertProducto(Producto producto) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Establecer la conexión a la base de datos
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Preparar la consulta SQL para la inserción
            String insertQuery = "INSERT INTO producto (nombreProducto, precioUnitario, cantidadProducto, fechaVencimiento) VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, producto.getNombre());
            preparedStatement.setDouble(2, producto.getPrecio());
            preparedStatement.setInt(3, producto.getCantidad());
            preparedStatement.setDate(4, producto.getFechaVencimiento());

            // Ejecutar la consulta
            preparedStatement.executeUpdate();

            showAlert("Éxito", "Producto insertado correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Error al insertar el producto en la base de datos.");
        } finally {
            // Cerrar recursos
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void deleteProducto(int productoId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Establecer la conexión a la base de datos
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Preparar la consulta SQL para la eliminación
            String deleteQuery = "DELETE FROM producto WHERE codigoProducto = ?";
            preparedStatement = connection.prepareStatement(deleteQuery);
            preparedStatement.setInt(1, productoId);

            // Ejecutar la consulta
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                showAlert("Éxito", "Producto eliminado correctamente.");
            } else {
                showAlert("Error", "No se encontró ningún producto con el ID especificado.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Error al eliminar el producto de la base de datos.");
        } finally {
            // Cerrar recursos
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private List<Producto> getAllProductos() {
        List<Producto> productos = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Establecer la conexión a la base de datos
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Crear una declaración SQL
            statement = connection.createStatement();

            // Ejecutar la consulta para obtener todos los productos
            String selectQuery = "SELECT * FROM producto";
            resultSet = statement.executeQuery(selectQuery);

            // Procesar los resultados
            while (resultSet.next()) {
                int id = resultSet.getInt("codigoProducto");
                String nombre = resultSet.getString("nombreProducto");
                double precio = resultSet.getDouble("precioUnitario");
                int cantidad = resultSet.getInt("cantidadProducto");
                Date fechaVencimiento = resultSet.getDate("fechaVencimiento");

                Producto producto = new Producto(nombre, cantidad, precio, fechaVencimiento);
                producto.setId(id); // Asignar el valor de id obtenido de la base de datos
                productos.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Error al obtener la lista de productos de la base de datos.");
        } finally {
            // Cerrar recursos
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return productos;
    }


    private void showAlert(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    @FXML
    private void onVolverButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mainmenu.fxml"));
            Parent root = loader.load();

            MainMenuController mainMenuController = loader.getController();
            mainMenuController.setMainStage(mainStage); // Configura el mainStage en el controlador MainMenuController

            Scene scene = new Scene(root, 1080, 720); // Establece las dimensiones deseadas
            mainStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private boolean showConfirmationDialog(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        Optional<ButtonType> result = alert.showAndWait();
        return ((Optional<?>) result).isPresent() && result.get() == ButtonType.OK;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }
}

