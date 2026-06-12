package com.daniel.tupista.utils;

import android.text.TextUtils;
import android.util.Patterns;

// Clase de utilidad que agrupa las validaciones utilizadas durante el registro y modificación de datos de usuario
public class Validaciones {

    // Comprueba que el correo no esté vacío y tenga un formato válido
    public static boolean emailValido(String email) {
        return !TextUtils.isEmpty(email)
                && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Comprueba que la contraseña no esté vacía y tenga al menos 6 caracteres
    public static boolean contrasenaValida(String contrasena) {
        return !TextUtils.isEmpty(contrasena)
                && contrasena.length() >= 6;
    }

    // Comprueba que el nombre tenga entre 3 y 15 caracteres
    public static boolean nombreValido(String nombre) {
        return !TextUtils.isEmpty(nombre)
                && nombre.trim().length() >= 3
                && nombre.trim().length() <= 15;
    }

    // Comprueba que el teléfono tenga exactamente 9 dígitos
    public static boolean telefonoValido(String telefono) {
        return !TextUtils.isEmpty(telefono)
                && telefono.matches("\\d{9}");
    }
}