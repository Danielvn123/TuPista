package com.daniel.tupista.model;

// Clase que representa una reseña realizada por un usuario sobre un club
public class Resena {

    // Identificadores de la reseña, usuario y club
    private int id, usuarioId, clubId, estrellas;

    // Datos principales de la reseña
    private String nombreUsuario, comentario;

    // Relaciones obtenidas desde Supabase
    private Usuario usuarios;
    private Club clubes;

    // Constructor vacío necesario para Retrofit
    public Resena() {}

    // Constructor con los datos principales de una reseña
    public Resena(int id, int usuarioId, int clubId, String nombreUsuario, int estrellas, String comentario) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.clubId = clubId;
        this.nombreUsuario = nombreUsuario;
        this.estrellas = estrellas;
        this.comentario = comentario;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public int getClubId() { return clubId; }
    public void setClubId(int clubId) { this.clubId = clubId; }

    // Devuelve el nombre del usuario que escribió la reseña
    // Si no existe en el atributo local, lo obtiene de la relación con Usuario
    public String getNombreUsuario() {
        if (nombreUsuario != null) return nombreUsuario;
        if (usuarios != null) return usuarios.getNombre();
        return "";
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    // Devuelve el nombre del club asociado a la reseña
    public String getClub() {
        return clubes == null ? "" : clubes.getNombre();
    }

    // Devuelve el objeto Club completo relacionado con la reseña
    public Club getClubes() { return clubes; }

    public void setClubes(Club clubes) {
        this.clubes = clubes;
    }

    public int getEstrellas() { return estrellas; }

    public void setEstrellas(int estrellas) {
        this.estrellas = estrellas;
    }

    // Devuelve el comentario evitando valores null
    public String getComentario() {
        return comentario == null ? "" : comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    // Devuelve el usuario asociado a la reseña
    public Usuario getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Usuario usuarios) {
        this.usuarios = usuarios;
    }
}