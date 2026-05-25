package techpark.estructuras.arbol;

/**
 * Nodo de un árbol binario de búsqueda, con clave de tipo String y dato genérico.
 * @param <T>
 */
public class NodoArbol<T> {
    private String clave;
    private T dato;
    private NodoArbol<T> izquierdo;
    private NodoArbol<T> derecho;

    /**
     * Constructor para crear un nuevo nodo con la clave y dato especificados.
     * @param clave
     * @param dato
     */
    public NodoArbol(String clave, T dato) {
        this.clave = clave; this.dato = dato;
    }

    /**
     * Devuelve la clave del nodo.
     * @return
     */
    public String getClave() {
        return clave;
    }

    /**
     * Devuelve el dato almacenado en el nodo.
     * @return
     */
    public T getDato() {
        return dato;
    }

    /**
     * Devuelve el nodo hijo izquierdo.
     * @return
     */
    public NodoArbol<T> getIzquierdo() {
        return izquierdo;
    }

    /**
     * Devuelve el nodo hijo derecho.
     * @return
     */
    public NodoArbol<T> getDerecho() {
        return derecho;
    }

    /**
     * Establece el dato almacenado en el nodo.
     * @param dato
     */
    public void setDato(T dato) {
        this.dato = dato;
    }

    /**
     * Establece el nodo hijo izquierdo.
     * @param izquierdo
     */
    public void setIzquierdo(NodoArbol<T> izquierdo) {
        this.izquierdo = izquierdo;
    }

    /**
     * Establece el nodo hijo derecho.
     * @param derecho
     */
    public void setDerecho(NodoArbol<T> derecho) {
        this.derecho = derecho;
    }
}
