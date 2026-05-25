package techpark.api.dto;

/**
 * DTO para actualizar una zona existente.
 * @param nombre
 * @param capacidadMaxima
 * @param disponible
 */
public record ActualizarZonaRequest(String nombre, Integer capacidadMaxima, Boolean disponible) {
}
