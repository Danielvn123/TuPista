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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Adapter encargado de mostrar las reservas del usuario en el RecyclerView
public class ReservaUsuarioAdapter extends RecyclerView.Adapter<ReservaUsuarioAdapter.ReservaViewHolder> {

    // Listener que permite notificar a la Activity cuando el usuario quiere cancelar una reserva
    public interface OnReservaClickListener {
        void onCancelarReserva(Reserva reserva);
    }

    // Lista de reservas que se mostrarán en pantalla
    private final List<Reserva> reservas;

    // Listener recibido desde la Activity
    private final OnReservaClickListener listener;

    public ReservaUsuarioAdapter(List<Reserva> reservas, OnReservaClickListener listener) {
        this.reservas = reservas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReservaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reserva_usuario, parent, false);
        return new ReservaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservaViewHolder holder, int position) {

        Reserva reserva = reservas.get(position);

        holder.tvClubUsuario.setText("Club: " + reserva.getClub());

        // Si no se recibe el nombre de la pista, se muestra el identificador como alternativa
        String nombrePista = reserva.getNombrePista() == null ? "Pista " + reserva.getPistaId() : reserva.getNombrePista();

        // Muestra los datos principales de la reserva
        holder.tvPistaUsuario.setText("Pista: " + nombrePista);
        holder.tvFechaUsuario.setText(formatearFecha(reserva.getFecha()));
        holder.tvHoraUsuario.setText(reserva.getHoraInicio() + " - " + reserva.getHoraFin());

        // Calcula el estado de la reserva según la fecha y la hora actual
        String estado = calcularEstadoReserva(reserva.getFecha(), reserva.getHoraInicio(), reserva.getHoraFin());

        // Muestra el estado de la reserva y aplica los colores correspondientes
        holder.tvEstadoReservaUsuario.setText(estado);
        pintarEstado(holder.tvEstadoReservaUsuario, estado);

        // Si la reserva ya terminó, no se permite cancelarla
        if (estado.equals("Finalizado")) {

            holder.btnCancelarReservaUsuario.setVisibility(View.GONE);

        } else {

            holder.btnCancelarReservaUsuario.setVisibility(View.VISIBLE);

            holder.btnCancelarReservaUsuario.setEnabled(true);
            holder.btnCancelarReservaUsuario.setText("Cancelar reserva");
            holder.btnCancelarReservaUsuario.setAlpha(1f);

            holder.btnCancelarReservaUsuario.setOnClickListener(v ->
                    listener.onCancelarReserva(reserva)
            );
        }
    }

    @Override
    public int getItemCount() {

        return reservas.size();
    }

    // Aplica colores distintos según el estado de la reserva
    private void pintarEstado(TextView textView, String estado) {
        int colorFondo, colorTexto;

        switch (estado) {
            case "Próximo":
                colorFondo = textView.getContext().getColor(R.color.verde_claro);
                colorTexto = textView.getContext().getColor(R.color.verde);
                break;

            case "En curso":
                colorFondo = textView.getContext().getColor(R.color.azul_claro);
                colorTexto = textView.getContext().getColor(R.color.azul_principal);
                break;

            case "Finalizado":
                colorFondo = textView.getContext().getColor(R.color.rojo_claro);
                colorTexto = textView.getContext().getColor(R.color.rojo);
                break;

            default:
                colorFondo = textView.getContext().getColor(R.color.gris_borde);
                colorTexto = textView.getContext().getColor(R.color.gris);
                break;
        }

        textView.setBackgroundColor(colorFondo);
        textView.setTextColor(colorTexto);
    }

    // Calcula si una reserva está próxima, en curso o finalizada
    private String calcularEstadoReserva(String fecha, String horaInicio, String horaFin) {
        String[] formatosFecha = {
                "yyyy-MM-dd HH:mm", "dd/MM/yyyy HH:mm", "dd-MM-yyyy HH:mm"
        };

        // Se prueban varios formatos para evitar errores si la fecha llega con otro formato
        for (String formatoTexto : formatosFecha) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(formatoTexto, Locale.getDefault());

                Date ahora = new Date();
                Date inicio = sdf.parse(fecha + " " + horaInicio);
                Date fin = sdf.parse(fecha + " " + horaFin);

                if (inicio == null || fin == null) {
                    continue;
                }

                // Si la fecha actual es anterior al inicio, la reserva es próxima
                if (ahora.before(inicio)) {
                    return "Próximo";
                }

                // Si la fecha actual es posterior al fin, la reserva está finalizada
                if (ahora.after(fin)) {
                    return "Finalizado";
                }

                // Si no es próxima ni finalizada, está en curso
                return "En curso";

            } catch (Exception ignored) {
            }
        }

        // Valor por defecto si no se puede calcular correctamente
        return "Próximo";
    }

    // Convierte la fecha recibida de la base de datos a un formato más legible
    private String formatearFecha(String fechaBD) {
        String[] formatosFecha = {
                "yyyy-MM-dd", "dd/MM/yyyy", "dd-MM-yyyy"};

        // Intenta interpretar la fecha usando distintos formatos posibles
        for (String formatoTexto : formatosFecha) {
            try {
                Date date = new SimpleDateFormat(
                        formatoTexto,
                        Locale.getDefault()
                ).parse(fechaBD);

                if (date != null) {

                    // Devuelve la fecha con día de la semana en español
                    return new SimpleDateFormat("EEEE dd/MM/yyyy", new Locale("es", "ES")).format(date);
                }

            } catch (Exception ignored) {
            }
        }

        // Si no se puede formatear, se devuelve la fecha original
        return fechaBD;
    }

    // ViewHolder que guarda las referencias a los elementos visuales de cada reserva
    public static class ReservaViewHolder extends RecyclerView.ViewHolder {

        TextView tvEstadoReservaUsuario, tvClubUsuario, tvPistaUsuario, tvFechaUsuario, tvHoraUsuario;
        Button btnCancelarReservaUsuario;

        public ReservaViewHolder(@NonNull View itemView) {
            super(itemView);

            tvEstadoReservaUsuario = itemView.findViewById(R.id.tvEstadoReservaUsuario);
            tvClubUsuario = itemView.findViewById(R.id.tvClubUsuario);
            tvPistaUsuario = itemView.findViewById(R.id.tvPistaUsuario);
            tvFechaUsuario = itemView.findViewById(R.id.tvFechaUsuario);
            tvHoraUsuario = itemView.findViewById(R.id.tvHoraUsuario);
            btnCancelarReservaUsuario = itemView.findViewById(R.id.btnCancelarReservaUsuario);
        }
    }
}