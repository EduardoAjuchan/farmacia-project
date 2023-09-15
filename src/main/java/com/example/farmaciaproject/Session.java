package com.example.farmaciaproject;

public class Session {
    private static Session instance;
    private String username;
    private String userType; // Tipo de usuario: "Administrador" o "Empleado"
    private int userId; // Agregar el ID del usuario
    private String userNombre; // Agregar el nombre del usuario

    private Session() {
    }

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public void setUser(String username, int userId, String userNombre) {
        this.username = username;
        this.userId = userId; // Establecer el ID del usuario
        this.userNombre = userNombre; // Establecer el nombre del usuario
    }

    public String getUser() {
        return username;
    }

    public int getUserId() { // Obtener el ID del usuario
        return userId;
    }

    public String getUserNombre() { // Obtener el nombre del usuario
        return userNombre;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserType() {
        return userType;
    }

    public void clearSession() {
        username = null;
        userType = null;
        userId = -1; // Restablecer el ID del usuario
        userNombre = null; // Restablecer el nombre del usuario
    }

    public void setUser(String username) {
        this.username = username;

    }
}


