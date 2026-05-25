package techpark.api.controllers;

import org.springframework.web.bind.annotation.*;
import techpark.api.dto.ApiResponse;
import techpark.api.dto.ColaRequest;
import techpark.api.dto.ColaResponse;
import techpark.config.AppState;
import techpark.model.parque.Atraccion;
import techpark.model.reportes.ResultadoAcceso;
import techpark.model.tickets.EntradaEnCola;
import techpark.model.usuarios.Operador;
import techpark.model.usuarios.Usuario;
import techpark.model.usuarios.Visitante;

import java.util.List;

/**
 * ColasRestController es un controlador REST que maneja las solicitudes relacionadas con las colas virtuales de las atracciones del parque.
 */
@RestController
@RequestMapping("/api/colas")
public class ColasRestController {
    private final AppState appState;

    /**
     * Constructor de la clase ColasRestController.
     * @param appState
     */
    public ColasRestController(AppState appState) { this.appState = appState; }

    /**
     * Método que permite a un visitante unirse a la cola virtual de una atracción específica. La solicitud debe incluir el documento del visitante y el ID de la atracción. La respuesta incluye un mensaje de confirmación, el estado actual de la cola y la posición aproximada del visitante en la cola.
     * @param request
     * @return
     */
    @PostMapping("/unirse")
    public ApiResponse<ColaResponse> unirse(@RequestBody ColaRequest request) {
        Visitante visitante = buscarVisitante(request.documentoVisitante());
        Atraccion atraccion = buscarAtraccion(request.idAtraccion());
        String mensaje = appState.getServicioColas().unirseACola(visitante, atraccion);
        return ApiResponse.ok(mensaje, crearColaResponse(atraccion, posicion(atraccion, visitante.getDocumento()), mensaje));
    }

    /**
     * Método que permite a un operador procesar al siguiente visitante en la cola virtual de una atracción específica. La solicitud debe incluir el documento del operador y el ID de la atracción. La respuesta incluye el resultado del procesamiento, indicando si el siguiente visitante fue admitido, rechazado o si no hay visitantes en la cola.
     * @param request
     * @return
     */
    @PostMapping("/procesar-siguiente")
    public ApiResponse<String> procesarSiguiente(@RequestBody ColaRequest request) {
        Operador operador = buscarOperador(request.documentoOperador());
        Atraccion atraccion = buscarAtraccion(request.idAtraccion());
        ResultadoAcceso resultado = appState.getServicioOperador().procesarSiguienteEnCola(operador, atraccion);
        return ApiResponse.ok(resultado.toString());
    }

    /**
     * Método que devuelve el estado actual de la cola virtual de una atracción específica, incluyendo el número de personas en la cola y el tiempo estimado de espera. La solicitud debe incluir el ID de la atracción. La respuesta incluye el estado de la cola y un mensaje descriptivo.
     * @param idAtraccion
     * @return
     */
    @GetMapping("/{idAtraccion}")
    public ApiResponse<ColaResponse> estadoCola(@PathVariable String idAtraccion) {
        Atraccion atraccion = buscarAtraccion(idAtraccion);
        return ApiResponse.ok(crearColaResponse(atraccion, null, "Estado de cola"));
    }

    /**
     * Método que devuelve la posición aproximada de un visitante en la cola virtual de una atracción específica. La solicitud debe incluir el ID de la atracción y el documento del visitante. La respuesta incluye la posición aproximada del visitante en la cola y un mensaje descriptivo. Si el visitante no está en la cola, se indica que no se encuentra en la cola.
     * @param idAtraccion
     * @param documentoVisitante
     * @return
     */
    @GetMapping("/{idAtraccion}/posicion/{documentoVisitante}")
    public ApiResponse<ColaResponse> posicionVisitante(@PathVariable String idAtraccion, @PathVariable String documentoVisitante) {
        Atraccion atraccion = buscarAtraccion(idAtraccion);
        Integer posicion = posicion(atraccion, documentoVisitante);
        String mensaje = posicion == null ? "El visitante no esta en la cola" : "Posicion aproximada: " + posicion;
        return ApiResponse.ok(crearColaResponse(atraccion, posicion, mensaje));
    }

    /**
     * Método auxiliar que crea una instancia de ColaResponse con la información relevante de la atracción, la posición del visitante en la cola y un mensaje descriptivo.
     * @param atraccion
     * @param posicion
     * @param mensaje
     * @return
     */
    private ColaResponse crearColaResponse(Atraccion atraccion, Integer posicion, String mensaje) {
        return new ColaResponse(atraccion.getId(), atraccion.getNombre(), atraccion.getColaVirtual().getTamanio(), atraccion.getTiempoEstimadoEspera(), posicion, mensaje);
    }

    /**
     * Método auxiliar que calcula la posición aproximada de un visitante en la cola virtual de una atracción específica. La posición se determina ordenando las entradas en la cola por su tiempo de ingreso y buscando la posición del visitante según su documento.
     * @param atraccion
     * @param documentoVisitante
     * @return
     */
    private Integer posicion(Atraccion atraccion, String documentoVisitante) {
        List<EntradaEnCola> entradas = atraccion.getColaVirtual().comoLista();
        entradas.sort(EntradaEnCola::compareTo);
        for (int i = 0; i < entradas.size(); i++) {
            if (entradas.get(i).getVisitante().getDocumento().equalsIgnoreCase(documentoVisitante)) return i + 1;
        }
        return null;
    }

    /**
     * Método auxiliar que busca una atracción específica por su ID en el parque. Si la atracción no se encuentra, se lanza una excepción indicando que la atracción no fue encontrada.
     * @param id
     * @return
     */
    private Atraccion buscarAtraccion(String id) {
        Atraccion atraccion = appState.getParque().buscarAtraccion(id);
        if (atraccion == null) throw new IllegalArgumentException("Atraccion no encontrada: " + id);
        return atraccion;
    }

    /**
     * Método auxiliar que busca un visitante específico por su documento en el parque. Si el usuario encontrado no es un visitante o si no se encuentra ningún usuario con el documento proporcionado, se lanza una excepción indicando que el visitante no fue encontrado.
     * @param documento
     * @return
     */
    private Visitante buscarVisitante(String documento) {
        Usuario usuario = appState.getParque().buscarUsuarioPorDocumento(documento);
        if (!(usuario instanceof Visitante visitante)) throw new IllegalArgumentException("Visitante no encontrado: " + documento);
        return visitante;
    }

    /**
     * Método auxiliar que busca un operador específico por su documento en el parque.
     * @param documento
     * @return
     */
    private Operador buscarOperador(String documento) {
        Usuario usuario = appState.getParque().buscarUsuarioPorDocumento(documento);
        if (!(usuario instanceof Operador operador)) throw new SecurityException("El documento no corresponde a un operador");
        return operador;
    }
}

