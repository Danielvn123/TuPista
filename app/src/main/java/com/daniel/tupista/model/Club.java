package com.daniel.tupista.model;

import java.util.List;

// Clase que representa un club de pádel dentro de la aplicación
public class Club {

    // Identificador del club y datos básicos
    private int id;
    private String nombre, direccion, telefono;

    // Lista de pistas asociadas al club
    private List<Pista> pistas;

    // Constructor vacío necesario para que Retrofit pueda crear objetos Club automáticamente
    public Club() {}

    // Constructor con los datos principales del club
    public Club(int id, String nombre, String direccion, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    // Devuelve el nombre del club evitando valores null
    public String getNombre() { return nombre == null ? "" : nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    // Devuelve la dirección evitando valores null
    public String getDireccion() { return direccion == null ? "" : direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    // Devuelve el teléfono evitando valores null
    public String getTelefono() { return telefono == null ? "" : telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    // Devuelve la lista de pistas relacionadas con este club
    public List<Pista> getPistas() { return pistas; }
    public void setPistas(List<Pista> pistas) { this.pistas = pistas; }

    // Calcula el número total de pistas del club
    public int getTotalPistas() {
        return pistas == null ? 0 : pistas.size();
    }
}