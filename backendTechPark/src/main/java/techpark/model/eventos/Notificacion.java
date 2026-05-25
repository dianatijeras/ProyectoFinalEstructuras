package techpark.model.eventos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa una notificación que se envía a los visitantes del parque. Contiene un mensaje, un tipo de notificación, la fecha y hora de generación, y una lista de destinatarios (visitantes) a los que se les ha enviado la notificación.
 */
public class Notificacion {
    private String id;
    private String mensaje;
    private TipoNotif tipo;
    private LocalDateTime fechaHora;
    private final List<Visitante> destinatarios = new ArrayList<>();

    /**
     * Constructor de la clase Notificacion. Recibe un id, un mensaje y un tipo de notificación, y establece la fecha y hora de generación como el momento actual.
     * @param id
     * @param mensaje
     * @param tipo
     */
    public Notificacion(String id, String mensaje, TipoNotif tipo) {
        this.id = id; this.mensaje = mensaje; this.tipo = tipo; this.fechaHora = LocalDateTime.now();
    }

    /**
     * Agrega un destinatario (visitante) a la lista de destinatarios de la notificación, y también agrega esta notificación a la lista de notificaciones del visitante.
     * @param v
     */
    public void agregarDestinatario(Visitante v) {
        destinatarios.add(v); v.agregarNotificacion(this);
    }

    /**
     * Método getter para obtener el id de la notificación.
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Método getter para obtener el mensaje de la notificación.
     * @return
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Método getter para obtener el tipo de la notificación.
     * @return
     */
    public TipoNotif getTipo() {
        return tipo;
    }

    /**
     * Método getter para obtener la fecha y hora de generación de la notificación.
     * @return
     */
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    /**
     * Método getter para obtener la lista de destinatarios (visitantes) a los que se les ha enviado la notificación.
     * @return
     */
    public List<Visitante> getDestinatarios() {
        return destinatarios;
    }
}
