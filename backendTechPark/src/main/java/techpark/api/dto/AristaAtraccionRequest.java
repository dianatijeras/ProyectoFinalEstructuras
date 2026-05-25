package techpark.api.dto;

/**
 * DTO para representar una arista entre atracciones, con el ID de la atracción destino y el peso de la arista.
 * @param idDestino
 * @param peso
 */
public record AristaAtraccionRequest(String idDestino, double peso) {
}
