package com.daniel.tupista.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class MiPerfilActivity extends AppCompatActivity {
    private EditText etNombrePerfil, etEmailPerfil, etTelefonoPerfil, etContrasenaPerfil;
    private int idUsuario;

    // Servicio Retrofit para comunicarse con las tablas de Supabase
    private ApiService api;

    // Servicio Retrofit para operaciones de autenticación, como cambiar la contraseña
    private AuthService authApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_perfil);

        // Inicializa los servicios de comunicación con Supabase
        api = RetrofitClient.getApiService();
        authApi = AuthRetrofitClient.getAuthService();

        // Recupera el id del usuario recibido desde la pantalla anterior
        idUsuario = getIntent().getIntExtra("idUsuario", -1);

        // Inicializa la interfaz, configura los botones y carga los datos del usuario
        inicializarVistas();
        configurarBotones();
        cargarDatosUsuario();
    }

    // Relaciona las variables Java con los elementos del layout XML
    private void inicializarVistas() {
        etNombrePerfil = findViewById(R.id.etNombrePerfil);
        etEmailPerfil = findViewById(R.id.etEmailPerfil);
        etTelefonoPerfil = findViewById(R.id.etTelefonoPerfil);
        etContrasenaPerfil = findViewById(R.id.etContrasenaPerfil);

        // El correo se muestra solo como información y no se permite modificar
        etEmailPerfil.setEnabled(false);
        etEmailPerfil.setFocusable(false);
    }

    // Configura los botones y la navegación inferior de la pantalla de perfil
    private void configurarBotones() {
        Button btnGuardarPerfil = findViewById(R.id.btnGuardarPerfil);
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        LinearLayout navInicio = findViewById(R.id.navInicio);
        LinearLayout navMisReservas = findViewById(R.id.navMisReservas);

        // Guarda los cambios realizados en el perfil
        btnGuardarPerfil.setOnClickListener(v -> guardarCambios());

        // Cierra la sesión actual del usuario
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());

        // Vuelve a la pantalla principal
        navInicio.setOnClickListener(v -> abrirMain());

        // Abre la pantalla de reservas del usuario
        navMisReservas.setOnClickListener(v -> abrirConUsuario(MisReservasActivity.class));
    }

    // Carga desde Supabase los datos actuales del usuario
    private void cargarDatosUsuario() {
        api.obtenerUsuarioPorId("eq." + idUsuario).enqueue(new Callback<List<Usuario>>() {

            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {

                if (!response.isSuccessful() || response.body() == null || response.body().isEmpty()) {
                    Toast.makeText(MiPerfilActivity.this, "No se pudo cargar el perfil", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                Usuario usuario = response.body().get(0);

                etNombrePerfil.setText(usuario.getNombre());
                etEmailPerfil.setText(usuario.getEmail());
                etTelefonoPerfil.setText(usuario.getTelefono());

                etContrasenaPerfil.setText("");
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Toast.makeText(MiPerfilActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Recoge los datos del formulario y actualiza el perfil del usuario
    private void guardarCambios() {
        String nombre = etNombrePerfil.getText().toString().trim();
        String telefono = etTelefonoPerfil.getText().toString().trim();
        String contrasena = etContrasenaPerfil.getText().toString().trim();

        if (nombre.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Rellena nombre y teléfono", Toast.LENGTH_SHORT).show();
            return;
        }

        // Datos que se actualizarán en la tabla usuarios
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nombre", nombre);
        usuario.put("telefono", telefono);

        if (!contrasena.isEmpty()) {
            actualizarContrasenaEnAuth(contrasena, usuario);
            return;
        }

        actualizarDatosPerfil(usuario);
    }

    // Actualiza la contraseña del usuario en Supabase Auth
    private void actualizarContrasenaEnAuth(String contrasena, Map<String, Object> usuario) {
        SharedPreferences preferences = getSharedPreferences("sesion", MODE_PRIVATE);

        String accessToken = preferences.getString("accessToken", null);

        if (accessToken == null || accessToken.isEmpty()) {
            Toast.makeText(this, "Vuelve a iniciar sesión para cambiar la contraseña", Toast.LENGTH_LONG).show();
            return;
        }

        // Datos que se envían a Supabase Auth para cambiar la contraseña
        Map<String, Object> datosAuth = new HashMap<>();
        datosAuth.put("password", contrasena);

        // Petición autenticada para actualizar la contraseña
        authApi.actualizarContrasena("Bearer " + accessToken, datosAuth).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(MiPerfilActivity.this, "No se pudo actualizar la contraseña", Toast.LENGTH_LONG).show();
                    return;
                }

                actualizarDatosPerfil(usuario);
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(MiPerfilActivity.this, "Error al actualizar contraseña: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Actualiza en Supabase los datos personales del usuario
    private void actualizarDatosPerfil(Map<String, Object> usuario) {
        api.actualizarUsuario("eq." + idUsuario, usuario).enqueue(new Callback<List<Usuario>>() {

            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(MiPerfilActivity.this, "No se pudieron actualizar los datos", Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(MiPerfilActivity.this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();

                abrirMain();
                finish();
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Toast.makeText(MiPerfilActivity.this, "Error al actualizar: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Cierra la sesión eliminando los datos guardados localmente
    private void cerrarSesion() {
        getSharedPreferences("sesion", MODE_PRIVATE).edit().clear().apply();

        Intent intent = new Intent(this, LoginActivity.class);

        // Limpia la pila de actividades para impedir volver atrás después de cerrar sesión
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }

    // Abre la pantalla principal manteniendo el id del usuario
    private void abrirMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("idUsuario", idUsuario);

        // Evita crear varias instancias de MainActivity si ya está abierta
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }

    // Abre otra pantalla enviando siempre el id del usuario
    private void abrirConUsuario(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra("idUsuario", idUsuario);
        startActivity(intent);
    }
}