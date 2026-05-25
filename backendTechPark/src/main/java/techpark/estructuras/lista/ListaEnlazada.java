package techpark.estructuras.lista;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * Implementación de una lista enlazada genérica.
 * @param <T>
 */
public class ListaEnlazada<T> implements Iterable<T> {
    private NodoLista<T> cabeza;
    private int tamanio;

    /**
     * Agrega un elemento al inicio de la lista.
     * @param dato
     */
    public void agregarInicio(T dato) {
        NodoLista<T> nuevo = new NodoLista<>(dato);
        nuevo.setSiguiente(cabeza);
        cabeza = nuevo;
        tamanio++;
    }

    /**
     * Agrega un elemento al final de la lista.
     * @param dato
     */
    public void agregarFinal(T dato) {
        NodoLista<T> nuevo = new NodoLista<>(dato);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            NodoLista<T> actual = cabeza;
            while (actual.getSiguiente() != null) actual = actual.getSiguiente();
            actual.setSiguiente(nuevo);
        }
        tamanio++;
    }

    /**
     * Elimina la primera ocurrencia del elemento dado en la lista.
     * @param dato
     * @return
     */
    public boolean eliminar(T dato) {
        if (cabeza == null) return false;
        if ((cabeza.getDato() == null && dato == null) || (cabeza.getDato() != null && cabeza.getDato().equals(dato))) {
            cabeza = cabeza.getSiguiente();
            tamanio--;
            return true;
        }
        NodoLista<T> actual = cabeza;
        while (actual.getSiguiente() != null) {
            T valor = actual.getSiguiente().getDato();
            if ((valor == null && dato == null) || (valor != null && valor.equals(dato))) {
                actual.setSiguiente(actual.getSiguiente().getSiguiente());
                tamanio--;
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    /**
     * Verifica si la lista contiene el elemento dado.
     * @param dato
     * @return
     */
    public boolean contiene(T dato) {
        for (T item : this) if ((item == null && dato == null) || (item != null && item.equals(dato))) return true;
        return false;
    }

    /**
     * Obtiene el elemento en la posición dada .
     * @param indice
     * @return
     */
    public T obtener(int indice) {
        if (indice < 0 || indice >= tamanio) throw new IndexOutOfBoundsException("Indice fuera de rango");
        NodoLista<T> actual = cabeza;
        for (int i = 0; i < indice; i++) actual = actual.getSiguiente();
        return actual.getDato();
    }

    /**
     * Obtiene el tamaño de la lista.
     * @return
     */
    public int getTamanio() {
        return tamanio;
    }

    /**
     * Verifica si la lista está vacía.
     * @return
     */
    public boolean estaVacia() {
        return tamanio == 0;
    }

    /**
     * Obtiene el nodo cabeza de la lista (para uso interno).
     * @return
     */
    public NodoLista<T> getCabeza() {
        return cabeza;
    }

    /**
     * Aplica la acción dada a cada elemento de la lista.
     * @param accion
     */
    public void forEachItem(Consumer<T> accion) {
        for (T item : this) accion.accept(item);
    }

    /**
     * Devuelve un iterador para recorrer los elementos de la lista.
     * @return
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private NodoLista<T> actual = cabeza;
            public boolean hasNext() { return actual != null; }
            public T next() {
                if (actual == null) throw new NoSuchElementException();
                T dato = actual.getDato();
                actual = actual.getSiguiente();
                return dato;
            }
        };
    }
}

