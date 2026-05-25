package techpark.model.eventos;

import techpark.enums.TipoTicket;
import techpark.model.parque.Atraccion;

import java.time.LocalDateTime;

/**
 * Registro de una visita a una atracción.
 */
public class RegistroVisita {
    private Atraccion atraccion;
    private LocalDateTime fechaHora;
    private TipoTicket tipoTicket;
    private double costoDeducido;

    /**
     * Constructor de la clase RegistroVisita.
     * @param atraccion
     * @param tipoTicket
     * @param costoDeducido
     */
    public RegistroVisita(Atraccion atraccion, TipoTicket tipoTicket, double costoDeducido) {
        this(atraccion, tipoTicket, costoDeducido, LocalDateTime.now());
    }

    /**
     * Constructor de la clase RegistroVisita.
     * @param atraccion
     * @param tipoTicket
     * @param costoDeducido
     * @param fechaHora
     */
    public RegistroVisita(Atraccion atraccion, TipoTicket tipoTicket, double costoDeducido, LocalDateTime fechaHora) {
        this.atraccion = atraccion;
        this.tipoTicket = tipoTicket;
        this.costoDeducido = costoDeducido;
        this.fechaHora = fechaHora == null ? LocalDateTime.now() : fechaHora;
    }

    /**
     * Método getter para obtener la atracción visitada.
     * @return
     */
    public Atraccion getAtraccion() {
        return atraccion;
    }

    /**
     * Método getter para obtener la fecha y hora de la visita.
     * @return
     */
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    /**
     * Método getter para obtener el tipo de ticket utilizado en la visita.
     * @return
     */
    public TipoTicket getTipoTicket() {
        return tipoTicket;
    }

    /**
     * Método getter para obtener el costo deducido por la visita a la atracción.
     * @return
     */
    public double getCostoDeducido() {
        return costoDeducido;
    }

    /**
     * Método toString para representar el registro de visita como una cadena de texto.
      * @return
     */
    public String toString() {
        return fechaHora + " - " + atraccion.getNombre() + " - " + tipoTicket;
    }
}

