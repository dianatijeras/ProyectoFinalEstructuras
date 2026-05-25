package techpark.estructuras.cola;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de una cola de prioridad usando un heap binario.
 * @param <T>
 */
public class ColaPrioridad<T extends Comparable<T>> {
    private final ArrayList<T> heap = new ArrayList<>();

    /**
     * Inserta un elemento en la cola de prioridad. El elemento se coloca en la posición correcta para mantener la propiedad del heap.
     * @param dato
     */
    public void insertar(T dato) {
        if (dato == null) throw new IllegalArgumentException("No se puede insertar null");
        heap.add(dato);
        subir(heap.size() - 1);
    }

    /**
     * Extrae y devuelve el elemento de mayor prioridad (el mínimo en un heap de min). El último elemento se mueve a la raíz y se ajusta el heap para mantener la propiedad.
     * @return
     */
    public T extraer() {
        if (estaVacia()) return null;
        T raiz = heap.get(0);
        T ultimo = heap.remove(heap.size() - 1);
        if (!heap.isEmpty()) {
            heap.set(0, ultimo);
            bajar(0);
        }
        return raiz;
    }

    /**
     * Devuelve el elemento de mayor prioridad sin eliminarlo de la cola.
     */
    public T mirar() {
        return estaVacia() ? null : heap.get(0);
    }

    /**
     * Indica si la cola de prioridad está vacía.
     * @return
     */
    public boolean estaVacia() {
        return heap.isEmpty();
    }

    /**
     * Devuelve el número de elementos en la cola de prioridad.
     * @return
     */
    public int getTamanio() {
        return heap.size();
    }

    /**
     * Devuelve una lista con los elementos de la cola de prioridad en el orden en que se encuentran en el heap.
     * @return
     */
    public List<T> comoLista() {
        return new ArrayList<>(heap);
    }

    /**
     * Elimina todos los elementos de la cola de prioridad
     */
    public void limpiar() {
        heap.clear();
    }

    /**
     * Sube un elemento en el heap para restaurar la propiedad del heap después de una inserción. Compara el elemento con su padre y lo intercambia si es menor, repitiendo el proceso hasta que se alcance la raíz o se encuentre un padre menor o igual.
     * @param i
     */
    private void subir(int i) {
        while (i > 0) {
            int padre = (i - 1) / 2;
            if (heap.get(i).compareTo(heap.get(padre)) >= 0) break;
            intercambiar(i, padre);
            i = padre;
        }
    }

    /**
     * Baja un elemento en el heap para restaurar la propiedad del heap después de una extracción. Compara el elemento con sus hijos y lo intercambia con el menor de ellos si es mayor, repitiendo el proceso hasta que se alcance una hoja o se encuentre un hijo mayor o igual.
     * @param i
     */
    private void bajar(int i) {
        while (true) {
            int izq = 2 * i + 1;
            int der = 2 * i + 2;
            int menor = i;
            if (izq < heap.size() && heap.get(izq).compareTo(heap.get(menor)) < 0) menor = izq;
            if (der < heap.size() && heap.get(der).compareTo(heap.get(menor)) < 0) menor = der;
            if (menor == i) break;
            intercambiar(i, menor);
            i = menor;
        }
    }

    /**
     * Intercambia dos elementos en el heap dado sus índices.
     * @param a
     * @param b
     */
    private void intercambiar(int a, int b) {
        T tmp = heap.get(a); heap.set(a, heap.get(b)); heap.set(b, tmp);
    }
}

