package techpark.api.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import techpark.api.dto.ApiResponse;
import techpark.api.dto.NotificacionGlobalDTO;
import techpark.api.mappers.ApiMapper;
import techpark.config.AppState;
import techpark.model.eventos.AlertaClimatica;
import techpark.model.eventos.AlertaMantenimiento;
import techpark.model.eventos.Notificacion;
import techpark.model.usuarios.Usuario;
import techpark.model.usuarios.Visitante;

/**
 * Controlador REST para gestionar las notificaciones del parque.
 */
@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionesRestController {
    private final AppState appState;
    public NotificacionesRestController(AppState appState) { this.appState = appState; }

    /**
     * Devuelve una lista de notificaciones para un usuario específico, identificando al usuario mediante su número de documento.
     * @param documento
     * @return
     */
    @GetMapping
    public ApiResponse<List<Object>> listar(@RequestParam(required = false) String documento) {
        appState.getServicioShows().listarShowsDelDia();
        Map<String, Object> respuesta = new LinkedHashMap<>();

        if (documento != null && !documento.isBlank()) {
            Usuario usuario = appState.getParque().buscarUsuarioPorDocumento(documento);
            if (usuario instanceof Visitante visitante) {
                for (Notificacion notificacion : visitante.getNotificaciones()) {
                    respuesta.put(notificacion.getId(), ApiMapper.toNotificacionDTO(notificacion));
                }
            }
        }

        for (Notificacion notificacion : appState.getParque().getNotificacionesGlobales()) {
            respuesta.putIfAbsent(notificacion.getId(), ApiMapper.toNotificacionDTO(notificacion));
        }

        for (AlertaMantenimiento alerta : appState.getServicioAlertas().getHistorialMantenimiento()) {
            if (!alerta.isAtendida()) {
                respuesta.putIfAbsent(alerta.getId(), new NotificacionGlobalDTO(alerta.getId(), "MANTENIMIENTO", "Mantenimiento", "La atraccion " + alerta.getAtraccion().getNombre() + " requiere revision", alerta.getFechaGeneracion().toString()));
            }
        }
        for (AlertaClimatica alerta : appState.getServicioAlertas().getHistorialClima()) {
            if (alerta.isActiva()) {
                respuesta.putIfAbsent(alerta.getId(), new NotificacionGlobalDTO(alerta.getId(), "CLIMA", "Alerta climatica", alerta.getTipo() + " afecta " + alerta.getAtraccionesAfectadas().size() + " atracciones", alerta.getFechaHora().toString()));
            }
        }
        return ApiResponse.ok(new ArrayList<>(respuesta.values()));
    }
}
