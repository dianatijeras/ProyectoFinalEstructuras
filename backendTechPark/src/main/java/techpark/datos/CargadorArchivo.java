package techpark.datos;

import techpark.enums.*;
import techpark.model.eventos.*;
import techpark.model.parque.Atraccion;
import techpark.model.parque.Parque;
import techpark.model.parque.Zona;
import techpark.model.tickets.EntradaEnCola;
import techpark.model.tickets.Ticket;
import techpark.model.usuarios.Administrador;
import techpark.model.usuarios.Operador;
import techpark.model.usuarios.Usuario;
import techpark.model.usuarios.Visitante;
import techpark.utilidades.GeneradorId;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Clase encargada de cargar los datos del parque desde un archivo CSV.
 * El formato del archivo debe ser el siguiente:
 * Cada línea representa un registro de un tipo específico (parque, zona, atracción visitante, ticket, visita, cola, operador, asignación de zona, asignación de atracción, admin, show, alerta climática, alerta de mantenimiento, incidente o estado de atracción).
 */
public class CargadorArchivo {
    private Parque parque;
    private final List<Visitante> visitantes = new ArrayList<>();
    private final List<Operador> operadores = new ArrayList<>();
    private final List<AlertaClimatica> alertasClima = new ArrayList<>();
    private final List<AlertaMantenimiento> alertasMantenimiento = new ArrayList<>();
    private final Set<String> documentosConIngresoRegistrado = new HashSet<>();
    private final Set<String> visitasRegistradas = new HashSet<>();
    private final Set<String> colasRegistradas = new HashSet<>();
    private Administrador administrador;
    private ResultadoCargaCsv resultado = new ResultadoCargaCsv();

    /**
     * Carga los datos del parque desde el archivo CSV especificado.
     * El método reinicia el estado actual del parque y sus entidades relacionadas antes de cargar los nuevos datos.
     * @param archivo
     * @throws IOException
     */
    public void cargar(File archivo) throws IOException {
        parque = new Parque("PAR-CSV", "Tech-Park UQ", 1000);
        visitantes.clear();
        operadores.clear();
        alertasClima.clear();
        alertasMantenimiento.clear();
        documentosConIngresoRegistrado.clear();
        visitasRegistradas.clear();
        colasRegistradas.clear();
        administrador = null;
        resultado = new ResultadoCargaCsv();

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            int numeroLinea = 0;
            while ((linea = br.readLine()) != null) {
                numeroLinea++;
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) continue;

                String[] p = linea.split(";", -1);
                String tipo = p.length == 0 ? "" : p[0].trim().toUpperCase();
                if (tipo.equals("TIPOREGISTRO")) continue;

                try {
                    switch (tipo) {
                        case "PARQUE" -> cargarParque(p);
                        case "ZONA" -> cargarZona(p);
                        case "ATRACCION" -> cargarAtraccion(p);
                        case "SENDERO" -> cargarSendero(p);
                        case "VISITANTE" -> cargarVisitante(p);
                        case "TICKET" -> cargarTicket(p);
                        case "VISITA" -> cargarVisita(p);
                        case "COLA" -> cargarCola(p);
                        case "OPERADOR" -> cargarOperador(p);
                        case "ASIGNACION_ZONA" -> cargarAsignacionZona(p);
                        case "ASIGNACION_ATRACCION" -> cargarAsignacionAtraccion(p);
                        case "ADMIN" -> cargarAdmin(p);
                        case "SHOW" -> cargarShow(p);
                        case "ALERTA_CLIMA" -> cargarAlertaClima(p);
                        case "ALERTA_MANTENIMIENTO" -> cargarAlertaMantenimiento(p);
                        case "INCIDENTE" -> cargarIncidente(p);
                        case "ESTADO_ATRACCION" -> cargarEstadoAtraccion(p);
                        default -> throw new IOException("Tipo de registro no soportado: " + tipo);
                    }
                    resultado.registrarExito(tipo);
                } catch (Exception ex) {
                    resultado.registrarError(numeroLinea, tipo, ex.getMessage());
                }
            }
        }
    }

    /**
     * Carga los datos del parque desde un arreglo de strings, donde cada posición representa un campo específico del parque.
     * @param p
     * @throws IOException
     */
    private void cargarParque(String[] p) throws IOException {
        validarCantidad(p, 4, "PARQUE;id;nombre;capacidadMaxima");
        parque = new Parque(p[1].trim(), p[2].trim(), entero(p[3]));
    }

    /**
     * Carga los datos de una zona del parque desde un arreglo de strings, donde cada posición representa un campo específico de la zona.
     * @param p
     * @throws IOException
     */
    private void cargarZona(String[] p) throws IOException {
        validarCantidad(p, 4, "ZONA;id;nombre;capacidadMaxima[;disponible]");
        if (buscarZona(p[1]) != null) return;
        Zona zona = new Zona(p[1].trim(), p[2].trim(), entero(p[3]));
        if (p.length >= 5 && !estaVacio(p[4])) zona.cambiarDisponibilidad(Boolean.parseBoolean(p[4].trim()));
        parque.agregarZona(zona);
    }

    /**
     * Carga los datos de una atracción del parque desde un arreglo de strings, donde cada posición representa un campo específico de la atracción.
     * @param p
     * @throws IOException
     */
    private void cargarAtraccion(String[] p) throws IOException {
        validarCantidad(p, 9, "ATRACCION;id;zonaId;nombre;tipo;capacidad;alturaMinima;edadMinima;costoAdicional[;contador;tiempoEspera;estado;motivoCierre]");
        if (parque.buscarAtraccion(p[1]) != null) {
            aplicarDatosExtendidosAtraccion(parque.buscarAtraccion(p[1]), p, 9);
            return;
        }
        Zona zona = buscarZona(p[2]);
        if (zona == null) throw new IOException("Zona no encontrada para atraccion: " + p[2]);
        Atraccion a = new Atraccion(p[1].trim(), p[3].trim(), TipoAtraccion.valueOf(p[4].trim().toUpperCase()),
                entero(p[5]), decimal(p[6]), entero(p[7]), decimal(p[8]));
        zona.agregarAtraccion(a);
        parque.registrarAtraccion(a);
        aplicarDatosExtendidosAtraccion(a, p, 9);
    }

    /**
     * Aplica los datos extendidos de una atracción (contador acumulado de visitantes, tiempo estimado de espera, estado y motivo de cierre) a una atracción existente, utilizando un arreglo de strings donde cada posición representa un campo específico.
     * @param a
     * @param p
     * @param inicio
     */
    private void aplicarDatosExtendidosAtraccion(Atraccion a, String[] p, int inicio) {
        if (p.length > inicio && !estaVacio(p[inicio])) a.setContadorAcumuladoVisitantes(entero(p[inicio]));
        if (p.length > inicio + 1 && !estaVacio(p[inicio + 1])) a.setTiempoEstimadoEspera(entero(p[inicio + 1]));
        if (p.length > inicio + 2 && !estaVacio(p[inicio + 2])) {
            EstadoAtraccion estado = EstadoAtraccion.valueOf(p[inicio + 2].trim().toUpperCase());
            String motivo = p.length > inicio + 3 ? limpiarNulo(p[inicio + 3]) : null;
            if (estado == EstadoAtraccion.CERRADA && estaVacio(motivo)) motivo = "Cierre cargado desde CSV";
            a.cambiarEstado(estado, motivo);
        }
        if (a.requiereMantenimiento() && a.getEstado() == EstadoAtraccion.ACTIVA) {
            crearAlertaMantenimiento(a, "Mantenimiento preventivo por contador cargado desde CSV", true, null);
        }
    }

    /**
     * Carga los datos de un sendero del parque desde un arreglo de strings, donde cada posición representa un campo específico del sendero.
     * @param p
     * @throws IOException
     */
    private void cargarSendero(String[] p) throws IOException {
        validarCantidad(p, 4, "SENDERO;idOrigen;idDestino;peso");
        parque.getMapa().agregarArista(p[1].trim(), p[2].trim(), decimal(p[3]));
    }

    /**
     * Carga los datos de un visitante del parque desde un arreglo de strings, donde cada posición representa un campo específico del visitante.
     * Si el visitante ya existe, se actualizan sus datos básicos y se carga un ticket opcional si se proporciona en el arreglo.
     * @param p
     * @throws IOException
     */
    private void cargarVisitante(String[] p) throws IOException {
        validarCantidad(p, 8, "VISITANTE;id;nombre;documento;edad;password;estatura;saldo[;tipoTicket;zonaId;activoEnParque;cantidadPersonasFamilia]");
        Visitante visitante = buscarVisitantePorDocumento(p[3]);
        if (visitante == null) {
            int edad = entero(p[4]);
            double estatura = decimal(p[6]);
            double saldo = decimal(p[7]);
            if (edad < 0) throw new IOException("La edad no puede ser negativa para visitante: " + p[3]);
            if (estatura < 0) throw new IOException("La estatura no puede ser negativa para visitante: " + p[3]);
            if (saldo < 0) throw new IOException("El saldo virtual no puede ser negativo para visitante: " + p[3]);
            visitante = new Visitante(p[1].trim(), p[2].trim(), p[3].trim(), edad, p[5].trim(), estatura, saldo, null);
            visitantes.add(visitante);
            parque.registrarUsuario(visitante);
        }
        cargarTicketOpcionalDesdeVisitante(visitante, p);
    }

    /**
     * Carga un ticket opcional para un visitante existente, utilizando un arreglo de strings donde cada posición representa un campo específico del ticket.
     * @param visitante
     * @param p
     * @throws IOException
     */
    private void cargarTicketOpcionalDesdeVisitante(Visitante visitante, String[] p) throws IOException {
        if (p.length < 9 || estaVacio(p[8])) return;
        TipoTicket tipoTicket = TipoTicket.valueOf(p[8].trim().toUpperCase());
        Zona zona = p.length >= 10 && !estaVacio(p[9]) ? buscarZona(p[9]) : null;
        if (p.length >= 10 && !estaVacio(p[9]) && zona == null) {
            throw new IOException("Zona no encontrada para ticket del visitante " + visitante.getDocumento() + ": " + p[9]);
        }
        boolean activoEnParque = p.length >= 11 && Boolean.parseBoolean(p[10].trim());
        int cantidadFamilia = p.length >= 12 && !estaVacio(p[11]) ? entero(p[11]) : 1;
        crearTicketParaVisitante("TIC-" + visitante.getDocumento(), visitante, tipoTicket, null, EstadoTicket.ACTIVO, zona, activoEnParque, cantidadFamilia);
    }

    /**
     * Carga los datos de un ticket del parque desde un arreglo de strings, donde cada posición representa un campo específico del ticket.
     * @param p
     * @throws IOException
     */
    private void cargarTicket(String[] p) throws IOException {
        validarCantidad(p, 8, "TICKET;id;documentoVisitante;tipoTicket;precio;estado;zonaId;activoEnParque[;cantidadPersonasFamilia]");
        Visitante visitante = buscarVisitantePorDocumento(p[2]);
        if (visitante == null) throw new IOException("Visitante no encontrado para ticket: " + p[2]);
        TipoTicket tipoTicket = TipoTicket.valueOf(p[3].trim().toUpperCase());
        Double precio = estaVacio(p[4]) ? null : decimal(p[4]);
        EstadoTicket estado = estaVacio(p[5]) ? EstadoTicket.ACTIVO : EstadoTicket.valueOf(p[5].trim().toUpperCase());
        Zona zona = estaVacio(p[6]) ? null : buscarZona(p[6]);
        if (!estaVacio(p[6]) && zona == null) throw new IOException("Zona no encontrada para ticket: " + p[6]);
        boolean activoEnParque = Boolean.parseBoolean(p[7].trim());
        int cantidadFamilia = p.length >= 9 && !estaVacio(p[8]) ? entero(p[8]) : 1;
        crearTicketParaVisitante(p[1].trim(), visitante, tipoTicket, precio, estado, zona, activoEnParque, cantidadFamilia);
    }

    /**
     * Carga los datos de una visita del parque desde un arreglo de strings, donde cada posición representa un campo específico de la visita.
     * @param p
     * @throws IOException
     */
    private void cargarVisita(String[] p) throws IOException {
        validarCantidad(p, 4, "VISITA;documentoVisitante;idAtraccion;fechaHoraISO[;tipoTicket;costoDeducido]");
        Visitante visitante = buscarVisitantePorDocumento(p[1]);
        if (visitante == null) throw new IOException("Visitante no encontrado para visita: " + p[1]);
        Atraccion atraccion = parque.buscarAtraccion(p[2]);
        if (atraccion == null) throw new IOException("Atraccion no encontrada para visita: " + p[2]);
        LocalDateTime fecha = fecha(p[3]);
        String clave = visitante.getDocumento() + "|" + atraccion.getId() + "|" + fecha;
        if (visitasRegistradas.contains(clave)) return;

        TipoTicket tipoTicket = visitante.getTicketActivo() != null ? visitante.getTicketActivo().getTipo() : TipoTicket.GENERAL;
        if (p.length >= 5 && !estaVacio(p[4])) tipoTicket = TipoTicket.valueOf(p[4].trim().toUpperCase());
        double costo = p.length >= 6 && !estaVacio(p[5]) ? decimal(p[5]) : 0;

        visitante.agregarHistorial(new RegistroVisita(atraccion, tipoTicket, costo, fecha));
        atraccion.incrementarVisitantes();
        visitasRegistradas.add(clave);

        if (atraccion.requiereMantenimiento()) {
            crearAlertaMantenimiento(atraccion, "Mantenimiento preventivo por visitas cargadas desde CSV", true, fecha);
        }
    }

    /**
     * Carga los datos de una cola de atracción del parque desde un arreglo de strings, donde cada posición representa un campo específico de la cola.
     * @param p
     * @throws IOException
     */
    private void cargarCola(String[] p) throws IOException {
        validarCantidad(p, 5, "COLA;idAtraccion;documentoVisitante;tipoTicket;fechaHoraIngreso");
        Atraccion atraccion = parque.buscarAtraccion(p[1]);
        if (atraccion == null) throw new IOException("Atraccion no encontrada para cola: " + p[1]);
        Visitante visitante = buscarVisitantePorDocumento(p[2]);
        if (visitante == null) throw new IOException("Visitante no encontrado para cola: " + p[2]);

        String clave = atraccion.getId() + "|" + visitante.getDocumento();
        if (colasRegistradas.contains(clave)) return;

        TipoTicket tipoTicket = TipoTicket.valueOf(p[3].trim().toUpperCase());
        Ticket ticket = visitante.getTicketActivo();
        if (ticket == null || !ticket.estaActivo() || ticket.getTipo() != tipoTicket) {
            ticket = new Ticket("TIC-COLA-" + visitante.getDocumento(), tipoTicket, calcularPrecioTicket(tipoTicket, 1), visitante);
            visitante.setTicketActivo(ticket);
            parque.registrarIngresoDiario(ticket.getPrecio());
        }

        if (!documentosConIngresoRegistrado.contains(visitante.getDocumento())) {
            registrarIngresoUnico(visitante, atraccion.getZona());
        }

        atraccion.getColaVirtual().insertar(new EntradaEnCola(visitante, ticket, fecha(p[4])));
        atraccion.actualizarTiempoEspera();
        visitante.setEnCola(true);
        colasRegistradas.add(clave);
        notificarVisitante(visitante, "Te uniste a la cola de " + atraccion.getNombre() + " desde CSV", TipoNotif.COLA);
    }

    /**
     * Carga los datos de un operador del parque desde un arreglo de strings, donde cada posición representa un campo específico del operador.
     * @param p
     * @throws IOException
     */
    private void cargarOperador(String[] p) throws IOException {
        validarCantidad(p, 7, "OPERADOR;id;nombre;documento;edad;password;zonaId");
        Operador existente = buscarOperadorPorDocumento(p[3]);
        Zona zona = buscarZona(p[6]);
        if (zona == null) throw new IOException("Zona no encontrada para operador: " + p[6]);
        Operador operador = existente;
        if (operador == null) {
            int edad = entero(p[4]);
            if (edad < 0) throw new IOException("La edad no puede ser negativa para operador: " + p[3]);
            operador = new Operador(p[1].trim(), p[2].trim(), p[3].trim(), edad, p[5].trim());
            operadores.add(operador);
            parque.registrarUsuario(operador);
        }
        asignarOperadorAZona(operador, zona);
        for (Atraccion atraccion : zona.getAtracciones()) atraccion.asignarOperadorResponsable(operador);
    }

    /**
     * Asigna un operador a una zona específica del parque, actualizando la asignación tanto en el operador como en la zona.
     * @param p
     * @throws IOException
     */
    private void cargarAsignacionZona(String[] p) throws IOException {
        validarCantidad(p, 3, "ASIGNACION_ZONA;documentoOperador;idZona");
        Operador operador = buscarOperadorPorDocumento(p[1]);
        if (operador == null) throw new IOException("Operador no encontrado para asignacion: " + p[1]);
        Zona zona = buscarZona(p[2]);
        if (zona == null) throw new IOException("Zona no encontrada para asignacion: " + p[2]);
        asignarOperadorAZona(operador, zona);
    }

    /**
     * Asigna un operador a una atracción específica del parque, validando que el operador pertenezca a la zona de la atracción y actualizando la asignación tanto en el operador como en la atracción.
     * @param p
     * @throws IOException
     */
    private void cargarAsignacionAtraccion(String[] p) throws IOException {
        validarCantidad(p, 3, "ASIGNACION_ATRACCION;documentoOperador;idAtraccion");
        Operador operador = buscarOperadorPorDocumento(p[1]);
        if (operador == null) throw new IOException("Operador no encontrado para asignacion de atraccion: " + p[1]);
        Atraccion atraccion = parque.buscarAtraccion(p[2]);
        if (atraccion == null) throw new IOException("Atraccion no encontrada para asignacion: " + p[2]);
        if (atraccion.getZona() == null) throw new IOException("Atraccion sin zona: " + p[2]);
        if (operador.getZonaAsignada() == null) asignarOperadorAZona(operador, atraccion.getZona());
        if (operador.getZonaAsignada() != atraccion.getZona()) throw new IOException("El operador no pertenece a la zona de la atraccion");
        atraccion.asignarOperadorResponsable(operador);
    }

    /**
     * Carga los datos de un administrador del parque desde un arreglo de strings, donde cada posición representa un campo específico del administrador.
     * @param p
     * @throws IOException
     */
    private void cargarAdmin(String[] p) throws IOException {
        validarCantidad(p, 6, "ADMIN;id;nombre;documento;edad;password");
        administrador = new Administrador(p[1].trim(), p[2].trim(), p[3].trim(), entero(p[4]), p[5].trim());
        administrador.setParqueGestionado(parque);
        parque.registrarUsuario(administrador);
    }

    /**
     * Carga los datos de un show del parque desde un arreglo de strings, donde cada posición representa un campo específico del show.
     * @param p
     * @throws IOException
     */
    private void cargarShow(String[] p) throws IOException {
        validarCantidad(p, 6, "SHOW;id;nombre;zonaId;horarioISO;duracionMinutos");
        Zona zona = buscarZona(p[3]);
        if (zona == null) throw new IOException("Zona no encontrada para show: " + p[3]);
        parque.registrarShow(new Show(p[1].trim(), p[2].trim(), zona, fecha(p[4]), entero(p[5])));
    }

    /**
     * Carga los datos de una alerta climática del parque desde un arreglo de strings, donde cada posición representa un campo específico de la alerta climática.
     * @param p
     * @throws IOException
     */
    private void cargarAlertaClima(String[] p) throws IOException {
        validarCantidad(p, 4, "ALERTA_CLIMA;tipoClima;activa;fechaHoraISO[;descripcion] o ALERTA_CLIMA;id;tipoClima;fechaHoraISO;activa");
        String id;
        TipoClima tipo;
        boolean activa;
        LocalDateTime fecha;

        if (esTipoClima(p[1])) {
            id = GeneradorId.generarId("CLI-");
            tipo = TipoClima.valueOf(p[1].trim().toUpperCase());
            activa = Boolean.parseBoolean(p[2].trim());
            fecha = fecha(p[3]);
        } else {
            validarCantidad(p, 5, "ALERTA_CLIMA;id;tipoClima;fechaHoraISO;activa");
            id = p[1].trim();
            tipo = TipoClima.valueOf(p[2].trim().toUpperCase());
            fecha = fecha(p[3]);
            activa = Boolean.parseBoolean(p[4].trim());
        }

        AlertaClimatica alerta = new AlertaClimatica(id, tipo, fecha, activa);
        if (activa) {
            for (Atraccion atraccion : parque.getCatalogoAtracciones().inorden()) {
                if (atraccion.getTipo() == TipoAtraccion.ACUATICA || atraccion.getTipo() == TipoAtraccion.MECANICA_ALTURA) {
                    atraccion.cambiarEstado(EstadoAtraccion.CERRADA, tipo.name());
                    alerta.agregarAtraccionAfectada(atraccion);
                }
            }
            notificarTodos("Alerta climatica activa desde CSV: " + tipo, TipoNotif.CLIMA);
        }
        alertasClima.add(alerta);
        parque.registrarNotificacionGlobal(new Notificacion(GeneradorId.generarId("NOT-"), "Alerta climatica cargada: " + tipo + " activa=" + activa, TipoNotif.CLIMA));
    }

    /**
     * Valida si el string dado corresponde a un tipo de clima válido.
     * @param p
     * @throws IOException
     */
    private void cargarAlertaMantenimiento(String[] p) throws IOException {
        validarCantidad(p, 6, "ALERTA_MANTENIMIENTO;idAtraccion;motivo;prioridad;activa;fechaHoraISO");
        Atraccion atraccion = parque.buscarAtraccion(p[1]);
        if (atraccion == null) throw new IOException("Atraccion no encontrada para alerta de mantenimiento: " + p[1]);
        boolean activa = Boolean.parseBoolean(p[4].trim());
        crearAlertaMantenimiento(atraccion, p[2], activa, fecha(p[5]));
    }

    /**
     * Valida si el string dado corresponde a un tipo de clima válido.
     * @param p
     * @throws IOException
     */
    private void cargarIncidente(String[] p) throws IOException {
        validarCantidad(p, 5, "INCIDENTE;idAtraccion;descripcion;fechaHoraISO;gravedad");
        Atraccion atraccion = parque.buscarAtraccion(p[1]);
        if (atraccion == null) throw new IOException("Atraccion no encontrada para incidente: " + p[1]);
        IncidenteOperativo incidente = new IncidenteOperativo(GeneradorId.generarId("INC-"), atraccion, p[2].trim(), p[4].trim(), fecha(p[3]));
        atraccion.registrarIncidente();
        atraccion.cambiarEstado(EstadoAtraccion.CERRADA, "Cerrada por incidente operativo");
        parque.registrarIncidente(incidente);
        notificarTodos("Incidente " + incidente.getGravedad() + " cargado en " + atraccion.getNombre() + ": " + incidente.getDescripcion(), TipoNotif.INCIDENTE);
    }

    /**
     * Carga los datos de un estado de atracción del parque desde un arreglo de strings, donde cada posición representa un campo específico del estado de atracción.
     * @param p
     * @throws IOException
     */
    private void cargarEstadoAtraccion(String[] p) throws IOException {
        validarCantidad(p, 4, "ESTADO_ATRACCION;idAtraccion;estado;motivo[;fechaHoraISO]");
        Atraccion atraccion = parque.buscarAtraccion(p[1]);
        if (atraccion == null) throw new IOException("Atraccion no encontrada para estado: " + p[1]);
        EstadoAtraccion estado = EstadoAtraccion.valueOf(p[2].trim().toUpperCase());
        String motivo = limpiarNulo(p[3]);
        if (estado == EstadoAtraccion.CERRADA && estaVacio(motivo)) motivo = "Cierre cargado desde CSV";
        if (estado == EstadoAtraccion.EN_MANTENIMIENTO && estaVacio(motivo)) motivo = "Mantenimiento cargado desde CSV";
        atraccion.cambiarEstado(estado, estado == EstadoAtraccion.ACTIVA ? null : motivo);
        TipoNotif tipo = estado == EstadoAtraccion.EN_MANTENIMIENTO ? TipoNotif.MANTENIMIENTO : TipoNotif.ESTADO_ATRACCION;
        notificarTodos("La atraccion " + atraccion.getNombre() + " cambio a estado " + estado + " desde CSV", tipo);
    }

    /**
     * Crea un ticket para un visitante específico, aplicando las reglas de negocio correspondientes para determinar su precio, estado y si se debe registrar un ingreso al parque.
     * Si el visitante ya tiene un ticket activo del mismo tipo, no se crea un nuevo ticket y se registra un ingreso único si corresponde.
     * @param idTicket
     * @param visitante
     * @param tipoTicket
     * @param precioDefinido
     * @param estado
     * @param zona
     * @param activoEnParque
     * @param cantidadFamilia
     */
    private void crearTicketParaVisitante(String idTicket, Visitante visitante, TipoTicket tipoTicket, Double precioDefinido,
                                          EstadoTicket estado, Zona zona, boolean activoEnParque, int cantidadFamilia) {
        if (visitante.getTicketActivo() != null
                && visitante.getTicketActivo().estaActivo()
                && estado == EstadoTicket.ACTIVO
                && visitante.getTicketActivo().getTipo() == tipoTicket) {
            if (activoEnParque) registrarIngresoUnico(visitante, zona);
            return;
        }

        double precio = precioDefinido != null ? precioDefinido : calcularPrecioTicket(tipoTicket, cantidadFamilia);
        Ticket ticket = new Ticket(idTicket, tipoTicket, precio, visitante);
        ticket.setZonaIngreso(zona);
        if (estado == EstadoTicket.USADO) ticket.marcarUsado();
        if (estado == EstadoTicket.EXPIRADO) ticket.expirar();
        visitante.setTicketActivo(ticket);
        parque.registrarIngresoDiario(precio);

        if (activoEnParque && ticket.estaActivo()) {
            registrarIngresoUnico(visitante, zona);
        }
    }

    /**
     * Calcula el precio de un ticket según su tipo y la cantidad de personas en caso de ser un ticket familiar.
     * @param tipo
     * @param cantidadPersonasFamilia
     * @return
     */
    private double calcularPrecioTicket(TipoTicket tipo, int cantidadPersonasFamilia) {
        double precioBase = 50000;
        if (tipo == TipoTicket.FAST_PASS) return 80000;
        if (tipo == TipoTicket.FAMILIAR) return cantidadPersonasFamilia >= 4 ? precioBase * 0.80 : precioBase;
        return precioBase;
    }

    /**
     * Registra un ingreso al parque para un visitante específico, asegurándose de que solo se registre un ingreso por visitante durante la carga desde CSV.
     * @param visitante
     * @param zona
     */
    private void registrarIngresoUnico(Visitante visitante, Zona zona) {
        if (documentosConIngresoRegistrado.contains(visitante.getDocumento())) return;
        if (!parque.hayAforoDisponible()) throw new IllegalStateException("Parque sin aforo disponible al cargar visitante activo: " + visitante.getDocumento());
        if (zona != null && !zona.hayAforoDisponible()) throw new IllegalStateException("Zona sin aforo disponible al cargar visitante activo: " + zona.getId());
        parque.aumentarAforo();
        if (zona != null) zona.aumentarAforo();
        documentosConIngresoRegistrado.add(visitante.getDocumento());
    }

    /**
     * Asigna un operador a una zona específica, asegurándose de removerlo de su zona anterior si ya estaba asignado a otra zona diferente.
     * @param operador
     * @param zona
     */
    private void asignarOperadorAZona(Operador operador, Zona zona) {
        if (operador.getZonaAsignada() != null && operador.getZonaAsignada() != zona) {
            operador.getZonaAsignada().removerOperador(operador);
        }
        zona.agregarOperador(operador);
    }

    /**
     * Crea una alerta de mantenimiento para una atracción específica, cambiando su estado a "En Mantenimiento" si la alerta está activa y notificando a los visitantes si corresponde.
     * @param atraccion
     * @param motivo
     * @param activa
     * @param fecha
     */
    private void crearAlertaMantenimiento(Atraccion atraccion, String motivo, boolean activa, LocalDateTime fecha) {
        if (activa) {
            atraccion.cambiarEstado(EstadoAtraccion.EN_MANTENIMIENTO, estaVacio(motivo) ? "Mantenimiento cargado desde CSV" : motivo);
        }
        AlertaMantenimiento alerta = new AlertaMantenimiento(GeneradorId.generarId("MAN-"), atraccion, fecha, !activa);
        alertasMantenimiento.add(alerta);
        if (activa) notificarTodos("La atraccion " + atraccion.getNombre() + " entro en mantenimiento desde CSV", TipoNotif.MANTENIMIENTO);
    }

    /**
     * Notifica a todos los visitantes con ticket activo sobre un evento específico, creando una notificación global en el parque y agregando a cada visitante como destinatario de la notificación.
     * @param mensaje
     * @param tipo
     */
    private void notificarTodos(String mensaje, TipoNotif tipo) {
        Notificacion notificacion = new Notificacion(GeneradorId.generarId("NOT-"), mensaje, tipo);
        parque.registrarNotificacionGlobal(notificacion);
        for (Visitante visitante : parque.getVisitantesConTicketActivo()) {
            notificacion.agregarDestinatario(visitante);
        }
    }

    /**
     * Notifica a un visitante específico sobre un evento relacionado con él, creando una notificación personalizada y registrándola como global en el parque para que el visitante pueda recibirla.
     * @param visitante
     * @param mensaje
     * @param tipo
     */
    private void notificarVisitante(Visitante visitante, String mensaje, TipoNotif tipo) {
        Notificacion notificacion = new Notificacion(GeneradorId.generarId("NOT-"), mensaje, tipo);
        notificacion.agregarDestinatario(visitante);
        parque.registrarNotificacionGlobal(notificacion);
    }

    /**
     * Busca una zona en el parque por su ID, devolviendo la zona encontrada o null si no se encuentra una zona con el ID proporcionado.
     * El método también valida que el ID no esté vacío antes de realizar la búsqueda.
     * @param id
     * @return
     */
    private Zona buscarZona(String id) {
        if (estaVacio(id)) return null;
        return parque.buscarZona(id.trim());
    }

    /**
     * Busca un visitante en la lista de visitantes cargados o en el parque por su documento, devolviendo el visitante encontrado o null si no se encuentra un visitante con el documento proporcionado.
     * @param documento
     * @return
     */
    private Visitante buscarVisitantePorDocumento(String documento) {
        if (estaVacio(documento)) return null;
        for (Visitante visitante : visitantes) {
            if (visitante.getDocumento().equalsIgnoreCase(documento.trim())) return visitante;
        }
        Usuario usuario = parque.buscarUsuarioPorDocumento(documento.trim());
        return usuario instanceof Visitante v ? v : null;
    }

    /**
     * Busca un operador en la lista de operadores cargados o en el parque por su documento, devolviendo el operador encontrado o null si no se encuentra un operador con el documento proporcionado.
     * @param documento
     * @return
     */
    private Operador buscarOperadorPorDocumento(String documento) {
        if (estaVacio(documento)) return null;
        for (Operador operador : operadores) {
            if (operador.getDocumento().equalsIgnoreCase(documento.trim())) return operador;
        }
        Usuario usuario = parque.buscarUsuarioPorDocumento(documento.trim());
        return usuario instanceof Operador o ? o : null;
    }

    /**
     * Valida si un string corresponde a un tipo de clima definido en el sistema, devolviendo true si el string es un tipo de clima válido o false si no lo es.
     * El método también considera los strings vacíos o nulos como no válidos.
     * @param valor
     * @return
     */
    private boolean esTipoClima(String valor) {
        if (estaVacio(valor)) return false;
        try {
            TipoClima.valueOf(valor.trim().toUpperCase());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Valida si un string está vacío o es nulo, considerando también los espacios en blanco como vacíos.
     * @param valor
     * @return
     */
    private boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    /**
     * Limpia un string de espacios en blanco y lo convierte a null si está vacío después de limpiar. Esto es útil para campos opcionales donde un valor vacío debe ser tratado como ausencia de valor.
     * @param valor
     * @return
     */
    private String limpiarNulo(String valor) {
        return estaVacio(valor) ? null : valor.trim();
    }

    /**
     * Convierte un string a entero, lanzando una excepción si el formato no es válido.
     * El método también limpia los espacios en blanco antes de la conversión.
     * @param valor
     * @return
     */
    private int entero(String valor) {
        return Integer.parseInt(valor.trim());
    }

    /**
     * Convierte un string a double, lanzando una excepción si el formato no es válido.
     * @param valor
     * @return
     */
    private double decimal(String valor) {
        return Double.parseDouble(valor.trim());
    }

    /**
     * Convierte un string a LocalDateTime. Si el string está vacío, se devuelve la fecha y hora actual.
     * El método también limpia los espacios en blanco antes de la conversión.
     * @param valor
     * @return
     */
    private LocalDateTime fecha(String valor) {
        if (estaVacio(valor)) return LocalDateTime.now();
        return LocalDateTime.parse(valor.trim());
    }

    /**
     * Valida que un arreglo de strings tenga al menos una cantidad mínima de elementos, lanzando una excepción con un mensaje específico si no se cumple la validación.
     * @param p
     * @param cantidad
     * @param formato
     * @throws IOException
     */
    private void validarCantidad(String[] p, int cantidad, String formato) throws IOException {
        if (p.length < cantidad) throw new IOException("Formato invalido. Use: " + formato);
    }

    /**
     * Devuelve el parque cargado desde el archivo CSV
     * @return
     */
    public Parque getParque(){
        return parque;
    }

    /**
     * Devuelve la lista de visitantes cargados desde el archivo CSV
     * @return
     */
    public List<Visitante> getVisitantes(){
        return visitantes;
    }

    /**
     * Devuelve la lista de operadores cargados desde el archivo CSV
     * @return
     */
    public List<Operador> getOperadores(){
        return operadores;
    }

    /**
     * Devuelve el administrador cargado desde el archivo CSV, o null si no se cargó un administrador
     * @return
     */
    public Administrador getAdministrador(){
        return administrador;
    }

    /**
     * Devuelve la lista de alertas climáticas cargadas desde el archivo CSV
     * @return
     */
    public List<AlertaClimatica> getAlertasClima(){
        return alertasClima;
    }

    /**
     * Devuelve la lista de alertas de mantenimiento cargadas desde el archivo CSV
     * @return
     */
    public List<AlertaMantenimiento> getAlertasMantenimiento(){
        return alertasMantenimiento;
    }

    /**
     * Devuelve el resultado de la carga desde el archivo CSV, incluyendo estadísticas de registros exitosos y errores encontrados durante la carga
     * @return
     */
    public ResultadoCargaCsv getResultado(){
        return resultado;
    }
}


