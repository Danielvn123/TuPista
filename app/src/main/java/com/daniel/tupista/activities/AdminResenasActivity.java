package com.daniel.tupista.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.tupista.R;
import com.daniel.tupista.adapters.ResenaAdminAdapter;
import com.daniel.tupista.model.Resena;
import com.daniel.tupista.retrofit.ApiService;
import com.daniel.tupista.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminResenasActivity extends AppCompatActivity {

    // RecyclerView donde se mostrarán todas las reseñas publicadas por los usuarios
    private RecyclerView recyclerResenasAdmin;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_resenas);

        // Inicializa el servicio API
        api = RetrofitClient.getApiService();

        // Inicializa la interfaz y configura la lista
        inicializarVistas();
        configurarRecyclerView();

        // Carga las reseñas al abrir la pantalla
        cargarResenas();
    }

    // Relaciona la variable Java con el RecyclerView del layout XML
    private void inicializarVistas() {
        recyclerResenasAdmin = findViewById(R.id.recyclerResenasAdmin);
    }

    // Configura el RecyclerView para mostrar las reseñas en forma de lista vertical
    private void configurarRecyclerView() {
        recyclerResenasAdmin.setLayoutManager(new LinearLayoutManager(this));
    }

    // Obtiene todas las reseñas de Supabase junto con el nombre del usuario y del club
    private void cargarResenas() {
        api.obtenerTodasResenas("*,usuarios(nombre),clubes(nombre)", "id.desc").enqueue(new Callback<List<Resena>>() {

            @Override
            public void onResponse(Call<List<Resena>> call, Response<List<Resena>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(AdminResenasActivity.this, "Error al cargar reseñas", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Resena> resenas = response.body();

                // Adaptador que muestra las reseñas y permite eliminarlas desde el panel de administración
                ResenaAdminAdapter adapter = new ResenaAdminAdapter(resenas, resena -> mostrarDialogoEliminar(resena));

                recyclerResenasAdmin.setAdapter(adapter);

                // Mensaje informativo si no existen reseñas registradas
                if (resenas.isEmpty()) {
                    Toast.makeText(AdminResenasActivity.this, "No hay reseñas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Resena>> call, Throwable t) {
                Toast.makeText(AdminResenasActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Muestra un diálogo de confirmación antes de eliminar una reseña
    private void mostrarDialogoEliminar(Resena resena) {

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Eliminar reseña")
                .setMessage("¿Seguro que quieres eliminar la reseña de " + resena.getNombreUsuario() + "?")
                .setPositiveButton("Sí, eliminar",
                        (dialogInterface, which) -> eliminarResena(resena.getId()))
                .setNegativeButton("No", null)
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(getColor(R.color.azul_principal));

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(getColor(R.color.rojo));
    }

    // Elimina de Supabase la reseña seleccionada por el administrador
    private void eliminarResena(int idResena) {
        api.eliminarResena("eq." + idResena).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(AdminResenasActivity.this, "Reseña eliminada", Toast.LENGTH_SHORT).show();

                    cargarResenas();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminResenasActivity.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}