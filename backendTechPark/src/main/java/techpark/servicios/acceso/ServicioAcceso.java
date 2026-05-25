package techpark.servicios.acceso;

import techpark.model.parque.Atraccion;
import techpark.model.reportes.ResultadoAcceso;
import techpark.model.tickets.Ticket;
import techpark.model.usuarios.Visitante;

/**
 * ServicioAcceso es la clase central que maneja la lógica de validación y registro de accesos a las atracciones del parque.
 * Se encarga de verificar que el visitante cumpla con todos los requisitos necesarios para acceder a una atracción
 */
public class ServicioAcceso {

    /**
     * Valida si un visitante puede acceder a una atracción específica y registra el acceso si es autorizado.
     * @param visitante
     * @param atraccion
     * @return
     */
    public ResultadoAcceso validarYRegistrarAcceso(Visitante visitante, Atraccion atraccion) {
        if (visitante == null || atraccion == null) return ResultadoAcceso.denegado("Datos incompletos");
        Ticket ticket = visitante.getTicketActivo();
        if (ticket == null || !ticket.estaActivo()) return ResultadoAcceso.denegado("Ticket no activo");
        if (atraccion.getEstado() != EstadoAtraccionEnum.ACTIVA) return ResultadoAcceso.denegado("La atraccion no esta activa");
        if (atraccion.getZona() != null && !atraccion.getZona().hayAforoDisponible()) return ResultadoAcceso.denegado("La zona no tiene aforo disponible");
        if (!atraccion.tieneOperadorResponsable()) return ResultadoAcceso.denegado("La atraccion no tiene operador responsable");
        if (!atraccion.hayCapacidadEnCiclo()) return ResultadoAcceso.denegado("La atraccion alcanzo la capacidad maxima del ciclo");
        if (visitante.getEdad() < atraccion.getEdadMinima()) return ResultadoAcceso.denegado("No cumple la edad minima");
        if (visitante.getEstatura() < atraccion.getAlturaMinima()) return ResultadoAcceso.denegado("No cumple la altura minima");

        double costo = 0;
        if (atraccion.getCostoAdicional() > 0 && ticket.getTipo() == TipoTicketEnum.GENERAL) {
            if (!visitante.descontarSaldo(atraccion.getCostoAdicional())) return ResultadoAcceso.denegado("Saldo virtual insuficiente");
            costo = atraccion.getCostoAdicional();
        }

        atraccion.incrementarVisitantes();
        if (atraccion.getZona() != null) atraccion.getZona().aumentarAforo();
        visitante.agregarHistorial(new RegistroVisita(atraccion, ticket.getTipo(), costo));
        visitante.setEnCola(false);
        return ResultadoAcceso.autorizado(costo);
    }
}

