package com.daniel.tupista.retrofit;

import com.daniel.tupista.BuildConfig;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Clase encargada de crear y configurar la conexión principal
// entre la aplicación y la API REST de Supabase
public class RetrofitClient {

    // Instancia única del servicio API utilizada en toda la aplicación
    private static ApiService apiService;

    // Devuelve una instancia reutilizable de ApiService
    public static ApiService getApiService() {

        // Si todavía no existe, se crea
        if (apiService == null) {
            apiService = crearRetrofit().create(ApiService.class);
        }

        return apiService;
    }

    // Configura Retrofit con la URL base de Supabase y los componentes necesarios
    private static Retrofit crearRetrofit() {

        return new Retrofit.Builder()

                // URL base de la API REST de Supabase
                .baseUrl(BuildConfig.SUPABASE_URL + "/rest/v1/")

                // Cliente HTTP personalizado
                .client(crearClienteHttp())

                // Conversor JSON ↔ Objetos Java
                .addConverterFactory(GsonConverterFactory.create())

                .build();
    }

    // Configura el cliente HTTP utilizado por Retrofit
    private static OkHttpClient crearClienteHttp() {

        return new OkHttpClient.Builder()

                // Interceptor que añade automáticamente las cabeceras necesarias
                // a todas las peticiones enviadas a Supabase
                .addInterceptor(chain ->
                        chain.proceed(
                                chain.request()
                                        .newBuilder()

                                        // Clave pública de acceso al proyecto Supabase
                                        .addHeader("apikey", BuildConfig.SUPABASE_ANON_KEY)

                                        // Token de autorización para acceder a la API REST
                                        .addHeader("Authorization", "Bearer " + BuildConfig.SUPABASE_ANON_KEY)

                                        // Indica que los datos enviados estarán en formato JSON
                                        .addHeader("Content-Type", "application/json")

                                        // Hace que Supabase devuelva el registro creado o actualizado
                                        // tras operaciones POST, PATCH o similares
                                        .addHeader("Prefer", "return=representation")

                                        .build()
                        )
                )

                .build();
    }
}