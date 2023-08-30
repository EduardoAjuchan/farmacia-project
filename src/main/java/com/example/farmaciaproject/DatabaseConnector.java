package com.example.farmaciaproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnector {

    private static final String URL = "jdbc:mysql://localhost:3306/bdnegocio";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";

    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Conexión a la base de datos establecida.");
            } catch (ClassNotFoundException | SQLException e) {
                System.err.println("Error al conectar a la base de datos.");
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexión a la base de datos cerrada.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void insertUser(String nombreUsuario, String contrasena, String tipoUsuario) {
        Connection connection = getConnection();
        String query = "INSERT INTO usuarios (nombre_usuario, contrasena, tipo_usuario) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, nombreUsuario);
            preparedStatement.setString(2, contrasena);
            preparedStatement.setString(3, tipoUsuario);
            preparedStatement.executeUpdate();
            System.out.println("Usuario insertado correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al insertar usuario.");
            e.printStackTrace();
        }
    }

    public static void updateUser(int id, String nombreUsuario, String contrasena, String tipoUsuario) {
        Connection connection = getConnection();
        String query = "UPDATE usuarios SET nombre_usuario = ?, contrasena = ?, tipo_usuario = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, nombreUsuario);
            preparedStatement.setString(2, contrasena);
            preparedStatement.setString(3, tipoUsuario);
            preparedStatement.setInt(4, id);
            preparedStatement.executeUpdate();
            System.out.println("Usuario actualizado correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario.");
            e.printStackTrace();
        }
    }

    public static void deleteUser(int id) {
        Connection connection = getConnection();
        String query = "DELETE FROM usuarios WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            System.out.println("Usuario eliminado correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario.");
            e.printStackTrace();
        }
    }
}

