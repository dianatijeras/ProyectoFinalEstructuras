package techpark.controladores;

import techpark.enums.EstadoAtraccion;
import techpark.enums.ResultadoRevision;
import techpark.model.parque.Parque;
import techpark.model.reportes.ResultadoAcceso;
import techpark.model.usuarios.Operador;
import techpark.servicios.operador.ServicioOperador;

/**
 * Controlador para gestionar las operaciones relacionadas con los operadores del parque de diversiones.
 * Este controlador actúa como intermediario entre la capa de servicios y la capa de presentación (API REST).
 */
public class ControladorOperador {
    private final Parque parque;
    private final ServicioOperador servicioOperador;

    /**
     * Constructor del controlador
     * @param parque
     * @param servicioOperador
     */
    public ControladorOperador(Parque parque, ServicioOperador servicioOperador){
        this.parque = parque; this.servicioOperador = servicioOperador;
    }

    /**
     * Procesa el siguiente visitante en la cola de una atracción, verificando su acceso y registrando la visita si es permitido.
     * @param operador
     * @param idAtraccion
     * @return
     */
    public ResultadoAcceso procesarSiguienteEnCola(Operador operador, String idAtraccion){
        return servicioOperador.procesarSiguienteEnCola(operador, parque.buscarAtraccion(idAtraccion));
    }

    /**
     * Gestiona el estado de una atracción, permitiendo a un operador cambiar su estado (por ejemplo, de operativa a en mantenimiento) y registrar el motivo del cambio.
     * @param operador
     * @param idAtraccion
     * @param estado
     * @param motivo
     */
    public void gestionarEstadoAtraccion(Operador operador, String idAtraccion, EstadoAtraccion estado, String motivo){
        servicioOperador.cambiarEstadoAtraccion(operador, parque.buscarAtraccion(idAtraccion), estado, motivo);
    }

    /**
     * Registra una revisión técnica realizada por un operador a una atracción, incluyendo una descripción de la revisión y el resultado de la misma (por ejemplo, si la atracción pasó o no la revisión).
     * @param operador
     * @param idAtraccion
     * @param descripcion
     * @param resultado
     */
    public void registrarRevisionTecnica(Operador operador, String idAtraccion, String descripcion, ResultadoRevision resultado){
        servicioOperador.registrarRevisionTecnica(operador, parque.buscarAtraccion(idAtraccion), descripcion, resultado);
    }
}
