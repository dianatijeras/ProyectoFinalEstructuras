package techpark.servicios.colas;

import techpark.enums.EstadoAtraccion;
import techpark.enums.TipoNotif;
import techpark.model.eventos.Notificacion;
import techpark.model.parque.Atraccion;
import techpark.model.parque.Parque;
import techpark.model.tickets.EntradaEnCola;
import techpark.model.tickets.Ticket;
import techpark.model.usuarios.Visitante;
import techpark.utilidades.GeneradorId;


import java.util.List;

/**
 * ServicioColas es la clase encargada de gestionar las colas virtuales de las atracciones del parque,
 * permitiendo a los visitantes unirse a las colas y a los operadores llamar al siguiente visitante.
 */
public class ServicioColas {
    private Parque parque;

    public ServicioColas() {}

    /**
     * Constructor para crear un nuevo servicio de colas, con una referencia al parque para poder registrar notificaciones globales.
     * @param parque
     */
    public ServicioColas(Parque parque) {
        this.parque = parque;
    }

    /**
     * Permite a un visitante unirse a la cola virtual de una atracción, verificando que el visitante tenga un ticket activo y que la atracción esté activa.
     * @param visitante
     * @param atraccion
     * @return
     */
    public String unirseACola(Visitante visitante, Atraccion atraccion) {
        if (visitante == null || atraccion == null) return "Datos incompletos";
        if (visitante.isEnCola()) return "El visitante ya esta en otra cola";
        Ticket ticket = visitante.getTicketActivo();
        if (ticket == null || !ticket.estaActivo()) return "El visitante no tiene ticket activo";
        if (atraccion.getEstado() != EstadoAtraccion.ACTIVA) return "No puede unirse: atraccion no activa";
        atraccion.getColaVirtual().insertar(new EntradaEnCola(visitante, ticket));
        atraccion.actualizarTiempoEspera();
        visitante.setEnCola(true);
        int posicion = calcularPosicion(atraccion, visitante.getDocumento());
        notificarUnionCola(visitante, atraccion, posicion);
        return "Visitante agregado a la cola de " + atraccion.getNombre() + ". Posicion aproximada: " + posicion;
    }

    /**
     * Permite a un operador llamar al siguiente visitante en la cola virtual de una atracción, actualizando el tiempo de espera estimado y notificando al visitante que ha sido llamado.
     * @param atraccion
     * @return
     */
    public EntradaEnCola llamarSiguiente(Atraccion atraccion) {
        EntradaEnCola entrada = atraccion.getColaVirtual().extraer();
        atraccion.actualizarTiempoEspera();
        if (entrada != null) entrada.getVisitante().setEnCola(false);
        return entrada;
    }

    /**
     * Calcula la posición aproximada de un visitante en la cola virtual de una atracción, ordenando las entradas por prioridad y hora de ingreso.
     * @param atraccion
     * @param documentoVisitante
     * @return
     */
    private int calcularPosicion(Atraccion atraccion, String documentoVisitante) {
        List<EntradaEnCola> entradas = atraccion.getColaVirtual().comoLista();
        entradas.sort(EntradaEnCola::compareTo);
        for (int i = 0; i < entradas.size(); i++) {
            if (entradas.get(i).getVisitante().getDocumento().equalsIgnoreCase(documentoVisitante)) return i + 1;
        }
        return entradas.size();
    }

    /**
     * Notifica al visitante que se ha unido a la cola de una atracción, indicando su posición aproximada en la cola.
     * La notificación se registra como global en el parque para que pueda ser consultada por el visitante.
     * @param visitante
     * @param atraccion
     * @param posicion
     */
    private void notificarUnionCola(Visitante visitante, Atraccion atraccion, int posicion) {
        String mensaje = "Te uniste a la cola de " + atraccion.getNombre() + ". Posicion aproximada: " + posicion;
        Notificacion notificacion = new Notificacion(GeneradorId.generarId("NOT-"), mensaje, TipoNotif.COLA);
        notificacion.agregarDestinatario(visitante);
        if (parque != null) parque.registrarNotificacionGlobal(notificacion);
    }
}

