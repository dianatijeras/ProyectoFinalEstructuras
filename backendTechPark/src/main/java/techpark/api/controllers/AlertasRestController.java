package techpark.api.controllers;

import org.springframework.web.bind.annotation.*;
import techpark.enums.EstadoAtraccion;
import techpark.enums.TipoClima;
import techpark.model.eventos.AlertaClimatica;
import techpark.model.eventos.AlertaMantenimiento;
import techpark.model.parque.Atraccion;

import java.util.ArrayList;
import java.util.List;

/**
 * AlertasRestController es un controlador REST que maneja las solicitudes relacionadas con las alertas del parque.
 */
@RestController
@RequestMapping("/api/alertas")
public class AlertasRestController {
    private final AppState appState;

    /**
     * Constructor de la clase AlertasRestController.
     * @param appState
     */
    public AlertasRestController(AppState appState) { this.appState = appState; }

    /**
     * Método que activa una alerta climática en el parque según el tipo de clima especificado en la solicitud.
     * @param request
     * @return
     */
    @PostMapping("/clima")
    public ApiResponse<String> activarClima(@RequestBody AlertaClimaticaRequest request) {
        TipoClima tipo = TipoClima.valueOf(request.tipoClima().toUpperCase());
        AlertaClimatica alerta = appState.getServicioAlertas().activarAlertaClimatica(appState.getParque(), tipo);
        return ApiResponse.ok("Alerta climatica activada", alerta.getId());
    }

    /**
     * Método que finaliza una alerta climática activa en el parque según el ID de la alerta especificado en la solicitud.
     * @param id
     * @return
     */
    @PatchMapping("/clima/{id}/finalizar")
    public ApiResponse<String> finalizarClima(@PathVariable String id) {
        AlertaClimatica alerta = appState.getServicioAlertas().finalizarAlertaClimatica(appState.getParque(), id);
        return ApiResponse.ok("Alerta climatica finalizada", alerta.getId());
    }

    /**
     * Método que devuelve una lista de alertas de mantenimiento, incluyendo tanto las alertas registradas en el historial como las atracciones que actualmente están en mantenimiento pero no tienen una alerta registrada.
     * @return
     */
    @GetMapping("/mantenimiento")
    public ApiResponse<List<String>> mantenimiento() {
        List<String> respuesta = new ArrayList<>();
        for (AlertaMantenimiento alerta : appState.getServicioAlertas().getHistorialMantenimiento()) {
            respuesta.add(alerta.getId() + ";" + alerta.getAtraccion().getId() + ";" + alerta.getAtraccion().getNombre() + ";atendida=" + alerta.isAtendida());
        }
        for (Atraccion atraccion : appState.getParque().getCatalogoAtracciones().inorden()) {
            if (atraccion.getEstado() == EstadoAtraccion.EN_MANTENIMIENTO) {
                boolean existe = false;
                for (String item : respuesta) if (item.contains(";" + atraccion.getId() + ";")) existe = true;
                if (!existe) respuesta.add("MANUAL-" + atraccion.getId() + ";" + atraccion.getId() + ";" + atraccion.getNombre() + ";atendida=false");
            }
        }
        return ApiResponse.ok(respuesta);
    }

    /**
     * Método que devuelve una lista de alertas climáticas registradas en el historial, incluyendo detalles como el ID de la alerta, el tipo de clima, el número de atracciones afectadas, si la alerta está activa y la fecha y hora de la alerta.
     * @return
     */
    @GetMapping("/clima")
    public ApiResponse<List<String>> clima() {
        List<String> respuesta = new ArrayList<>();
        for (AlertaClimatica alerta : appState.getServicioAlertas().getHistorialClima()) {
            respuesta.add(alerta.getId() + ";" + alerta.getTipo() + ";afectadas=" + alerta.getAtraccionesAfectadas().size() + ";activa=" + alerta.isActiva() + ";fecha=" + alerta.getFechaHora());
        }
        return ApiResponse.ok(respuesta);
    }
}

