# TuPista

TuPista es una aplicación Android para gestionar reservas de pistas de pádel. La aplicación permite registrarse, iniciar sesión, consultar clubes deportivos, reservar pistas, consultar las reservas realizadas, editar el perfil y publicar reseñas de clubes.

## Cambios en el modelo de datos

El proyecto incluye una entidad `Club`, siguiendo la corrección indicada en la revisión del proyecto. De esta forma se evita guardar el nombre del club como texto repetido en varias tablas.

Relaciones principales:

- Un `Club` tiene varias `Pista`.
- Una `Pista` pertenece a un único `Club` mediante `clubId`.
- Una `Reserva` pertenece a un `Usuario` y a una `Pista`.
- El club de una reserva se obtiene a través de la pista reservada: `Reserva -> Pista -> Club`.
- Una `Resena` relaciona un `Usuario` con un `Club`.
- Un usuario solo puede hacer una reseña por club mediante una restricción única sobre `usuarioId` y `clubId`.

## Tecnologías utilizadas

- Android Studio
- Java
- Supabase
- Supabase Auth
- Retrofit
- RecyclerView

## Licencia

Este proyecto se distribuye bajo la licencia MIT.

La licencia MIT es una licencia de software libre y permisiva. Permite usar, copiar, modificar, fusionar, publicar y distribuir el software, siempre que se mantenga el aviso de copyright y el texto de la licencia. El software se entrega sin garantía, por lo que el autor no se hace responsable de posibles errores, daños o problemas derivados de su uso.
