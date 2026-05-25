package techpark.api.dto;

/**
 * DTO para representar un resumen de un parque de diversiones, incluyendo su nombre, aforo actual, capacidad máxima, cantidad de zonas y cantidad de atracciones.
 * @param nombre
 * @param aforoActual
 * @param capacidadMaxima
 * @param cantidadZonas
 * @param cantidadAtracciones
 */
public record ParqueResumenDTO(String nombre, int aforoActual, int capacidadMaxima, int cantidadZonas, int cantidadAtracciones) {}
