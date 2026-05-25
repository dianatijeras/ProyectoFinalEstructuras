package techpark.api.controllers;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import techpark.api.dto.ApiResponse;
import techpark.api.dto.IncidenteOperativoDTO;
import techpark.api.dto.IncidenteOperativoRequest;
import techpark.api.dto.ResolverIncidenteRequest;
import techpark.api.mappers.ApiMapper;
import techpark.config.AppState;
import techpark.model.eventos.IncidenteOperativo;
import techpark.model.parque.Atraccion;

/**
 * Controlador REST para gestionar incidentes operativos en el parque de atracciones.
 */
@RestController
@RequestMapping("/api/incidentes")
public class IncidentesRestController {
    private final AppState appState;
    public IncidentesRestController(AppState appState) { this.appState = appState; }

    /**
     * Registra un nuevo incidente operativo asociado a una atracción específica.
     * @param request
     * @return
     */
    @PostMapping
    public ApiResponse<IncidenteOperativoDTO> registrar(@RequestBody IncidenteOperativoRequest request) {
        Atraccion atraccion = appState.getParque().buscarAtraccion(request.idAtraccion());
        if (atraccion == null) throw new IllegalArgumentException("Atraccion no encontrada: " + request.idAtraccion());
        IncidenteOperativo incidente = appState.getServicioAdministracion().registrarIncidente(atraccion, request.descripcion(), request.gravedad());
        return ApiResponse.ok("Incidente registrado", ApiMapper.toIncidenteDTO(incidente));
    }

    /**
     * Resuelve un incidente operativo existente, proporcionando una solución y actualizando su estado a resuelto.
     * @param id
     * @param request
     * @return
     */
    @PatchMapping("/{id}/resolver")
    public ApiResponse<IncidenteOperativoDTO> resolver(@PathVariable String id, @RequestBody ResolverIncidenteRequest request) {
        IncidenteOperativo incidente = appState.getServicioAdministracion().resolverIncidente(id, request.solucion());
        return ApiResponse.ok("Incidente resuelto", ApiMapper.toIncidenteDTO(incidente));
    }

    /**
     * Devuelve una lista de todos los incidentes operativos registrados en el parque.
     * @return
     */
    @GetMapping
    public ApiResponse<List<IncidenteOperativoDTO>> listar() {
        List<IncidenteOperativoDTO> respuesta = new ArrayList<>();
        for (IncidenteOperativo incidente : appState.getParque().getIncidentesOperativos()) respuesta.add(ApiMapper.toIncidenteDTO(incidente));
        return ApiResponse.ok(respuesta);
    }
}
