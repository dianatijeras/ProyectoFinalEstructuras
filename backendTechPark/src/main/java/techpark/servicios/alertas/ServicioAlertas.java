package techpark.servicios.alertas;

import techpark.enums.*;
import techpark.estructuras.conjunto.SetArbol;
import techpark.model.eventos.AlertaClimatica;
import techpark.model.eventos.AlertaMantenimiento;
import techpark.model.eventos.Notificacion;
import techpark.model.parque.Atraccion;
import techpark.model.parque.Parque;
import techpark.model.tickets.EntradaEnCola;
import techpark.model.usuarios.Visitante;
import techpark.utilidades.GeneradorId;

import java.util.ArrayList;
import java.util.List;

/**
 * ServicioAlertas es responsable de gestionar las alertas de mantenimiento y climáticas en el parque de diversiones.
 * Proporciona métodos para evaluar el mantenimiento de las atracciones, activar y finalizar alertas climáticas, y notificar a los visitantes afectados.
 */
public class ServicioAlertas {
    private final ColaPrioridad<AlertaMantenimiento> colaMantenimiento = new ColaPrioridad<>();
    private final List<AlertaClimatica> historialClima = new ArrayList<>();
    private final List<AlertaMantenimiento> historialMantenimiento = new ArrayList<>();

    /**
     * Evalúa si una atracción requiere mantenimiento preventivo basado en su contador acumulado de visitantes.
     * Si la atracción alcanza el límite de visitantes y está activa, se cambia su estado a "En Mantenimiento", se genera una alerta de mantenimiento y se notifica a los visitantes del parque.
     * @param atraccion
     */
    public void evaluarMantenimiento(Atraccion atraccion) {
        evaluarMantenimiento(null, atraccion);
    }

    /**
     * Evalúa si una atracción requiere mantenimiento preventivo basado en su contador acumulado de visitantes.
     * Si la atracción alcanza el límite de visitantes y está activa, se cambia su estado a "En Mantenimiento", se genera una alerta de mantenimiento y se notifica a los visitantes del parque.
     * @param parque
     * @param atraccion
     */
    public void evaluarMantenimiento(Parque parque, Atraccion atraccion) {
        if (atraccion.requiereMantenimiento() && atraccion.getEstado() == EstadoAtraccion.ACTIVA) {
            atraccion.cambiarEstado(EstadoAtraccion.EN_MANTENIMIENTO, "Mantenimiento preventivo por 500 visitantes");
            AlertaMantenimiento alerta = new AlertaMantenimiento(GeneradorId.generarId("MAN-"), atraccion);
            colaMantenimiento.insertar(alerta);
            historialMantenimiento.add(alerta);
            if (parque != null) notificarMantenimiento(parque, atraccion, alerta);
        }
    }

    /**
     * Notifica a los visitantes del parque que una atracción ha alcanzado el límite de visitantes y ha entrado en mantenimiento preventivo.
     * @param parque
     * @param atraccion
     * @param alerta
     */
    private void notificarMantenimiento(Parque parque, Atraccion atraccion, AlertaMantenimiento alerta) {
        Notificacion notificacion = new Notificacion(GeneradorId.generarId("NOT-"), "La atraccion " + atraccion.getNombre() + " alcanzo el limite de visitantes y entro en mantenimiento preventivo", TipoNotifEnum.MANTENIMIENTO);
        parque.registrarNotificacionGlobal(notificacion);
        for (Visitante visitante : parque.getVisitantesConTicketActivo()) {
            notificacion.agregarDestinatario(visitante);
        }
    }

    /**
     * Activa una alerta climática en el parque debido a condiciones climáticas adversas, como lluvia o tormenta.
     * @param parque
     * @param tipo
     * @return
     */
    public AlertaClimatica activarAlertaClimatica(Parque parque, TipoClima tipo) {
        AlertaClimatica alerta = new AlertaClimatica(GeneradorId.generarId("CLI-"), tipo);
        Notificacion global = new Notificacion(GeneradorId.generarId("NOT-"), "Alerta climatica activa: " + tipo, TipoNotif.CLIMA);
        parque.registrarNotificacionGlobal(global);

        for (Atraccion a : parque.getCatalogoAtracciones().inorden()) {
            if (a.getTipo() == TipoAtraccion.ACUATICA || a.getTipo() == TipoAtraccion.MECANICA_ALTURA) {
                a.cambiarEstado(EstadoAtraccion.CERRADA, tipo.name());
                alerta.agregarAtraccionAfectada(a);
                notificarVisitantesAfectados(parque, a, "La atraccion " + a.getNombre() + " fue cerrada por " + tipo);
            }
        }
        for (Visitante visitante : parque.getVisitantesConTicketActivo()) {
            global.agregarDestinatario(visitante);
        }
        historialClima.add(alerta);
        return alerta;
    }

    /**
     * Finaliza una alerta climática, reactivando las atracciones afectadas que no requieren mantenimiento y notificando a los visitantes del parque sobre la finalización de la alerta.
     * @param parque
     * @param idAlerta
     * @return
     */
    public AlertaClimatica finalizarAlertaClimatica(Parque parque, String idAlerta) {
        AlertaClimatica alerta = buscarAlertaClimatica(idAlerta);
        if (alerta == null) throw new IllegalArgumentException("Alerta climatica no encontrada: " + idAlerta);
        if (!alerta.isActiva()) return alerta;

        alerta.desactivar();
        for (Atraccion atraccion : alerta.getAtraccionesAfectadas()) {
            boolean cerradaPorEstaAlerta = atraccion.getEstado() == EstadoAtraccion.CERRADA
                    && atraccion.getMotivoCierre() != null
                    && atraccion.getMotivoCierre().equalsIgnoreCase(alerta.getTipo().name());
            if (cerradaPorEstaAlerta && !atraccion.requiereMantenimiento()) {
                atraccion.cambiarEstado(EstadoAtraccion.ACTIVA, null);
            }
        }

        Notificacion notificacion = new Notificacion(GeneradorId.generarId("NOT-"), "La alerta climatica " + alerta.getTipo() + " fue finalizada", TipoNotifEnum.CLIMA);
        parque.registrarNotificacionGlobal(notificacion);
        for (Visitante visitante : parque.getVisitantesConTicketActivo()) notificacion.agregarDestinatario(visitante);
        return alerta;
    }

    /**
     * Busca una alerta climática en el historial de alertas climáticas del parque utilizando su ID.
     * Si se encuentra una alerta con el ID proporcionado, se devuelve esa alerta; de lo contrario, se devuelve null.
     * @param idAlerta
     * @return
     */
    private AlertaClimatica buscarAlertaClimatica(String idAlerta) {
        for (AlertaClimatica alerta : historialClima) {
            if (alerta.getId().equalsIgnoreCase(idAlerta)) return alerta;
        }
        return null;
    }

    /**
     * Notifica a los visitantes del parque que una atracción ha sido cerrada debido a una alerta climática, y limpia la cola virtual de la atracción para evitar que los visitantes sigan esperando en la fila.
     * @param parque
     * @param atraccion
     * @param mensaje
     */
    private void notificarVisitantesAfectados(Parque parque, Atraccion atraccion, String mensaje) {
        Notificacion notificacion = new Notificacion(GeneradorId.generarId("NOT-"), mensaje, TipoNotif.CLIMA);
        parque.registrarNotificacionGlobal(notificacion);
        SetArbol<Visitante> visitantesNotificados = new SetArbol<>();

        for (EntradaEnCola entrada : atraccion.getColaVirtual().comoLista()) {
            Visitante visitante = entrada.getVisitante();
            agregarDestinatarioSiNoExiste(notificacion, visitantesNotificados, visitante);
            visitante.setEnCola(false);
        }

        for (Visitante visitante : parque.getVisitantesConTicketActivo()) {
            agregarDestinatarioSiNoExiste(notificacion, visitantesNotificados, visitante);
        }

        atraccion.getColaVirtual().limpiar();
    }

    /**
     * Agrega un visitante como destinatario de una notificación si el visitante no es nulo y no ha sido notificado previamente.
     * @param notificacion
     * @param visitantesNotificados
     * @param visitante
     */
    private void agregarDestinatarioSiNoExiste(Notificacion notificacion, SetArbol<Visitante> visitantesNotificados, Visitante visitante) {
        if (visitante != null && visitantesNotificados.agregar(visitante.getDocumento(), visitante)) {
            notificacion.agregarDestinatario(visitante);
        }
    }

    /**
     * Procesa el resultado de una revisión técnica para una atracción específica.
     * Si el resultado de la revisión es satisfactorio, se reinicia el contador acumulado de visitantes de la atracción, se reinicia su ciclo operativo, se cambia su estado a "Activa" y se marcan como atendidas todas las alertas de mantenimiento asociadas a esa atracción en el historial de mantenimiento.
     * @param atraccion
     * @param resultado
     */
    public void procesarRevisionTecnica(Atraccion atraccion, ResultadoRevision resultado) {
        if (resultado == ResultadoRevision.SATISFACTORIA) {
            atraccion.setContadorAcumuladoVisitantes(0);
            atraccion.reiniciarCiclo();
            atraccion.cambiarEstado(EstadoAtraccion.ACTIVA, null);
            for (AlertaMantenimiento alerta : historialMantenimiento) {
                if (alerta.getAtraccion().equals(atraccion)) alerta.marcarAtendida();
            }
        }
    }


    /**
     * Registra una alerta climática precargada en el historial de alertas climáticas del parque.
     * @param alerta
     */
    public void registrarAlertaClimaticaPrecargada(AlertaClimatica alerta) {
        if (alerta != null && !historialClima.contains(alerta)) {
            historialClima.add(alerta);
        }
    }

    /**
     * Registra una alerta de mantenimiento precargada en el historial de alertas de mantenimiento del parque.
     * Si la alerta no ha sido atendida, también se inserta en la cola de mantenimiento para su seguimiento.
     * @param alerta
     */
    public void registrarAlertaMantenimientoPrecargada(AlertaMantenimiento alerta) {
        if (alerta != null && !historialMantenimiento.contains(alerta)) {
            historialMantenimiento.add(alerta);
            if (!alerta.isAtendida()) colaMantenimiento.insertar(alerta);
        }
    }

    /**
     * Metodo que devuelve la cola de mantenimiento del parque, que contiene las alertas de mantenimiento pendientes de atención.
     * @return
     */
    public ColaPrioridad<AlertaMantenimiento> getColaMantenimiento(){
        return colaMantenimiento;
    }

    /**
     * Metodo que devuelve el historial de alertas climáticas del parque, que contiene todas las alertas climáticas generadas, tanto activas como inactivas.
     * @return
     */
    public List<AlertaClimatica> getHistorialClima(){
        return historialClima;
    }

    /**
     * Metodo que devuelve el historial de alertas de mantenimiento del parque, que contiene todas las alertas de mantenimiento generadas, tanto atendidas como pendientes.
     * @return
     */
    public List<AlertaMantenimiento> getHistorialMantenimiento(){
        return historialMantenimiento;
    }
}

