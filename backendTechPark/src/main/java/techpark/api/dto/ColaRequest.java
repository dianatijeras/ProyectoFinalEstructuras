package techpark.api.dto;

/**
 * DTO para la solicitud de ingreso a la cola de una atracción.
 * @param documentoVisitante
 * @param documentoOperador
 * @param idAtraccion
 */
public record ColaRequest(String documentoVisitante, String documentoOperador, String idAtraccion) {}
