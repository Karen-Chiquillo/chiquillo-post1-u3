package com.universidad.confudes.acceso;

public class Usuario {
    private final String nombre;
    private final String rol;

    public Usuario(String nombre, String rol) {
        this.nombre = nombre;
        this.rol = rol;
    }

    public String getRol() { return rol; }
    public String getNombre() { return nombre; }
}