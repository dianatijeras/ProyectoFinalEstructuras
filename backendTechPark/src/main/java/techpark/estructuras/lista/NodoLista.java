package techpark.estructuras.lista;

/**
 * Nodo de una lista enlazada genérica.
 * @param <T>
 */
public class NodoLista<T> {
    private T dato;
    private NodoLista<T> siguiente;

    /**
     * Constructor del nodo de lista.
     * @param dato
     */
    public NodoLista(T dato) {
        this.dato = dato;
    }

    /**
     * Obtiene el dato almacenado en el nodo.
     * @return
     */
    public T getDato() {
        return dato;
    }

    /**
     * Obtiene el siguiente nodo en la lista.
     * @return
     */
    public NodoLista<T> getSiguiente() {
        return siguiente;
    }

    /**
     * Establece el siguiente nodo en la lista.
     * @param siguiente
     */
    public void setSiguiente(NodoLista<T> siguiente) {
        this.siguiente = siguiente;
    }
}
