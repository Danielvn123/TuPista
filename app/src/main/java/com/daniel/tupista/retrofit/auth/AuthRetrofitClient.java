package com.daniel.tupista.retrofit.auth;

import com.daniel.tupista.BuildConfig;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Clase encargada de crear y configurar la conexión con Supabase Auth
public class AuthRetrofitClient {

    // Instancia única del servicio de autenticación
    private static AuthService authService;

    // Devuelve una instancia de AuthService reutilizable durante toda la aplicación
    public static AuthService getAuthService() {

        // Si todavía no existe, se crea
        if (authService == null) {
            authService = crearRetrofit().create(AuthService.class);
        }

        return authService;
    }

    // Configura Retrofit para conectarse al servicio de autenticación de Supabase
    private static Retrofit crearRetrofit() {

        return new Retrofit.Builder()

                // URL base del servicio Auth de Supabase
                .baseUrl(BuildConfig.SUPABASE_URL + "/auth/v1/")

                // Cliente HTTP personalizado con las cabeceras necesarias
                .client(crearClienteHttp())

                // Conversor para transformar JSON en objetos Java y viceversa
                .addConverterFactory(GsonConverterFactory.create())

                .build();
    }

    // Configura el cliente HTTP utilizado por Retrofit
    private static OkHttpClient crearClienteHttp() {

        return new OkHttpClient.Builder()

                // Interceptor que añade automáticamente las cabeceras necesarias
                // en cada petición enviada a Supabase Auth
                .addInterceptor(chain ->
                        chain.proceed(
                                chain.request()
                                        .newBuilder()

                                        // Clave pública de acceso al proyecto Supabase
                                        .addHeader("apikey", BuildConfig.SUPABASE_ANON_KEY)

                                        // Indica que los datos se enviarán en formato JSON
                                        .addHeader("Content-Type", "application/json")

                                        .build()
                        )
                )

                .build();
    }
}