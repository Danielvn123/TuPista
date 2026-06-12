package com.daniel.tupista.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.tupista.R;
import com.daniel.tupista.adapters.ClubAdapter;
import com.daniel.tupista.model.Club;
import com.daniel.tupista.model.Usuario;
import com.daniel.tupista.retrofit.ApiService;
import com.daniel.tupista.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    // RecyclerView donde se mostrarán los clubes disponibles
    private RecyclerView recyclerClubes;
    private LinearLayout navInicio, navMisReservas, navPerfil;
    private TextView tvBienvenida;
    private int idUsuario;

    // Servicio Retrofit para comunicarse con Supabase
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa el servicio API
        api = RetrofitClient.getApiService();

        // Recupera el id del usuario enviado desde el login
        idUsuario = getIntent().getIntExtra("idUsuario", -1);

        // Relaciona las variables Java con los elementos del layout XML
        tvBienvenida = findViewById(R.id.tvBienvenida);
        recyclerClubes = findViewById(R.id.recyclerClubes);
        navInicio = findViewById(R.id.navInicio);
        navMisReservas = findViewById(R.id.navMisReservas);
        navPerfil = findViewById(R.id.navPerfil);

        // Configura el RecyclerView para mostrar los clubes en lista vertical
        recyclerClubes.setLayoutManager(new LinearLayoutManager(this));

        // Actualiza el nombre del usuario y carga los clubes disponibles
        actualizarNombreUsuario();
        cargarClubes();

        navMisReservas.setOnClickListener(v -> abrirConUsuario(MisReservasActivity.class));
        navPerfil.setOnClickListener(v -> abrirConUsuario(MiPerfilActivity.class));
        navInicio.setOnClickListener(v -> cargarClubes());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Al volver a esta pantalla se actualiza el nombre por si fue modificado en el perfil
        actualizarNombreUsuario();
    }

    // Obtiene el nombre del usuario desde Supabase para mostrar un saludo personalizado
    private void actualizarNombreUsuario() {
        api.obtenerUsuarioPorId("eq." + idUsuario).enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                String nombre = "";

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    nombre = response.body().get(0).getNombre();
                }

                tvBienvenida.setText("¡Hola, " + nombre + "! 👋");
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                tvBienvenida.setText("¡Hola! 👋");
            }
        });
    }

    // Carga todos los clubes disponibles junto con sus pistas asociadas
    private void cargarClubes() {
        api.obtenerTodosClubes("*,pistas(id)", "nombre.asc").enqueue(new Callback<List<Club>>() {
            @Override
            public void onResponse(Call<List<Club>> call, Response<List<Club>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(MainActivity.this, "Error al cargar clubes", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Club> clubesBD = response.body();

                if (clubesBD.isEmpty()) {
                    recyclerClubes.setAdapter(new ClubAdapter(new ArrayList<>(), club -> {}));
                    Toast.makeText(MainActivity.this, "Aún no hay clubes disponibles", Toast.LENGTH_LONG).show();
                    return;
                }

                List<Club> clubesConPistas = new ArrayList<>();

                for (Club club : clubesBD) {
                    if (club.getTotalPistas() > 0) {
                        clubesConPistas.add(club);
                    }
                }

                if (clubesConPistas.isEmpty()) {
                    recyclerClubes.setAdapter(new ClubAdapter(new ArrayList<>(), club -> {}));
                    Toast.makeText(MainActivity.this, "No hay clubes con pistas disponibles", Toast.LENGTH_LONG).show();
                    return;
                }

                recyclerClubes.setAdapter(new ClubAdapter(
                        clubesConPistas,
                        club -> abrirReservarActivity(club.getId(), club.getNombre()),
                        club -> abrirResenasActivity(club.getId(), club.getNombre())
                ));
            }

            @Override
            public void onFailure(Call<List<Club>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Abre una pantalla enviando siempre el identificador del usuario
    private void abrirConUsuario(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra("idUsuario", idUsuario);
        startActivity(intent);
    }

    // Abre la pantalla de reserva enviando el usuario y el club seleccionado
    private void abrirReservarActivity(int clubId, String club) {
        Intent intent = new Intent(this, ReservarActivity.class);
        intent.putExtra("idUsuario", idUsuario);
        intent.putExtra("clubId", clubId);
        intent.putExtra("club", club);
        startActivity(intent);
    }

    // Abre la pantalla de reseñas enviando el usuario y el club seleccionado
    private void abrirResenasActivity(int clubId, String club) {
        Intent intent = new Intent(this, ResenasActivity.class);
        intent.putExtra("idUsuario", idUsuario);
        intent.putExtra("clubId", clubId);
        intent.putExtra("club", club);
        startActivity(intent);
    }
}