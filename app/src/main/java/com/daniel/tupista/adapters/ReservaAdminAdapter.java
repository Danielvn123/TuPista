package com.daniel.tupista.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.tupista.R;
import com.daniel.tupista.model.Reserva;

import java.util.List;

// Adapter encargado de mostrar todas las reservas en el panel de administración
public class ReservaAdminAdapter extends RecyclerView.Adapter<ReservaAdminAdapter.ReservaViewHolder> {

    // Listener que permite notificar a la Activity cuando se solicita cancelar una reserva
    public interface OnReservaClickListener {
        void onCancelarReserva(Reserva reserva);
    }

    // Lista de reservas que se mostrarán en el RecyclerView
    private final List<Reserva> reservas;

    // Listener recibido desde la Activity
    private final OnReservaClickListener listener;

    public ReservaAdminAdapter(List<Reserva> reservas, OnReservaClickListener listener) {
        this.reservas = reservas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReservaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reserva_admin, parent, false);

        return new ReservaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservaViewHolder holder, int position) {

        // Obtiene la reserva correspondiente a la posición actual
        Reserva reserva = reservas.get(position);

        // Muestra los datos principales de la reserva
        holder.tvUsuarioAdmin.setText("Usuario: " + reserva.getNombreUsuario());
        holder.tvClubAdmin.setText("Club: " + reserva.getClub());
        holder.tvPistaAdmin.setText("Pista: " + reserva.getNombrePista());
        holder.tvFechaAdmin.setText("Fecha: " + reserva.getFecha());
        holder.tvHoraAdmin.setText("Hora: " + reserva.getHoraInicio() + " - " + reserva.getHoraFin());

        holder.btnCancelarReservaAdmin.setOnClickListener(v ->
                listener.onCancelarReserva(reserva)
        );
    }

    @Override
    public int getItemCount() {

        // Devuelve el número total de reservas
        return reservas.size();
    }

    // ViewHolder que guarda las referencias a los elementos visuales de cada reserva
    public static class ReservaViewHolder extends RecyclerView.ViewHolder {

        TextView tvUsuarioAdmin, tvClubAdmin, tvPistaAdmin, tvFechaAdmin, tvHoraAdmin;

        Button btnCancelarReservaAdmin;

        public ReservaViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsuarioAdmin = itemView.findViewById(R.id.tvUsuarioAdmin);
            tvClubAdmin = itemView.findViewById(R.id.tvClubAdmin);
            tvPistaAdmin = itemView.findViewById(R.id.tvPistaAdmin);
            tvFechaAdmin = itemView.findViewById(R.id.tvFechaAdmin);
            tvHoraAdmin = itemView.findViewById(R.id.tvHoraAdmin);

            btnCancelarReservaAdmin = itemView.findViewById(R.id.btnCancelarReservaAdmin);
        }
    }
}