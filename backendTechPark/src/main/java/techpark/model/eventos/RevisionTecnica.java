package techpark.model.eventos;

import java.time.LocalDateTime;

/**
 * Representa una revisión técnica realizada a una atracción por un operador. Contiene información sobre la atracción revisada, el operador que realizó la revisión, la fecha y hora de la revisión, una descripción de la revisión y el resultado de la misma (aprobada o rechazada).
 */
public class RevisionTecnica {
    private String id;
    private Atraccion atraccion;
    private Operador operador;
    private LocalDateTime fechaHora;
    private String descripcion;
    private ResultadoRevision resultado;

    /**
     * Constructor de la clase RevisionTecnica. Recibe un id, una atracción, un operador, una descripción y un resultado, y establece la fecha y hora de la revisión como el momento actual.
     * @param id
     * @param atraccion
     * @param operador
     * @param descripcion
     * @param resultado
     */
    public RevisionTecnica(String id, Atraccion atraccion, Operador operador, String descripcion, ResultadoRevision resultado) {
        this.id = id; this.atraccion = atraccion; this.operador = operador; this.descripcion = descripcion; this.resultado = resultado; this.fechaHora = LocalDateTime.now();
    }

    /**
     * Método getter para obtener el id de la revisión técnica.
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Método getter para obtener la atracción que fue revisada.
     * @return
     */
    public Atraccion getAtraccion() {
        return atraccion;
    }

    /**
     * Método getter para obtener el operador que realizó la revisión técnica.
     * @return
     */
    public Operador getOperador() {
        return operador;
    }

    /**
     * Método getter para obtener la fecha y hora en que se realizó la revisión técnica.
     * @return
     */
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    /**
     * Método getter para obtener la descripción de la revisión técnica.
     * @return
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Método getter para obtener el resultado de la revisión técnica (aprobada o rechazada).
     * @return
     */
    public ResultadoRevision getResultado() {
        return resultado;
    }
}
