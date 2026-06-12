package com.daniel.tupista.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.tupista.R;
import com.daniel.tupista.adapters.ResenaAdapter;
import com.daniel.tupista.model.Resena;
import com.daniel.tupista.model.Reserva;
import com.daniel.tupista.retrofit.ApiService;
import com.daniel.tupista.retrofit.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ResenasActivity extends AppCompatActivity {
    private TextView tvTituloResenas, tvMediaResenas, tvSinResenas, tvAvisoResenas;
    private Spinner spinnerEstrellas;
    private EditText etComentario;
    private Button btnGuardarResena, btnVolver;
    private RecyclerView recyclerResenas;
    private int idUsuario, clubId;
    private String club;

    // Variables de control para saber si el usuario puede reseñar y si ya tiene una reseña
    private boolean puedeResenar = false, yaTieneResena = false;

    // Servicio Retrofit para comunicarse con Supabase
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resenas);

        // Inicializa el servicio API
        api = RetrofitClient.getApiService();

        // Recupera los datos enviados desde la pantalla principal
        idUsuario = getIntent().getIntExtra("idUsuario", -1);
        clubId = getIntent().getIntExtra("clubId", -1);
        club = getIntent().getStringExtra("club");

        // Comprueba que el club recibido sea válido
        if (clubId == -1 || club == null || club.trim().isEmpty()) {
            Toast.makeText(this, "Error: club no especificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Relaciona las variables Java con los elementos del layout XML
        tvTituloResenas = findViewById(R.id.tvTituloResenas);
        tvMediaResenas = findViewById(R.id.tvMediaResenas);
        tvSinResenas = findViewById(R.id.tvSinResenas);
        tvAvisoResenas = findViewById(R.id.tvAvisoResenas);
        spinnerEstrellas = findViewById(R.id.spinnerEstrellas);
        etComentario = findViewById(R.id.etComentario);
        btnGuardarResena = findViewById(R.id.btnGuardarResena);
        btnVolver = findViewById(R.id.btnVolverResenas);
        recyclerResenas = findViewById(R.id.recyclerResenas);

        // Configura el RecyclerView para mostrar las reseñas en lista vertical
        recyclerResenas.setLayoutManager(new LinearLayoutManager(this));

        // Desactiva el scroll interno para integrarlo dentro de la pantalla
        recyclerResenas.setNestedScrollingEnabled(false);

        // Muestra el nombre del club en el título de la pantalla
        tvTituloResenas.setText("Reseñas de Club " + club);

        // Configura el Spinner con las posibles valoraciones de 5 a 1 estrellas
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"5", "4", "3", "2", "1"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstrellas.setAdapter(adapter);

        btnGuardarResena.setOnClickListener(v -> guardarResena());

        btnVolver.setOnClickListener(v -> finish());

        comprobarPermisoResena();
        cargarResenas();
    }

    // Comprueba si el usuario tiene alguna reserva finalizada en este club
    private void comprobarPermisoResena() {
        api.obtenerReservasUsuarioClub("eq." + idUsuario, "fecha,horaFin,pistas!inner(clubId)", "eq." + clubId).enqueue(new Callback<List<Reserva>>() {
            @Override
            public void onResponse(Call<List<Reserva>> call, Response<List<Reserva>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    bloquearFormulario("No se pudo comprobar si puedes reseñar este club");
                    return;
                }

                puedeResenar = false;

                for (Reserva reserva : response.body()) {
                    if (reservaYaFinalizada(reserva)) {
                        puedeResenar = true;
                        break;
                    }
                }

                comprobarSiYaTieneResena();
            }

            @Override
            public void onFailure(Call<List<Reserva>> call, Throwable t) {
                bloquearFormulario("Error al comprobar reservas");
            }
        });
    }


    // Comprueba si el usuario ya ha escrito una reseña para este club
    private void comprobarSiYaTieneResena() {
        api.comprobarResenaUsuarioClub("eq." + idUsuario, "eq." + clubId, "id").enqueue(new Callback<List<Resena>>() {
            @Override
            public void onResponse(Call<List<Resena>> call, Response<List<Resena>> response) {

                yaTieneResena = response.body() != null && !response.body().isEmpty();

                actualizarFormularioResena();
            }

            @Override
            public void onFailure(Call<List<Resena>> call, Throwable t) {
                bloquearFormulario("Error al comprobar reseña");
            }
        });
    }


    // Activa o bloquea el formulario dependiendo de si el usuario puede escribir reseña
    private void actualizarFormularioResena() {
        if (!puedeResenar) {
            bloquearFormulario("Solo puedes escribir una reseña si ya has reservado y utilizado una pista de este club.");
            return;
        }

        if (yaTieneResena) {
            bloquearFormulario("Ya has escrito una reseña para este club.");
            return;
        }

        tvAvisoResenas.setText("Puedes valorar este club porque ya has utilizado una pista.");
        tvAvisoResenas.setVisibility(View.VISIBLE);
        spinnerEstrellas.setEnabled(true);
        etComentario.setEnabled(true);
        btnGuardarResena.setEnabled(true);
        btnGuardarResena.setAlpha(1f);
    }

    // Bloquea el formulario y muestra el motivo al usuario
    private void bloquearFormulario(String mensaje) {
        tvAvisoResenas.setText(mensaje);
        tvAvisoResenas.setVisibility(View.VISIBLE);
        spinnerEstrellas.setEnabled(false);
        etComentario.setEnabled(false);
        btnGuardarResena.setEnabled(false);
        btnGuardarResena.setAlpha(0.6f);
    }

    // Guarda una nueva reseña en Supabase
    private void guardarResena() {
        if (!puedeResenar || yaTieneResena) {
            Toast.makeText(this, "No puedes escribir una reseña para este club", Toast.LENGTH_SHORT).show();
            return;
        }

        String comentario = etComentario.getText().toString().trim();
        int estrellas = Integer.parseInt(spinnerEstrellas.getSelectedItem().toString());

        if (comentario.isEmpty()) {
            Toast.makeText(this, "Escribe una reseña", Toast.LENGTH_SHORT).show();
            return;
        }

        // Datos de la reseña que se enviarán a Supabase
        Map<String, Object> resena = new HashMap<>();
        resena.put("usuarioId", idUsuario);
        resena.put("clubId", clubId);
        resena.put("estrellas", estrellas);
        resena.put("comentario", comentario);

        api.insertarResena(resena).enqueue(new Callback<List<Resena>>() {
            @Override
            public void onResponse(Call<List<Resena>> call, Response<List<Resena>> response) {

                Toast.makeText(ResenasActivity.this, "Reseña guardada", Toast.LENGTH_SHORT).show();
                etComentario.setText("");
                spinnerEstrellas.setSelection(0);
                yaTieneResena = true;
                actualizarFormularioResena();
                cargarResenas();
            }

            @Override
            public void onFailure(Call<List<Resena>> call, Throwable t) {
                Toast.makeText(ResenasActivity.this, "No se pudo guardar la reseña", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Carga todas las reseñas del club seleccionado
    private void cargarResenas() {
        api.obtenerResenasClub("eq." + clubId, "*,usuarios(nombre),clubes(nombre)", "id.desc").enqueue(new Callback<List<Resena>>() {
            @Override
            public void onResponse(Call<List<Resena>> call, Response<List<Resena>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(ResenasActivity.this, "Error al cargar reseñas", Toast.LENGTH_SHORT).show();
                    return;
                }

                mostrarResenas(response.body());
            }

            @Override
            public void onFailure(Call<List<Resena>> call, Throwable t) {
                Toast.makeText(ResenasActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Muestra las reseñas y calcula la media de estrellas
    private void mostrarResenas(List<Resena> resenas) {
        if (resenas.isEmpty()) {
            tvMediaResenas.setText("⭐ Sin reseñas todavía");
            tvSinResenas.setVisibility(View.VISIBLE);
            recyclerResenas.setAdapter(new ResenaAdapter(resenas));
            return;
        }

        double suma = 0;

        for (Resena resena : resenas) {
            suma += resena.getEstrellas();
        }

        double media = suma / resenas.size();

        tvMediaResenas.setText(
                String.format(Locale.getDefault(), "⭐ %.1f (%d reseñas)", media, resenas.size())
        );

        tvSinResenas.setVisibility(View.GONE);
        recyclerResenas.setAdapter(new ResenaAdapter(resenas));
    }

    // Comprueba si una reserva ya ha finalizado comparando su fecha y hora final con la fecha actual
    private boolean reservaYaFinalizada(Reserva reserva) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date fin = sdf.parse(reserva.getFecha() + " " + reserva.getHoraFin());
            return fin != null && new Date().after(fin);
        } catch (Exception e) {
            return false;
        }
    }
}