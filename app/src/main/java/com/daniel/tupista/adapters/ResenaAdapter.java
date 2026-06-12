package com.daniel.tupista.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.tupista.R;
import com.daniel.tupista.model.Resena;

import java.util.List;

// Adapter encargado de mostrar las reseñas de un club en el RecyclerView
public class ResenaAdapter extends RecyclerView.Adapter<ResenaAdapter.ResenaViewHolder> {

    // Lista de reseñas que se mostrarán en pantalla
    private final List<Resena> resenas;

    public ResenaAdapter(List<Resena> resenas) {
        this.resenas = resenas;
    }

    @NonNull
    @Override
    public ResenaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resena, parent, false);
        return new ResenaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResenaViewHolder holder, int position) {

        Resena resena = resenas.get(position);

        holder.tvNombreUsuarioResena.setText(resena.getNombreUsuario());

        holder.tvEstrellasResena.setText(pintarEstrellas(resena.getEstrellas()));

        holder.tvComentarioResena.setText(resena.getComentario());
    }

    @Override
    public int getItemCount() {

        return resenas.size();
    }

    // Convierte una valoración numérica en una cadena de estrellas rellenas y vacías
    private String pintarEstrellas(int estrellas) {

        StringBuilder resultado = new StringBuilder();

        // Recorre las 5 posiciones posibles de valoración
        for (int i = 1; i <= 5; i++) {

            if (i <= estrellas) {
                resultado.append("⭐");
            } else {
                resultado.append("☆");
            }
        }

        return resultado.toString();
    }

    // ViewHolder que guarda las referencias a los elementos visuales de cada reseña
    public static class ResenaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreUsuarioResena, tvEstrellasResena, tvComentarioResena;

        public ResenaViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombreUsuarioResena = itemView.findViewById(R.id.tvNombreUsuarioResena);
            tvEstrellasResena = itemView.findViewById(R.id.tvEstrellasResena);
            tvComentarioResena = itemView.findViewById(R.id.tvComentarioResena);
        }
    }
}