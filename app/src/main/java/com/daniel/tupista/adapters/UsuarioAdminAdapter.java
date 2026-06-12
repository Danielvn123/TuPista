package com.daniel.tupista.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.tupista.R;
import com.daniel.tupista.model.Usuario;

import java.util.List;

// Adapter encargado de mostrar los usuarios en el panel de administración
public class UsuarioAdminAdapter extends RecyclerView.Adapter<UsuarioAdminAdapter.UsuarioViewHolder> {

    // Listener que permite notificar a la Activity cuando se solicita eliminar un usuario
    public interface OnUsuarioClickListener {
        void onEliminarUsuario(Usuario usuario);
    }

    // Lista de usuarios que se mostrarán en el RecyclerView
    private final List<Usuario> listaUsuarios;

    // Listener recibido desde la Activity
    private final OnUsuarioClickListener listener;

    public UsuarioAdminAdapter(List<Usuario> listaUsuarios, OnUsuarioClickListener listener) {
        this.listaUsuarios = listaUsuarios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario_admin, parent, false);

        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {

        // Obtiene el usuario correspondiente a la posición actual
        Usuario usuario = listaUsuarios.get(position);

        // Muestra todos los datos principales del usuario en un único TextView
        holder.tvDatosUsuario.setText("ID: " + usuario.getId() + "\n" + "Nombre: " + usuario.getNombre() + "\n" + "Email: " + usuario.getEmail() + "\n" + "Teléfono: " + usuario.getTelefono() + "\n" + "Rol: " + usuario.getRol());

        // Si el usuario es administrador, no se permite eliminarlo
        if (usuario.getRol().equalsIgnoreCase("admin")) {
            holder.btnEliminarUsuario.setVisibility(View.GONE);
        } else {

            // Para usuarios normales se muestra el botón de eliminación
            holder.btnEliminarUsuario.setVisibility(View.VISIBLE);

            // Notifica a la Activity cuando se pulsa el botón de eliminar usuario
            holder.btnEliminarUsuario.setOnClickListener(v ->
                    listener.onEliminarUsuario(usuario)
            );
        }
    }

    @Override
    public int getItemCount() {

        return listaUsuarios.size();
    }

    // ViewHolder que guarda las referencias a los elementos visuales de cada usuario
    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {

        TextView tvDatosUsuario;
        Button btnEliminarUsuario;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDatosUsuario = itemView.findViewById(R.id.tvDatosUsuario);
            btnEliminarUsuario = itemView.findViewById(R.id.btnEliminarUsuario);
        }
    }
}