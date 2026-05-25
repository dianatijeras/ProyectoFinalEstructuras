package techpark.estructuras.grafo;

/**
 * Clase que representa un nodo en un grafo. Cada nodo tiene un identificador único, un dato genérico y una lista de aristas que representan sus vecinos.
 * @param <T>
 */
public class NodoGrafo<T> {
    private String id;
    private T dato;
    private final ListaEnlazada<Arista<T>> vecinos = new ListaEnlazada<>();

    /**
     * Constructor para crear un nodo de grafo con un identificador y un dato asociado.
     * @param id
     * @param dato
     */
    public NodoGrafo(String id, T dato) {
        this.id = id;
        this.dato = dato;
    }

    /**
     * Obtiene el identificador del nodo.
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Obtiene el dato asociado al nodo.
     * @return
     */
    public T getDato() {
        return dato;
    }

    /**
     * Establece el dato asociado al nodo.
     * @param dato
     */
    public void setDato(T dato) {
        this.dato = dato;
    }

    /**
     * Obtiene la lista de aristas que representan los vecinos del nodo.
     * @return
     */
    public ListaEnlazada<Arista<T>> getVecinos() {
        return vecinos;
    }

    /**
     * Agrega una arista a la lista de vecinos del nodo.
     * @param arista
     */
    public void agregarVecino(Arista<T> arista) {
        vecinos.agregarFinal(arista);
    }

    /**
     * Devuelve una representación en cadena del nodo, que en este caso es su identificador.
     * @return
     */
    public String toString() {
        return id;
    }
}
