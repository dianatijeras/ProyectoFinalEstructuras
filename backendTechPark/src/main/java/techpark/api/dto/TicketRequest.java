package techpark.api.dto;

/**
 * DTO para la solicitud de creación de un ticket.
 * @param documentoVisitante
 * @param tipoTicket
 * @param zonaId
 * @param cantidadPersonasFamilia
 */
public record TicketRequest(String documentoVisitante, String tipoTicket, String zonaId, Integer cantidadPersonasFamilia) {}
