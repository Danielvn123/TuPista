package com.daniel.tupista.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.tupista.R;
import com.daniel.tupista.model.Resena;

import java.util.List;

// Adapter encargado de mostrar todas las reseñas en el panel de administración
public class ResenaAdminAdapter extends RecyclerView.Adapter<ResenaAdminAdapter.ResenaViewHolder> {

    // Listener que permite notificar a la Activity cuando se solicita eliminar una reseña
    public interface OnResenaClickListener {
        void onEliminarResena(Resena resena);
    }

    // Lista de reseñas que se mostrarán en el RecyclerView
    private final List<Resena> resenas;

    // Listener recibido desde la Activity
    private final OnResenaClickListener listener;

    public ResenaAdminAdapter(List<Resena> resenas, OnResenaClickListener listener) {
        this.resenas = resenas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ResenaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_resena_admin, parent, false);

        return new ResenaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResenaViewHolder holder, int position) {

        Resena resena = resenas.get(position);

        holder.tvUsuarioResenaAdmin.setText("Usuario: " + resena.getNombreUsuario());

        holder.tvClubResenaAdmin.setText("Club: " + resena.getClub());

        holder.tvEstrellasResenaAdmin.setText("Estrellas: " + resena.getEstrellas() + " ⭐");

        holder.tvComentarioResenaAdmin.setText("Comentario: " + resena.getComentario());

        holder.btnEliminarResenaAdmin.setOnClickListener(v ->
                listener.onEliminarResena(resena)
        );
    }

    @Override
    public int getItemCount() {

        return resenas.size();
    }

    // ViewHolder que almacena las referencias a los elementos visuales de cada reseña
    public static class ResenaViewHolder extends RecyclerView.ViewHolder {

        TextView tvUsuarioResenaAdmin, tvClubResenaAdmin, tvEstrellasResenaAdmin, tvComentarioResenaAdmin;

        Button btnEliminarResenaAdmin;

        public ResenaViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsuarioResenaAdmin = itemView.findViewById(R.id.tvUsuarioResenaAdmin);
            tvClubResenaAdmin = itemView.findViewById(R.id.tvClubResenaAdmin);
            tvEstrellasResenaAdmin = itemView.findViewById(R.id.tvEstrellasResenaAdmin);
            tvComentarioResenaAdmin = itemView.findViewById(R.id.tvComentarioResenaAdmin);

            btnEliminarResenaAdmin = itemView.findViewById(R.id.btnEliminarResenaAdmin);
        }
    }
}