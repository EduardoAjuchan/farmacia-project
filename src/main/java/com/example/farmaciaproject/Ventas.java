package com.example.farmaciaproject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.text.DecimalFormat;

public class Ventas {
    @FXML
    private Button ButtonVolver;
    @FXML
    private TextField codigoTextField;
    @FXML
    private TextField cantidadTextField;
    @FXML
    private TextField clienteTextField;
    @FXML
    private TableView<Producto> tableView;
    @FXML
    private TableColumn<Producto, String> productoColumn;
    @FXML
    private TableColumn<Producto, Integer> cantidadColumn;
    @FXML
    private TableColumn<Producto, Double> precioColumn;
    @FXML
    private TableColumn<Producto, Double> subtotalColumn;
    @FXML
    private Label totalLabel;
    @FXML
    private JFXButton agregarButton;
    @FXML
    private JFXButton quitarButton;
    @FXML
    private JFXButton venderButton;

    private Stage mainStage;
    private double totalVenta = 0.0;

    public Ventas() {
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @FXML
    protected void initialize() {
        ButtonVolver.setOnAction(event -> {
            onVolverButtonClick();
        });

        productoColumn.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        cantidadColumn.setCellValueFactory(cellData -> cellData.getValue().cantidadProperty().asObject());
        precioColumn.setCellValueFactory(cellData -> cellData.getValue().precioProperty().asObject());
        subtotalColumn.setCellValueFactory(cellData -> cellData.getValue().subtotalProperty().asObject());

        cantidadTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                cantidadTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        agregarButton.setOnAction(event -> {
            agregarProducto();
        });

        quitarButton.setOnAction(event -> {
            quitarProductoSeleccionado();
        });

        venderButton.setOnAction(event -> {
            vender();
        });

        quitarButton.setDisable(true);

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            quitarButton.setDisable(newValue == null);
        });
    }

    @FXML
    protected void onVolverButtonClick() {
        loadAndSetScene("mainmenu.fxml");
    }

    private void loadAndSetScene(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1080, 720);
            mainStage.setScene(scene);

            if (fxmlFileName.equals("mainmenu.fxml")) {
                MainMenuController mainMenuController = loader.getController();
                mainMenuController.setMainStage(mainStage);
                mainMenuController.initialize();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void agregarProducto() {
        String codigoProducto = codigoTextField.getText();
        Producto producto = obtenerProductoDesdeBD(codigoProducto);

        if (producto != null) {
            int cantidad = Integer.parseInt(cantidadTextField.getText());
            double subtotal = cantidad * producto.getPrecio();

            Producto nuevoProducto = new Producto(producto.getNombre(), cantidad, producto.getPrecio(), subtotal, producto.getCodigo());

            tableView.getItems().add(nuevoProducto);

            actualizarTotalVenta();
            codigoTextField.clear();
            cantidadTextField.clear();
        } else {
            mostrarMensajeError("Producto no encontrado en la base de datos.");
        }
    }

    private Producto obtenerProductoDesdeBD(String codigoProducto) {
        String url = "jdbc:mysql://localhost:3306/bdnegocio";
        String usuario = "root";
        String contraseña = "123456";

        try (Connection conn = DriverManager.getConnection(url, usuario, contraseña)) {
            String selectProductoSQL = "SELECT nombreProducto, precioUnitario, codigoProducto FROM producto WHERE codigoProducto = ?";
            PreparedStatement pstmtProducto = conn.prepareStatement(selectProductoSQL);
            pstmtProducto.setString(1, codigoProducto);

            ResultSet rsProducto = pstmtProducto.executeQuery();
            if (rsProducto.next()) {
                String nombreProducto = rsProducto.getString("nombreProducto");
                double precio = rsProducto.getDouble("precioUnitario");
                int codigo = rsProducto.getInt("codigoProducto");
                return new Producto(nombreProducto, 0, precio, 0.0, codigo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void mostrarMensajeError(String s) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }

    private void vender() {
        // Verificar si se han ingresado al menos un producto, un código y un cliente
        if (tableView.getItems().isEmpty() || codigoTextField.getText().isEmpty() || clienteTextField.getText().isEmpty()) {
            mostrarMensajeError("Debe ingresar al menos un producto, un código y un cliente.");
            return; // Salir de la función si no se cumplen los requisitos
        }
        guardarVentaEnBaseDeDatos();
        mostrarMensajeExito("Venta realizada con éxito");

        codigoTextField.clear();
        cantidadTextField.clear();
        clienteTextField.clear();
        tableView.getItems().clear();
        totalLabel.setText("Total: Q0.00");
    }

    private void actualizarTotalVenta() {
        double total = 0.0;
        for (Producto producto : tableView.getItems()) {
            total += producto.getSubtotal();
        }
        totalVenta = total;
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        totalLabel.setText("Total: Q" + decimalFormat.format(totalVenta));
    }

    private void mostrarMensajeExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void guardarVentaEnBaseDeDatos() {
        try {
            String url = "jdbc:mysql://localhost:3306/bdnegocio";
            String usuario = "root";
            String contraseña = "123456";

            Connection conn = DriverManager.getConnection(url, usuario, contraseña);

            String insertVentaSQL = "INSERT INTO Ventas (fechaVenta, nombreCliente, totalVenta) VALUES (?, ?, ?)";
            PreparedStatement pstmtVenta = conn.prepareStatement(insertVentaSQL, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmtVenta.setDate(1, java.sql.Date.valueOf(java.time.LocalDate.now()));
            pstmtVenta.setString(2, clienteTextField.getText());
            pstmtVenta.setDouble(3, totalVenta);

            pstmtVenta.executeUpdate();

            int codigoVentaGenerado = -1;
            ResultSet rsVenta = pstmtVenta.getGeneratedKeys();
            if (rsVenta.next()) {
                codigoVentaGenerado = rsVenta.getInt(1);
            }

            for (Producto producto : tableView.getItems()) {
                String insertDetalleVentaSQL = "INSERT INTO DetallesVenta (codigoVenta, codigoProducto, cantidadVendida, totalProducto, nombreProducto) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmtDetalleVenta = conn.prepareStatement(insertDetalleVentaSQL);
                pstmtDetalleVenta.setInt(1, codigoVentaGenerado);
                pstmtDetalleVenta.setInt(2, producto.getCodigo());
                pstmtDetalleVenta.setInt(3, producto.getCantidad());
                pstmtDetalleVenta.setDouble(4, producto.getSubtotal());
                pstmtDetalleVenta.setString(5, producto.getNombre());

                pstmtDetalleVenta.executeUpdate();

                String updateExistenciasSQL = "UPDATE producto SET cantidadProducto = cantidadProducto - ? WHERE codigoProducto = ?";
                PreparedStatement pstmtExistencias = conn.prepareStatement(updateExistenciasSQL);
                pstmtExistencias.setInt(1, producto.getCantidad());
                pstmtExistencias.setInt(2, producto.getCodigo());
                pstmtExistencias.executeUpdate();
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void quitarProductoSeleccionado() {
        Producto productoSeleccionado = tableView.getSelectionModel().getSelectedItem();

        if (productoSeleccionado != null) {
            tableView.getItems().remove(productoSeleccionado);
            actualizarTotalVenta();
        } else {
            mostrarMensajeError("Selecciona un producto para quitar.");
        }
    }

    public static class Producto {
        private final SimpleStringProperty nombre;
        private final SimpleIntegerProperty cantidad;
        private final SimpleDoubleProperty precio;
        private final SimpleDoubleProperty subtotal;
        private final int codigo;

        public Producto(String nombre, int cantidad, double precio, double subtotal, int codigo) {
            this.nombre = new SimpleStringProperty(nombre);
            this.cantidad = new SimpleIntegerProperty(cantidad);
            this.precio = new SimpleDoubleProperty(precio);
            this.subtotal = new SimpleDoubleProperty(subtotal);
            this.codigo = codigo;
        }

        public String getNombre() {
            return nombre.get();
        }

        public Integer getCantidad() {
            return cantidad.get();
        }

        public Double getPrecio() {
            return precio.get();
        }

        public Double getSubtotal() {
            return subtotal.get();
        }

        public int getCodigo() {
            return codigo;
        }

        public SimpleStringProperty nombreProperty() {
            return nombre;
        }

        public SimpleIntegerProperty cantidadProperty() {
            return cantidad;
        }

        public SimpleDoubleProperty precioProperty() {
            return precio;
        }

        public SimpleDoubleProperty subtotalProperty() {
            return subtotal;
        }

        public void setCantidad(int cantidad) {
            this.cantidad.set(cantidad);
        }

        public void setSubtotal(double subtotal) {
            this.subtotal.set(subtotal);
        }
    }
}




