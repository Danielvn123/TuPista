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
import com.daniel.tupista.model.Pista;

import java.util.List;

public class ClubAdminAdapter extends RecyclerView.Adapter<ClubAdminAdapter.PistaViewHolder> {

    public interface OnPistaClickListener {
        void onEliminarPista(Pista pista);
        void onEliminarClub(Pista pista);
    }

    private final List<Pista> pistas;
    private final OnPistaClickListener listener;

    public ClubAdminAdapter(List<Pista> pistas, OnPistaClickListener listener) {
        this.pistas = pistas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PistaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_club_admin, parent, false);

        return new PistaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PistaViewHolder holder, int position) {
        Pista pista = pistas.get(position);
        Club club = pista.getClubes();

        holder.tvNombrePista.setText("Pista: " + pista.getNombre());

        if (club != null) {
            holder.tvNombreClub.setText("Club: " + club.getNombre());
            holder.tvDireccionClub.setText("Dirección: " + club.getDireccion());
            holder.tvTelefonoClub.setText("Teléfono: " + club.getTelefono());
        } else {
            holder.tvNombreClub.setText("Club: No disponible");
            holder.tvDireccionClub.setText("Dirección: No disponible");
            holder.tvTelefonoClub.setText("Teléfono: No disponible");
        }

        holder.btnEliminarPista.setOnClickListener(v ->
                listener.onEliminarPista(pista)
        );

        holder.btnEliminarClub.setOnClickListener(v ->
                listener.onEliminarClub(pista)
        );
    }

    @Override
    public int getItemCount() {
        return pistas.size();
    }

    public static class PistaViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombreClub, tvDireccionClub, tvTelefonoClub, tvNombrePista;
        Button btnEliminarPista, btnEliminarClub;

        public PistaViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombreClub = itemView.findViewById(R.id.tvNombreClub);
            tvDireccionClub = itemView.findViewById(R.id.tvDireccionClub);
            tvTelefonoClub = itemView.findViewById(R.id.tvTelefonoClub);
            tvNombrePista = itemView.findViewById(R.id.tvNombrePista);
            btnEliminarPista = itemView.findViewById(R.id.btnEliminarPista);
            btnEliminarClub = itemView.findViewById(R.id.btnEliminarClub);
        }
    }
}