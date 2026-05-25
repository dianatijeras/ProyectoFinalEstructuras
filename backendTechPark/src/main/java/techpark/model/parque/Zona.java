package techpark.model.parque;

import techpark.estructuras.lista.ListaEnlazada;
import techpark.model.usuarios.Operador;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa una zona del parque, que puede contener varias atracciones y operadores asignados.
 */
public class Zona {
    private String id;
    private String nombre;
    private int capacidadMaxima;
    private int aforoActual;
    private boolean disponible = true;
    private final List<Atraccion> atracciones = new ArrayList<>();
    private final ListaEnlazada<Operador> operadoresAsignados = new ListaEnlazada<>();

    /**
     * Constructor de la clase Zona.
     * @param id
     * @param nombre
     * @param capacidadMaxima
     */
    public Zona(String id, String nombre, int capacidadMaxima) {
        this.id = id;
        this.nombre = nombre;
        this.capacidadMaxima = capacidadMaxima;
    }

    /**
     * Agrega una atracción a la zona y establece la relación bidireccional entre la zona y la atracción.
     * @param atraccion
     */
    public void agregarAtraccion(Atraccion atraccion) {
        atracciones.add(atraccion);
        atraccion.setZona(this);
    }

    /**
     * Agrega un operador a la zona y establece la relación bidireccional entre la zona y el operador.
     * @param operador
     */
    public void agregarOperador(Operador operador) {
        if (operador == null) throw new IllegalArgumentException("Operador invalido");
        if (!operadoresAsignados.contiene(operador)) {
            operadoresAsignados.agregarFinal(operador);
        }
        operador.setZonaAsignada(this);
    }

    /**
     * Remueve un operador de la zona, asegurándose de que la zona no quede sin operadores responsables.
     * @param operador
     */
    public void removerOperador(Operador operador) {
        if (operador == null) throw new IllegalArgumentException("Operador invalido");
        if (operadoresAsignados.getTamanio() <= 1) {
            throw new IllegalStateException("La zona no puede quedar sin operador responsable");
        }
        if (operadoresAsignados.eliminar(operador)) {
            operador.setZonaAsignada(null);
        }
    }

    /**
     * Verifica si hay aforo disponible en la zona, considerando tanto la capacidad máxima como la disponibilidad de la zona.
     * @return
     */
    public boolean hayAforoDisponible() { return disponible && aforoActual < capacidadMaxima; }
    public void actualizarDatos(String nombre, int capacidadMaxima) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre de zona obligatorio");
        if (capacidadMaxima < aforoActual) throw new IllegalArgumentException("La capacidad no puede ser menor al aforo actual");
        this.nombre = nombre;
        this.capacidadMaxima = capacidadMaxima;
    }

    /**
     * Actualiza los datos de la zona, incluyendo su nombre, capacidad máxima y disponibilidad.
     * @param nombre
     * @param capacidadMaxima
     * @param disponible
     */
    public void actualizarDatos(String nombre, int capacidadMaxima, boolean disponible) {
        actualizarDatos(nombre, capacidadMaxima);
        this.disponible = disponible;
    }

    /**
     * Cambia la disponibilidad de la zona, permitiendo marcarla como disponible o no disponible según sea necesario.
     * @param disponible
     */
    public void cambiarDisponibilidad(boolean disponible) {
        this.disponible = disponible;
    }

    /**
     * Aumenta el aforo actual de la zona en uno, siempre y cuando haya aforo disponible.
     */
    public void aumentarAforo() {
        if (hayAforoDisponible()) aforoActual++;
    }

    /**
     * Disminuye el aforo actual de la zona en uno, asegurándose de que no se reduzca por debajo de cero.
     */
    public void disminuirAforo() {
        if (aforoActual > 0) aforoActual--;
    }

    /**
     * Verifica si la zona tiene operadores asignados.
     * @return
     */
    public boolean tieneOperadores() {
        return operadoresAsignados.getTamanio() > 0;
    }

    /**
     * Metodo que devuelve el id de la zona
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Metodo que devuelve el nombre de la zona
     * @return
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Metodo que devuelve la capacidad máxima de la zona
     * @return
     */
    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    /**
     * Metodo que devuelve el aforo actual de la zona
     * @return
     */
    public int getAforoActual() {
        return aforoActual;
    }

    /**
     * Metodo que indica si la zona está disponible o no
     * @return
     */
    public boolean isDisponible() {
        return disponible;
    }

    /**
     * Metodo que devuelve la lista de atracciones asociadas a la zona
     * @return
     */
    public List<Atraccion> getAtracciones() {
        return atracciones;
    }

    /**
     * Metodo que devuelve la lista de operadores asignados a la zona
     * @return
     */
    public ListaEnlazada<Operador> getOperadoresAsignados() {
        return operadoresAsignados;
    }

    /**
     * Metodo que devuelve una representación en forma de cadena del nombre de la zona.
     * @return
     */
    public String toString() {
        return nombre;
    }
}

