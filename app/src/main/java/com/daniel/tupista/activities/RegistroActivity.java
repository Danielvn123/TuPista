package com.daniel.tupista.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.daniel.tupista.R;
import com.daniel.tupista.model.Usuario;
import com.daniel.tupista.retrofit.ApiService;
import com.daniel.tupista.retrofit.RetrofitClient;
import com.daniel.tupista.retrofit.auth.AuthResponse;
import com.daniel.tupista.retrofit.auth.AuthRetrofitClient;
import com.daniel.tupista.retrofit.auth.AuthService;
import com.daniel.tupista.utils.Validaciones;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroActivity extends AppCompatActivity {
    private EditText etNombre, etEmailRegistro, etTelefonoRegistro, etContrasenaRegistro;
    private Button btnRegistrar;

    // Servicio Retrofit para acceder a las tablas de Supabase
    private ApiService api;

    // Servicio Retrofit para registrar usuarios en Supabase Auth
    private AuthService authApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicializa los servicios de comunicación con Supabase
        api = RetrofitClient.getApiService();
        authApi = AuthRetrofitClient.getAuthService();

        // Inicializa los elementos de la interfaz y configura el botón de registro
        inicializarVistas();
        configurarBotones();
    }

    // Relaciona las variables Java con los elementos definidos en el layout XML
    private void inicializarVistas() {
        etNombre = findViewById(R.id.etNombre);
        etEmailRegistro = findViewById(R.id.etEmailRegistro);
        etTelefonoRegistro = findViewById(R.id.etTelefonoRegistro);
        etContrasenaRegistro = findViewById(R.id.etContrasenaRegistro);
        btnRegistrar = findViewById(R.id.btnRegistrar);
    }

    // Configura el botón que inicia el proceso de registro
    private void configurarBotones() {
        btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    // Recoge los datos introducidos por el usuario y comienza el registro
    private void registrarUsuario() {
        String nombre = etNombre.getText().toString().trim();
        String email = etEmailRegistro.getText().toString().trim();
        String telefono = etTelefonoRegistro.getText().toString().trim();
        String contrasena = etContrasenaRegistro.getText().toString().trim();

        if (!validarDatos(nombre, email, telefono, contrasena)) {
            return;
        }

        comprobarEmailDisponible(nombre, email, telefono, contrasena);
    }

    // Valida los datos introducidos usando la clase auxiliar Validaciones
    private boolean validarDatos(String nombre, String email, String telefono, String contrasena) {
        if (!Validaciones.nombreValido(nombre)) {
            etNombre.setError("El nombre debe tener al menos 3 caracteres");
            etNombre.requestFocus();
            return false;
        }

        if (!Validaciones.emailValido(email)) {
            etEmailRegistro.setError("Correo electrónico no válido");
            etEmailRegistro.requestFocus();
            return false;
        }

        if (!Validaciones.telefonoValido(telefono)) {
            etTelefonoRegistro.setError("El teléfono debe tener 9 números");
            etTelefonoRegistro.requestFocus();
            return false;
        }

        if (!Validaciones.contrasenaValida(contrasena)) {
            etContrasenaRegistro.setError("La contraseña debe tener al menos 6 caracteres");
            etContrasenaRegistro.requestFocus();
            return false;
        }

        return true;
    }

    // Comprueba en la tabla usuarios si ya existe una cuenta con ese correo
    private void comprobarEmailDisponible(String nombre, String email, String telefono, String contrasena) {
        api.obtenerUsuarioPorEmail("eq." + email).enqueue(new Callback<List<Usuario>>() {

            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(RegistroActivity.this, "Error al comprobar email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!response.body().isEmpty()) {
                    etEmailRegistro.setError("Ese correo ya está registrado");
                    etEmailRegistro.requestFocus();
                    return;
                }

                registrarEnSupabaseAuth(nombre, email, telefono, contrasena);
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Toast.makeText(RegistroActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Crea la cuenta del usuario en Supabase Auth
    private void registrarEnSupabaseAuth(String nombre, String email, String telefono, String contrasena) {
        Map<String, Object> datosAuth = new HashMap<>();
        datosAuth.put("email", email);
        datosAuth.put("password", contrasena);

        authApi.registrarConAuth(datosAuth).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(RegistroActivity.this, "Error al registrar en Supabase Auth", Toast.LENGTH_LONG).show();
                    return;
                }

                insertarUsuario(nombre, email, telefono);
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(RegistroActivity.this, "Error al registrar: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Inserta los datos del usuario en la tabla usuarios de Supabase
    private void insertarUsuario(String nombre, String email, String telefono) {
        Map<String, Object> usuario = new HashMap<>();

        usuario.put("nombre", nombre);
        usuario.put("email", email);
        usuario.put("telefono", telefono);
        usuario.put("rol", "usuario");

        api.registrarUsuario(usuario).enqueue(new Callback<List<Usuario>>() {

            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(RegistroActivity.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegistroActivity.this, "Usuario creado en Auth, pero no en la tabla usuarios", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Toast.makeText(RegistroActivity.this, "Error al guardar usuario: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}