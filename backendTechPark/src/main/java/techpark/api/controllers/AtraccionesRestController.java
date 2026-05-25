package techpark.api.controllers;


import org.springframework.web.bind.annotation.*;
import techpark.api.dto.ApiResponse;
import techpark.api.dto.AtraccionDTO;
import techpark.api.dto.EstadoAtraccionRequest;
import techpark.api.dto.RevisionTecnicaRequest;
import techpark.api.mappers.ApiMapper;
import techpark.config.AppState;
import techpark.enums.EstadoAtraccion;
import techpark.enums.ResultadoRevision;
import techpark.model.eventos.RevisionTecnica;
import techpark.model.parque.Atraccion;
import techpark.model.usuarios.Operador;
import techpark.model.usuarios.Usuario;

import java.util.ArrayList;
import java.util.List;

/**
 * AtraccionesRestController es un controlador REST que maneja las solicitudes relacionadas con las atracciones del parque, como listar atracciones, buscar por ID, filtrar por estado, cambiar estado y registrar revisiones técnicas.
 */
@RestController
@RequestMapping("/api/atracciones")
public class AtraccionesRestController {
    private final AppState appState;

    /**
     * Constructor de la clase AtraccionesRestController.
     * @param appState
     */
    public AtraccionesRestController(AppState appState) { this.appState = appState; }

    /**
     * Método que devuelve una lista de todas las atracciones del parque, incluyendo su ID, nombre, estado actual y otros detalles relevantes. La respuesta es una lista de objetos AtraccionDTO que representan cada atracción.
     * @return
     */
    @GetMapping
    public ApiResponse<List<AtraccionDTO>> listar() {
        List<AtraccionDTO> respuesta = new ArrayList<>();
        for (Atraccion a : appState.getParque().getCatalogoAtracciones().inorden()) respuesta.add(ApiMapper.toAtraccionDTO(a));
        return ApiResponse.ok(respuesta);
    }

    /**
     * Método que devuelve los detalles de una atracción específica según su ID.
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ApiResponse<AtraccionDTO> buscar(@PathVariable String id) {
        return ApiResponse.ok(ApiMapper.toAtraccionDTO(buscarAtraccion(id)));
    }

    /**
     * Método que devuelve una lista de atracciones filtradas por su estado actual, como "operativa", "en mantenimiento" o "cerrada". La solicitud debe incluir el estado como un parámetro de ruta, y la respuesta es una lista de objetos AtraccionDTO que representan las atracciones que coinciden con el estado especificado.
     * @param estado
     * @return
     */
    @GetMapping("/estado/{estado}")
    public ApiResponse<List<AtraccionDTO>> porEstado(@PathVariable String estado) {
        EstadoAtraccion estadoEnum = EstadoAtraccion.valueOf(estado.toUpperCase());
        List<AtraccionDTO> respuesta = new ArrayList<>();
        for (Atraccion a : appState.getParque().getCatalogoAtracciones().inorden()) {
            if (a.getEstado() == estadoEnum) respuesta.add(ApiMapper.toAtraccionDTO(a));
        }
        return ApiResponse.ok(respuesta);
    }

    /**
     * Método que permite a un operador cambiar el estado de una atracción específica. La solicitud debe incluir el ID de la atracción como un parámetro de ruta y un objeto EstadoAtraccionRequest en el cuerpo de la solicitud
     * @param id
     * @param request
     * @return
     */
    @PatchMapping("/{id}/estado")
    public ApiResponse<AtraccionDTO> cambiarEstado(@PathVariable String id, @RequestBody EstadoAtraccionRequest request) {
        Atraccion atraccion = buscarAtraccion(id);
        Operador operador = buscarOperador(request.documentoOperador());
        EstadoAtraccion estado = EstadoAtraccion.valueOf(request.estado().toUpperCase());
        appState.getServicioOperador().cambiarEstadoAtraccion(operador, atraccion, estado, request.motivo());
        return ApiResponse.ok("Estado actualizado", ApiMapper.toAtraccionDTO(atraccion));
    }

    /**
     * Método que permite a un operador registrar una revisión técnica para una atracción específica.
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/{id}/revision")
    public ApiResponse<String> revision(@PathVariable String id, @RequestBody RevisionTecnicaRequest request) {
        Atraccion atraccion = buscarAtraccion(id);
        Operador operador = buscarOperador(request.documentoOperador());
        ResultadoRevision resultado = ResultadoRevision.valueOf(request.resultado().toUpperCase());
        RevisionTecnica revision = appState.getServicioOperador().registrarRevisionTecnica(operador, atraccion, request.descripcion(), resultado);
        return ApiResponse.ok("Revision registrada", revision.getId());
    }

    /**
     * Método auxiliar que busca una atracción por su ID en el parque. Si la atracción no se encuentra, se lanza una excepción IllegalArgumentException con un mensaje indicando que la atracción no fue encontrada.
     * @param id
     * @return
     */
    private Atraccion buscarAtraccion(String id) {
        Atraccion atraccion = appState.getParque().buscarAtraccion(id);
        if (atraccion == null) throw new IllegalArgumentException("Atraccion no encontrada: " + id);
        return atraccion;
    }

    /**
     * Método auxiliar que busca un operador por su documento en el parque. Si el usuario encontrado no es un operador, se lanza una excepción SecurityException con un mensaje indicando que el documento no corresponde a un operador.
     * @param documento
     * @return
     */
    private Operador buscarOperador(String documento) {
        Usuario usuario = appState.getParque().buscarUsuarioPorDocumento(documento);
        if (!(usuario instanceof Operador operador)) throw new SecurityException("El documento no corresponde a un operador");
        return operador;
    }
}
