package techpark.api.dto;

/**
 * DTO para actualizar el estado de una atracción.
 * @param estado
 * @param motivo
 * @param documentoOperador
 */
public record EstadoAtraccionRequest(String estado, String motivo, String documentoOperador) {}
