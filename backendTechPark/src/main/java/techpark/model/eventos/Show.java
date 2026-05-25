package techpark.model.eventos;

import java.time.LocalDateTime;

/**
 * Clase que representa un show programado en el parque.
 */
public class Show {
    private String id;
    private String nombre;
    private Zona zona;
    private LocalDateTime horario;
    private int duracion;
    private EstadoShow estado = EstadoShow.PROGRAMADO;

    /**
     * Constructor de la clase Show, establece el estado del show como PROGRAMADO por defecto.
     * @param id
     * @param nombre
     * @param zona
     * @param horario
     * @param duracion
     */
    public Show(String id, String nombre, Zona zona, LocalDateTime horario, int duracion) {
        this.id=id; this.nombre=nombre; this.zona=zona; this.horario=horario; this.duracion=duracion;
    }

    /**
     * Método getter para obtener el id del show.
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Método getter para obtener el nombre del show.
     * @return
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Método getter para obtener la zona donde se realizará el show.
     * @return
     */
    public Zona getZona() {
        return zona;
    }

    /**
     * Método getter para obtener el horario programado del show.
     */
    public LocalDateTime getHorario() {
        return horario;
    }

    /**
     * Método getter para obtener la duración del show en minutos.
     * @return
     */
    public int getDuracion() {
        return duracion;
    }

    /**
     * Método getter para obtener el estado actual del show (PROGRAMADO, EN_CURSO o CANCELADO).
     * @return
     */
    public EstadoShow getEstado() {
        return estado;
    }

    /**
     * Método setter para actualizar el estado del show (PROGRAMADO, EN_CURSO o CANCELADO).
     * @param estado
     */
    public void setEstado(EstadoShow estado) {
        this.estado = estado;
    }
}