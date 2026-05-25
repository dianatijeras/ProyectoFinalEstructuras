package techpark.model.eventos;

import techpark.model.parque.Atraccion;

import java.time.LocalDateTime;

/**
 * Representa un incidente operativo que ocurre en una atracción del parque, como una falla técnica, un accidente o cualquier situación que requiera atención inmediata.
 * Permite marcar el incidente como resuelto y registrar la solución aplicada.
 */
public class IncidenteOperativo {
    private String id;
    private Atraccion atraccion;
    private String descripcion;
    private String gravedad;
    private LocalDateTime fechaHora;
    private boolean resuelto;
    private String solucion;

    /**
     * Constructor de la clase IncidenteOperativo.
     * El incidente se establece como no resuelto por defecto.
     * @param id
     * @param atraccion
     * @param descripcion
     * @param gravedad
     */
    public IncidenteOperativo(String id, Atraccion atraccion, String descripcion, String gravedad) {
        this(id, atraccion, descripcion, gravedad, LocalDateTime.now());
    }

    /**
     * Constructor de la clase IncidenteOperativo.
     * @param id
     * @param atraccion
     * @param descripcion
     * @param gravedad
     * @param fechaHora
     */
    public IncidenteOperativo(String id, Atraccion atraccion, String descripcion, String gravedad, LocalDateTime fechaHora) {
        if (atraccion == null) throw new IllegalArgumentException("Debe indicar la atraccion del incidente");
        if (descripcion == null || descripcion.isBlank()) throw new IllegalArgumentException("Debe indicar la descripcion del incidente");
        this.id = id;
        this.atraccion = atraccion;
        this.descripcion = descripcion;
        this.gravedad = (gravedad == null || gravedad.isBlank()) ? "MEDIA" : gravedad.toUpperCase();
        this.fechaHora = fechaHora == null ? LocalDateTime.now() : fechaHora;
    }

    /**
     * Marca este incidente operativo como resuelto, estableciendo su estado como true.
     */
    public void marcarResuelto() {
        this.resuelto = true;
    }

    /**
     * Marca este incidente operativo como resuelto y registra la solución aplicada. Si la solución proporcionada es nula o está en blanco, se establece un mensaje predeterminado indicando que el incidente ha sido resuelto.
     * @param solucion
     */
    public void resolver(String solucion) {
        this.resuelto = true;
        this.solucion = solucion == null || solucion.isBlank() ? "Incidente resuelto" : solucion;
    }

    /**
     * Metodo que devuelve el id del incidente operativo
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Metodo que devuelve la atracción asociada a este incidente operativo
     * @return
     */
    public Atraccion getAtraccion() {
        return atraccion;
    }

    /**
     * Metodo que devuelve la descripción del incidente operativo
     * @return
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Metodo que devuelve la gravedad del incidente operativo
     * @return
     */
    public String getGravedad() {
        return gravedad;
    }

    /**
     * Metodo que devuelve la fecha y hora en que ocurrió este incidente operativo
     * @return
     */
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    /**
     * Metodo que indica si este incidente operativo ha sido resuelto o no
     * @return
     */
    public boolean isResuelto() {
        return resuelto;
    }

    /**
     * Metodo que devuelve la solución aplicada para resolver este incidente operativo, o null si el incidente aún no ha sido resuelto.
     * @return
     */
    public String getSolucion() {
        return solucion;
    }
}

