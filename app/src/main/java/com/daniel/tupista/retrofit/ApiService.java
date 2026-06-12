package com.daniel.tupista.retrofit;

import com.daniel.tupista.model.Club;
import com.daniel.tupista.model.Pista;
import com.daniel.tupista.model.Resena;
import com.daniel.tupista.model.Reserva;
import com.daniel.tupista.model.Usuario;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    // USUARIOS

    @GET("usuarios")
    Call<List<Usuario>> obtenerUsuarioPorEmail(@Query("email") String email);

    @GET("usuarios")
    Call<List<Usuario>> obtenerUsuarioPorId(@Query("id") String id);

    @GET("usuarios?order=id.asc")
    Call<List<Usuario>> obtenerTodosUsuarios();

    @POST("usuarios")
    Call<List<Usuario>> registrarUsuario(@Body Map<String, Object> usuario);

    @PATCH("usuarios")
    Call<List<Usuario>> actualizarUsuario(@Query("id") String id, @Body Map<String, Object> usuario);

    @DELETE("usuarios")
    Call<Void> eliminarUsuario(@Query("id") String id);


    // CLUBES

    @GET("clubes")
    Call<List<Club>> obtenerTodosClubes(
            @Query(value = "select", encoded = true) String select,
            @Query("order") String order
    );

    @GET("clubes")
    Call<List<Club>> obtenerClubPorNombre(@Query("nombre") String nombre);

    @POST("clubes")
    Call<List<Club>> insertarClub(@Body Map<String, Object> club);

    @DELETE("clubes")
    Call<Void> eliminarClub(@Query("id") String id);


    // PISTAS

    @GET("pistas")
    Call<List<Pista>> obtenerTodasPistas(
            @Query(value = "select", encoded = true) String select,
            @Query("order") String order
    );

    @GET("pistas")
    Call<List<Pista>> obtenerPistasPorClub(
            @Query("clubId") String clubId,
            @Query("order") String order
    );

    @POST("pistas")
    Call<List<Pista>> insertarPista(@Body Map<String, Object> pista);

    @DELETE("pistas")
    Call<Void> eliminarPista(@Query("id") String id);


    // RESERVAS

    @GET("reservas")
    Call<List<Reserva>> obtenerReservasPorPista(
            @Query("pistaId") String pistaId,
            @Query(value = "select", encoded = true) String select
    );

    @GET("reservas")
    Call<List<Reserva>> obtenerReservasUsuario(
            @Query("usuarioId") String usuarioId,
            @Query(value = "select", encoded = true) String select,
            @Query("order") String order
    );

    @GET("reservas")
    Call<List<Reserva>> obtenerTodasReservas(
            @Query(value = "select", encoded = true) String select,
            @Query("order") String order
    );

    @GET("reservas")
    Call<List<Reserva>> obtenerReservasPistaFecha(
            @Query("pistaId") String pistaId,
            @Query("fecha") String fecha,
            @Query(value = "select", encoded = true) String select
    );

    @GET("reservas")
    Call<List<Reserva>> comprobarReservaExistente(
            @Query("pistaId") String pistaId,
            @Query("fecha") String fecha,
            @Query("horaInicio") String horaInicio
    );

    @GET("reservas")
    Call<List<Reserva>> comprobarReservaUsuarioHoraClub(
            @Query("usuarioId") String usuarioId,
            @Query("fecha") String fecha,
            @Query("horaInicio") String horaInicio,
            @Query(value = "select", encoded = true) String select,
            @Query("pistas.clubId") String clubId
    );

    @GET("reservas")
    Call<List<Reserva>> obtenerReservasUsuarioClub(
            @Query("usuarioId") String usuarioId,
            @Query(value = "select", encoded = true) String select,
            @Query("pistas.clubId") String clubId
    );

    @POST("reservas")
    Call<List<Reserva>> crearReserva(@Body Map<String, Object> reserva);

    @DELETE("reservas")
    Call<Void> cancelarReserva(@Query("id") String id);


    // RESEÑAS

    @GET("resenas")
    Call<List<Resena>> obtenerResenasClub(
            @Query("clubId") String clubId,
            @Query(value = "select", encoded = true) String select,
            @Query("order") String order
    );

    @GET("resenas")
    Call<List<Resena>> obtenerTodasResenas(
            @Query(value = "select", encoded = true) String select,
            @Query("order") String order
    );

    @GET("resenas")
    Call<List<Resena>> comprobarResenaUsuarioClub(
            @Query("usuarioId") String usuarioId,
            @Query("clubId") String clubId,
            @Query(value = "select", encoded = true) String select
    );

    @POST("resenas")
    Call<List<Resena>> insertarResena(@Body Map<String, Object> resena);

    @DELETE("resenas")
    Call<Void> eliminarResena(@Query("id") String id);
}