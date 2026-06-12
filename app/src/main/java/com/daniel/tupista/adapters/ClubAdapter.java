package com.daniel.tupista.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.tupista.R;
import com.daniel.tupista.model.Club;

import java.util.List;

public class ClubAdapter extends RecyclerView.Adapter<ClubAdapter.ClubViewHolder> {

    // Listener para detectar cuándo el usuario pulsa sobre un club
    public interface OnClubClickListener {
        void onClubClick(Club club);
    }

    // Listener para detectar cuándo el usuario pulsa el botón de ver reseñas
    public interface OnResenasClickListener {
        void onResenasClick(Club club);
    }

    // Lista de clubes que se mostrarán en pantalla
    private final List<Club> clubes;

    // Listener principal para abrir la pantalla de reserva
    private final OnClubClickListener listener;
    private final OnResenasClickListener resenasListener;

    public ClubAdapter(List<Club> clubes, OnClubClickListener listener) {
        this(clubes, listener, null);
    }

    public ClubAdapter(List<Club> clubes, OnClubClickListener listener, OnResenasClickListener resenasListener) {
        this.clubes = clubes;
        this.listener = listener;
        this.resenasListener = resenasListener;
    }

    @NonNull
    @Override
    public ClubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_club, parent, false);
        return new ClubViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClubViewHolder holder, int position) {
        Club club = clubes.get(position);

        holder.tvNombreClub.setText(club.getNombre());

        holder.tvDireccionClub.setText(
                club.getDireccion().isEmpty()
                        ? "Dirección: No disponible"
                        : "Dirección: " + club.getDireccion()
        );

        holder.tvTelefonoClub.setText(
                club.getTelefono().isEmpty()
                        ? "Teléfono: No disponible"
                        : "Teléfono: " + club.getTelefono()
        );

        holder.tvTotalPistas.setText(
                club.getTotalPistas() == 1
                        ? "1 pista"
                        : club.getTotalPistas() + " pistas"
        );

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClubClick(club);
            }
        });

        holder.btnVerResenas.setOnClickListener(v -> {
            if (resenasListener != null) {
                resenasListener.onResenasClick(club);
            }
        });
    }

    @Override
    public int getItemCount() {
        return clubes.size();
    }

    // ViewHolder que guarda las referencias a los elementos visuales de cada club
    public static class ClubViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombreClub, tvDireccionClub, tvTelefonoClub, tvTotalPistas;
        Button btnVerResenas;

        public ClubViewHolder(@NonNull View itemView) {
            super(itemView);

            // Relaciona cada variable con su vista correspondiente del XML
            tvNombreClub = itemView.findViewById(R.id.tvNombreClub);
            tvDireccionClub = itemView.findViewById(R.id.tvDireccionClub);
            tvTelefonoClub = itemView.findViewById(R.id.tvTelefonoClub);
            tvTotalPistas = itemView.findViewById(R.id.tvTotalPistas);
            btnVerResenas = itemView.findViewById(R.id.btnVerResenas);
        }
    }
}