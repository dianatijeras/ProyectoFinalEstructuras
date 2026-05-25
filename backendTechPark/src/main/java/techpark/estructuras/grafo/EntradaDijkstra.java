package techpark.estructuras.grafo;

/**
 * Clase auxiliar para el algoritmo de Dijkstra, que representa una entrada en la cola de prioridad.
 * @param <T>
 */
public class EntradaDijkstra<T> implements Comparable<EntradaDijkstra<T>> {
    private final NodoGrafo<T> nodo;
    private final double distancia;

    /**
     * Constructor para crear una nueva entrada de Dijkstra con el nodo y la distancia acumulada.
     * @param nodo
     * @param distancia
     */
    public EntradaDijkstra(NodoGrafo<T> nodo, double distancia) {
        this.nodo = nodo;
        this.distancia = distancia;
    }

    /**
     * Métodos para obtener el nodo y la distancia acumulada de esta entrada de Dijkstra.
     * @return
     */
    public NodoGrafo<T> getNodo() {
        return nodo;
    }

    /**
     * Método para obtener la distancia acumulada desde el nodo de origen hasta el nodo actual representado por esta entrada de Dijkstra.
     * @return
     */
    public double getDistancia() {
        return distancia;
    }

    /**
     * Método para comparar esta entrada de Dijkstra con otra, basado en la distancia acumulada. Esto es necesario para que la cola de prioridad pueda ordenar las entradas correctamente.
     * @param otra the object to be compared.
     * @return
     */
    @Override
    public int compareTo(EntradaDijkstra<T> otra) {
        return Double.compare(this.distancia, otra.distancia);
    }
}
