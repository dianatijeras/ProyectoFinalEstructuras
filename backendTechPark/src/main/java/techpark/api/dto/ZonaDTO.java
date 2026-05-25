package techpark.api.dto;

/**
 * DTO para representar la información de una zona en el parque de diversiones.
 * @param id
 * @param nombre
 * @param capacidadMaxima
 * @param aforoActual
 * @param cantidadAtracciones
 * @param cantidadOperadores
 * @param disponible
 */
public record ZonaDTO(String id, String nombre, int capacidadMaxima, int aforoActual, int cantidadAtracciones, int cantidadOperadores, boolean disponible) {}
