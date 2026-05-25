package techpark.api.controllers;

import org.springframework.web.bind.annotation.*;
import techpark.api.dto.*;
import techpark.api.mappers.ApiMapper;
import techpark.config.AppState;
import techpark.enums.EstadoAtraccion;
import techpark.enums.TipoAtraccion;
import techpark.model.parque.Atraccion;
import techpark.model.parque.Zona;
import techpark.model.usuarios.Operador;
import techpark.model.usuarios.Usuario;
import techpark.model.usuarios.Visitante;

import java.util.ArrayList;
import java.util.List;

/**
 * AdminRestController es el controlador REST encargado de manejar las solicitudes relacionadas con la administración del parque, como la gestión de visitantes, operadores, zonas y atracciones.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminRestController {
    private final AppState appState;
    public AdminRestController(AppState appState) { this.appState = appState; }

    /**
     * Método para crear un nuevo visitante en el parque, recibe los datos necesarios en el cuerpo de la solicitud y devuelve la información del visitante creado.
     * @param request
     * @return
     */
    @PostMapping("/visitantes")
    public ApiResponse<VisitanteDTO> crearVisitante(@RequestBody CrearVisitanteRequest request) {
        Visitante visitante = appState.getServicioAdministracion().crearVisitante(request.nombre(), request.documento(), request.edad(), request.password(), request.estatura(), request.saldoVirtual());
        return ApiResponse.ok("Visitante creado", ApiMapper.toVisitanteDTO(visitante));
    }

    /**
     * Método para modificar la información de un visitante existente, recibe el documento del visitante a modificar como parámetro de ruta y los datos a actualizar en el cuerpo de la solicitud, devuelve la información actualizada del visitante.
     * @param documento
     * @param request
     * @return
     */
    @PatchMapping("/visitantes/{documento}")
    public ApiResponse<VisitanteDTO> modificarVisitante(@PathVariable String documento, @RequestBody ActualizarVisitanteRequest request) {
        Usuario usuario = appState.getParque().buscarUsuarioPorDocumento(documento);
        if (!(usuario instanceof Visitante visitante)) throw new IllegalArgumentException("Visitante no encontrado: " + documento);
        return ApiResponse.ok(ApiMapper.toVisitanteDTO(appState.getServicioAdministracion().modificarVisitante(visitante, request.nombre(), request.edad(), request.estatura(), request.saldoVirtual(), request.password())));
    }

    /**
     * Método para modificar la información de un operador existente, recibe el documento del operador a modificar como parámetro de ruta y los datos a actualizar en el cuerpo de la solicitud, devuelve la información actualizada del operador.
     * @param documento
     * @param request
     * @return
     */
    @PatchMapping("/operadores/{documento}")
    public ApiResponse<OperadorDTO> modificarOperador(@PathVariable String documento, @RequestBody ActualizarOperadorRequest request) {
        Usuario usuario = appState.getParque().buscarUsuarioPorDocumento(documento);
        if (!(usuario instanceof Operador operador)) throw new IllegalArgumentException("Operador no encontrado: " + documento);
        return ApiResponse.ok(ApiMapper.toOperadorDTO(appState.getServicioAdministracion().modificarOperador(operador, request.nombre(), request.edad(), request.password())));
    }

    /**
     * Método para remover la asignación de un operador a una zona, recibe el documento del operador como parámetro de ruta y devuelve la información actualizada del operador sin la zona asignada.
     * @param documento
     * @return
     */
    @PatchMapping("/operadores/{documento}/remover-zona")
    public ApiResponse<OperadorDTO> removerOperadorDeZona(@PathVariable String documento) {
        Usuario usuario = appState.getParque().buscarUsuarioPorDocumento(documento);
        if (!(usuario instanceof Operador operador)) throw new IllegalArgumentException("Operador no encontrado: " + documento);
        appState.getServicioAdministracion().removerOperadorDeZona(operador);
        return ApiResponse.ok(ApiMapper.toOperadorDTO(operador));
    }

    /**
     * Método para crear una nueva zona en el parque, recibe los datos necesarios en el cuerpo de la solicitud y devuelve la información de la zona creada.
     * @param request
     * @return
     */
    @PostMapping("/zonas")
    public ApiResponse<ZonaDTO> crearZona(@RequestBody CrearZonaRequest request) {
        Zona zona = appState.getServicioAdministracion().crearZona(request.id(), request.nombre(), request.capacidadMaxima(), request.disponible());
        return ApiResponse.ok("Zona creada", ApiMapper.toZonaDTO(zona));
    }

    /**
     * Método para modificar la información de una zona existente, recibe el id de la zona a modificar como parámetro de ruta y los datos a actualizar en el cuerpo de la solicitud, devuelve la información actualizada de la zona.
     * @param id
     * @param request
     * @return
     */
    @PatchMapping("/zonas/{id}")
    public ApiResponse<ZonaDTO> modificarZona(@PathVariable String id, @RequestBody ActualizarZonaRequest request) {
        Zona zona = appState.getServicioAdministracion().modificarZona(buscarZona(id), request.nombre(), request.capacidadMaxima(), request.disponible());
        return ApiResponse.ok("Zona actualizada", ApiMapper.toZonaDTO(zona));
    }

    /**
     * Método para crear un nuevo operador en el parque, recibe los datos necesarios en el cuerpo de la solicitud y devuelve la información del operador creado. Si se proporciona una zonaId válida, el operador se asignará automáticamente a esa zona.
     * @param request
     * @return
     */
    @PostMapping("/operadores")
    public ApiResponse<OperadorDTO> crearOperador(@RequestBody CrearOperadorRequest request) {
        Zona zona = request.zonaId() == null || request.zonaId().isBlank() ? null : buscarZona(request.zonaId());
        Operador operador = appState.getServicioAdministracion().crearOperador(request.nombre(), request.documento(), request.edad(), request.password(), zona);
        return ApiResponse.ok("Operador creado", ApiMapper.toOperadorDTO(operador));
    }

    /**
     * Método para listar todos los operadores registrados en el parque, devuelve una lista con la información de cada operador, incluyendo su zona asignada si tiene una.
     * @return
     */
    @GetMapping("/operadores")
    public ApiResponse<List<OperadorDTO>> listarOperadores() {
        List<OperadorDTO> respuesta = new ArrayList<>();
        for (Operador operador : appState.getOperadores()) respuesta.add(ApiMapper.toOperadorDTO(operador));
        return ApiResponse.ok(respuesta);
    }

    /**
     * Método para crear una nueva atracción en el parque, recibe los datos necesarios en el cuerpo de la solicitud y devuelve la información de la atracción creada. Si se proporciona una zonaId válida, la atracción se asignará automáticamente a esa zona. Además, si se proporcionan aristas, se agregarán al mapa del parque conectando esta atracción con las atracciones destino indicadas.
     * @param request
     * @return
     */
    @PostMapping("/atracciones")
    public ApiResponse<AtraccionDTO> crearAtraccion(@RequestBody CrearAtraccionRequest request) {
        Zona zona = buscarZona(request.zonaId());
        TipoAtraccion tipo = TipoAtraccion.valueOf(request.tipo().toUpperCase());
        EstadoAtraccion estado = request.estadoInicial() == null || request.estadoInicial().isBlank() ? EstadoAtraccion.ACTIVA : EstadoAtraccion.valueOf(request.estadoInicial().toUpperCase());
        Atraccion atraccion = appState.getServicioAdministracion().crearAtraccion(request.id(), request.nombre(), tipo, zona, request.capacidadMaximaPorCiclo(), request.alturaMinima(), request.edadMinima(), request.costoAdicional(), estado, request.tiempoEstimadoEspera() == null ? 0 : request.tiempoEstimadoEspera());
        agregarAristas(request.aristas(), atraccion.getId());
        return ApiResponse.ok("Atraccion creada", ApiMapper.toAtraccionDTO(atraccion));
    }

    /**
     * Método para modificar la información de una atracción existente, recibe el id de la atracción a modificar como parámetro de ruta y los datos a actualizar en el cuerpo de la solicitud, devuelve la información actualizada de la atracción. Si se proporciona una zonaId válida, la atracción se reasignará automáticamente a esa zona. Además, si se proporcionan aristas, se agregarán al mapa del parque conectando esta atracción con las atracciones destino indicadas.
     * @param id
     * @param request
     * @return
     */
    @PatchMapping("/atracciones/{id}")
    public ApiResponse<AtraccionDTO> modificarAtraccion(@PathVariable String id, @RequestBody ActualizarAtraccionRequest request) {
        Atraccion atraccion = buscarAtraccion(id);
        TipoAtraccion tipo = request.tipo() == null || request.tipo().isBlank() ? null : TipoAtraccion.valueOf(request.tipo().toUpperCase());
        EstadoAtraccion estado = request.estado() == null || request.estado().isBlank() ? null : EstadoAtraccion.valueOf(request.estado().toUpperCase());
        Zona zona = request.zonaId() == null || request.zonaId().isBlank() ? null : buscarZona(request.zonaId());
        Atraccion actualizada = appState.getServicioAdministracion().modificarAtraccion(atraccion, request.nombre(), tipo, zona, request.capacidadMaximaPorCiclo(), request.alturaMinima(), request.edadMinima(), request.costoAdicional(), estado, request.motivoCierre(), request.tiempoEstimadoEspera());
        agregarAristas(request.aristas(), actualizada.getId());
        return ApiResponse.ok("Atraccion actualizada", ApiMapper.toAtraccionDTO(actualizada));
    }

    /**
     * Método para asignar un operador a una zona específica, recibe el documento del operador y el id de la zona en el cuerpo de la solicitud, devuelve la información actualizada del operador con la zona asignada.
     * @param request
     * @return
     */
    @PatchMapping("/operadores/asignar-zona")
    public ApiResponse<OperadorDTO> asignarZona(@RequestBody AsignarOperadorRequest request) {
        Operador operador = buscarOperador(request.documentoOperador());
        Zona zona = buscarZona(request.zonaId());
        appState.getServicioAdministracion().asignarOperadorAZona(operador, zona);
        return ApiResponse.ok("Operador asignado a zona", ApiMapper.toOperadorDTO(operador));
    }

    /**
     * Método para asignar un operador a una atracción específica, recibe el documento del operador y el id de la atracción en el cuerpo de la solicitud, devuelve la información actualizada del operador con la atracción asignada.
     * @param request
     * @return
     */
    @PatchMapping("/operadores/asignar-atraccion")
    public ApiResponse<OperadorAsignacionDTO> asignarAtraccion(@RequestBody AsignarOperadorRequest request) {
        Operador operador = buscarOperador(request.documentoOperador());
        Atraccion atraccion = buscarAtraccion(request.idAtraccion());
        appState.getServicioAdministracion().asignarOperadorAAtraccion(operador, atraccion);
        return ApiResponse.ok("Operador asignado a atraccion", new OperadorAsignacionDTO(operador.getDocumento(), operador.getNombre(), atraccion.getZona().getId(), atraccion.getZona().getNombre(), atraccion.getId(), atraccion.getNombre()));
    }

    /**
     * Método para obtener una lista de los usuarios activos en el parque, es decir, aquellos visitantes que tienen un ticket activo o aquellos operadores que están asignados a una zona o atracción. Se puede aplicar un filtro de búsqueda por nombre o documento utilizando el parámetro de consulta "q". Devuelve una lista con la información de cada usuario activo, incluyendo su estado actual en el parque.
     * @param q
     * @return
     */
    @GetMapping("/usuarios-activos")
    public ApiResponse<List<UsuarioActivoDTO>> usuariosActivos(@RequestParam(defaultValue = "") String q) {
        appState.verificarExpiracionTickets();
        String filtro = q == null ? "" : q.toLowerCase();
        List<UsuarioActivoDTO> respuesta = new ArrayList<>();
        for (Usuario usuario : appState.getParque().getCatalogoUsuarios().inorden()) {
            boolean coincide = usuario.getDocumento().toLowerCase().contains(filtro) || usuario.getNombre().toLowerCase().contains(filtro);
            if (!coincide) continue;
            if (usuario instanceof Visitante visitante) {
                boolean activo = visitante.getTicketActivo() != null && visitante.getTicketActivo().estaActivo();
                if (activo || filtro.length() > 0) {
                    respuesta.add(new UsuarioActivoDTO(
                            visitante.getId(),
                            visitante.getNombre(),
                            visitante.getDocumento(),
                            visitante.getRol().name(),
                            activo,
                            visitante.getTicketActivo() == null ? null : visitante.getTicketActivo().getTipo().name(),
                            visitante.getNombreUbicacionActual(),
                            visitante.getSaldoVirtual()
                    ));
                }
            } else {
                respuesta.add(new UsuarioActivoDTO(usuario.getId(), usuario.getNombre(), usuario.getDocumento(), usuario.getRol().name(), false, null, null, null));
            }
        }
        return ApiResponse.ok(respuesta);
    }

    /**
     * Método para buscar usuarios en el parque, se puede aplicar un filtro de búsqueda por nombre o documento utilizando el parámetro de consulta "q". Devuelve una lista con la información de cada usuario que coincide con el filtro, incluyendo su rol y estado actual en el parque.
     * @param q
     * @return
     */
    @GetMapping("/buscar/usuarios")
    public ApiResponse<List<Object>> buscarUsuarios(@RequestParam(defaultValue = "") String q) {
        List<Object> respuesta = new ArrayList<>();
        for (Usuario usuario : appState.getServicioAdministracion().buscarUsuarios(q)) {
            if (usuario instanceof Visitante visitante) respuesta.add(ApiMapper.toVisitanteDTO(visitante));
            else if (usuario instanceof Operador operador) respuesta.add(ApiMapper.toOperadorDTO(operador));
        }
        return ApiResponse.ok(respuesta);
    }

    /**
     * Método para buscar zonas en el parque, se puede aplicar un filtro de búsqueda por nombre o id utilizando el parámetro de consulta "q". Devuelve una lista con la información de cada zona que coincide con el filtro, incluyendo su capacidad máxima y disponibilidad actual.
     * @param q
     * @return
     */
    @GetMapping("/buscar/zonas")
    public ApiResponse<List<ZonaDTO>> buscarZonas(@RequestParam(defaultValue = "") String q) {
        List<ZonaDTO> respuesta = new ArrayList<>();
        for (Zona zona : appState.getServicioAdministracion().buscarZonas(q)) respuesta.add(ApiMapper.toZonaDTO(zona));
        return ApiResponse.ok(respuesta);
    }

    /**
     * Método para buscar atracciones en el parque, se puede aplicar un filtro de búsqueda por nombre o id utilizando el parámetro de consulta "q". Devuelve una lista con la información de cada atracción que coincide con el filtro, incluyendo su tipo, estado actual y zona asignada.
     * @param q
     * @return
     */
    @GetMapping("/buscar/atracciones")
    public ApiResponse<List<AtraccionDTO>> buscarAtracciones(@RequestParam(defaultValue = "") String q) {
        List<AtraccionDTO> respuesta = new ArrayList<>();
        for (Atraccion atraccion : appState.getServicioAdministracion().buscarAtracciones(q)) respuesta.add(ApiMapper.toAtraccionDTO(atraccion));
        return ApiResponse.ok(respuesta);
    }

    /**
     * Método privado para agregar aristas al mapa del parque, recibe una lista de aristas a agregar y el id de la atracción origen. Para cada arista, se verifica que tenga un destino válido y un peso positivo, luego se busca la atracción destino para asegurarse de que exista y finalmente se agrega la arista al mapa conectando la atracción origen con la atracción destino utilizando el peso indicado.
     * @param aristas
     * @param idOrigen
     */
    private void agregarAristas(List<AristaAtraccionRequest> aristas, String idOrigen) {
        if (aristas == null) return;
        for (AristaAtraccionRequest arista : aristas) {
            if (arista == null || arista.idDestino() == null || arista.idDestino().isBlank() || arista.peso() <= 0) continue;
            buscarAtraccion(arista.idDestino());
            appState.getParque().getMapa().agregarArista(idOrigen, arista.idDestino(), arista.peso());
        }
    }

    /**
     * Método privado para buscar una zona específica por su id en la lista de zonas del parque, y devuelve la zona si se encuentra o lanza una excepción si no se encuentra.
     * @param id
     * @return
     */
    private Zona buscarZona(String id) {
        Zona zona = appState.getParque().buscarZona(id);
        if (zona == null) throw new IllegalArgumentException("Zona no encontrada: " + id);
        return zona;
    }

    /**
     * Método privado para buscar una atracción específica por su id en la lista de atracciones del parque, y devuelve la atracción si se encuentra o lanza una excepción si no se encuentra.
     * @param id
     * @return
     */
    private Atraccion buscarAtraccion(String id) {
        Atraccion atraccion = appState.getParque().buscarAtraccion(id);
        if (atraccion == null) throw new IllegalArgumentException("Atraccion no encontrada: " + id);
        return atraccion;
    }

    /**
     * Método privado para buscar un operador específico por su documento en la lista de usuarios del parque, y devuelve el operador si se encuentra o lanza una excepción si no se encuentra o si el documento no corresponde a un operador.
     * @param documento
     * @return
     */
    private Operador buscarOperador(String documento) {
        Usuario usuario = appState.getParque().buscarUsuarioPorDocumento(documento);
        if (!(usuario instanceof Operador operador)) throw new SecurityException("El documento no corresponde a un operador");
        return operador;
    }
}

