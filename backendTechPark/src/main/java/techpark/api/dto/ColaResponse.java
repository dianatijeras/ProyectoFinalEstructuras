package techpark.api.dto;

/**
 * DTO para la respuesta de la consulta de la cola de una atracción.
 * @param idAtraccion
 * @param nombreAtraccion
 * @param tamanioCola
 * @param tiempoEstimadoEspera
 * @param posicion
 * @param mensaje
 */
public record ColaResponse(String idAtraccion, String nombreAtraccion, int tamanioCola, int tiempoEstimadoEspera, Integer posicion, String mensaje) {}
