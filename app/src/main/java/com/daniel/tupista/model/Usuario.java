package com.daniel.tupista.model;

// Clase que representa un usuario registrado en la aplicación
public class Usuario {

    // Identificador del usuario y datos principales de su perfil
    private int id;
    private String nombre, email, telefono, rol;

    // Constructor vacío necesario para Retrofit
    public Usuario() {
    }

    // Constructor con los datos principales del usuario
    public Usuario(int id, String nombre, String email, String telefono, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.rol = rol;
    }

    // Devuelve el identificador único del usuario
    public int getId() {
        return id;
    }

    // Modifica el identificador del usuario
    public void setId(int id) {
        this.id = id;
    }

    // Devuelve el nombre del usuario
    public String getNombre() {
        return nombre;
    }

    // Modifica el nombre del usuario
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Devuelve el correo electrónico del usuario
    public String getEmail() {
        return email;
    }

    // Modifica el correo electrónico del usuario
    public void setEmail(String email) {
        this.email = email;
    }

    // Devuelve el teléfono del usuario
    public String getTelefono() {
        return telefono;
    }

    // Modifica el teléfono del usuario
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    // Devuelve el rol del usuario, por ejemplo usuario o admin
    public String getRol() {
        return rol;
    }

    // Modifica el rol del usuario
    public void setRol(String rol) {
        this.rol = rol;
    }
}