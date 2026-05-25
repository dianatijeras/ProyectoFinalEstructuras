package techpark.api.controllers;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import techpark.api.dto.ApiResponse;
import techpark.api.dto.ZonaDTO;
import techpark.api.mappers.ApiMapper;
import techpark.config.AppState;
import techpark.model.parque.Zona;

/**
 * Controlador REST para gestionar las zonas del parque de atracciones, permitiendo listar todas las zonas y buscar una zona específica por su ID.
 */
@RestController
@RequestMapping("/api/zonas")
public class ZonasRestController {
    private final AppState appState;

    /**
     * Constructor para inyectar el estado de la aplicación, que proporciona acceso a los servicios y datos relacionados con las zonas del parque.
     * @param appState
     */
    public ZonasRestController(AppState appState) { this.appState = appState; }

    /**
     * Endpoint para listar todas las zonas del parque.
     * @return
     */
    @GetMapping
    public ApiResponse<List<ZonaDTO>> listar() {
        List<ZonaDTO> respuesta = new ArrayList<>();
        for (Zona z : appState.getParque().getZonas()) respuesta.add(ApiMapper.toZonaDTO(z));
        return ApiResponse.ok(respuesta);
    }

    /**
     * Endpoint para buscar una zona específica por su ID.
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ApiResponse<ZonaDTO> buscar(@PathVariable String id) {
        Zona zona = buscarZona(id);
        return ApiResponse.ok(ApiMapper.toZonaDTO(zona));
    }

    /**
     * Método auxiliar para buscar una zona por su ID.
     * @param id
     * @return
     */
    private Zona buscarZona(String id) {
        for (Zona z : appState.getParque().getZonas()) if (z.getId().equalsIgnoreCase(id)) return z;
        throw new IllegalArgumentException("Zona no encontrada: " + id);
    }
}
