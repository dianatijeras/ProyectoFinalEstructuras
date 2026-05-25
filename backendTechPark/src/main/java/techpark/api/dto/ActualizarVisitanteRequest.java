package techpark.api.dto;

/**
 * DTO para actualizar la información de un visitante.
 * @param nombre
 * @param edad
 * @param estatura
 * @param saldoVirtual
 * @param password
 */
public record ActualizarVisitanteRequest(String nombre, Integer edad, Double estatura, Double saldoVirtual, String password) {
}
