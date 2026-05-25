package techpark.servicios.parque;

import techpark.enums.EstadoAtraccion;
import techpark.enums.TipoAtraccion;
import techpark.enums.TipoNotif;
import techpark.model.eventos.IncidenteOperativo;
import techpark.model.eventos.Notificacion;
import techpark.model.parque.Atraccion;
import techpark.model.parque.Parque;
import techpark.model.parque.Zona;
import techpark.model.tickets.EntradaEnCola;
import techpark.model.usuarios.Operador;
import techpark.model.usuarios.Usuario;
import techpark.model.usuarios.Visitante;
import techpark.utilidades.GeneradorId;

import java.util.ArrayList;
import java.util.List;

/**
 * clase encargada de gestionar las operaciones administrativas del parque de diversiones.
 */
public class ServicioAdministracion {
    private final Parque parque;
    private final List<Visitante> visitantes;
    private final List<Operador> operadores;
    private final ServicioParque servicioParque;

    /**
     * Constructor de la clase ServicioAdministracion.
     * @param parque
     * @param visitantes
     * @param operadores
     * @param servicioParque
     */
    public ServicioAdministracion(Parque parque, List<Visitante> visitantes, List<Operador> operadores, ServicioParque servicioParque) {
        this.parque = parque;
        this.visitantes = visitantes;
        this.operadores = operadores;
        this.servicioParque = servicioParque;
    }

    /**
     * Crea un nuevo visitante y lo registra en el parque.
     * @param nombre
     * @param documento
     * @param edad
     * @param password
     * @param estatura
     * @param saldoVirtual
     * @return
     */
    public Visitante crearVisitante(String nombre, String documento, int edad, String password, double estatura, double saldoVirtual) {
        if (parque.buscarUsuarioPorDocumento(documento) != null) throw new IllegalArgumentException("Ya existe un usuario con ese documento");
        if (edad < 0) throw new IllegalArgumentException("La edad del visitante no puede ser negativa");
        if (estatura < 0) throw new IllegalArgumentException("La estatura del visitante no puede ser negativa");
        if (saldoVirtual < 0) throw new IllegalArgumentException("El saldo virtual no puede ser negativo");
        Visitante visitante = new Visitante(GeneradorId.generarId("VIS-"), nombre, documento, edad, normalizarPassword(password), estatura, saldoVirtual, null);
        visitantes.add(visitante);
        parque.registrarUsuario(visitante);
        return visitante;
    }

    /**
     * Modifica los datos de un visitante existente.
     * Si algún campo es nulo o inválido, se mantiene el valor actual del visitante.
     * @param visitante
     * @param nombre
     * @param edad
     * @param estatura
     * @param saldoVirtual
     * @param password
     * @return
     */
    public Visitante modificarVisitante(Visitante visitante, String nombre, Integer edad, Double estatura, Double saldoVirtual, String password) {
        if (visitante == null) throw new IllegalArgumentException("Visitante invalido");
        visitante.actualizarPerfil(
                nombre == null || nombre.isBlank() ? visitante.getNombre() : nombre.trim(),
                edad == null ? visitante.getEdad() : edad,
                estatura == null ? visitante.getEstatura() : estatura,
                saldoVirtual == null ? visitante.getSaldoVirtual() : saldoVirtual
        );
        visitante.cambiarPassword(password);
        return visitante;
    }

    /**
     * Modifica los datos de un operador existente.
     * Si algún campo es nulo o inválido, se mantiene el valor actual del operador.
     * @param operador
     * @param nombre
     * @param edad
     * @param password
     * @return
     */
    public Operador modificarOperador(Operador operador, String nombre, Integer edad, String password) {
        if (operador == null) throw new IllegalArgumentException("Operador invalido");
        operador.actualizarDatosBasicos(
                nombre == null || nombre.isBlank() ? operador.getNombre() : nombre.trim(),
                edad == null ? operador.getEdad() : edad
        );
        operador.cambiarPassword(password);
        return operador;
    }

    /**
     * Crea una nueva zona y la agrega al parque.
     * @param id
     * @param nombre
     * @param capacidadMaxima
     * @param disponible
     * @return
     */
    public Zona crearZona(String id, String nombre, int capacidadMaxima, Boolean disponible) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("El id de la zona es obligatorio");
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre de la zona es obligatorio");
        if (capacidadMaxima <= 0) throw new IllegalArgumentException("La capacidad de la zona debe ser mayor que cero");
        if (parque.buscarZona(id) != null) throw new IllegalArgumentException("Ya existe una zona con ese identificador");
        Zona zona = new Zona(id.trim(), nombre.trim(), capacidadMaxima);
        if (disponible != null) zona.cambiarDisponibilidad(disponible);
        parque.agregarZona(zona);
        return zona;
    }

    /**
     * Modifica los datos de una zona existente.
     * Si algún campo es nulo o inválido, se mantiene el valor actual de la zona.
     * @param zona
     * @param nombre
     * @param capacidadMaxima
     * @param disponible
     * @return
     */
    public Zona modificarZona(Zona zona, String nombre, Integer capacidadMaxima, Boolean disponible) {
        if (zona == null) throw new IllegalArgumentException("Zona invalida");
        zona.actualizarDatos(
                nombre == null || nombre.isBlank() ? zona.getNombre() : nombre.trim(),
                capacidadMaxima == null ? zona.getCapacidadMaxima() : capacidadMaxima,
                disponible == null ? zona.isDisponible() : disponible
        );
        return zona;
    }

    /**
     * Crea un nuevo operador y lo registra en el parque.
     * Si se especifica una zona, también asigna el operador a esa zona.
     * @param nombre
     * @param documento
     * @param edad
     * @param password
     * @param zona
     * @return
     */
    public Operador crearOperador(String nombre, String documento, int edad, String password, Zona zona) {
        if (parque.buscarUsuarioPorDocumento(documento) != null) throw new IllegalArgumentException("Ya existe un usuario con ese documento");
        if (edad < 0) throw new IllegalArgumentException("La edad del operador no puede ser negativa");
        Operador operador = new Operador(GeneradorId.generarId("OPE-"), nombre, documento, edad, normalizarPassword(password));
        operadores.add(operador);
        parque.registrarUsuario(operador);
        if (zona != null) servicioParque.asignarOperador(operador, zona);
        return operador;
    }

    /**
     * Crea una nueva atracción y la asigna a una zona específica.
     * @param id
     * @param nombre
     * @param tipo
     * @param zona
     * @param capacidad
     * @param alturaMinima
     * @param edadMinima
     * @param costoAdicional
     * @param estado
     * @param tiempoEstimadoEspera
     * @return
     */
    public Atraccion crearAtraccion(String id, String nombre, TipoAtraccion tipo, Zona zona, int capacidad, double alturaMinima, int edadMinima, double costoAdicional, EstadoAtraccion estado, int tiempoEstimadoEspera) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("El id de la atraccion es obligatorio");
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre de la atraccion es obligatorio");
        if (parque.buscarAtraccion(id) != null) throw new IllegalArgumentException("Ya existe una atraccion con ese identificador");
        if (zona == null) throw new IllegalArgumentException("La zona es obligatoria");
        Atraccion atraccion = new Atraccion(id, nombre, tipo, capacidad, alturaMinima, edadMinima, costoAdicional);
        zona.agregarAtraccion(atraccion);
        for (Operador operador : zona.getOperadoresAsignados()) atraccion.asignarOperadorResponsable(operador);
        if (estado != null && estado != EstadoAtraccion.ACTIVA) atraccion.cambiarEstado(estado, estado == EstadoAtraccion.CERRADA ? "Creada cerrada" : "Creada en mantenimiento");
        atraccion.setTiempoEstimadoEspera(tiempoEstimadoEspera);
        parque.registrarAtraccion(atraccion);
        return atraccion;
    }

    /**
     * Modifica los datos de una atracción existente.
     * Si algún campo es nulo o inválido, se mantiene el valor actual de la atracción.
     * @param atraccion
     * @param nombre
     * @param tipo
     * @param nuevaZona
     * @param capacidad
     * @param alturaMinima
     * @param edadMinima
     * @param costoAdicional
     * @param estado
     * @param motivo
     * @param tiempoEstimadoEspera
     * @return
     */
    public Atraccion modificarAtraccion(Atraccion atraccion, String nombre, TipoAtraccion tipo, Zona nuevaZona, Integer capacidad, Double alturaMinima, Integer edadMinima, Double costoAdicional, EstadoAtraccion estado, String motivo, Integer tiempoEstimadoEspera) {
        if (atraccion == null) throw new IllegalArgumentException("Atraccion invalida");
        Zona zonaActual = atraccion.getZona();
        if (nuevaZona != null && zonaActual != null && nuevaZona != zonaActual) {
            zonaActual.getAtracciones().remove(atraccion);
            nuevaZona.agregarAtraccion(atraccion);
        }
        atraccion.actualizarDatos(
                nombre == null || nombre.isBlank() ? atraccion.getNombre() : nombre.trim(),
                tipo == null ? atraccion.getTipo() : tipo,
                capacidad == null ? atraccion.getCapacidadMaximaPorCiclo() : capacidad,
                alturaMinima == null ? atraccion.getAlturaMinima() : alturaMinima,
                edadMinima == null ? atraccion.getEdadMinima() : edadMinima,
                costoAdicional == null ? atraccion.getCostoAdicional() : costoAdicional,
                tiempoEstimadoEspera == null ? atraccion.getTiempoEstimadoEspera() : tiempoEstimadoEspera
        );
        if (estado != null) {
            String motivoFinal = estado == EstadoAtraccion.CERRADA && (motivo == null || motivo.isBlank()) ? "Cierre administrativo" : motivo;
            if (estado == EstadoAtraccion.EN_MANTENIMIENTO && (motivoFinal == null || motivoFinal.isBlank())) motivoFinal = "Mantenimiento administrativo";
            atraccion.cambiarEstado(estado, estado == EstadoAtraccion.ACTIVA ? null : motivoFinal);
        }
        return atraccion;
    }

    /**
     * Asigna un operador a una zona específica.
     * Si el operador ya estaba asignado a otra zona, se reasigna automáticamente a la nueva zona.
     * Además, se actualizan las asignaciones de operador responsable en todas las atracciones de la zona.
     * @param operador
     * @param zona
     */
    public void asignarOperadorAZona(Operador operador, Zona zona) {
        servicioParque.reasignarOperador(operador, zona);
        for (Atraccion atraccion : zona.getAtracciones()) atraccion.asignarOperadorResponsable(operador);
    }

    /**
     * Asigna un operador responsable a una atracción específica.
     * @param operador
     * @param atraccion
     */
    public void asignarOperadorAAtraccion(Operador operador, Atraccion atraccion) {
        if (atraccion.getZona() == null) throw new IllegalStateException("La atraccion no tiene zona");
        if (operador.getZonaAsignada() == null) servicioParque.asignarOperador(operador, atraccion.getZona());
        if (operador.getZonaAsignada() != atraccion.getZona()) throw new IllegalStateException("El operador no pertenece a la zona de la atraccion");
        atraccion.asignarOperadorResponsable(operador);
    }

    /**
     * Registra un nuevo incidente operativo para una atracción específica, cerrando la atracción afectada y notificando a los visitantes que tienen un ticket activo o están en la cola virtual de esa atracción.
     * @param atraccion
     * @param descripcion
     * @param gravedad
     * @return
     */
    public IncidenteOperativo registrarIncidente(Atraccion atraccion, String descripcion, String gravedad) {
        IncidenteOperativo incidente = new IncidenteOperativo(GeneradorId.generarId("INC-"), atraccion, descripcion, gravedad);
        atraccion.registrarIncidente();
        atraccion.cambiarEstado(EstadoAtraccion.CERRADA, "Cerrada por incidente operativo");
        parque.registrarIncidente(incidente);
        Notificacion notificacion = new Notificacion(GeneradorId.generarId("NOT-"), "Incidente " + incidente.getGravedad() + " registrado en " + atraccion.getNombre() + ": " + incidente.getDescripcion(), TipoNotif.INCIDENTE);
        parque.registrarNotificacionGlobal(notificacion);
        notificarVisitantesActivosYEnCola(notificacion, atraccion);
        return incidente;
    }

    /**
     * Envía una notificación a todos los visitantes que tienen un ticket activo o están en la cola virtual de la atracción afectada por un incidente operativo.
     * @param notificacion
     * @param atraccion
     */
    private void notificarVisitantesActivosYEnCola(Notificacion notificacion, Atraccion atraccion) {
        java.util.HashSet<String> documentos = new java.util.HashSet<>();
        for (Visitante visitante : parque.getVisitantesConTicketActivo()) {
            if (documentos.add(visitante.getDocumento())) notificacion.agregarDestinatario(visitante);
        }
        if (atraccion != null) {
            for (EntradaEnCola entrada : atraccion.getColaVirtual().comoLista()) {
                Visitante visitante = entrada.getVisitante();
                if (visitante != null && documentos.add(visitante.getDocumento())) notificacion.agregarDestinatario(visitante);
            }
        }
    }

    /**
     * Remueve la asignación de un operador a su zona actual, si tiene una asignada.
     * @param operador
     */
    public void removerOperadorDeZona(Operador operador) {
        if (operador == null) throw new IllegalArgumentException("Operador invalido");
        Zona zona = operador.getZonaAsignada();
        if (zona == null) return;
        zona.removerOperador(operador);
    }

    /**
     * Marca un incidente operativo como resuelto, registrando la solución aplicada.
     * @param idIncidente
     * @param solucion
     * @return
     */
    public IncidenteOperativo resolverIncidente(String idIncidente, String solucion) {
        for (IncidenteOperativo incidente : parque.getIncidentesOperativos()) {
            if (incidente.getId().equalsIgnoreCase(idIncidente)) {
                incidente.resolver(solucion);
                Notificacion notificacion = new Notificacion(GeneradorId.generarId("NOT-"), "Incidente resuelto en " + incidente.getAtraccion().getNombre() + ": " + incidente.getSolucion(), TipoNotif.INCIDENTE);
                parque.registrarNotificacionGlobal(notificacion);
                return incidente;
            }
        }
        throw new IllegalArgumentException("Incidente no encontrado: " + idIncidente);
    }

    /**
     * Busca usuarios (visitantes y operadores) cuyo nombre o documento contenga el texto de búsqueda, ignorando mayúsculas y acentos.
     * @param texto
     * @return
     */
    public List<Usuario> buscarUsuarios(String texto) {
        String q = texto == null ? "" : texto.toLowerCase();
        List<Usuario> resultado = new ArrayList<>();
        for (Usuario usuario : parque.getCatalogoUsuarios().inorden()) {
            if (usuario.getDocumento().toLowerCase().contains(q) || usuario.getNombre().toLowerCase().contains(q)) resultado.add(usuario);
        }
        return resultado;
    }

    /**
     * Busca zonas cuyo nombre o id contenga el texto de búsqueda, ignorando mayúsculas y acentos.
     * @param texto
     * @return
     */
    public List<Zona> buscarZonas(String texto) {
        String q = texto == null ? "" : texto.toLowerCase();
        List<Zona> resultado = new ArrayList<>();
        for (Zona zona : parque.getCatalogoZonas().inorden()) {
            if (zona.getId().toLowerCase().contains(q) || zona.getNombre().toLowerCase().contains(q)) resultado.add(zona);
        }
        return resultado;
    }

    /**
     * Busca atracciones cuyo nombre o id contenga el texto de búsqueda, ignorando mayúsculas y acentos.
     * @param texto
     * @return
     */
    public List<Atraccion> buscarAtracciones(String texto) {
        String q = texto == null ? "" : texto.toLowerCase();
        List<Atraccion> resultado = new ArrayList<>();
        for (Atraccion atraccion : parque.getCatalogoAtracciones().inorden()) {
            if (atraccion.getId().toLowerCase().contains(q) || atraccion.getNombre().toLowerCase().contains(q)) resultado.add(atraccion);
        }
        return resultado;
    }

    /**
     * Normaliza la contraseña proporcionada, asignando un valor predeterminado si es nula o está en blanco.
     * @param password
     * @return
     */
    private String normalizarPassword(String password) {
        return (password == null || password.isBlank()) ? "123" : password;
    }
}
