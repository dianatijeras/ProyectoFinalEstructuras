package techpark.api.dto;

/**
 * DTO para la solicitud de revisión técnica de un vehículo.
 * @param documentoOperador
 * @param descripcion
 * @param resultado
 */
public record RevisionTecnicaRequest(String documentoOperador, String descripcion, String resultado) {}
