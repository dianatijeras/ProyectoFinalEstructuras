package techpark.controladores;

import techpark.enums.TipoTicket;
import techpark.estructuras.grafo.NodoGrafo;
import techpark.model.parque.Atraccion;
import techpark.model.parque.Parque;
import techpark.model.parque.Zona;
import techpark.model.tickets.Ticket;
import techpark.model.usuarios.Visitante;
import techpark.servicios.colas.ServicioColas;
import techpark.servicios.parque.ServicioParque;

import java.util.List;

/**
 * Controlador para gestionar las interacciones de los visitantes con el parque.
 */
public class ControladorVisitante {
    private final Parque parque;
    private final ServicioParque servicioParque;
    private final ServicioColas servicioColas;

    /**
     * Constructor para el controlador de visitantes, que recibe las dependencias necesarias para gestionar las operaciones relacionadas con los visitantes.
     * @param parque
     * @param servicioParque
     * @param servicioColas
     */
    public ControladorVisitante(Parque parque, ServicioParque servicioParque, ServicioColas servicioColas) {
        this.parque = parque; this.servicioParque = servicioParque; this.servicioColas = servicioColas;
    }

    /**
     * Permite a un visitante comprar un ticket y registrar su ingreso al parque en una zona específica.
     * El método utiliza el servicio de parque para vender el ticket y registrar el ingreso, y devuelve el ticket comprado.
     * @param visitante
     * @param tipoTicket
     * @param zonaEntrada
     * @return
     */
    public Ticket comprarTicketYEntrar(Visitante visitante, TipoTicket tipoTicket, Zona zonaEntrada) {
        Ticket ticket = servicioParque.venderTicket(visitante, tipoTicket, zonaEntrada);
        servicioParque.registrarIngreso(zonaEntrada);
        return ticket;
    }

    /**
     * Permite a un visitante unirse a la cola de una atracción específica.
     * El método utiliza el servicio de colas para gestionar la operación, y devuelve un mensaje indicando el resultado de la acción.
     * @param visitante
     * @param idAtraccion
     * @return
     */
    public String unirseAColaDeAtraccion(Visitante visitante, String idAtraccion) {
        return servicioColas.unirseACola(visitante, parque.buscarAtraccion(idAtraccion));
    }

    /**
     * Calcula la ruta óptima entre dos atracciones utilizando el algoritmo de Dijkstra en el mapa del parque.
     * @param idOrigen
     * @param idDestino
     * @return
     */
    public List<NodoGrafo<Atraccion>> calcularRutaOptima(String idOrigen, String idDestino) {
        return parque.getMapa().dijkstra(idOrigen, idDestino);
    }

    /**
     * Calcula un recorrido por el parque utilizando el algoritmo de búsqueda en anchura (BFS) a partir de una atracción de origen, visitando todas las atracciones accesibles desde esa atracción.
     * @param idOrigen
     * @return
     */
    public List<NodoGrafo<Atraccion>> calcularRecorridoBFS(String idOrigen) {
        return parque.getMapa().bfs(idOrigen);
    }

    /**
     * Permite a un visitante agregar una atracción a su lista de favoritos.
     * @param visitante
     * @param idAtraccion
     * @return
     */
    public boolean agregarFavorito(Visitante visitante, String idAtraccion) {
        Atraccion a = parque.buscarAtraccion(idAtraccion);
        return a != null && visitante.agregarFavorito(a);
    }
}

