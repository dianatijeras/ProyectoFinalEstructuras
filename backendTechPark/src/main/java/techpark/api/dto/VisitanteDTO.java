package techpark.api.dto;

/**
 * DTO para representar la información de un visitante en la aplicación.
 * @param id
 * @param nombre
 * @param documento
 * @param edad
 * @param estatura
 * @param saldoVirtual
 * @param ticketActivo
 * @param ticketActivoTipo
 * @param ticketActivoEstado
 * @param enCola
 * @param ubicacionActual
 * @param ubicacionActualId
 */
public record VisitanteDTO(String id, String nombre, String documento, int edad, double estatura, double saldoVirtual, String ticketActivo, String ticketActivoTipo, String ticketActivoEstado, boolean enCola, String ubicacionActual, String ubicacionActualId) {}
