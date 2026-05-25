package techpark.api.dto;

/**
 * DTO para representar una notificación.
 * @param id
 * @param mensaje
 * @param tipo
 * @param fechaHora
 */
public record NotificacionDTO(String id, String mensaje, String tipo, String fechaHora) {}
