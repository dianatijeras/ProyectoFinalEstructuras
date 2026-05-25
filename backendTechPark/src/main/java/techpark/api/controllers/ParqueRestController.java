package techpark.api.controllers;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import techpark.api.dto.ApiResponse;
import techpark.api.dto.EstadoParqueDTO;
import techpark.api.dto.ParqueResumenDTO;
import techpark.api.mappers.ApiMapper;
import techpark.config.AppState;
import techpark.model.parque.Atraccion;
import techpark.model.parque.Parque;
import techpark.model.parque.Zona;

/**
 * Controlador REST para gestionar la información general del parque de atracciones, incluyendo su resumen y estado actual.
 */
@RestController
@RequestMapping("/api/parque")
public class ParqueRestController {
    private final AppState appState;

    /**
     * Constructor para inyectar el estado de la aplicación, que proporciona acceso a los servicios y datos del parque.
     * @param appState
     */
    public ParqueRestController(AppState appState) { this.appState = appState; }

    /**
     * Devuelve un resumen general del parque, incluyendo su nombre, aforo actual, capacidad máxima, número de zonas y número de atracciones.
     * @return
     */
    @GetMapping("/resumen")
    public ApiResponse<ParqueResumenDTO> resumen() {
        appState.verificarExpiracionTickets();
        Parque parque = appState.getParque();
        return ApiResponse.ok(new ParqueResumenDTO(parque.getNombre(), parque.getAforoActual(), parque.getCapacidadMaxima(), parque.getZonas().size(), parque.getCatalogoAtracciones().getTamanio()));
    }

    /**
     * Devuelve el estado actual del parque, incluyendo un resumen general y el estado detallado de cada zona, con sus atracciones correspondientes.
     * @return
     */
    @GetMapping("/estado")
    public ApiResponse<EstadoParqueDTO> estado() {
        appState.verificarExpiracionTickets();
        Parque parque = appState.getParque();
        ParqueResumenDTO resumen = new ParqueResumenDTO(parque.getNombre(), parque.getAforoActual(), parque.getCapacidadMaxima(), parque.getZonas().size(), parque.getCatalogoAtracciones().getTamanio());
        List<EstadoParqueDTO.ZonaEstadoDTO> zonas = new ArrayList<>();
        for (Zona zona : parque.getZonas()) {
            List<techpark.api.dto.AtraccionDTO> atracciones = new ArrayList<>();
            for (Atraccion a : zona.getAtracciones()) atracciones.add(ApiMapper.toAtraccionDTO(a));
            zonas.add(new EstadoParqueDTO.ZonaEstadoDTO(ApiMapper.toZonaDTO(zona), atracciones));
        }
        return ApiResponse.ok(new EstadoParqueDTO(resumen, zonas));
    }
}
