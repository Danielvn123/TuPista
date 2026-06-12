package com.daniel.tupista.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.daniel.tupista.R;
import com.daniel.tupista.model.Pista;
import com.daniel.tupista.model.Reserva;
import com.daniel.tupista.retrofit.ApiService;
import com.daniel.tupista.retrofit.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReservarActivity extends AppCompatActivity {
    private TextView tvClubSeleccionado, tvClubResumen, tvPistaResumen, tvFechaResumen, tvHoraResumen;
    private LinearLayout layoutPistas, layoutDias, layoutHoras;
    private Button btnConfirmarReserva, botonPistaSeleccionada = null, botonDiaSeleccionado = null, botonHoraSeleccionada = null;
    private int idUsuario, idPista = -1, clubId = -1;
    private String nombrePista = "", clubSeleccionado = "", fechaSeleccionada = "", fechaResumen = "", horaSeleccionada = "";

    // Servicio Retrofit para comunicarse con Supabase
    private ApiService api;

    // Horarios disponibles para realizar reservas
    private final String[] horas = {
            "10:00", "11:00", "12:00", "16:00", "17:00",
            "18:00", "19:00", "20:00", "21:00", "22:00"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservar);

        // Inicializa el servicio API
        api = RetrofitClient.getApiService();

        // Relaciona las variables Java con los elementos del layout XML
        tvClubSeleccionado = findViewById(R.id.tvClubSeleccionado);
        tvClubResumen = findViewById(R.id.tvClubResumen);
        tvPistaResumen = findViewById(R.id.tvPistaResumen);
        tvFechaResumen = findViewById(R.id.tvFechaResumen);
        tvHoraResumen = findViewById(R.id.tvHoraResumen);

        layoutPistas = findViewById(R.id.layoutPistas);
        layoutDias = findViewById(R.id.layoutDias);
        layoutHoras = findViewById(R.id.layoutHoras);

        btnConfirmarReserva = findViewById(R.id.btnConfirmarReserva);

        // Recupera los datos enviados desde la pantalla principal
        idUsuario = getIntent().getIntExtra("idUsuario", -1);
        clubId = getIntent().getIntExtra("clubId", -1);
        clubSeleccionado = getIntent().getStringExtra("club");

        // Validación de seguridad por si no se recibe un club válido
        if (clubSeleccionado == null || clubSeleccionado.trim().isEmpty()) {
            clubSeleccionado = "Sin club";
        }

        tvClubSeleccionado.setText("Club: " + clubSeleccionado);

        // Carga los datos iniciales de la pantalla
        cargarPistas();
        cargarDias();
        cargarHoras();
        actualizarResumen();

        // Configura el botón para confirmar la reserva
        btnConfirmarReserva.setOnClickListener(v -> confirmarReserva());
    }

    // Carga las pistas pertenecientes al club seleccionado
    private void cargarPistas() {
        layoutPistas.removeAllViews();

        api.obtenerPistasPorClub("eq." + clubId, "id.asc").enqueue(new Callback<List<Pista>>() {
            @Override
            public void onResponse(Call<List<Pista>> call, Response<List<Pista>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(ReservarActivity.this, "Error al cargar pistas", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Pista> pistas = response.body();

                if (pistas.isEmpty()) {
                    TextView tvVacio = new TextView(ReservarActivity.this);
                    tvVacio.setText("No hay pistas creadas para este club");
                    tvVacio.setTextColor(getColor(R.color.gris));
                    tvVacio.setTextSize(16);
                    tvVacio.setPadding(8, 8, 8, 8);
                    layoutPistas.addView(tvVacio);
                    return;
                }

                // Crea dinámicamente un botón para cada pista
                for (Pista pista : pistas) {
                    int idPistaBD = pista.getId();
                    String nombrePistaBD = pista.getNombre().isEmpty() ? "Pista" : pista.getNombre();

                    Button btnPista = crearBoton(nombrePistaBD);

                    btnPista.setOnClickListener(v -> {
                        idPista = idPistaBD;
                        nombrePista = nombrePistaBD;

                        seleccionarBoton(btnPista, botonPistaSeleccionada);
                        botonPistaSeleccionada = btnPista;

                        // Al cambiar de pista se reinicia la selección de hora
                        horaSeleccionada = "";
                        botonHoraSeleccionada = null;

                        cargarHoras();
                        actualizarResumen();
                    });

                    layoutPistas.addView(btnPista);
                }
            }

            @Override
            public void onFailure(Call<List<Pista>> call, Throwable t) {
                Toast.makeText(ReservarActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Genera los próximos 7 días disponibles para reservar (excepto domingos)
    private void cargarDias() {
        layoutDias.removeAllViews();

        Calendar calendar = Calendar.getInstance();
        int diasCreados = 0;

        while (diasCreados < 7) {
            int diaSemana = calendar.get(Calendar.DAY_OF_WEEK);

            if (diaSemana != Calendar.SUNDAY) {

                String fechaBD = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

                String textoBoton = new SimpleDateFormat("EEE dd/MM", new Locale("es", "ES")).format(calendar.getTime());

                String textoResumen = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime());

                Button btnDia = crearBoton(textoBoton);

                btnDia.setOnClickListener(v -> {
                    fechaSeleccionada = fechaBD;
                    fechaResumen = textoResumen;

                    seleccionarBoton(btnDia, botonDiaSeleccionado);
                    botonDiaSeleccionado = btnDia;

                    // Al cambiar de día se reinicia la selección de hora
                    horaSeleccionada = "";
                    botonHoraSeleccionada = null;

                    cargarHoras();
                    actualizarResumen();
                });

                layoutDias.addView(btnDia);
                diasCreados++;
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    // Carga las horas disponibles según la pista y fecha seleccionadas
    private void cargarHoras() {
        layoutHoras.removeAllViews();

        // Mientras no se seleccione pista y fecha, las horas permanecen desactivadas
        if (idPista == -1 || fechaSeleccionada.isEmpty()) {
            for (String hora : horas) {
                Button btnHora = crearBoton(hora);
                btnHora.setEnabled(false);
                btnHora.setBackgroundColor(getColor(R.color.gris_borde));
                btnHora.setTextColor(getColor(R.color.negro));
                layoutHoras.addView(btnHora);
            }
            return;
        }

        api.obtenerReservasPistaFecha("eq." + idPista, "eq." + fechaSeleccionada, "horaInicio")
                .enqueue(new Callback<List<Reserva>>() {
                    @Override
                    public void onResponse(Call<List<Reserva>> call, Response<List<Reserva>> response) {

                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(ReservarActivity.this, "Error al cargar horas", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<Reserva> reservas = response.body();

                        for (String hora : horas) {

                            Button btnHora = crearBoton(hora);

                            // Comprueba si la hora ya pasó o está ocupada
                            boolean pasada = esHoy(fechaSeleccionada) && horaYaPaso(hora);
                            boolean ocupada = false;

                            for (Reserva reserva : reservas) {
                                if (reserva.getHoraInicio().equals(hora)) {
                                    ocupada = true;
                                    break;
                                }
                            }

                            if (pasada) {

                                // Hora pasada
                                btnHora.setEnabled(false);
                                btnHora.setBackgroundColor(getColor(R.color.gris_borde));
                                btnHora.setTextColor(getColor(R.color.gris));

                            } else if (ocupada) {

                                // Hora ocupada
                                btnHora.setEnabled(false);
                                btnHora.setBackgroundColor(getColor(R.color.rojo_claro));
                                btnHora.setTextColor(getColor(R.color.rojo));
                                btnHora.setText(hora + "\nOcupada");

                            } else {

                                // Hora disponible
                                btnHora.setOnClickListener(v -> {
                                    horaSeleccionada = hora;

                                    seleccionarBoton(btnHora, botonHoraSeleccionada);
                                    botonHoraSeleccionada = btnHora;

                                    actualizarResumen();
                                });
                            }

                            layoutHoras.addView(btnHora);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Reserva>> call, Throwable t) {
                        Toast.makeText(ReservarActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Crea un botón con el estilo común utilizado en la pantalla
    private Button crearBoton(String texto) {
        Button btn = new Button(this);

        btn.setText(texto);
        btn.setAllCaps(false);
        btn.setBackgroundColor(getColor(R.color.blanco));
        btn.setTextColor(getColor(R.color.negro));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(8, 0, 8, 0);
        btn.setLayoutParams(params);

        return btn;
    }

    // Cambia visualmente el botón seleccionado
    private void seleccionarBoton(Button nuevo, Button anterior) {
        if (anterior != null) {
            anterior.setBackgroundColor(getColor(R.color.blanco));
            anterior.setTextColor(getColor(R.color.negro));
        }

        nuevo.setBackgroundColor(getColor(R.color.azul_principal));
        nuevo.setTextColor(getColor(R.color.blanco));
    }

    // Comprueba si la fecha seleccionada corresponde al día actual
    private boolean esHoy(String fechaBD) {
        String hoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());
        return fechaBD.equals(hoy);
    }

    // Comprueba si una hora ya ha pasado respecto a la hora actual
    private boolean horaYaPaso(String horaInicio) {
        Calendar ahora = Calendar.getInstance();

        int horaActual = ahora.get(Calendar.HOUR_OF_DAY);
        int minutoActual = ahora.get(Calendar.MINUTE);

        int horaReserva = Integer.parseInt(horaInicio.split(":")[0]);

        return horaReserva < horaActual || (horaReserva == horaActual && minutoActual > 0);
    }

    // Actualiza el resumen mostrado al usuario
    private void actualizarResumen() {
        String pistaTexto = idPista == -1 ? "Sin seleccionar" : nombrePista;
        String fechaTexto = fechaResumen.isEmpty() ? "Sin seleccionar" : fechaResumen;
        String horaTexto = horaSeleccionada.isEmpty()
                ? "Sin seleccionar"
                : horaSeleccionada + " - " + calcularHoraFin(horaSeleccionada);

        tvClubResumen.setText("Club: " + clubSeleccionado);
        tvPistaResumen.setText("Pista: " + pistaTexto);
        tvFechaResumen.setText("Fecha: " + fechaTexto);
        tvHoraResumen.setText("Hora: " + horaTexto);
    }

    // Comprueba los datos antes de crear la reserva
    private void confirmarReserva() {

        if (idUsuario == -1) {
            Toast.makeText(this, "Error: usuario no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idPista == -1) {
            Toast.makeText(this, "Selecciona una pista", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fechaSeleccionada.isEmpty()) {
            Toast.makeText(this, "Selecciona una fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        if (horaSeleccionada.isEmpty()) {
            Toast.makeText(this, "Selecciona una hora", Toast.LENGTH_SHORT).show();
            return;
        }

        // Comprueba que nadie haya reservado esa pista y hora
        api.comprobarReservaExistente("eq." + idPista, "eq." + fechaSeleccionada, "eq." + horaSeleccionada)
                .enqueue(new Callback<List<Reserva>>() {
                    @Override
                    public void onResponse(Call<List<Reserva>> call, Response<List<Reserva>> response) {

                        if (response.body() != null && !response.body().isEmpty()) {
                            Toast.makeText(ReservarActivity.this, "Esa hora ya está ocupada en este club", Toast.LENGTH_LONG).show();
                            return;
                        }

                        comprobarReservaUsuario();
                    }

                    @Override
                    public void onFailure(Call<List<Reserva>> call, Throwable t) {
                        Toast.makeText(ReservarActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Comprueba que el usuario no tenga otra reserva a la misma hora en ese club
    private void comprobarReservaUsuario() {
        api.comprobarReservaUsuarioHoraClub(
                        "eq." + idUsuario,
                        "eq." + fechaSeleccionada,
                        "eq." + horaSeleccionada,
                        "id,pistas!inner(clubId)",
                        "eq." + clubId
                )
                .enqueue(new Callback<List<Reserva>>() {
                    @Override
                    public void onResponse(Call<List<Reserva>> call, Response<List<Reserva>> response) {

                        if (response.body() != null && !response.body().isEmpty()) {
                            Toast.makeText(ReservarActivity.this, "Ya tienes una reserva a esa hora en este club", Toast.LENGTH_LONG).show();
                            return;
                        }

                        guardarReserva();
                    }

                    @Override
                    public void onFailure(Call<List<Reserva>> call, Throwable t) {
                        Toast.makeText(ReservarActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Guarda la reserva en Supabase
    private void guardarReserva() {
        Map<String, Object> reserva = new HashMap<>();

        reserva.put("usuarioId", idUsuario);
        reserva.put("pistaId", idPista);
        reserva.put("fecha", fechaSeleccionada);
        reserva.put("horaInicio", horaSeleccionada);
        reserva.put("horaFin", calcularHoraFin(horaSeleccionada));

        api.crearReserva(reserva).enqueue(new Callback<List<Reserva>>() {
            @Override
            public void onResponse(Call<List<Reserva>> call, Response<List<Reserva>> response) {

                Toast.makeText(ReservarActivity.this, "Reserva realizada correctamente", Toast.LENGTH_SHORT).show();

                finish();
            }

            @Override
            public void onFailure(Call<List<Reserva>> call, Throwable t) {
                Toast.makeText(ReservarActivity.this, "Error al reservar: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Calcula automáticamente la hora de finalización sumando una hora
    private String calcularHoraFin(String horaInicio) {
        int hora = Integer.parseInt(horaInicio.substring(0, 2));
        hora++;

        return String.format(Locale.getDefault(), "%02d:00", hora);
    }
}