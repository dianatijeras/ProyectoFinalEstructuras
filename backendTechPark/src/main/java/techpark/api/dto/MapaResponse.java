package techpark.api.dto;
import java.util.List;

/**
 * DTO para representar el mapa del parque, incluyendo nodos (atracciones) y aristas (conexiones entre atracciones).
 * @param nodos
 * @param aristas
 */
public record MapaResponse(List<NodoMapaDTO> nodos, List<AristaMapaDTO> aristas) {
    /**
     * DTO para representar un nodo en el mapa, que corresponde a una atracción del parque.
     * @param id
     * @param nombreAtraccion
     * @param estado
     * @param tipo
     * @param zona
     * @param zonaId
     * @param zonaDisponible
     */
    public record NodoMapaDTO(String id, String nombreAtraccion, String estado, String tipo, String zona, String zonaId, boolean zonaDisponible) {}

    /**
     * DTO para representar una arista en el mapa, que corresponde a una conexión entre dos atracciones del parque, con un peso que puede representar la distancia o el tiempo de recorrido entre ellas.
     * @param origenId
     * @param destinoId
     * @param peso
     */
    public record AristaMapaDTO(String origenId, String destinoId, double peso) {}
}
