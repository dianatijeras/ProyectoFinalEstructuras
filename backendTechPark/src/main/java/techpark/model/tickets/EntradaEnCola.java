package techpark.model.tickets;

import techpark.model.usuarios.Visitante;

import java.time.LocalDateTime;

/**
 * Representa una entrada en la cola de espera para acceder a una atracción.
 * Implementa Comparable para ordenar por prioridad y hora de ingreso.
 */
public class EntradaEnCola implements Comparable<EntradaEnCola> {
    private Visitante visitante;
    private Ticket ticket;
    private LocalDateTime horaIngreso;
    private int prioridad;


    /**
     * Constructor para crear una nueva entrada en la cola.
     * La hora de ingreso se establece automáticamente al momento de crear la entrada.
     * @param visitante
     * @param ticket
     */
    public EntradaEnCola(Visitante visitante, Ticket ticket){
        this(visitante, ticket, LocalDateTime.now());
    }

    /**
     * Constructor para crear una nueva entrada en la cola con una hora de ingreso específica.
     * @param visitante
     * @param ticket
     * @param horaIngreso
     */
    public EntradaEnCola(Visitante visitante, Ticket ticket, LocalDateTime horaIngreso) {
        this.visitante = visitante;
        this.ticket = ticket;
        this.horaIngreso = horaIngreso == null ? LocalDateTime.now() : horaIngreso;
        this.prioridad = ticket.getPrioridad();
    }

    /**
     * Compara esta entrada con otra para determinar su orden en la cola.
     * Primero se compara por prioridad (menor valor es mayor prioridad), y si son iguales
     * @param otra the object to be compared.
     * @return
     */
    @Override
    public int compareTo(EntradaEnCola otra) {
        int cmp = Integer.compare(this.prioridad, otra.prioridad);
        if (cmp != 0) return cmp;
        return this.horaIngreso.compareTo(otra.horaIngreso);
    }

    /**
     * Metodo que devuelve el visitante asociado a esta entrada en la cola
     * @return
     */
    public Visitante getVisitante(){
        return visitante;
    }

    /**
     * Metodo que devuelve el ticket asociado a esta entrada en la cola
     * @return
     */
    public Ticket getTicket(){
        return ticket;
    }

    /**
     * Metodo que devuelve la hora de ingreso a la cola
     * @return
     */
    public LocalDateTime getHoraIngreso(){
        return horaIngreso;
    }

    /**
     * Metodo que devuelve la prioridad de esta entrada en la cola, basada en el ticket del visitante
     * @return
     */
    public int getPrioridad(){
        return prioridad;
    }

    /**
     * Metodo que devuelve una representación en cadena de esta entrada en la cola, mostrando el nombre del visitante y su prioridad
     * @return
     */
    public String toString(){
        return visitante.getNombre() + " prioridad " + prioridad;
    }
}
