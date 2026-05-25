package techpark.api.dto;

/**
 *
 * @param id
 * @param nombre
 * @param capacidadMaxima
 * @param disponible
 */
public record CrearZonaRequest(String id, String nombre, int capacidadMaxima, Boolean disponible) {}
