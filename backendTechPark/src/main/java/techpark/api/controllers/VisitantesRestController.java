package techpark.api.controllers;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import techpark.api.dto.ApiResponse;
import techpark.api.dto.AtraccionDTO;
import techpark.api.dto.NotificacionDTO;
import techpark.api.dto.RegistroVisitaDTO;
import techpark.api.dto.RecargaSaldoRequest;
import techpark.api.dto.VisitanteDTO;
import techpark.api.mappers.ApiMapper;
import techpark.config.AppState;
import techpark.model.eventos.Notificacion;
import techpark.model.eventos.RegistroVisita;
import techpark.model.parque.Atraccion;
import techpark.model.usuarios.Usuario;
import techpark.model.usuarios.Visitante;

/**
 * Controlador REST para gestionar las operaciones relacionadas con los visitantes del parque.
 */
@RestController
@RequestMapping("/api/visitantes")
public class VisitantesRestController {
    private final AppState appState;

    /**
     * Constructor para inyectar el estado de la aplicación, que proporciona acceso a los servicios y datos relacionados con los visitantes del parque.
     * @param appState
     */
    public VisitantesRestController(AppState appState) { this.appState = appState; }

    /**
     * Devuelve una lista de todos los visitantes registrados en el parque, incluyendo su información básica como nombre, documento, saldo actual y número de visitas realizadas.
     * @return
     */
    @GetMapping
    public ApiResponse<List<VisitanteDTO>> listar() {
        appState.verificarExpiracionTickets();
        List<VisitanteDTO> respuesta = new ArrayList<>();
        for (Visitante v : appState.getVisitantes()) respuesta.add(ApiMapper.toVisitanteDTO(v));
        return ApiResponse.ok(respuesta);
    }

    /**
     * Devuelve los detalles de un visitante específico según su número de documento, incluyendo su información básica, historial de visitas, atracciones favoritas y notificaciones recibidas.
     * @param documento
     * @return
     */
    @GetMapping("/{documento}")
    public ApiResponse<VisitanteDTO> buscar(@PathVariable String documento) {
        appState.verificarExpiracionTickets();
        return ApiResponse.ok(ApiMapper.toVisitanteDTO(buscarVisitante(documento)));
    }

    /**
     * Devuelve el historial de visitas de un visitante específico, mostrando las atracciones visitadas, fechas y horas de cada visita.
     * @param documento
     * @return
     */
    @GetMapping("/{documento}/historial")
    public ApiResponse<List<RegistroVisitaDTO>> historial(@PathVariable String documento) {
        Visitante visitante = buscarVisitante(documento);
        List<RegistroVisitaDTO> respuesta = new ArrayList<>();
        for (RegistroVisita r : visitante.getHistorialVisitas()) respuesta.add(ApiMapper.toRegistroVisitaDTO(r));
        return ApiResponse.ok(respuesta);
    }

    /**
     * Devuelve la lista de atracciones favoritas de un visitante específico, mostrando las atracciones que ha marcado como favoritas para futuras visitas.
     * @param documento
     * @return
     */
    @GetMapping("/{documento}/favoritos")
    public ApiResponse<List<AtraccionDTO>> favoritos(@PathVariable String documento) {
        Visitante visitante = buscarVisitante(documento);
        List<AtraccionDTO> respuesta = new ArrayList<>();
        for (Atraccion a : visitante.getFavoritos().listar()) respuesta.add(ApiMapper.toAtraccionDTO(a));
        return ApiResponse.ok(respuesta);
    }

    /**
     * Permite a un visitante agregar una atracción a su lista de favoritos. La solicitud debe incluir el número de documento del visitante como un parámetro de ruta y el ID de la atracción que desea agregar como otro parámetro de ruta.
     * @param documento
     * @param idAtraccion
     * @return
     */
    @PostMapping("/{documento}/favoritos/{idAtraccion}")
    public ApiResponse<List<AtraccionDTO>> agregarFavorito(@PathVariable String documento, @PathVariable String idAtraccion) {
        Visitante visitante = buscarVisitante(documento);
        Atraccion atraccion = appState.getParque().buscarAtraccion(idAtraccion);
        if (atraccion == null) throw new IllegalArgumentException("Atraccion no encontrada: " + idAtraccion);
        visitante.agregarFavorito(atraccion);
        return favoritos(documento);
    }


    /**
     * Permite a un visitante recargar saldo en su cuenta para poder comprar tickets y acceder a las atracciones del parque.
     * @param documento
     * @param request
     * @return
     */
    @PatchMapping("/{documento}/saldo")
    public ApiResponse<VisitanteDTO> recargarSaldo(@PathVariable String documento, @RequestBody RecargaSaldoRequest request) {
        if (request.valor() <= 0) throw new IllegalArgumentException("El valor de recarga debe ser mayor que cero");
        Visitante visitante = buscarVisitante(documento);
        visitante.recargarSaldo(request.valor());
        return ApiResponse.ok("Saldo recargado", ApiMapper.toVisitanteDTO(visitante));
    }

    /**
     * Devuelve la lista de notificaciones recibidas por un visitante específico, mostrando los mensajes enviados por el parque relacionados con eventos, promociones, cambios en las atracciones o cualquier otra información relevante para el visitante.
     * @param documento
     * @return
     */
    @GetMapping("/{documento}/notificaciones")
    public ApiResponse<List<NotificacionDTO>> notificaciones(@PathVariable String documento) {
        Visitante visitante = buscarVisitante(documento);
        List<NotificacionDTO> respuesta = new ArrayList<>();
        for (Notificacion n : visitante.getNotificaciones()) respuesta.add(ApiMapper.toNotificacionDTO(n));
        return ApiResponse.ok(respuesta);
    }

    /**
     * Método auxiliar para buscar un visitante por su número de documento.
     * @param documento
     * @return
     */
    private Visitante buscarVisitante(String documento) {
        Usuario usuario = appState.getParque().buscarUsuarioPorDocumento(documento);
        if (!(usuario instanceof Visitante visitante)) throw new IllegalArgumentException("Visitante no encontrado: " + documento);
        return visitante;
    }
}
