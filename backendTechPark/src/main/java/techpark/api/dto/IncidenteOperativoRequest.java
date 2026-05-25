package techpark.api.dto;

/**
 * DTO para representar la solicitud de creación de un incidente operativo.
 * @param idAtraccion
 * @param descripcion
 * @param gravedad
 */
public record IncidenteOperativoRequest(String idAtraccion, String descripcion, String gravedad) {}
