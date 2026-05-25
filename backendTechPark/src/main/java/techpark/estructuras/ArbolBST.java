package techpark.estructuras;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de un árbol binario de búsqueda (BST) genérico.
 * @param <T>
 */
public class ArbolBST<T> {
    private NodoArbol<T> raiz;
    private int tamanio;

    /**
     * Inserta un nuevo nodo con la clave y dato especificados. Si la clave ya existe, actualiza el dato.
     * @param clave
     * @param dato
     */
    public void insertar(String clave, T dato) {
        if (clave == null || clave.isBlank()) throw new IllegalArgumentException("La clave no puede estar vacia");
        raiz = insertarRec(raiz, clave, dato);
    }

    /**
     * Método recursivo para insertar un nuevo nodo en el árbol. Compara la clave con el nodo actual y decide si ir a la izquierda o derecha.
     * @param nodo
     * @param clave
     * @param dato
     * @return
     */
    private NodoArbol<T> insertarRec(NodoArbol<T> nodo, String clave, T dato) {
        if (nodo == null) { tamanio++; return new NodoArbol<>(clave, dato); }
        int cmp = clave.compareToIgnoreCase(nodo.getClave());
        if (cmp < 0) nodo.setIzquierdo(insertarRec(nodo.getIzquierdo(), clave, dato));
        else if (cmp > 0) nodo.setDerecho(insertarRec(nodo.getDerecho(), clave, dato));
        else nodo.setDato(dato);
        return nodo;
    }

    /**
     * Busca un nodo por su clave y devuelve el dato asociado. Si la clave no se encuentra, devuelve null.
     * @param clave
     * @return
     */
    public T buscar(String clave) {
        NodoArbol<T> actual = raiz;
        while (actual != null) {
            int cmp = clave.compareToIgnoreCase(actual.getClave());
            if (cmp == 0) return actual.getDato();
            actual = cmp < 0 ? actual.getIzquierdo() : actual.getDerecho();
        }
        return null;
    }

    /**
     * Verifica si el árbol contiene un nodo con la clave especificada.
     * @param clave
     * @return
     */
    public boolean contiene(String clave) { return buscar(clave) != null; }

    /**
     * Devuelve una lista de los datos almacenados en el árbol siguiendo un recorrido inorden.
     * @return
     */
    public List<T> inorden() {
        List<T> datos = new ArrayList<>();
        inordenRec(raiz, datos);
        return datos;
    }

    /**
     * Método recursivo para realizar un recorrido inorden del árbol. Agrega los datos de cada nodo a la lista proporcionada.
     * @param nodo
     * @param datos
     */
    private void inordenRec(NodoArbol<T> nodo, List<T> datos) {
        if (nodo == null) return;
        inordenRec(nodo.getIzquierdo(), datos);
        datos.add(nodo.getDato());
        inordenRec(nodo.getDerecho(), datos);
    }

    /**
     * Devuelve el número de nodos almacenados en el árbol.
     * @return
     */
    public int getTamanio() {
        return tamanio;
    }

    /**
     * Devuelve la raíz del árbol.
     * @return
     */
    public NodoArbol<T> getRaiz() {
        return raiz;
    }
}

