package com.daniel.tupista.model;

// Clase que representa una reserva realizada por un usuario
public class Reserva {

    // Identificadores de la reserva, del usuario y de la pista reservada
    private int id, usuarioId, pistaId;

    // Datos de fecha, hora de inicio y hora de fin de la reserva
    private String fecha, horaInicio, horaFin, nombreUsuario, nombrePista;

    // Relaciones obtenidas desde Supabase
    private Usuario usuarios;
    private Pista pistas;

    // Constructor vacío necesario para Retrofit
    public Reserva() {}

    // Constructor con los datos principales de una reserva
    public Reserva(int id, int usuarioId, int pistaId, String fecha, String horaInicio, String horaFin, String nombreUsuario, String nombrePista) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.pistaId = pistaId;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.nombreUsuario = nombreUsuario;
        this.nombrePista = nombrePista;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public int getPistaId() { return pistaId; }
    public void setPistaId(int pistaId) { this.pistaId = pistaId; }

    // Devuelve la fecha evitando valores null
    public String getFecha() { return fecha == null ? "" : fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    // Devuelve la hora de inicio evitando valores null
    public String getHoraInicio() { return horaInicio == null ? "" : horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    // Devuelve la hora de fin evitando valores null
    public String getHoraFin() { return horaFin == null ? "" : horaFin; }
    public void setHoraFin(String horaFin) { this.horaFin = horaFin; }

    // Devuelve el nombre del club a través de la pista asociada
    public String getClub() {
        if (pistas != null) {
            return pistas.getClub();
        }
        return "";
    }

    // Devuelve el nombre del usuario asociado a la reserva
    // Si no existe en el atributo local, lo obtiene de la relación con Usuario
    public String getNombreUsuario() {
        if (nombreUsuario != null) return nombreUsuario;
        if (usuarios != null) return usuarios.getNombre();
        return "";
    }

    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    // Devuelve el nombre de la pista reservada
    // Si no existe en el atributo local, lo obtiene de la relación con Pista
    public String getNombrePista() {
        if (nombrePista != null) return nombrePista;
        if (pistas != null) return pistas.getNombre();
        return "";
    }

    public void setNombrePista(String nombrePista) { this.nombrePista = nombrePista; }

    // Devuelve el objeto Usuario relacionado con la reserva
    public Usuario getUsuarios() { return usuarios; }
    public void setUsuarios(Usuario usuarios) { this.usuarios = usuarios; }

    // Devuelve el objeto Pista relacionado con la reserva
    public Pista getPistas() { return pistas; }
    public void setPistas(Pista pistas) { this.pistas = pistas; }
}