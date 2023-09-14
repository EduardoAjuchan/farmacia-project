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
import javafx.scene.text.Text;
import javafx.scene.control.Alert;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class Ventas {
    @FXML
    private javafx.scene.control.Label totalLabel;
    @FXML
    private DatePicker dePicker;
    @FXML
    private DatePicker hastaPicker;
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
    private JFXButton agregarButton;
    @FXML
    private JFXButton quitarButton;
    @FXML
    private JFXButton venderButton;
    @FXML
    private JFXButton exportButton;
    @FXML
    private javafx.scene.control.Label bienvenidoLabel;
    private Stage mainStage;
    private double totalVenta = 0.0;

    // Lista para mantener un registro de los productos vendidos
    private List<Producto> productosVendidos = new ArrayList<>();

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

        exportButton.setOnAction(event -> {
            exportarAExcel();
        });

        venderButton.setOnAction(event -> {
            vender();
        });

        quitarButton.setDisable(true);

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            quitarButton.setDisable(newValue == null);
        });

        // Actualiza el texto del Label con el nombre de usuario
        Session session = Session.getInstance();
        String nombreUsuario = session.getUser();
        bienvenidoLabel.setText("¡Bienvenido, " + nombreUsuario + "!");
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

            productosVendidos.add(nuevoProducto); // Agregar a la lista de productos vendidos
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
        // Verificar si se han ingresado al menos un producto en el TableView
        if (tableView.getItems().isEmpty()) {
            mostrarMensajeError("Debe ingresar al menos un producto.");
            return; // Salir de la función si no hay productos en el TableView
        }

        String nombreCliente = clienteTextField.getText().isEmpty() ? "CF" : clienteTextField.getText();
        guardarVentaEnBaseDeDatos(nombreCliente);
        mostrarMensajeExito("Venta realizada con éxito");

        codigoTextField.clear();
        cantidadTextField.clear();
        clienteTextField.clear();
        tableView.getItems().clear();

        // Reiniciar la variable totalVenta después de cada venta
        totalVenta = 0.0;
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        totalLabel.setText("Total: Q" + decimalFormat.format(totalVenta));
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

    private void guardarVentaEnBaseDeDatos(String nombreCliente) {
        try {
            String url = "jdbc:mysql://localhost:3306/bdnegocio";
            String usuario = "root";
            String contraseña = "123456";

            Connection conn = DriverManager.getConnection(url, usuario, contraseña);

            String insertVentaSQL = "INSERT INTO Ventas (fechaVenta, nombreCliente, totalVenta, nombreUsuario) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmtVenta = conn.prepareStatement(insertVentaSQL, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmtVenta.setDate(1, java.sql.Date.valueOf(java.time.LocalDate.now()));
            pstmtVenta.setString(2, nombreCliente);
            pstmtVenta.setDouble(3, totalVenta);

            Session session = Session.getInstance();
            String nombreUsuario = session.getUser(); // Obtener el nombre del usuario

            pstmtVenta.setString(4, nombreUsuario); // Establecer el nombre del usuario

            int affectedRows = pstmtVenta.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("La inserción de la venta falló, no se generó ningún ID.");
            }

            // Obtener el ID de la venta generada automáticamente
            try (ResultSet generatedKeys = pstmtVenta.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int ventaId = generatedKeys.getInt(1); // Obtén el ID generado automáticamente

                    // Insertar detalles de la venta en la tabla DetallesVenta
                    String insertDetallesSQL = "INSERT INTO DetallesVenta (codigoVenta, codigoProducto, nombreProducto, cantidadVendida, totalProducto) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement pstmtDetalles = conn.prepareStatement(insertDetallesSQL);

                    for (Producto producto : productosVendidos) {
                        pstmtDetalles.setInt(1, ventaId); // Asigna el ID de la venta
                        pstmtDetalles.setInt(2, producto.getCodigo()); // Código del producto
                        pstmtDetalles.setString(3, producto.getNombre()); // Nombre del producto
                        pstmtDetalles.setInt(4, producto.getCantidad()); // Cantidad vendida
                        pstmtDetalles.setDouble(5, producto.getSubtotal()); // Total del producto
                        pstmtDetalles.executeUpdate();
                    }

                    pstmtDetalles.close();

                    // Limpiar la lista de productos vendidos después de guardar la venta
                    productosVendidos.clear();
                } else {
                    throw new SQLException("La inserción de la venta falló, no se generó ningún ID.");
                }
            }

            // Cierra las conexiones y recursos
            pstmtVenta.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void exportarAExcel() {
        try {
            Date fechaInicio = Date.valueOf(dePicker.getValue());
            Date fechaFin = Date.valueOf(hastaPicker.getValue());

            if (fechaInicio == null || fechaFin == null) {
                mostrarMensajeError("Selecciona una fecha de inicio y una fecha final.");
                return;
            }

            if (fechaFin.before(fechaInicio)) {
                mostrarMensajeError("La fecha de inicio no puede ser posterior a la fecha final.");
                return;
            }

            // Obtener la ruta del escritorio del usuario
            String desktopPath = System.getProperty("user.home") + "/Desktop/";

            // Crear un nuevo libro de trabajo de Excel en el escritorio
            WritableWorkbook workbook = Workbook.createWorkbook(new File(desktopPath + "ventas.xls"));

            // Crear una hoja de Excel
            WritableSheet sheet = workbook.createSheet("Ventas", 0);

            // Crear la fila de encabezados
            sheet.addCell(new Label(0, 0, "CodigoVenta"));
            sheet.addCell(new Label(1, 0, "FechaVenta"));
            sheet.addCell(new Label(2, 0, "NombreCliente"));
            sheet.addCell(new Label(3, 0, "TotalVenta"));
            sheet.addCell(new Label(4, 0, "NombreUsuario"));
            sheet.addCell(new Label(5, 0, "CodigoProducto"));
            sheet.addCell(new Label(6, 0, "CantidadVendida"));
            sheet.addCell(new Label(7, 0, "TotalProducto"));
            sheet.addCell(new Label(8, 0, "NombreProducto"));

            // Consultar la base de datos para obtener las ventas dentro del rango de fechas
            String url = "jdbc:mysql://localhost:3306/bdnegocio";
            String usuario = "root";
            String contraseña = "123456";

            try (Connection conn = DriverManager.getConnection(url, usuario, contraseña)) {
                String selectSQL = "SELECT V.codigoVenta, V.fechaVenta, V.nombreCliente, V.totalVenta, V.nombreUsuario, " +
                        "DV.codigoProducto, DV.cantidadVendida, DV.totalProducto, DV.nombreProducto " +
                        "FROM Ventas V " +
                        "INNER JOIN DetallesVenta DV ON V.codigoVenta = DV.codigoVenta " +
                        "WHERE V.fechaVenta BETWEEN ? AND ?";

                PreparedStatement pstmt = conn.prepareStatement(selectSQL);
                pstmt.setDate(1, fechaInicio);
                pstmt.setDate(2, fechaFin);

                ResultSet rs = pstmt.executeQuery();
                int rowNum = 1;

                while (rs.next()) {
                    // Obtener los datos de la consulta
                    int codigoVenta = rs.getInt("codigoVenta");
                    Date fechaVenta = rs.getDate("fechaVenta");
                    String nombreCliente = rs.getString("nombreCliente");
                    double totalVenta = rs.getDouble("totalVenta");
                    String nombreUsuario = rs.getString("nombreUsuario");
                    int codigoProducto = rs.getInt("codigoProducto");
                    int cantidadVendida = rs.getInt("cantidadVendida");
                    double totalProducto = rs.getDouble("totalProducto");
                    String nombreProducto = rs.getString("nombreProducto");

                    // Llenar la hoja de Excel con los datos
                    sheet.addCell(new jxl.write.Number(0, rowNum, codigoVenta));
                    sheet.addCell(new Label(1, rowNum, fechaVenta.toString()));
                    sheet.addCell(new Label(2, rowNum, nombreCliente));
                    sheet.addCell(new jxl.write.Number(3, rowNum, totalVenta));
                    sheet.addCell(new Label(4, rowNum, nombreUsuario));
                    sheet.addCell(new jxl.write.Number(5, rowNum, codigoProducto));
                    sheet.addCell(new jxl.write.Number(6, rowNum, cantidadVendida));
                    sheet.addCell(new jxl.write.Number(7, rowNum, totalProducto));
                    sheet.addCell(new Label(8, rowNum, nombreProducto));

                    rowNum++;
                }

                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarMensajeError("Error al consultar la base de datos.");
                return;
            }

            // Escribir el libro en un archivo
            workbook.write();
            workbook.close();

            mostrarMensajeExito("Datos exportados a Excel en el escritorio correctamente.");

            // Limpiar los DatePicker
            dePicker.setValue(null);
            hastaPicker.setValue(null);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensajeError("Error al exportar los datos a Excel.");
        }
    }



    private void quitarProductoSeleccionado() {
        Producto productoSeleccionado = tableView.getSelectionModel().getSelectedItem();

        if (productoSeleccionado != null) {
            tableView.getItems().remove(productoSeleccionado);
            productosVendidos.remove(productoSeleccionado); // Quitar de la lista de productos vendidos
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









