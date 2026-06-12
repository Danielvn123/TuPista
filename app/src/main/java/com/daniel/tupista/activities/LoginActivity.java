package com.daniel.tupista.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.daniel.tupista.R;
import com.daniel.tupista.model.Usuario;
import com.daniel.tupista.retrofit.ApiService;
import com.daniel.tupista.retrofit.RetrofitClient;
import com.daniel.tupista.retrofit.auth.AuthResponse;
import com.daniel.tupista.retrofit.auth.AuthRetrofitClient;
import com.daniel.tupista.retrofit.auth.AuthService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etContrasena;
    private Button btnLogin;
    private TextView btnIrRegistro;
    private ApiService api;

    // Servicio Retrofit específico para la autenticación con Supabase Auth
    private AuthService authApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializa los servicios necesarios para acceder a Supabase
        api = RetrofitClient.getApiService();
        authApi = AuthRetrofitClient.getAuthService();

        // Relaciona las variables Java con los elementos del layout XML
        etEmail = findViewById(R.id.etEmail);
        etContrasena = findViewById(R.id.etContrasena);
        btnLogin = findViewById(R.id.btnLogin);
        btnIrRegistro = findViewById(R.id.btnIrRegistro);

        // Ejecuta el proceso de login al pulsar el botón
        btnLogin.setOnClickListener(v -> iniciarSesion());

        // Abre la pantalla de registro si el usuario todavía no tiene cuenta
        btnIrRegistro.setOnClickListener(v -> startActivity(new Intent(this, RegistroActivity.class)));
    }

    // Valida los campos e inicia sesión mediante Supabase Auth
    private void iniciarSesion() {
        String email = etEmail.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();

        if (email.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Datos necesarios para enviar la petición de login a Supabase Auth
        Map<String, Object> datosLogin = new HashMap<>();
        datosLogin.put("email", email);
        datosLogin.put("password", contrasena);

        // Petición a Supabase Auth para comprobar las credenciales
        authApi.iniciarSesionConAuth("password", datosLogin).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(LoginActivity.this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    return;
                }

                AuthResponse authResponse = response.body();

                buscarUsuarioEnBaseDatos(email, authResponse);
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Busca en la tabla usuarios los datos asociados al correo autenticado
    private void buscarUsuarioEnBaseDatos(String email, AuthResponse authResponse) {
        api.obtenerUsuarioPorEmail("eq." + email).enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(LoginActivity.this, "Error al cargar datos del usuario", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Usuario> usuarios = response.body();

                if (usuarios.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "El usuario existe en Auth, pero no en la tabla usuarios", Toast.LENGTH_LONG).show();
                    return;
                }

                Usuario usuario = usuarios.get(0);

                guardarSesion(usuario, authResponse);
                abrirPantallaSegunRol(usuario);
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Guarda los datos principales de la sesión en SharedPreferences
    private void guardarSesion(Usuario usuario, AuthResponse authResponse) {

        SharedPreferences preferences = getSharedPreferences("sesion", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Guarda datos básicos del usuario para usarlos en otras pantallas
        editor.putInt("idUsuario", usuario.getId());
        editor.putString("email", usuario.getEmail());
        editor.putString("rol", usuario.getRol());

        // Guarda el token de acceso si Supabase lo devuelve correctamente
        if (authResponse.getAccess_token() != null) {
            editor.putString("accessToken", authResponse.getAccess_token());
        }

        // Guarda el refresh token para poder mantener o renovar la sesión
        if (authResponse.getRefresh_token() != null) {
            editor.putString("refreshToken", authResponse.getRefresh_token());
        }

        editor.apply();
    }

    // Redirige al usuario a una pantalla distinta según su rol
    private void abrirPantallaSegunRol(Usuario usuario) {
        Intent intent;

        if ("admin".equalsIgnoreCase(usuario.getRol())) {
            Toast.makeText(LoginActivity.this, "Inicio de sesión como administrador", Toast.LENGTH_SHORT).show();
            intent = new Intent(LoginActivity.this, AdminActivity.class);
        } else {

            Toast.makeText(LoginActivity.this, "Inicio de sesión correcto", Toast.LENGTH_SHORT).show();
            intent = new Intent(LoginActivity.this, MainActivity.class);
        }

        intent.putExtra("idUsuario", usuario.getId());

        startActivity(intent);

        finish();
    }
}