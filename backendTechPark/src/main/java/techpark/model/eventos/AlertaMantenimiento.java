package techpark.model.eventos;

import java.time.LocalDateTime;

/**
 * AlertaMantenimiento representa una notificación de mantenimiento para una atracción específica en el parque.
 */
public class AlertaMantenimiento implements Comparable<AlertaMantenimiento> {
    private String id;
    private Atraccion atraccion;
    private LocalDateTime fechaGeneracion;
    private boolean atendida;

    /**
     * Constructor de la clase AlertaMantenimiento.
     * La alerta se establece como no atendida por defecto.
     * @param id
     * @param atraccion
     */
    public AlertaMantenimiento(String id, Atraccion atraccion) {
        this(id, atraccion, LocalDateTime.now(), false);
    }

    /**
     * Constructor de la clase AlertaMantenimiento.
     * @param id
     * @param atraccion
     * @param fechaGeneracion
     * @param atendida
     */
    public AlertaMantenimiento(String id, Atraccion atraccion, LocalDateTime fechaGeneracion, boolean atendida) {
        this.id = id;
        this.atraccion = atraccion;
        this.fechaGeneracion = fechaGeneracion == null ? LocalDateTime.now() : fechaGeneracion;
        this.atendida = atendida;
    }

    /**
     * Compara esta alerta de mantenimiento con otra basada en la fecha de generación.
     * Devuelve un valor negativo si esta alerta es anterior a la otra, un valor positivo si es posterior, o cero si ambas alertas tienen la misma fecha de generación.
     * @param otra the object to be compared.
     * @return
     */
    public int compareTo(AlertaMantenimiento otra) {
        return this.fechaGeneracion.compareTo(otra.fechaGeneracion);
    }

    /**
     * Marca esta alerta de mantenimiento como atendida, estableciendo su estado como true.
     */
    public void marcarAtendida() {
        atendida = true;
    }

    /**
     * Metodo que devuelve el id de la alerta de mantenimiento
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Metodo que devuelve la atracción asociada a esta alerta de mantenimiento
     * @return
     */
    public Atraccion getAtraccion() {
        return atraccion;
    }

    /**
     * Metodo que devuelve la fecha y hora en que se generó esta alerta de mantenimiento
     * @return
     */
    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    /**
     * Metodo que indica si esta alerta de mantenimiento ha sido atendida o no
     * @return
     */
    public boolean isAtendida() {
        return atendida;
    }

    /**
     * Metodo que devuelve una representación en cadena de esta alerta de mantenimiento, mostrando su id y el nombre de la atracción asociada.
     * @return
     */
    public String toString() {
        return "Alerta " + id + " - " + atraccion.getNombre();
    }
}

