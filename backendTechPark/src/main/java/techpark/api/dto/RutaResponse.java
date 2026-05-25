package techpark.api.dto;
import java.util.List;

/**
 * DTO para la respuesta de la ruta más corta entre dos nodos.
 * @param origenId
 * @param destinoId
 * @param distanciaTotal
 * @param nodos
 */
public record RutaResponse(String origenId, String destinoId, double distanciaTotal, List<String> nodos) {}
