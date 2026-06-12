package com.daniel.tupista.retrofit.auth;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

// Interfaz que define los endpoints utilizados para interactuar con el sistema de autenticación de Supabase Auth
public interface AuthService {

    // Endpoint utilizado para registrar un nuevo usuario en Supabase Auth
    @POST("signup")
    Call<AuthResponse> registrarConAuth(

            // Datos enviados en formato JSON (email y contraseña)
            @Body Map<String, Object> datos
    );

    // Endpoint utilizado para iniciar sesión en Supabase Auth
    @POST("token")
    Call<AuthResponse> iniciarSesionConAuth(

            // Tipo de autenticación utilizada (password)
            @Query("grant_type") String grantType,

            // Credenciales del usuario (email y contraseña)
            @Body Map<String, Object> datos
    );

    // Endpoint utilizado para actualizar la contraseña del usuario autenticado
    @PUT("user")
    Call<AuthResponse> actualizarContrasena(

            // Token de autorización necesario para identificar al usuario
            @Header("Authorization") String authorization,

            // Nueva contraseña enviada en formato JSON
            @Body Map<String, Object> datos
    );
}