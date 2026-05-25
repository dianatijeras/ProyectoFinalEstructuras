package techpark.api.dto;

/**
 * DTO para la respuesta de un ticket.
 * @param id
 * @param tipo
 * @param precio
 * @param estado
 * @param prioridad
 * @param documentoVisitante
 */
public record TicketResponse(String id, String tipo, double precio, String estado, int prioridad, String documentoVisitante) {}
