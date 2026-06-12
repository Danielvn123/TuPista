package com.daniel.tupista.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.tupista.R;
import com.daniel.tupista.adapters.ReservaAdminAdapter;
import com.daniel.tupista.model.Reserva;
import com.daniel.tupista.retrofit.ApiService;
import com.daniel.tupista.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminReservasActivity extends AppCompatActivity {

    // RecyclerView donde se mostrarán todas las reservas registradas
    private RecyclerView recyclerReservasAdmin;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reservas);

        // Inicializa el servicio API
        api = RetrofitClient.getApiService();

        // Configuración inicial de la interfaz
        inicializarVistas();
        configurarRecyclerView();

        // Carga todas las reservas existentes
        cargarReservas();
    }

    // Relaciona la variable Java con el RecyclerView definido en el XML
    private void inicializarVistas() {
        recyclerReservasAdmin = findViewById(R.id.recyclerReservasAdmin);
    }

    // Configura el RecyclerView para mostrar los elementos en formato de lista vertical
    private void configurarRecyclerView() {
        recyclerReservasAdmin.setLayoutManager(new LinearLayoutManager(this));
    }

    // Obtiene todas las reservas junto con la información del usuario, pista y club
    private void cargarReservas() {
        api.obtenerTodasReservas("*,usuarios(nombre),pistas(nombre,clubes(nombre))", "fecha.asc,horaInicio.asc").enqueue(new Callback<List<Reserva>>() {

            @Override
            public void onResponse(Call<List<Reserva>> call, Response<List<Reserva>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(AdminReservasActivity.this, "Error al cargar reservas", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Reserva> reservas = response.body();

                // Adaptador encargado de mostrar las reservas y permitir su cancelación
                ReservaAdminAdapter adapter = new ReservaAdminAdapter(
                        reservas,
                        reserva -> mostrarDialogoCancelar(reserva)
                );

                recyclerReservasAdmin.setAdapter(adapter);

                if (reservas.isEmpty()) {
                    Toast.makeText(AdminReservasActivity.this, "No hay reservas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Reserva>> call, Throwable t) {
                Toast.makeText(AdminReservasActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Muestra un diálogo de confirmación antes de cancelar una reserva
    private void mostrarDialogoCancelar(Reserva reserva) {

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Cancelar reserva")
                .setMessage("¿Seguro que quieres cancelar esta reserva?")
                .setPositiveButton("Sí, cancelar",
                        (dialogInterface, which) ->
                                cancelarReserva(reserva.getId()))
                .setNegativeButton("No", null)
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(getColor(R.color.azul_principal));

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(getColor(R.color.rojo));
    }

    // Elimina la reserva seleccionada de la base de datos
    private void cancelarReserva(int idReserva) {
        api.cancelarReserva("eq." + idReserva).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(AdminReservasActivity.this, "Reserva cancelada", Toast.LENGTH_SHORT).show();

                    cargarReservas();
                } else {
                    Toast.makeText(AdminReservasActivity.this, "Error al cancelar reserva", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminReservasActivity.this, "Error al cancelar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}