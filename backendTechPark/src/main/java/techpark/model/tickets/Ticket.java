package techpark.model.tickets;

import techpark.model.usuarios.Visitante;

import java.time.LocalDateTime;

/**
 * Representa un ticket de entrada al parque.
 */
public class Ticket {
    private String id;
    private TipoTicket tipo;
    private double precio;
    private LocalDateTime fechaCompra;
    private EstadoTicket estado;
    private Visitante visitante;
    private Zona zonaIngreso;

    /**
     * Constructor para crear un nuevo ticket.
     * La fecha de compra se establece automáticamente al momento de crear el ticket, y el estado inicial es ACTIVO.
     * @param id
     * @param tipo
     * @param precio
     * @param visitante
     */
    public Ticket(String id, TipoTicket tipo, double precio, Visitante visitante) {
        this.id = id;
        this.tipo = tipo;
        this.precio = precio;
        this.visitante = visitante;
        this.fechaCompra = LocalDateTime.now();
        this.estado = EstadoTicket.ACTIVO;
    }

    /**
     * Metodo que devuelve la prioridad del ticket.
     * Los tickets FAST_PASS tienen mayor prioridad (1) que los tickets REGULAR (2).
     * @return
     */
    public int getPrioridad(){
        return tipo == TipoTicketEnum.FAST_PASS ? 1 : 2;
    }

    /**
     * Metodo que indica si el ticket está activo.
     * Un ticket solo puede ser usado si está en estado ACTIVO.
     * @return
     */
    public boolean estaActivo(){
        return estado == EstadoTicket.ACTIVO;
    }

    /**
     * Marca el ticket como usado.
     * Solo se puede marcar como usado un ticket que esté en estado ACTIVO.
     */
    public void marcarUsado(){
        estado = EstadoTicket.USADO;
    }

    /**
     * Marca el ticket como expirado.
     */
    public void expirar() {
        estado = EstadoTicket.EXPIRADO;
    }

    /**
     * Metodo que devuelve el id del ticket
     * @return
     */
    public String getId(){
        return id;
    }

    /**
     * Metodo que devuelve el tipo de ticket
     * @return
     */
    public TipoTicket getTipo(){
        return tipo;
    }

    /**
     * Metodo que devuelve el precio del ticket
     * @return
     */
    public double getPrecio(){
        return precio;
    }

    /**
     * Metodo que devuelve la fecha de compra del ticket
     * @return
     */
    public LocalDateTime getFechaCompra(){
        return fechaCompra;
    }

    /**
     * Metodo que devuelve el estado del ticket
     * @return
     */
    public EstadoTicket getEstado(){
        return estado;
    }

    /**
     * Metodo que devuelve el visitante asociado al ticket
     * @return
     */
    public Visitante getVisitante(){
        return visitante;
    }

    /**
     * Metodo que devuelve la zona de ingreso asociada al ticket.
     * @return
     */
    public Zona getZonaIngreso(){
        return zonaIngreso;
    }

    /**
     * Metodo que actualiza la zona de ingreso asociada al ticket.
     * @param zonaIngreso
     */
    public void setZonaIngreso(Zona zonaIngreso){
        this.zonaIngreso = zonaIngreso;
    }
}

