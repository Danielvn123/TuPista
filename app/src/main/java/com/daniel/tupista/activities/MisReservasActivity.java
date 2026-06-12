package com.daniel.tupista.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.tupista.R;
import com.daniel.tupista.adapters.ReservaUsuarioAdapter;
import com.daniel.tupista.model.Reserva;
import com.daniel.tupista.retrofit.ApiService;
import com.daniel.tupista.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MisReservasActivity extends AppCompatActivity {
    private RecyclerView recyclerMisReservas;
    private int idUsuario;

    // Servicio Retrofit para comunicarse con Supabase
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_reservas);

        // Inicializa el servicio API
        api = RetrofitClient.getApiService();

        // Recupera el id del usuario recibido desde la pantalla anterior
        idUsuario = getIntent().getIntExtra("idUsuario", -1);

        // Configuración inicial de la pantalla
        inicializarVistas();
        configurarRecyclerView();
        configurarNavegacion();

        // Carga las reservas del usuario
        cargarReservas();
    }

    // Relaciona la variable Java con el RecyclerView del layout XML
    private void inicializarVistas() {
        recyclerMisReservas = findViewById(R.id.recyclerMisReservas);
    }

    // Configura el RecyclerView para mostrar las reservas en lista vertical
    private void configurarRecyclerView() {
        recyclerMisReservas.setLayoutManager(new LinearLayoutManager(this));
    }

    // Configura la navegación inferior de la pantalla
    private void configurarNavegacion() {
        LinearLayout navInicio = findViewById(R.id.navInicio);
        LinearLayout navMisReservas = findViewById(R.id.navMisReservas);
        LinearLayout navPerfil = findViewById(R.id.navPerfil);

        navInicio.setOnClickListener(v -> abrirMain());

        navMisReservas.setOnClickListener(v -> cargarReservas());

        navPerfil.setOnClickListener(v ->
                abrirConUsuario(MiPerfilActivity.class)
        );
    }

    // Obtiene de Supabase todas las reservas del usuario junto con la pista y el club
    private void cargarReservas() {
        api.obtenerReservasUsuario("eq." + idUsuario, "*,pistas(nombre,clubes(nombre))", "fecha.asc,horaInicio.asc").enqueue(new Callback<List<Reserva>>() {

            @Override
            public void onResponse(Call<List<Reserva>> call, Response<List<Reserva>> response) {

                if (!response.isSuccessful() || response.body() == null) {

                    Toast.makeText(MisReservasActivity.this, "Error al cargar reservas", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Reserva> reservas = response.body();

                // Adaptador que muestra las reservas y permite cancelarlas
                ReservaUsuarioAdapter adapter = new ReservaUsuarioAdapter(reservas, reserva -> confirmarCancelacion(reserva.getId()));

                recyclerMisReservas.setAdapter(adapter);

                // Mensaje informativo si el usuario no tiene reservas
                if (reservas.isEmpty()) {
                    Toast.makeText(MisReservasActivity.this, "No tienes reservas todavía", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Reserva>> call, Throwable t) {
                Toast.makeText(MisReservasActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Muestra un diálogo de confirmación antes de cancelar una reserva
    private void confirmarCancelacion(int idReserva) {

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Cancelar reserva")
                .setMessage("¿Seguro que quieres cancelar esta reserva?")
                .setPositiveButton("Sí",
                        (dialogInterface, which) ->
                                cancelarReserva(idReserva))
                .setNegativeButton("No", null)
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(getColor(R.color.azul_principal));

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(getColor(R.color.rojo));
    }

    // Cancela la reserva seleccionada eliminándola de Supabase
    private void cancelarReserva(int idReserva) {
        api.cancelarReserva("eq." + idReserva).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(MisReservasActivity.this, "Reserva cancelada", Toast.LENGTH_SHORT).show();

                    cargarReservas();
                } else {
                    Toast.makeText(MisReservasActivity.this, "Error al cancelar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MisReservasActivity.this, "Error al cancelar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Abre la pantalla principal manteniendo el id del usuario
    private void abrirMain() {
        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra("idUsuario", idUsuario);

        // Evita crear varias pantallas principales encima de la pila
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
        finish();
    }

    // Abre otra pantalla enviando siempre el id del usuario
    private void abrirConUsuario(Class<?> activity) {
        Intent intent = new Intent(this, activity);

        intent.putExtra("idUsuario", idUsuario);

        startActivity(intent);
    }
}