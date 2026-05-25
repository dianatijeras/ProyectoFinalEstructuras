package techpark.api.dto;

/**
 * DTO para la asignación de operadores a zonas y atracciones.
 * @param documentoOperador
 * @param nombreOperador
 * @param zonaId
 * @param zonaNombre
 * @param idAtraccion
 * @param nombreAtraccion
 */
public record OperadorAsignacionDTO(String documentoOperador, String nombreOperador, String zonaId, String zonaNombre, String idAtraccion, String nombreAtraccion) {}
