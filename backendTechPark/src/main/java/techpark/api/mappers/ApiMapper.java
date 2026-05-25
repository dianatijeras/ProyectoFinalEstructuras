package techpark.api.mappers;

import techpark.api.dto.*;
import techpark.model.eventos.AlertaMantenimiento;
import techpark.model.eventos.IncidenteOperativo;
import techpark.model.eventos.Notificacion;
import techpark.model.eventos.RegistroVisita;
import techpark.model.parque.Atraccion;
import techpark.model.parque.Zona;
import techpark.model.reportes.ReporteJornada;
import techpark.model.tickets.Ticket;
import techpark.model.usuarios.Operador;
import techpark.model.usuarios.Usuario;
import techpark.model.usuarios.Visitante;

import java.util.ArrayList;
import java.util.List;

public final class ApiMapper {
    private ApiMapper() {}

    public static LoginResponse toLoginResponse(Usuario usuario) {
        return new LoginResponse(usuario.getId(), usuario.getNombre(), usuario.getDocumento(), usuario.getRol().name());
    }

    public static VisitanteDTO toVisitanteDTO(Visitante visitante) {
        String ticket = visitante.getTicketActivo() == null ? null : visitante.getTicketActivo().getId();
        String tipoTicket = visitante.getTicketActivo() == null ? null : visitante.getTicketActivo().getTipo().name();
        String estadoTicket = visitante.getTicketActivo() == null ? null : visitante.getTicketActivo().getEstado().name();
        String ubicacionId = visitante.getUltimaAtraccionVisitada() == null ? null : visitante.getUltimaAtraccionVisitada().getId();
        return new VisitanteDTO(visitante.getId(), visitante.getNombre(), visitante.getDocumento(), visitante.getEdad(), visitante.getEstatura(), visitante.getSaldoVirtual(), ticket, tipoTicket, estadoTicket, visitante.isEnCola(), visitante.getNombreUbicacionActual(), ubicacionId);
    }

    public static OperadorDTO toOperadorDTO(Operador operador) {
        Zona zona = operador.getZonaAsignada();
        List<String> atracciones = new ArrayList<>();
        if (zona != null) {
            for (Atraccion atraccion : zona.getAtracciones()) {
                if (atraccion.getOperadoresResponsables().contiene(operador)) {
                    atracciones.add(atraccion.getId() + " - " + atraccion.getNombre());
                }
            }
        }
        return new OperadorDTO(operador.getId(), operador.getNombre(), operador.getDocumento(), operador.getEdad(), zona == null ? null : zona.getId(), zona == null ? null : zona.getNombre(), atracciones);
    }

    public static ZonaDTO toZonaDTO(Zona zona) {
        return new ZonaDTO(zona.getId(), zona.getNombre(), zona.getCapacidadMaxima(), zona.getAforoActual(), zona.getAtracciones().size(), zona.getOperadoresAsignados().getTamanio(), zona.isDisponible());
    }

    public static AtraccionDTO toAtraccionDTO(Atraccion a) {
        Zona zona = a.getZona();
        return new AtraccionDTO(a.getId(), a.getNombre(), a.getTipo().name(), a.getCapacidadMaximaPorCiclo(), a.getAlturaMinima(), a.getEdadMinima(), a.getCostoAdicional(), a.getContadorAcumuladoVisitantes(), a.getVisitantesCicloActual(), a.getTiempoEstimadoEspera(), a.getEstado().name(), a.getMotivoCierre(), zona == null ? null : zona.getId(), zona == null ? null : zona.getNombre(), a.getColaVirtual().getTamanio(), a.getIncidentesOperativos(), a.getOperadoresResponsables().getTamanio());
    }

    public static TicketResponse toTicketResponse(Ticket ticket) {
        return new TicketResponse(ticket.getId(), ticket.getTipo().name(), ticket.getPrecio(), ticket.getEstado().name(), ticket.getPrioridad(), ticket.getVisitante().getDocumento());
    }

    public static RegistroVisitaDTO toRegistroVisitaDTO(RegistroVisita r) {
        return new RegistroVisitaDTO(r.getAtraccion().getId(), r.getAtraccion().getNombre(), r.getFechaHora().toString(), r.getTipoTicket().name(), r.getCostoDeducido());
    }

    public static NotificacionDTO toNotificacionDTO(Notificacion n) {
        return new NotificacionDTO(n.getId(), n.getMensaje(), n.getTipo().name(), n.getFechaHora().toString());
    }

    public static techpark.api.dto.IncidenteOperativoDTO toIncidenteDTO(IncidenteOperativo incidente) {
        return new techpark.api.dto.IncidenteOperativoDTO(
                incidente.getId(),
                incidente.getAtraccion().getId(),
                incidente.getAtraccion().getNombre(),
                incidente.getDescripcion(),
                incidente.getGravedad(),
                incidente.getFechaHora().toString(),
                incidente.isResuelto(),
                incidente.getSolucion()
        );
    }

    public static ReporteJornadaDTO toReporteDTO(ReporteJornada reporte) {
        List<AtraccionDTO> masVisitadas = new ArrayList<>();
        for (Atraccion a : reporte.getAtraccionesMasVisitadas()) masVisitadas.add(toAtraccionDTO(a));

        List<String> alertas = new ArrayList<>();
        for (AlertaMantenimiento a : reporte.getAlertasMantenimiento()) {
            alertas.add(a.getId() + " - " + a.getAtraccion().getNombre() + " - atendida=" + a.isAtendida());
        }

        List<AtraccionDTO> incidentes = new ArrayList<>();
        for (Atraccion a : reporte.getAtraccionesConMasIncidentes()) incidentes.add(toAtraccionDTO(a));

        List<techpark.api.dto.IncidenteOperativoDTO> incidentesDetalle = new ArrayList<>();
        for (IncidenteOperativo incidente : reporte.getIncidentesOperativos()) incidentesDetalle.add(toIncidenteDTO(incidente));

        return new ReporteJornadaDTO(reporte.getFecha().toString(), reporte.getIngresosDiarios(), masVisitadas, reporte.getTiemposPromedioEspera(), reporte.getCierresPorClima(), alertas, incidentes, incidentesDetalle);
    }
}
