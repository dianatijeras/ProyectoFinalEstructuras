package techpark.model.eventos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa una alerta climática en el parque de diversiones. Contiene información sobre el tipo de clima, la fecha y hora de la alerta, si está activa o no, y las atracciones afectadas por la alerta.
 */
public class AlertaClimatica {
    private String id;
    private TipoClima tipo;
    private LocalDateTime fechaHora;
    private boolean activa;
    private final List<Atraccion> atraccionesAfectadas = new ArrayList<>();

    /**
     * Constructor de la clase AlertaClimatica. Crea una nueva alerta climática con el id y tipo especificados, y establece la fecha y hora actual como la fecha de la alerta. La alerta se establece como activa por defecto.
     * @param id
     * @param tipo
     */
    public AlertaClimatica(String id, TipoClima tipo) {
        this(id, tipo, LocalDateTime.now(), true);
    }

    /**
     * Constructor de la clase AlertaClimatica. Crea una nueva alerta climática con el id, tipo, fecha y hora, y estado de actividad especificados.
     * @param id
     * @param tipo
     * @param fechaHora
     * @param activa
     */
    public AlertaClimatica(String id, TipoClima tipo, LocalDateTime fechaHora, boolean activa) {
        this.id = id;
        this.tipo = tipo;
        this.fechaHora = fechaHora == null ? LocalDateTime.now() : fechaHora;
        this.activa = activa;
    }

    /**
     * Agrega una atracción a la lista de atracciones afectadas por esta alerta climática.
     * @param a
     */
    public void agregarAtraccionAfectada(Atraccion a) {
        atraccionesAfectadas.add(a);
    }

    /**
     * Desactiva la alerta climática, estableciendo su estado como inactiva.
     */
    public void desactivar() {
        activa = false;
    }

    /**
     * Metodo que devuelve el id de la alerta climática
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Metodo que devuelve el tipo de clima asociado a la alerta climática
     * @return
     */
    public TipoClima getTipo() {
        return tipo;
    }

    /**
     * Metodo que devuelve la fecha y hora en que se generó la alerta climática
     * @return
     */
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    /**
     * Metodo que indica si la alerta climática está activa o no
     * @return
     */
    public boolean isActiva() {
        return activa;
    }

    /**
     * Metodo que devuelve la lista de atracciones afectadas por esta alerta climática
     * @return
     */
    public List<Atraccion> getAtraccionesAfectadas() {
        return atraccionesAfectadas;
    }
}

