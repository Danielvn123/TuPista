package com.daniel.tupista.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.tupista.R;
import com.daniel.tupista.adapters.UsuarioAdminAdapter;
import com.daniel.tupista.model.Reserva;
import com.daniel.tupista.model.Usuario;
import com.daniel.tupista.retrofit.ApiService;
import com.daniel.tupista.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUsuariosActivity extends AppCompatActivity {

    // RecyclerView donde se mostrarán todos los usuarios registrados
    private RecyclerView recyclerUsuariosAdmin;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_usuarios);

        // Inicializa el servicio API
        api = RetrofitClient.getApiService();

        // Configuración inicial de la interfaz
        inicializarVistas();
        configurarRecyclerView();

        // Carga todos los usuarios al abrir la pantalla
        cargarUsuarios();
    }

    // Relaciona la variable Java con el RecyclerView del layout XML
    private void inicializarVistas() {
        recyclerUsuariosAdmin = findViewById(R.id.recyclerUsuariosAdmin);
    }

    // Configura el RecyclerView para mostrar los usuarios en forma de lista vertical
    private void configurarRecyclerView() {
        recyclerUsuariosAdmin.setLayoutManager(new LinearLayoutManager(this));
    }

    // Obtiene todos los usuarios almacenados en Supabase
    private void cargarUsuarios() {
        api.obtenerTodosUsuarios().enqueue(new Callback<List<Usuario>>() {

            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(AdminUsuariosActivity.this, "Error al cargar usuarios", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Usuario> usuarios = response.body();

                // Adaptador que muestra los usuarios y permite solicitar su eliminación
                UsuarioAdminAdapter adapter = new UsuarioAdminAdapter(usuarios, usuario -> comprobarYEliminar(usuario.getId()));

                recyclerUsuariosAdmin.setAdapter(adapter);

                // Mensaje informativo si no hay usuarios registrados
                if (usuarios.isEmpty()) {
                    Toast.makeText(AdminUsuariosActivity.this, "No hay usuarios registrados", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Toast.makeText(AdminUsuariosActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Comprueba si el usuario tiene reservas antes de permitir eliminarlo
    private void comprobarYEliminar(int idUsuario) {
        api.obtenerReservasUsuario("eq." + idUsuario, "id", "id.asc").enqueue(new Callback<List<Reserva>>() {

            @Override
            public void onResponse(Call<List<Reserva>> call, Response<List<Reserva>> response) {

                if (response.body() != null && !response.body().isEmpty()) {
                    Toast.makeText(AdminUsuariosActivity.this, "No se puede eliminar porque tiene reservas", Toast.LENGTH_LONG).show();
                } else {

                    eliminarUsuario(idUsuario);
                }
            }

            @Override
            public void onFailure(Call<List<Reserva>> call, Throwable t) {
                Toast.makeText(AdminUsuariosActivity.this, "Error al comprobar reservas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Elimina el usuario seleccionado de la tabla de usuarios
    private void eliminarUsuario(int idUsuario) {
        api.eliminarUsuario("eq." + idUsuario).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(AdminUsuariosActivity.this, "Usuario eliminado", Toast.LENGTH_SHORT).show();

                    cargarUsuarios();
                } else {
                    Toast.makeText(AdminUsuariosActivity.this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminUsuariosActivity.this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}