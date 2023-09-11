package com.example.farmaciaproject;

public class Session {
    private static Session instance;
    private String username;
    private String userType; // Tipo de usuario: "Administrador" o "Empleado"

    private Session() {
        // Constructor privado para garantizar que solo haya una instancia
    }

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public void setUser(String username) {
        this.username = username;
    }

    public String getUser() {
        return username;
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
    }
}

