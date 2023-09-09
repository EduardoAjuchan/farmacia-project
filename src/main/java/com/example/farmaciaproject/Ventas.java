package com.example.farmaciaproject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;

import com.jfoenix.controls.JFXButton;
import javafx.beans.value.ObservableValue;
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

        venderButton.setOnAction(event -> {
            vender();
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
        // Obtén el código ingresado en el TextField "Codigo"
        String codigoProducto = codigoTextField.getText();

        // Realiza una consulta a la base de datos para obtener la información del producto
        Producto producto = obtenerProductoDesdeBD(codigoProducto);

        // Verifica si se encontró el producto en la base de datos
        if (producto != null) {
            int cantidad = Integer.parseInt(cantidadTextField.getText()); // Cantidad ingresada
            double subtotal = cantidad * producto.getPrecio(); // Calcula el subtotal

            // Actualiza la cantidad y el subtotal del producto
            producto.setCantidad(cantidad);
            producto.setSubtotal(subtotal);

            // Agrega el producto a la tabla
            tableView.getItems().add(producto);

            // Actualiza el total de la venta
            actualizarTotalVenta();
        } else {
            // Muestra un mensaje de error si el producto no se encontró en la base de datos
            mostrarMensajeError("Producto no encontrado en la base de datos.");
        }
    }

    private Producto obtenerProductoDesdeBD(String codigoProducto) {
        // Realiza una consulta a la base de datos para obtener la información del producto
        // Debes reemplazar los siguientes valores con la configuración de tu base de datos
        String url = "jdbc:mysql://localhost:3306/bdnegocio";
        String usuario = "root";
        String contraseña = "123456";

        try (Connection conn = DriverManager.getConnection(url, usuario, contraseña)) {
            String selectProductoSQL = "SELECT nombreProducto, precioUnitario FROM producto WHERE codigoProducto = ?";
            PreparedStatement pstmtProducto = conn.prepareStatement(selectProductoSQL);
            pstmtProducto.setString(1, codigoProducto);

            ResultSet rsProducto = pstmtProducto.executeQuery();
            if (rsProducto.next()) {
                String nombreProducto = rsProducto.getString("nombreProducto");
                double precio = rsProducto.getDouble("precioUnitario");
                return new Producto(nombreProducto, 0, precio, 0.0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Retorna null si el producto no se encontró en la base de datos
    }


    private void mostrarMensajeError(String s) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }

    private void vender() {
        // Aquí debes agregar la lógica para guardar la venta en la base de datos (Tabla Ventas y DetallesVenta)
        // y mostrar el mensaje "Venta realizada con éxito".
        // Ejemplo de cómo mostrar el mensaje de éxito:
        guardarVentaEnBaseDeDatos();
        mostrarMensajeExito("Venta realizada con éxito");
    }

    private void actualizarTotalVenta() {
        // Calcular el total de la venta sumando los subtotales de los productos en la tabla
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

            // Obtener el código de venta generado automáticamente
            String insertVentaSQL = "INSERT INTO Ventas (fechaVenta, nombreCliente, totalVenta) VALUES (?, ?, ?)";
            PreparedStatement pstmtVenta = conn.prepareStatement(insertVentaSQL, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmtVenta.setDate(1, java.sql.Date.valueOf(java.time.LocalDate.now())); // Fecha actual
            pstmtVenta.setString(2, clienteTextField.getText()); // Nombre del cliente
            pstmtVenta.setDouble(3, totalVenta); // Total de la venta

            pstmtVenta.executeUpdate();

            // Obtener el código de venta generado
            int codigoVentaGenerado = -1;
            ResultSet rsVenta = pstmtVenta.getGeneratedKeys();
            if (rsVenta.next()) {
                codigoVentaGenerado = rsVenta.getInt(1);
            }

            // Insertar detalles de venta en DetallesVenta
            for (Producto producto : tableView.getItems()) {
                String insertDetalleVentaSQL = "INSERT INTO DetallesVenta (codigoVenta, codigoProducto, cantidadVendida, totalProducto) VALUES (?, ?, ?, ?)";
                PreparedStatement pstmtDetalleVenta = conn.prepareStatement(insertDetalleVentaSQL);
                pstmtDetalleVenta.setInt(1, codigoVentaGenerado);
                pstmtDetalleVenta.setInt(2, obtenerCodigoProductoDesdeBD(producto.getNombre())); // Obtener el código de producto desde la base de datos
                pstmtDetalleVenta.setInt(3, producto.getCantidad());
                pstmtDetalleVenta.setDouble(4, producto.getSubtotal());

                pstmtDetalleVenta.executeUpdate();
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int obtenerCodigoProductoDesdeBD(String nombreProducto) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/bdnegocio";
        String usuario = "root";
        String contraseña = "123456";

        Connection conn = DriverManager.getConnection(url, usuario, contraseña);

        String selectProductoSQL = "SELECT codigoProducto FROM producto WHERE nombreProducto = ?";
        PreparedStatement pstmtProducto = conn.prepareStatement(selectProductoSQL);
        pstmtProducto.setString(1, nombreProducto);

        ResultSet rsProducto = pstmtProducto.executeQuery();
        int codigoProducto = -1;
        if (rsProducto.next()) {
            codigoProducto = rsProducto.getInt("codigoProducto");
        }

        conn.close();

        return codigoProducto;
    }

    public static class Producto {
        private final SimpleStringProperty nombre;
        private final SimpleIntegerProperty cantidad;
        private final SimpleDoubleProperty precio;
        private final SimpleDoubleProperty subtotal;

        public Producto(String nombre, int cantidad, double precio, double subtotal) {
            this.nombre = new SimpleStringProperty(nombre);
            this.cantidad = new SimpleIntegerProperty(cantidad);
            this.precio = new SimpleDoubleProperty(precio);
            this.subtotal = new SimpleDoubleProperty(subtotal);
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

        // Agrega métodos setter para actualizar cantidad y subtotal
        public void setCantidad(int cantidad) {
            this.cantidad.set(cantidad);
        }

        public void setSubtotal(double subtotal) {
            this.subtotal.set(subtotal);
        }
    }
}


