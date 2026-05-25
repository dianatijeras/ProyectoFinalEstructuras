package techpark.api.dto;

/**
 * DTO para la creación de un visitante.
 * @param nombre
 * @param documento
 * @param edad
 * @param password
 * @param estatura
 * @param saldoVirtual
 */
public record CrearVisitanteRequest(String nombre, String documento, int edad, String password, double estatura, double saldoVirtual) {}
