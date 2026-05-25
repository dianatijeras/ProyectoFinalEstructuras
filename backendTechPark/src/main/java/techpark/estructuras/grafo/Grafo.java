package techpark.estructuras.grafo;

import techpark.estructuras.arbol.ArbolBST;
import techpark.estructuras.cola.ColaPrioridad;
import techpark.estructuras.conjunto.SetArbol;
import techpark.estructuras.lista.ListaEnlazada;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementación de un grafo no dirigido con nodos identificados por cadenas y datos genéricos.
 * @param <T>
 */
public class Grafo<T> {
    private final ArbolBST<NodoGrafo<T>> catalogoNodos = new ArbolBST<>();
    private final ListaEnlazada<NodoGrafo<T>> nodos = new ListaEnlazada<>();

    /**
     * Agrega un nodo al grafo. Si el nodo ya existe, actualiza su dato.
     * @param id
     * @param dato
     */
    public void agregarNodo(String id, T dato) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Id de nodo invalido");
        if (catalogoNodos.contiene(id)) {
            NodoGrafo<T> existente = catalogoNodos.buscar(id);
            if (existente != null) existente.setDato(dato);
            return;
        }
        NodoGrafo<T> nuevo = new NodoGrafo<>(id, dato);
        catalogoNodos.insertar(id, nuevo);
        nodos.agregarFinal(nuevo);
    }

    /**
     * Agrega una arista no dirigida entre dos nodos con un peso positivo.
     * @param idOrigen
     * @param idDestino
     * @param peso
     */
    public void agregarArista(String idOrigen, String idDestino, double peso) {
        if (peso <= 0) throw new IllegalArgumentException("El peso del sendero debe ser positivo");
        NodoGrafo<T> origen = catalogoNodos.buscar(idOrigen);
        NodoGrafo<T> destino = catalogoNodos.buscar(idDestino);
        if (origen == null || destino == null) throw new IllegalArgumentException("Nodo origen o destino no existe");
        origen.agregarVecino(new Arista<>(origen, destino, peso));
        destino.agregarVecino(new Arista<>(destino, origen, peso));
    }

    /**
     * Realiza un recorrido en anchura (BFS) desde el nodo de inicio y devuelve la lista de nodos visitados en orden.
     * @param idInicio
     * @return
     */
    public List<NodoGrafo<T>> bfs(String idInicio) {
        List<NodoGrafo<T>> recorrido = new ArrayList<>();
        NodoGrafo<T> inicio = catalogoNodos.buscar(idInicio);
        if (inicio == null) return recorrido;

        SetArbol<NodoGrafo<T>> visitados = new SetArbol<>();
        ListaEnlazada<NodoGrafo<T>> cola = new ListaEnlazada<>();
        cola.agregarFinal(inicio);
        visitados.agregar(inicio.getId(), inicio);

        int posicion = 0;
        while (posicion < cola.getTamanio()) {
            NodoGrafo<T> actual = cola.obtener(posicion++);
            recorrido.add(actual);
            for (Arista<T> arista : actual.getVecinos()) {
                NodoGrafo<T> vecino = arista.getDestino();
                if (!visitados.contiene(vecino.getId())) {
                    visitados.agregar(vecino.getId(), vecino);
                    cola.agregarFinal(vecino);
                }
            }
        }
        return recorrido;
    }

    /**
     * Realiza el algoritmo de Dijkstra para encontrar la ruta más corta entre dos nodos. Devuelve la lista de nodos en la ruta, o una lista vacía si no hay ruta.
     * @param idOrigen
     * @param idDestino
     * @return
     */
    public List<NodoGrafo<T>> dijkstra(String idOrigen, String idDestino) {
        List<NodoGrafo<T>> ruta = new LinkedList<>();
        NodoGrafo<T> origen = catalogoNodos.buscar(idOrigen);
        NodoGrafo<T> destino = catalogoNodos.buscar(idDestino);
        if (origen == null || destino == null) return ruta;

        ArbolBST<RegistroDijkstra<T>> registros = new ArbolBST<>();
        for (NodoGrafo<T> nodo : nodos) registros.insertar(nodo.getId(), new RegistroDijkstra<>(nodo));

        RegistroDijkstra<T> registroOrigen = registros.buscar(idOrigen);
        registroOrigen.distancia = 0;

        ColaPrioridad<EntradaDijkstra<T>> pendientes = new ColaPrioridad<>();
        pendientes.insertar(new EntradaDijkstra<>(origen, 0));

        while (!pendientes.estaVacia()) {
            EntradaDijkstra<T> entrada = pendientes.extraer();
            RegistroDijkstra<T> actual = registros.buscar(entrada.getNodo().getId());
            if (actual == null || actual.visitado) continue;
            if (entrada.getDistancia() > actual.distancia) continue;

            actual.visitado = true;
            if (actual.nodo.getId().equals(idDestino)) break;

            for (Arista<T> arista : actual.nodo.getVecinos()) {
                RegistroDijkstra<T> vecino = registros.buscar(arista.getDestino().getId());
                if (vecino == null || vecino.visitado) continue;
                double nuevaDistancia = actual.distancia + arista.getPeso();
                if (nuevaDistancia < vecino.distancia) {
                    vecino.distancia = nuevaDistancia;
                    vecino.previo = actual.nodo;
                    pendientes.insertar(new EntradaDijkstra<>(vecino.nodo, nuevaDistancia));
                }
            }
        }

        RegistroDijkstra<T> registroDestino = registros.buscar(idDestino);
        if (registroDestino == null || registroDestino.distancia == Double.MAX_VALUE) return ruta;

        NodoGrafo<T> actual = registroDestino.nodo;
        while (actual != null) {
            ruta.add(0, actual);
            RegistroDijkstra<T> registroActual = registros.buscar(actual.getId());
            actual = registroActual != null ? registroActual.previo : null;
        }
        return ruta;
    }

    /**
     * Busca un nodo por su id y devuelve el nodo si existe, o null si no existe.
     * @param id
     * @return
     */
    public NodoGrafo<T> buscarNodo(String id) {
        return catalogoNodos.buscar(id);
    }

    /**
     * Devuelve una lista de todos los nodos del grafo.
     * @return
     */
    public List<NodoGrafo<T>> getNodos() {
        List<NodoGrafo<T>> lista = new ArrayList<>();
        for (NodoGrafo<T> nodo : nodos) lista.add(nodo);
        return lista;
    }

    /**
     * Clase interna para almacenar la información de cada nodo durante el algoritmo de Dijkstra.
     * @param <T>
     */
    private static class RegistroDijkstra<T> {
        private final NodoGrafo<T> nodo;
        private double distancia = Double.MAX_VALUE;
        private NodoGrafo<T> previo;
        private boolean visitado;

        private RegistroDijkstra(NodoGrafo<T> nodo) {
            this.nodo = nodo;
        }
    }
}

