package com.daniel.tupista.retrofit.auth;

// Clase que representa la respuesta devuelta por Supabase Auth tras iniciar sesión o actualizar credenciales
public class AuthResponse {

    // Token de acceso utilizado para autenticar peticiones protegidas
    private String access_token;

    // Token utilizado para renovar el access token cuando expire
    private String refresh_token;

    // Devuelve el token de acceso de la sesión actual
    public String getAccess_token() {
        return access_token;
    }

    // Devuelve el token de refresco asociado a la sesión
    public String getRefresh_token() {
        return refresh_token;
    }
}