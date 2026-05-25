package techpark.model.parque;

import techpark.estructuras.arbol.ArbolBST;
import techpark.estructuras.grafo.Grafo;
import techpark.model.eventos.IncidenteOperativo;
import techpark.model.eventos.Notificacion;
import techpark.model.eventos.Show;
import techpark.model.usuarios.Usuario;
import techpark.model.usuarios.Visitante;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase central que representa el parque de atracciones, gestionando sus zonas, atracciones, usuarios, shows, incidentes operativos y notificaciones globales.
 */
public class Parque {
    private String id;
    private String nombre;
    private int capacidadMaxima;
    private int aforoActual;
    private final List<Zona> zonas = new ArrayList<>();
    private final Grafo<Atraccion> mapa = new Grafo<>();
    private final ArbolBST<Atraccion> catalogoAtracciones = new ArbolBST<>();
    private final ArbolBST<Usuario> catalogoUsuarios = new ArbolBST<>();
    private final ArbolBST<Zona> catalogoZonas = new ArbolBST<>();
    private final List<Show> shows = new ArrayList<>();
    private final List<IncidenteOperativo> incidentesOperativos = new ArrayList<>();
    private final List<Notificacion> notificacionesGlobales = new ArrayList<>();
    private double ingresosDiarios;

    /**
     * Constructor de la clase Parque, inicializa el parque con un id, nombre y capacidad máxima.
     * @param id
     * @param nombre
     * @param capacidadMaxima
     */
    public Parque(String id, String nombre, int capacidadMaxima) {
        this.id = id;
        this.nombre = nombre;
        this.capacidadMaxima = capacidadMaxima;
    }

    /**
     * Agrega una zona al parque, añadiéndola a la lista de zonas y al catálogo de zonas para su búsqueda eficiente.
     * @param zona
     */
    public void agregarZona(Zona zona) { zonas.add(zona); catalogoZonas.insertar(zona.getId(), zona); }

    /**
     * Registra una atracción en el parque, añadiéndola al catálogo de atracciones para su búsqueda eficiente y al mapa del parque para la gestión de rutas entre atracciones.
     * @param a
     */
    public void registrarAtraccion(Atraccion a) {
        catalogoAtracciones.insertar(a.getId(), a);
        mapa.agregarNodo(a.getId(), a);
    }

    /**
     * Registra un usuario en el parque, añadiéndolo al catálogo de usuarios para su búsqueda eficiente por documento.
     * @param u
     */
    public void registrarUsuario(Usuario u) { catalogoUsuarios.insertar(u.getDocumento(), u); }

    /**
     * Registra un show programado en el parque, añadiéndolo a la lista de shows para su gestión y consulta.
     * @param show
     */
    public void registrarShow(Show show) {
        if (show != null) shows.add(show);
    }

    /**
     * Registra un incidente operativo ocurrido en el parque, añadiéndolo a la lista de incidentes operativos para su seguimiento y análisis.
     * @param incidente
     */
    public void registrarIncidente(IncidenteOperativo incidente) {
        if (incidente != null) incidentesOperativos.add(incidente);
    }

    /**
     * Registra una notificación global que se enviará a todos los visitantes del parque, añadiéndola a la lista de notificaciones globales para su gestión y consulta.
     * @param notificacion
     */
    public void registrarNotificacionGlobal(Notificacion notificacion) {
        if (notificacion != null) notificacionesGlobales.add(notificacion);
    }

    /**
     * Registra un ingreso diario al parque, sumando el valor del ingreso al total de ingresos diarios, siempre y cuando el valor sea positivo.
     * @param valor
     */
    public void registrarIngresoDiario(double valor) {
        if (valor > 0) ingresosDiarios += valor;
    }

    /**
     * Verifica si hay aforo disponible en el parque, comparando el aforo actual con la capacidad máxima del parque.
     * @return
     */
    public boolean hayAforoDisponible() {
        return aforoActual < capacidadMaxima;
    }

    /**
     * Aumenta el aforo actual del parque en uno, siempre y cuando haya aforo disponible, es decir, que el aforo actual sea menor que la capacidad máxima del parque.
     */
    public void aumentarAforo() {
        if (hayAforoDisponible()) aforoActual++;
    }

    /**
     * Disminuye el aforo actual del parque en uno, siempre y cuando el aforo actual sea mayor que cero, para evitar que el aforo actual sea negativo.
     */
    public void disminuirAforo() {
        if (aforoActual > 0) aforoActual--;
    }

    /**
     * Busca una atracción en el parque por su id, utilizando el catálogo de atracciones para una búsqueda eficiente, y devuelve la atracción encontrada o null si no se encuentra.
     * @param id
     * @return
     */
    public Atraccion buscarAtraccion(String id) {
        return catalogoAtracciones.buscar(id);
    }

    /**
     * Busca una zona en el parque por su id, utilizando el catálogo de zonas para una búsqueda eficiente, y devuelve la zona encontrada o null si no se encuentra.
     * @param id
     * @return
     */
    public Zona buscarZona(String id) {
        return catalogoZonas.buscar(id);
    }

    /**
     * Busca un usuario en el parque por su documento, utilizando el catálogo de usuarios para una búsqueda eficiente, y devuelve el usuario encontrado o null si no se encuentra.
     * @param doc
     * @return
     */
    public Usuario buscarUsuarioPorDocumento(String doc) {
        return catalogoUsuarios.buscar(doc);
    }

    /**
     * Obtiene una lista de visitantes que tienen un ticket activo en el parque, recorriendo el catálogo de usuarios y filtrando aquellos que son instancias de Visitante y tienen un ticket activo que está vigente.
     * @return
     */
    public List<Visitante> getVisitantesConTicketActivo() {
        List<Visitante> visitantes = new ArrayList<>();
        for (Usuario usuario : catalogoUsuarios.inorden()) {
            if (usuario instanceof Visitante visitante
                    && visitante.getTicketActivo() != null
                    && visitante.getTicketActivo().estaActivo()) {
                visitantes.add(visitante);
            }
        }
        return visitantes;
    }

    /**
     * Método getter para obtener el id del parque.
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Método getter para obtener el nombre del parque.
     * @return
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Método getter para obtener la capacidad máxima del parque.
     * @return
     */
    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    /**
     * Método getter para obtener el aforo actual del parque.
     * @return
     */
    public int getAforoActual() {
        return aforoActual;
    }

    /**
     * Método getter para obtener la lista de zonas del parque.
     * @return
     */
    public List<Zona> getZonas() {
        return zonas;
    }

    /**
     * Método getter para obtener el mapa del parque, que es un grafo que representa las atracciones y sus conexiones, permitiendo la gestión de rutas entre atracciones.
     * @return
     */
    public Grafo<Atraccion> getMapa() {
        return mapa;
    }

    /**
     * Método getter para obtener el catálogo de atracciones del parque, que es un árbol binario de búsqueda que permite la búsqueda eficiente de atracciones por su id.
     * @return
     */
    public ArbolBST<Atraccion> getCatalogoAtracciones() {
        return catalogoAtracciones;
    }

    /**
     * Método getter para obtener el catálogo de usuarios del parque, que es un árbol binario de búsqueda que permite la búsqueda eficiente de usuarios por su documento.
     * @return
     */
    public ArbolBST<Usuario> getCatalogoUsuarios() {
        return catalogoUsuarios;
    }

    /**
     * Método getter para obtener el catálogo de zonas del parque, que es un árbol binario de búsqueda que permite la búsqueda eficiente de zonas por su id.
     * @return
     */
    public ArbolBST<Zona> getCatalogoZonas() {
        return catalogoZonas;
    }

    /**
     * Método getter para obtener la lista de shows programados en el parque.
     * @return
     */
    public List<Show> getShows() {
        return shows;
    }

    /**
     * Método getter para obtener la lista de incidentes operativos ocurridos en el parque.
     * @return
     */
    public List<IncidenteOperativo> getIncidentesOperativos() {
        return incidentesOperativos;
    }

    /**
     * Método getter para obtener la lista de notificaciones globales que se han registrado en el parque.
     * @return
     */
    public List<Notificacion> getNotificacionesGlobales() {
        return notificacionesGlobales;
    }

    /**
     * Método getter para obtener el total de ingresos diarios registrados en el parque.
     * @return
     */
    public double getIngresosDiarios() {
        return ingresosDiarios;
    }
}
