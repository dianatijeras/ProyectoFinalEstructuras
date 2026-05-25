package techpark.estructuras.grafo;

/**
 * Representa una arista en un grafo, con un nodo de origen, un nodo de destino y un peso asociado.
 * @param <T>
 */
public class Arista<T> {
    private NodoGrafo<T> origen;
    private NodoGrafo<T> destino;
    private double peso;

    /**
     * Crea una nueva arista con el nodo de origen, el nodo de destino y el peso especificados.
     * @param origen
     * @param destino
     * @param peso
     */
    public Arista(NodoGrafo<T> origen, NodoGrafo<T> destino, double peso) {
        this.origen = origen; this.destino = destino; this.peso = peso;
    }

    /**
     * Devuelve el nodo de origen, el nodo de destino y el peso de la arista.
     * @return
     */
    public NodoGrafo<T> getOrigen() {
        return origen;
    }

    /**
     * Devuelve el nodo de destino de la arista.
     * @return
     */
    public NodoGrafo<T> getDestino() {
        return destino;
    }

    /**
     * Devuelve el peso de la arista.
     * @return
     */
    public double getPeso() {
        return peso;
    }
}
