package techpark.api.dto;

/**
 * DTO para asignar un operador a una atracción.
 * @param documentoOperador
 * @param zonaId
 * @param idAtraccion
 */
public record AsignarOperadorRequest(String documentoOperador, String zonaId, String idAtraccion) {
}
