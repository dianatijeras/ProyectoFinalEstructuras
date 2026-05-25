package techpark.api.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import techpark.api.dto.ApiResponse;
import techpark.api.dto.TicketRequest;
import techpark.api.dto.TicketResponse;
import techpark.api.mappers.ApiMapper;
import techpark.config.AppState;
import techpark.enums.TipoTicket;
import techpark.model.parque.Zona;
import techpark.model.tickets.Ticket;
import techpark.model.usuarios.Usuario;
import techpark.model.usuarios.Visitante;

/**
 * Controlador REST para la gestión de tickets en el parque. Permite a los visitantes comprar tickets para acceder a las zonas del parque.
 */
@RestController
@RequestMapping("/api/tickets")
public class TicketsRestController {
    private final AppState appState;

    /**
     * Constructor para inyectar el estado de la aplicación, que proporciona acceso a los servicios y datos relacionados con la gestión de tickets en el parque.
     * @param appState
     */
    public TicketsRestController(AppState appState) { this.appState = appState; }

    /**
     * Endpoint para comprar un ticket. Recibe una solicitud con el documento del visitante, el tipo de ticket, la zona a la que desea acceder y la cantidad de personas en caso de ser un ticket familiar.
     * @param request
     * @return
     */
    @PostMapping("/comprar")
    public ApiResponse<TicketResponse> comprar(@RequestBody TicketRequest request) {
        Visitante visitante = buscarVisitante(request.documentoVisitante());
        Zona zona = buscarZona(request.zonaId());
        TipoTicket tipo = TipoTicket.valueOf(request.tipoTicket().toUpperCase());
        int cantidadFamilia = request.cantidadPersonasFamilia() == null ? 1 : request.cantidadPersonasFamilia();
        Ticket ticket = appState.getServicioParque().venderTicket(visitante, tipo, zona, cantidadFamilia);
        appState.getServicioParque().registrarIngreso(zona);
        return ApiResponse.ok("Ticket comprado", ApiMapper.toTicketResponse(ticket));
    }

    /**
     * Método auxiliar para buscar un visitante por su documento.
     * @param documento
     * @return
     */
    private Visitante buscarVisitante(String documento) {
        Usuario usuario = appState.getParque().buscarUsuarioPorDocumento(documento);
        if (!(usuario instanceof Visitante visitante)) throw new IllegalArgumentException("Visitante no encontrado: " + documento);
        return visitante;
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
