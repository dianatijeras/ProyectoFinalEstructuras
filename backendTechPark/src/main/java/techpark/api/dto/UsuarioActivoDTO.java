package techpark.api.dto;

/**
 * DTO para representar la información de un usuario activo, incluyendo su estado de ticket y saldo virtual.
 * @param id
 * @param nombre
 * @param documento
 * @param rol
 * @param ticketActivo
 * @param tipoTicket
 * @param ubicacionActual
 * @param saldoVirtual
 */
public record UsuarioActivoDTO(String id, String nombre, String documento, String rol, boolean ticketActivo, String tipoTicket, String ubicacionActual, Double saldoVirtual) {}
