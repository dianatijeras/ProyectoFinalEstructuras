package techpark.api.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import techpark.api.dto.ApiResponse;
import techpark.api.dto.ReporteJornadaDTO;
import techpark.api.mappers.ApiMapper;
import techpark.config.AppState;

/**
 * Controlador REST para gestionar los reportes del parque de atracciones, incluyendo el reporte de la jornada actual.
 */
@RestController
@RequestMapping("/api/reportes")
public class ReportesRestController {
    private final AppState appState;

    /**
     * Constructor para inyectar el estado de la aplicación, que proporciona acceso a los servicios y datos del parque.
     * @param appState
     */
    public ReportesRestController(AppState appState) { this.appState = appState; }

    /**
     * Devuelve un reporte detallado de la jornada actual, incluyendo información sobre el número de visitantes, ingresos generados, atracciones más populares, eventos ocurridos y cualquier otro dato relevante para evaluar el desempeño del parque durante el día.
     * @return
     */
    @GetMapping("/jornada")
    public ApiResponse<ReporteJornadaDTO> jornada() {
        return ApiResponse.ok(ApiMapper.toReporteDTO(appState.getServicioReportes().generarReporteJornada()));
    }
}
