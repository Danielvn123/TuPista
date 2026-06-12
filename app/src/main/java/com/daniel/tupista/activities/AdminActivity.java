package com.daniel.tupista.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.daniel.tupista.R;

public class AdminActivity extends AppCompatActivity {

    private Button btnVerUsuarios, btnVerReservas, btnGestionarPistas, btnVerResenas, btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Asocia esta actividad con su diseño XML
        setContentView(R.layout.activity_admin);

        // Enlaza cada botón del layout con su variable correspondiente en Java
        btnVerUsuarios = findViewById(R.id.btnUsuarios);
        btnVerReservas = findViewById(R.id.btnReservas);
        btnGestionarPistas = findViewById(R.id.btnClubes);
        btnVerResenas = findViewById(R.id.btnResenas);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesionAdmin);

        // Abre la pantalla de gestión de usuarios
        btnVerUsuarios.setOnClickListener(v ->
                startActivity(new Intent(this, AdminUsuariosActivity.class)));

        // Abre la pantalla de gestión de reservas
        btnVerReservas.setOnClickListener(v ->
                startActivity(new Intent(this, AdminReservasActivity.class)));

        // Abre la pantalla de gestión de pistas
        btnGestionarPistas.setOnClickListener(v ->
                startActivity(new Intent(this, AdminClubesActivity.class)));

        // Abre la pantalla de gestión de reseñas
        btnVerResenas.setOnClickListener(v ->
                startActivity(new Intent(this, AdminResenasActivity.class)));

        // Cierra la sesión del administrador y vuelve a la pantalla de login
        btnCerrarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);

            // Limpia la pila de actividades para que no se pueda volver atrás al panel de administración
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        });
    }
}