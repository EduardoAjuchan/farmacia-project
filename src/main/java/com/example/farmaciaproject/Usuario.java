package com.example.farmaciaproject;

public class Usuario {

    private int id;
    private String nombreUsuario;
    private String contrasena;
    private String tipoUsuario;

    public Usuario(int id, String nombreUsuario, String contrasena, String tipoUsuario) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.tipoUsuario = tipoUsuario;
    }

    public int getId() {
        return id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }
}
