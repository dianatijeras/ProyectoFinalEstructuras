package techpark.api.controllers;

import org.springframework.web.bind.annotation.*;
import techpark.api.dto.ApiResponse;
import techpark.api.dto.MapaResponse;
import techpark.api.dto.RutaResponse;
import techpark.config.AppState;
import techpark.estructuras.grafo.Arista;
import techpark.estructuras.grafo.NodoGrafo;
import techpark.model.parque.Atraccion;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador REST para manejar las operaciones relacionadas con el mapa del parque.
 */
@RestController
@RequestMapping("/api/mapa")
public class MapaRestController {
    private final AppState appState;

    /**
     * Constructor para inyectar el estado de la aplicación.
     * @param appState
     */
    public MapaRestController(AppState appState) { this.appState = appState; }

    /**
     * Devuelve la representación del mapa del parque, incluyendo los nodos (atracciones) y las aristas (caminos entre atracciones).
     * @return
     */
    @GetMapping
    public ApiResponse<MapaResponse> mapa() {
        List<MapaResponse.NodoMapaDTO> nodos = new ArrayList<>();
        List<MapaResponse.AristaMapaDTO> aristas = new ArrayList<>();
        for (NodoGrafo<Atraccion> nodo : appState.getParque().getMapa().getNodos()) {
            Atraccion a = nodo.getDato();
            nodos.add(new MapaResponse.NodoMapaDTO(a.getId(), a.getNombre(), a.getEstado().name(), a.getTipo().name(), a.getZona() == null ? null : a.getZona().getNombre(), a.getZona() == null ? null : a.getZona().getId(), a.getZona() == null || a.getZona().hayAforoDisponible()));
            for (Arista<Atraccion> arista : nodo.getVecinos()) {
                if (arista.getOrigen().getId().compareToIgnoreCase(arista.getDestino().getId()) < 0) {
                    aristas.add(new MapaResponse.AristaMapaDTO(arista.getOrigen().getId(), arista.getDestino().getId(), arista.getPeso()));
                }
            }
        }
        return ApiResponse.ok(new MapaResponse(nodos, aristas));
    }

    /**
     * Calcula la ruta más corta entre dos atracciones utilizando el algoritmo de Dijkstra.
     * @param origenId
     * @param destinoId
     * @return
     */
    @GetMapping("/ruta")
    public ApiResponse<RutaResponse> ruta(@RequestParam String origenId, @RequestParam String destinoId) {
        List<NodoGrafo<Atraccion>> ruta = appState.getParque().getMapa().dijkstra(origenId, destinoId);
        if (ruta.isEmpty()) throw new IllegalArgumentException("No se encontro ruta entre " + origenId + " y " + destinoId);
        List<String> nodos = new ArrayList<>();
        for (NodoGrafo<Atraccion> nodo : ruta) nodos.add(nodo.getId() + " - " + nodo.getDato().getNombre());
        return ApiResponse.ok(new RutaResponse(origenId, destinoId, calcularDistancia(ruta), nodos));
    }

    /**
     * Realiza un recorrido por el mapa utilizando el algoritmo de búsqueda en anchura (BFS) a partir de una atracción de origen especificada por su ID.
     * @param origenId
     * @return
     */
    @GetMapping("/bfs/{origenId}")
    public ApiResponse<List<String>> bfs(@PathVariable String origenId) {
        List<NodoGrafo<Atraccion>> recorrido = appState.getParque().getMapa().bfs(origenId);
        List<String> respuesta = new ArrayList<>();
        for (NodoGrafo<Atraccion> nodo : recorrido) respuesta.add(nodo.getId() + " - " + nodo.getDato().getNombre());
        return ApiResponse.ok(respuesta);
    }

    /**
     * Calcula la distancia total de una ruta dada, sumando los pesos de las aristas entre los nodos de la ruta.
     * @param ruta
     * @return
     */
    private double calcularDistancia(List<NodoGrafo<Atraccion>> ruta) {
        double total = 0;
        for (int i = 0; i < ruta.size() - 1; i++) {
            NodoGrafo<Atraccion> actual = ruta.get(i);
            NodoGrafo<Atraccion> siguiente = ruta.get(i + 1);
            for (Arista<Atraccion> arista : actual.getVecinos()) {
                if (arista.getDestino().getId().equalsIgnoreCase(siguiente.getId())) {
                    total += arista.getPeso();
                    break;
                }
            }
        }
        return total;
    }
}
