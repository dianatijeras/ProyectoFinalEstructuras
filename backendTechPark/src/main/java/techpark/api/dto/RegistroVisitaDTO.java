package techpark.api.dto;

/**
 * DTO para registrar una visita a una atracción, incluyendo detalles como el ID y nombre de la atracción, fecha y hora de la visita, tipo de ticket utilizado y el costo deducido.
 * @param atraccionId
 * @param atraccionNombre
 * @param fechaHora
 * @param tipoTicket
 * @param costoDeducido
 */
public record RegistroVisitaDTO(String atraccionId, String atraccionNombre, String fechaHora, String tipoTicket, double costoDeducido) {}
