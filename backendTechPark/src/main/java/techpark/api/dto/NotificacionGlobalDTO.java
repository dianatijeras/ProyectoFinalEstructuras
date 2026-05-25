package techpark.api.dto;

/**
 * DTO para representar una notificación global.
 * @param id
 * @param tipo
 * @param titulo
 * @param mensaje
 * @param fechaHora
 */
public record NotificacionGlobalDTO(String id, String tipo, String titulo, String mensaje, String fechaHora) {}
