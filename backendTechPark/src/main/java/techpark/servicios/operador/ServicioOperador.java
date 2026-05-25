package techpark.servicios.operador;

import techpark.enums.EstadoAtraccion;
import techpark.enums.ResultadoRevision;
import techpark.enums.TipoNotif;
import techpark.model.eventos.Notificacion;
import techpark.model.eventos.RevisionTecnica;
import techpark.model.parque.Atraccion;
import techpark.model.reportes.ResultadoAcceso;
import techpark.model.tickets.EntradaEnCola;
import techpark.model.usuarios.Operador;
import techpark.model.usuarios.Visitante;
import techpark.servicios.acceso.ServicioAcceso;
import techpark.servicios.alertas.ServicioAlertas;
import techpark.servicios.colas.ServicioColas;
import techpark.utilidades.GeneradorId;

/**
 * Servicio que maneja las operaciones que un operador puede realizar, como procesar accesos desde la cola, cambiar el estado de las atracciones y registrar revisiones técnicas.
 * Este servicio se encarga de validar que el operador tenga autorización para operar en la zona de la atracción y de coordinar con los servicios de acceso, colas y alertas.
 */
public class ServicioOperador {
    private final ServicioAcceso servicioAcceso;
    private final ServicioColas servicioColas;
    private final ServicioAlertas servicioAlertas;
    private techpark.model.parque.Parque parque;

    /**
     * Constructor del servicio de operador, que recibe las dependencias necesarias para su funcionamiento.
     * @param servicioAcceso
     * @param servicioColas
     * @param servicioAlertas
     */
    public ServicioOperador(ServicioAcceso servicioAcceso, ServicioColas servicioColas, ServicioAlertas servicioAlertas) {
        this.servicioAcceso = servicioAcceso; this.servicioColas = servicioColas; this.servicioAlertas = servicioAlertas;
    }

    /**
     * Constructor del servicio de operador, que recibe las dependencias necesarias para su funcionamiento y una referencia al parque para registrar notificaciones globales.
     * @param servicioAcceso
     * @param servicioColas
     * @param servicioAlertas
     * @param parque
     */
    public ServicioOperador(ServicioAcceso servicioAcceso, ServicioColas servicioColas, ServicioAlertas servicioAlertas, techpark.model.parque.Parque parque) {
        this(servicioAcceso, servicioColas, servicioAlertas);
        this.parque = parque;
    }

    /**
     * Método privado que valida si el operador tiene autorización para operar en la zona de la atracción.
     * Si el operador no tiene una zona asignada o si la zona asignada no coincide con la zona de la atracción, se lanza una excepción de seguridad.
     * @param operador
     * @param atraccion
     */
    private void validarZona(Operador operador, Atraccion atraccion) {
        if (operador.getZonaAsignada() == null || atraccion.getZona() == null || !operador.getZonaAsignada().equals(atraccion.getZona())) {
            throw new SecurityException("El operador no esta autorizado para esta zona");
        }
    }

    /**
     * Procesa el siguiente visitante en la cola de una atracción, validando su acceso y registrando el resultado.
     * @param operador
     * @param atraccion
     * @return
     */
    public ResultadoAcceso procesarSiguienteEnCola(Operador operador, Atraccion atraccion) {
        validarZona(operador, atraccion);
        EntradaEnCola entrada = servicioColas.llamarSiguiente(atraccion);
        if (entrada == null) return ResultadoAcceso.denegado("No hay visitantes en cola");
        ResultadoAcceso resultado = servicioAcceso.validarYRegistrarAcceso(entrada.getVisitante(), atraccion);
        if (resultado.fueAutorizado() && atraccion.getVisitantesCicloActual() >= atraccion.getCapacidadMaximaPorCiclo()) {
            atraccion.reiniciarCiclo();
        }
        servicioAlertas.evaluarMantenimiento(parque, atraccion);
        if (resultado.fueAutorizado() && parque != null) {
            Notificacion notificacion = new Notificacion(GeneradorId.generarId("NOT-"), "Acceso autorizado a " + atraccion.getNombre() + " para " + entrada.getVisitante().getNombre(), TipoNotif.ESTADO_ATRACCION);
            parque.registrarNotificacionGlobal(notificacion);
            notificacion.agregarDestinatario(entrada.getVisitante());
        }
        return resultado;
    }

    /**
     * Cambia el estado de una atracción, validando que el operador tenga autorización para operar en la zona de la atracción.
     * @param operador
     * @param atraccion
     * @param estado
     * @param motivo
     */
    public void cambiarEstadoAtraccion(Operador operador, Atraccion atraccion, EstadoAtraccion estado, String motivo) {
        validarZona(operador, atraccion);
        atraccion.cambiarEstado(estado, motivo);
        if (parque != null) {
            TipoNotif tipoNotificacion = estado == EstadoAtraccion.EN_MANTENIMIENTO
                    ? TipoNotif.MANTENIMIENTO
                    : TipoNotif.ESTADO_ATRACCION;
            String mensaje = estado == EstadoAtraccion.EN_MANTENIMIENTO
                    ? "La atraccion " + atraccion.getNombre() + " entro en mantenimiento"
                    : "La atraccion " + atraccion.getNombre() + " cambio a estado " + estado;
            Notificacion notificacion = new Notificacion(GeneradorId.generarId("NOT-"), mensaje, tipoNotificacion);
            parque.registrarNotificacionGlobal(notificacion);
            for (Visitante visitante : parque.getVisitantesConTicketActivo()) notificacion.agregarDestinatario(visitante);
        }
    }

    /**
     * Registra una revisión técnica para una atracción, validando que el operador tenga autorización para operar en la zona de la atracción.
     * @param operador
     * @param atraccion
     * @param descripcion
     * @param resultado
     * @return
     */
    public RevisionTecnica registrarRevisionTecnica(Operador operador, Atraccion atraccion, String descripcion, ResultadoRevision resultado) {
        validarZona(operador, atraccion);
        RevisionTecnica revision = new RevisionTecnica(GeneradorId.generarId("REV-"), atraccion, operador, descripcion, resultado);
        servicioAlertas.procesarRevisionTecnica(atraccion, resultado);
        if (parque != null) {
            Notificacion notificacion = new Notificacion(GeneradorId.generarId("NOT-"), "Revision tecnica " + resultado + " registrada para " + atraccion.getNombre(), TipoNotif.MANTENIMIENTO);
            parque.registrarNotificacionGlobal(notificacion);
            for (Visitante visitante : parque.getVisitantesConTicketActivo()) notificacion.agregarDestinatario(visitante);
        }
        return revision;
    }
}

