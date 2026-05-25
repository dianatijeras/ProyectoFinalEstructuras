package techpark.model.usuarios;

import techpark.enums.Rol;
import techpark.estructuras.conjunto.SetArbol;
import techpark.estructuras.grafo.NodoGrafo;
import techpark.estructuras.lista.ListaEnlazada;
import techpark.model.eventos.Notificacion;
import techpark.model.eventos.RegistroVisita;
import techpark.model.parque.Atraccion;
import techpark.model.tickets.Ticket;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa a un visitante del parque.
 */
public class Visitante extends Usuario {
    private double estatura;
    private double saldoVirtual;
    private NodoGrafo<Atraccion> ubicacionActual;
    private Atraccion ultimaAtraccionVisitada;
    private Ticket ticketActivo;
    private final ListaEnlazada<RegistroVisita> historialVisitas = new ListaEnlazada<>();
    private final SetArbol<Atraccion> favoritos = new SetArbol<>();
    private final List<Notificacion> notificaciones = new ArrayList<>();
    private boolean enCola;

    /**
     * Constructor para crear un nuevo visitante. El rol se asigna automáticamente como VISITANTE.
     * @param id
     * @param nombre
     * @param documento
     * @param edad
     * @param password
     * @param estatura
     * @param saldoVirtual
     * @param foto
     */
    public Visitante(String id, String nombre, String documento, int edad, String password, double estatura, double saldoVirtual, String foto) {
        super(id, nombre, documento, edad, password, Rol.VISITANTE);
        this.estatura = estatura;
        this.saldoVirtual = Math.max(0, saldoVirtual);
    }

    /**
     * Actualiza el perfil del visitante.
     * El nombre no puede ser nulo o vacío, la edad no puede ser negativa, la estatura no puede ser negativa y el saldo virtual no puede ser negativo.
     * @param nombre
     * @param edad
     * @param estatura
     * @param saldoVirtual
     */
    public void actualizarPerfil(String nombre, int edad, double estatura, double saldoVirtual) {
        actualizarDatosBasicos(nombre, edad);
        if (estatura < 0) throw new IllegalArgumentException("La estatura no puede ser negativa");
        if (saldoVirtual < 0) throw new IllegalArgumentException("El saldo virtual no puede ser negativo");
        this.estatura = estatura;
        this.saldoVirtual = saldoVirtual;
    }

    /**
     * Recarga el saldo virtual del visitante.
     * El valor a recargar debe ser positivo.
     * @param valor
     */
    public void recargarSaldo(double valor) { if (valor > 0) saldoVirtual += valor; }
    public boolean descontarSaldo(double valor) { if (saldoVirtual >= valor) { saldoVirtual -= valor; return true; } return false; }
    public void agregarHistorial(RegistroVisita registro) {
        historialVisitas.agregarInicio(registro);
        if (registro != null) this.ultimaAtraccionVisitada = registro.getAtraccion();
    }

    /**
     * Agrega una atracción a la lista de favoritos del visitante.
     * @param atraccion
     * @return
     */
    public boolean agregarFavorito(Atraccion atraccion){
        return favoritos.agregar(atraccion.getId(), atraccion);
    }

    /*+
        * Agrega una notificación a la lista de notificaciones del visitante.
     */
    public void agregarNotificacion(Notificacion n){
        notificaciones.add(n);
    }

    /**
     * Metodo que devuelve la estatura del visitante
     * @return
     */
    public double getEstatura(){
        return estatura;
    }

    /**
     * Metodo que devuelve el saldo virtual del visitante
     * @return
     */
    public double getSaldoVirtual(){
        return saldoVirtual;
    }

    /**
     * Metodo que devuelve la ubicación actual del visitante en el parque.
     * La ubicación se representa como un nodo en el grafo de atracciones.
     * @return
     */
    public NodoGrafo<Atraccion> getUbicacionActual(){
        return ubicacionActual;
    }

    /**
     * Metodo que devuelve el nombre de la ubicación actual del visitante.
     * Si el visitante ha visitado una atracción, se devuelve el nombre de esa atracción.
     * @return
     */
    public String getNombreUbicacionActual() {
        if (ultimaAtraccionVisitada != null) return ultimaAtraccionVisitada.getNombre();
        return ubicacionActual != null && ubicacionActual.getDato() != null ? ubicacionActual.getDato().getNombre() : null;
    }

    /**
     * Metodo que devuelve la última atracción visitada por el visitante.
     * Si el visitante no ha visitado ninguna atracción, se devuelve null.
     * @return
     */
    public Atraccion getUltimaAtraccionVisitada(){
        return ultimaAtraccionVisitada;
    }

    /**
     * Metodo que actualiza la ubicación actual del visitante en el parque.
     * @param ubicacionActual
     */
    public void setUbicacionActual(NodoGrafo<Atraccion> ubicacionActual){
        this.ubicacionActual = ubicacionActual;
    }

    /**
     * Metodo que devuelve el ticket activo del visitante.
     * Si el visitante no tiene un ticket activo, se devuelve null.
     * @return
     */
    public Ticket getTicketActivo(){
        return ticketActivo;
    }

    /**
     * Metodo que actualiza el ticket activo del visitante.
     * @param ticketActivo
     */
    public void setTicketActivo(Ticket ticketActivo){
        this.ticketActivo = ticketActivo;
    }

    /**
     * Metodo que devuelve el historial de visitas del visitante.
     * El historial se representa como una lista enlazada de registros de visita.
     * @return
     */
    public ListaEnlazada<RegistroVisita> getHistorialVisitas(){
        return historialVisitas;
    }

    /**
     * Metodo que devuelve la lista de atracciones favoritas del visitante.
     * La lista de favoritos se representa como un conjunto implementado con un árbol.
     * @return
     */
    public SetArbol<Atraccion> getFavoritos(){
        return favoritos;
    }

    /**
     * Metodo que devuelve la lista de notificaciones del visitante.
     * La lista de notificaciones se representa como una lista de objetos Notificacion.
     * @return
     */
    public List<Notificacion> getNotificaciones(){
        return notificaciones;
    }

    /**
     * Metodo que indica si el visitante se encuentra actualmente en una cola para una atracción.
     * @return
     */
    public boolean isEnCola(){
        return enCola;
    }

    /**
     * Metodo que actualiza el estado de si el visitante se encuentra actualmente en una cola para una atracción.
     * @param enCola
     */
    public void setEnCola(boolean enCola){
        this.enCola = enCola;
    }
}
