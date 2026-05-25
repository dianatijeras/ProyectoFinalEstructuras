package techpark.model.parque;

/**
 * Representa un sendero entre dos atracciones en el parque, con un peso que puede representar la distancia o el tiempo de recorrido.
 */
public class Sendero {
    private Atraccion origen;
    private Atraccion destino;
    private double peso;

    /**
     * Constructor de la clase Sendero.
     * @param origen
     * @param destino
     * @param peso
     */
    public Sendero(Atraccion origen, Atraccion destino, double peso) {
        this.origen = origen; this.destino = destino; this.peso = peso;
    }

    /**
     * Metodo que devuelve la atracción de origen del sendero
     * @return
     */
    public Atraccion getOrigen() {
        return origen;
    }

    /**
     * Metodo que devuelve la atracción de destino del sendero
     * @return
     */
    public Atraccion getDestino() {
        return destino;
    }

    /**
     * Metodo que devuelve el peso del sendero, que puede representar la distancia o el tiempo de recorrido entre las atracciones de origen y destino
     * @return
     */
    public double getPeso() {
        return peso;
    }
}
