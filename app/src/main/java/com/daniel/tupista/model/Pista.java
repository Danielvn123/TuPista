package com.daniel.tupista.model;

// Clase que representa una pista de pádel perteneciente a un club
public class Pista {

    // Identificador de la pista y del club al que pertenece
    private int id, clubId;

    // Nombre de la pista
    private String nombre;

    // Indica si la pista está disponible para reservas
    private boolean disponible;

    // Objeto Club obtenido mediante relaciones de Supabase
    private Club clubes;

    // Constructor vacío necesario para Retrofit
    public Pista() {}

    // Constructor con los datos principales de la pista
    public Pista(int id, String nombre, int clubId) {
        this.id = id;
        this.nombre = nombre;
        this.clubId = clubId;
        this.disponible = true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    // Devuelve el nombre evitando valores null
    public String getNombre() { return nombre == null ? "" : nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getClubId() { return clubId; }
    public void setClubId(int clubId) { this.clubId = clubId; }

    // Devuelve el nombre del club asociado a la pista
    public String getClub() {
        return clubes == null ? "" : clubes.getNombre();
    }

    // Devuelve el objeto Club completo asociado a la pista
    public Club getClubes() { return clubes; }
    public void setClubes(Club clubes) { this.clubes = clubes; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}