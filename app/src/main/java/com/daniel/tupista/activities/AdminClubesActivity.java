package com.daniel.tupista.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.tupista.R;
import com.daniel.tupista.adapters.ClubAdminAdapter;
import com.daniel.tupista.model.Club;
import com.daniel.tupista.model.Pista;
import com.daniel.tupista.model.Reserva;
import com.daniel.tupista.retrofit.ApiService;
import com.daniel.tupista.retrofit.RetrofitClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminClubesActivity extends AppCompatActivity {

    // Campos de texto utilizados para introducir los datos del club y de la pista
    private EditText etClubPista, etDireccionClub, etTelefonoClub, etNombrePista;

    // RecyclerView donde se mostrarán todas las pistas registradas
    private RecyclerView recyclerPistasAdmin;

    // Servicio Retrofit para comunicarse con Supabase
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_clubes);

        // Inicializa el servicio API
        api = RetrofitClient.getApiService();

        // Inicializa la interfaz, configura la lista y el botón de creación
        inicializarVistas();
        configurarRecyclerView();
        configurarBotones();

        // Carga las pistas existentes al abrir la pantalla
        cargarPistas();
    }

    // Relaciona las variables Java con los elementos del layout XML
    private void inicializarVistas() {
        etClubPista = findViewById(R.id.etClubPista);
        etDireccionClub = findViewById(R.id.etDireccionClub);
        etTelefonoClub = findViewById(R.id.etTelefonoClub);
        etNombrePista = findViewById(R.id.etNombrePista);
        recyclerPistasAdmin = findViewById(R.id.recyclerClubesAdmin);
    }

    // Configura el RecyclerView para mostrar las pistas en forma de lista vertical
    private void configurarRecyclerView() {
        recyclerPistasAdmin.setLayoutManager(new LinearLayoutManager(this));
    }

    // Configura el botón encargado de crear una nueva pista
    private void configurarBotones() {
        Button btnCrearPista = findViewById(R.id.btnCrearClub);
        btnCrearPista.setOnClickListener(v -> crearPista());
    }

    // Valida los datos introducidos y crea una nueva pista.
    // Si el club no existe, se crea automáticamente antes de insertar la pista.
    private void crearPista() {
        String nombreClub = etClubPista.getText().toString().trim();
        String direccion = etDireccionClub.getText().toString().trim();
        String telefono = etTelefonoClub.getText().toString().trim();
        String nombrePista = etNombrePista.getText().toString().trim();

        // Comprueba que todos los campos obligatorios estén cubiertos
        if (nombreClub.isEmpty() || direccion.isEmpty() || telefono.isEmpty() || nombrePista.isEmpty()) {
            Toast.makeText(this, "Rellena nombre del club, dirección, teléfono y nombre de pista", Toast.LENGTH_SHORT).show();
            return;
        }

        // Valida que el teléfono tenga exactamente 9 dígitos
        if (!telefono.matches("\\d{9}")) {
            Toast.makeText(this, "El teléfono debe tener 9 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Comprueba si ya existe un club con el nombre introducido
        api.obtenerClubPorNombre("eq." + nombreClub).enqueue(new Callback<List<Club>>() {
            @Override
            public void onResponse(Call<List<Club>> call, Response<List<Club>> response) {

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {

                    Club clubExistente = response.body().get(0);

                    // Comprueba que el club encontrado coincida también en dirección y teléfono
                    boolean mismaDireccion = clubExistente.getDireccion().equalsIgnoreCase(direccion);
                    boolean mismoTelefono = clubExistente.getTelefono().equals(telefono);

                    if (mismaDireccion && mismoTelefono) {
                        // Si el club ya existe con los mismos datos, solo se inserta la nueva pista
                        insertarPista(nombrePista, clubExistente.getId());
                    } else {
                        Toast.makeText(AdminClubesActivity.this,
                                "Ya existe un club con ese nombre pero con otra dirección o teléfono",
                                Toast.LENGTH_LONG).show();
                    }

                    return;
                }

                // Si el club no existe, se crea primero el club y después la pista
                crearClubEInsertarPista(nombreClub, direccion, telefono, nombrePista);
            }

            @Override
            public void onFailure(Call<List<Club>> call, Throwable t) {
                Toast.makeText(AdminClubesActivity.this, "Error al buscar club", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Crea un nuevo club en Supabase y posteriormente registra la pista asociada
    private void crearClubEInsertarPista(String nombreClub, String direccion, String telefono, String nombrePista) {
        Map<String, Object> club = new HashMap<>();
        club.put("nombre", nombreClub);
        club.put("direccion", direccion);
        club.put("telefono", telefono);

        api.insertarClub(club).enqueue(new Callback<List<Club>>() {
            @Override
            public void onResponse(Call<List<Club>> call, Response<List<Club>> response) {

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Utiliza el id del club creado para insertar la pista correspondiente
                    insertarPista(nombrePista, response.body().get(0).getId());
                } else {
                    Toast.makeText(AdminClubesActivity.this, "Error al crear club", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Club>> call, Throwable t) {
                Toast.makeText(AdminClubesActivity.this, "Error al crear club", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Inserta una nueva pista asociada al club indicado
    private void insertarPista(String nombre, int clubId) {
        Map<String, Object> pista = new HashMap<>();
        pista.put("nombre", nombre);
        pista.put("clubId", clubId);
        pista.put("disponible", true);

        api.insertarPista(pista).enqueue(new Callback<List<Pista>>() {
            @Override
            public void onResponse(Call<List<Pista>> call, Response<List<Pista>> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(AdminClubesActivity.this, "Pista creada", Toast.LENGTH_SHORT).show();

                    // Limpia el formulario y actualiza la lista de pistas
                    limpiarCampos();
                    cargarPistas();
                } else {
                    Toast.makeText(AdminClubesActivity.this, "Error al crear pista", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Pista>> call, Throwable t) {
                Toast.makeText(AdminClubesActivity.this, "Error al crear: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Obtiene todas las pistas junto con la información del club al que pertenecen
    private void cargarPistas() {
        api.obtenerTodasPistas("*,clubes(nombre,direccion,telefono)", "clubId.asc,id.asc")
                .enqueue(new Callback<List<Pista>>() {
                    @Override
                    public void onResponse(Call<List<Pista>> call, Response<List<Pista>> response) {

                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(AdminClubesActivity.this, "Error al cargar pistas", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<Pista> pistas = response.body();

                        // Adaptador que muestra las pistas y permite eliminar pistas o clubes
                        ClubAdminAdapter adapter = new ClubAdminAdapter(
                                pistas,
                                new ClubAdminAdapter.OnPistaClickListener() {
                                    @Override
                                    public void onEliminarPista(Pista pista) {
                                        mostrarDialogoEliminarPista(pista);
                                    }

                                    @Override
                                    public void onEliminarClub(Pista pista) {
                                        mostrarDialogoEliminarClub(pista);
                                    }
                                }
                        );

                        recyclerPistasAdmin.setAdapter(adapter);

                        // Mensaje informativo si no hay pistas registradas
                        if (pistas.isEmpty()) {
                            Toast.makeText(AdminClubesActivity.this, "No hay pistas creadas", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Pista>> call, Throwable t) {
                        Toast.makeText(AdminClubesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Muestra un diálogo de confirmación antes de eliminar una pista
    private void mostrarDialogoEliminarPista(Pista pista) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Eliminar pista")
                .setMessage("¿Estás seguro de que deseas eliminar la pista \"" + pista.getNombre() + "\"?")
                .setPositiveButton("Eliminar", (d, which) -> comprobarYEliminarPista(pista.getId()))
                .setNegativeButton("Cancelar", null)
                .create();

        // Cambia el color de los botones del diálogo
        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(getResources().getColor(R.color.azul_principal));

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(getResources().getColor(R.color.rojo));
        });

        dialog.show();
    }

    // Comprueba que la pista no tenga reservas asociadas antes de eliminarla
    private void comprobarYEliminarPista(int idPista) {
        api.obtenerReservasPorPista("eq." + idPista, "id").enqueue(new Callback<List<Reserva>>() {
            @Override
            public void onResponse(Call<List<Reserva>> call, Response<List<Reserva>> response) {

                if (response.body() != null && !response.body().isEmpty()) {
                    Toast.makeText(AdminClubesActivity.this,
                            "No puedes eliminar una pista con reservas",
                            Toast.LENGTH_LONG).show();
                } else {
                    eliminarPista(idPista);
                }
            }

            @Override
            public void onFailure(Call<List<Reserva>> call, Throwable t) {
                Toast.makeText(AdminClubesActivity.this, "Error al comprobar reservas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Elimina definitivamente una pista de Supabase
    private void eliminarPista(int idPista) {
        api.eliminarPista("eq." + idPista).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(AdminClubesActivity.this, "Pista eliminada", Toast.LENGTH_SHORT).show();

                    // Actualiza la lista después de eliminar la pista
                    cargarPistas();
                } else {
                    Toast.makeText(AdminClubesActivity.this, "No se pudo eliminar la pista", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminClubesActivity.this, "No se pudo eliminar la pista", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Muestra un diálogo de confirmación antes de eliminar un club completo
    private void mostrarDialogoEliminarClub(Pista pista) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Eliminar club")
                .setMessage("¿Estás seguro de que deseas eliminar el club \"" + pista.getClub() + "\" y todas sus pistas?")
                .setPositiveButton("Eliminar", (d, which) -> comprobarYEliminarClub(pista.getClubId()))
                .setNegativeButton("Cancelar", null)
                .create();

        // Cambia el color de los botones del diálogo
        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(getResources().getColor(R.color.azul_principal));

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(getResources().getColor(R.color.rojo));
        });

        dialog.show();
    }

    // Comprueba si el club puede eliminarse verificando las pistas asociadas
    private void comprobarYEliminarClub(int idClub) {
        api.obtenerPistasPorClub("eq." + idClub, "id.asc").enqueue(new Callback<List<Pista>>() {
            @Override
            public void onResponse(Call<List<Pista>> call, Response<List<Pista>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(AdminClubesActivity.this, "Error al comprobar pistas", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Pista> pistasClub = response.body();

                // Si el club no tiene pistas, puede eliminarse directamente
                if (pistasClub.isEmpty()) {
                    eliminarClub(idClub);
                    return;
                }

                // Si tiene pistas, primero se comprueba que ninguna tenga reservas
                comprobarReservasDePistas(idClub, pistasClub, 0);
            }

            @Override
            public void onFailure(Call<List<Pista>> call, Throwable t) {
                Toast.makeText(AdminClubesActivity.this, "Error al comprobar pistas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Recorre todas las pistas del club comprobando que ninguna tenga reservas asociadas
    private void comprobarReservasDePistas(int idClub, List<Pista> pistasClub, int posicion) {
        if (posicion >= pistasClub.size()) {
            eliminarPistasDelClub(idClub, pistasClub, 0);
            return;
        }

        Pista pista = pistasClub.get(posicion);

        api.obtenerReservasPorPista("eq." + pista.getId(), "id").enqueue(new Callback<List<Reserva>>() {
            @Override
            public void onResponse(Call<List<Reserva>> call, Response<List<Reserva>> response) {

                if (response.body() != null && !response.body().isEmpty()) {
                    Toast.makeText(AdminClubesActivity.this,
                            "No puedes eliminar un club con reservas",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // Continúa comprobando la siguiente pista del club
                comprobarReservasDePistas(idClub, pistasClub, posicion + 1);
            }

            @Override
            public void onFailure(Call<List<Reserva>> call, Throwable t) {
                Toast.makeText(AdminClubesActivity.this, "Error al comprobar reservas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Elimina todas las pistas del club antes de eliminar el propio club
    private void eliminarPistasDelClub(int idClub, List<Pista> pistasClub, int posicion) {
        if (posicion >= pistasClub.size()) {
            eliminarClub(idClub);
            return;
        }

        Pista pista = pistasClub.get(posicion);

        api.eliminarPista("eq." + pista.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {
                    // Continúa eliminando la siguiente pista del club
                    eliminarPistasDelClub(idClub, pistasClub, posicion + 1);
                } else {
                    Toast.makeText(AdminClubesActivity.this,
                            "No se pudo eliminar una pista del club",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminClubesActivity.this,
                        "No se pudo eliminar una pista del club",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Elimina definitivamente el club seleccionado de Supabase
    private void eliminarClub(int idClub) {
        api.eliminarClub("eq." + idClub).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(AdminClubesActivity.this, "Club eliminado", Toast.LENGTH_SHORT).show();

                    // Actualiza la lista después de eliminar el club
                    cargarPistas();
                } else {
                    Toast.makeText(AdminClubesActivity.this, "No se pudo eliminar el club", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminClubesActivity.this, "No se pudo eliminar el club", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Limpia los campos del formulario después de crear una pista
    private void limpiarCampos() {
        etClubPista.setText("");
        etDireccionClub.setText("");
        etTelefonoClub.setText("");
        etNombrePista.setText("");
    }
}
