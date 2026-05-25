package techpark.estructuras.conjunto;

import techpark.estructuras.arbol.ArbolBST;

import java.util.List;

/**
 * Implementación de un conjunto utilizando un árbol binario de búsqueda (BST).
 * @param <T>
 */
public class SetArbol<T> {
    private final ArbolBST<T> arbol = new ArbolBST<>();

    /**
     * Agrega un elemento al conjunto. Si la clave ya existe, no se agrega y se retorna false.
     * @param clave
     * @param dato
     * @return
     */
    public boolean agregar(String clave, T dato) {
        if (arbol.contiene(clave)) return false;
        arbol.insertar(clave, dato);
        return true;
    }

    /**
     *
     * @param clave
     * @return
     */
    public boolean contiene(String clave) {
        return arbol.contiene(clave);
    }

    /**
     * Elimina un elemento del conjunto por su clave. Retorna true si se eliminó, false si no se encontró.
     * @param clave
     * @return
     */
    public T buscar(String clave) {
        return arbol.buscar(clave);
    }

    /**
     * Lista todos los elementos del conjunto en orden alfabético de sus claves.
     * @return
     */
    public List<T> listar() {
        return arbol.inorden();
    }

    /**
     * Retorna el número de elementos en el conjunto.
     * @return
     */
    public int getTamanio() {
        return arbol.getTamanio();
    }
}
