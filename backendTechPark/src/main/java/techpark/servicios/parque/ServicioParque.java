package techpark.servicios.parque;

import techpark.enums.TipoClima;
import techpark.enums.TipoTicket;
import techpark.model.parque.Parque;
import techpark.model.parque.Zona;
import techpark.model.tickets.Ticket;
import techpark.model.usuarios.Operador;
import techpark.model.usuarios.Usuario;
import techpark.model.usuarios.Visitante;
import techpark.servicios.alertas.ServicioAlertas;
import techpark.utilidades.GeneradorId;

import java.time.LocalTime;

/**
 * clase central que maneja las operaciones principales del parque de diversiones.
 * Se encarga de coordinar las interacciones entre los visitantes, el parque y el servicio de alertas
 */
public class ServicioParque {
    private final Parque parque;
    private final ServicioAlertas servicioAlertas;

    /**
     * Constructor de la clase ServicioParque, recibe una instancia del parque y del servicio de alertas para poder coordinar las operaciones del parque.
     * @param parque
     * @param servicioAlertas
     */
    public ServicioParque(Parque parque, ServicioAlertas servicioAlertas) {
        this.parque = parque;
        this.servicioAlertas = servicioAlertas;
    }

    /**
     * Método para vender un ticket a un visitante.
     * Si el parque o la zona no tienen aforo disponible, se lanza una excepción.
     * @param visitante
     * @param tipo
     * @param zonaEntrada
     * @return
     */
    public Ticket venderTicket(Visitante visitante, TipoTicket tipo, Zona zonaEntrada) {
        return venderTicket(visitante, tipo, zonaEntrada, 1);
    }

    /**
     * Sobrecarga del método venderTicket para manejar la venta de tickets familiares, donde se puede aplicar un descuento dependiendo de la cantidad de personas en el grupo familiar.
     * @param visitante
     * @param tipo
     * @param zonaEntrada
     * @param cantidadPersonasFamilia
     * @return
     */
    public Ticket venderTicket(Visitante visitante, TipoTicket tipo, Zona zonaEntrada, int cantidadPersonasFamilia) {
        if (!parque.hayAforoDisponible()) throw new IllegalStateException("Parque sin aforo disponible");
        if (zonaEntrada != null && !zonaEntrada.hayAforoDisponible()) throw new IllegalStateException("Zona sin aforo disponible");
        double precio = calcularPrecioTicket(tipo, cantidadPersonasFamilia);
        Ticket ticket = new Ticket(GeneradorId.generarId("TIC-"), tipo, precio, visitante);
        ticket.setZonaIngreso(zonaEntrada);
        visitante.setTicketActivo(ticket);
        parque.registrarIngresoDiario(precio);
        return ticket;
    }

    /**
     * Método privado para calcular el precio del ticket dependiendo del tipo de ticket y la cantidad de personas en el grupo familiar (en caso de ser un ticket familiar).
     * @param tipo
     * @param cantidadPersonasFamilia
     * @return
     */
    private double calcularPrecioTicket(TipoTicket tipo, int cantidadPersonasFamilia) {
        double precioBase = 50000;
        if (tipo == TipoTicket.FAST_PASS) return 80000;
        if (tipo == TipoTicket.FAMILIAR) {
            // Regla sencilla: 20% de descuento si el grupo familiar tiene 4 o mas personas.
            return cantidadPersonasFamilia >= 4 ? precioBase * 0.80 : precioBase;
        }
        return precioBase;
    }

    /**
     * Registra el ingreso de un visitante al parque, aumentando el aforo del parque y de la zona correspondiente si se especifica una zona de ingreso.
     * @param zona
     */
    public void registrarIngreso(Zona zona) {
        parque.aumentarAforo();
        if (zona != null) zona.aumentarAforo();
    }

    /**
     * Asigna un operador a una zona específica.
     * Si el operador ya estaba asignado a otra zona, se remueve de esa zona antes de asignarlo a la nueva zona.
     * @param operador
     * @param zona
     */
    public void asignarOperador(Operador operador, Zona zona) {
        if (operador == null || zona == null) throw new IllegalArgumentException("Datos incompletos");
        Zona zonaAnterior = operador.getZonaAsignada();
        if (zonaAnterior != null && zonaAnterior != zona) {
            zonaAnterior.removerOperador(operador);
        }
        zona.agregarOperador(operador);
    }

    /**
     * Reasigna un operador a una nueva zona, utilizando el método asignarOperador para manejar la lógica de reasignación.
     * @param operador
     * @param nuevaZona
     */
    public void reasignarOperador(Operador operador, Zona nuevaZona) {
        asignarOperador(operador, nuevaZona);
    }

    /**
     * Método para expirar los tickets activos de los visitantes al final del día, si la hora actual es igual o posterior a las 6:00 p. m.
     * @param horaActual
     * @return
     */
    public int expirarTicketsActivosSiCorresponde(LocalTime horaActual) {
        if (horaActual == null) horaActual = LocalTime.now();
        if (horaActual.isBefore(LocalTime.of(18, 0))) return 0;
        int expirados = 0;
        for (Usuario usuario : parque.getCatalogoUsuarios().inorden()) {
            if (usuario instanceof Visitante visitante && visitante.getTicketActivo() != null && visitante.getTicketActivo().estaActivo()) {
                Ticket ticket = visitante.getTicketActivo();
                ticket.expirar();
                parque.disminuirAforo();
                if (ticket.getZonaIngreso() != null) ticket.getZonaIngreso().disminuirAforo();
                Notificacion notificacion = new Notificacion(GeneradorId.generarId("NOT-"), "Tu ticket ha expirado por cierre de jornada a las 6:00 p. m.", TipoNotifEnum.ESTADO_ATRACCION);
                notificacion.agregarDestinatario(visitante);
                parque.registrarNotificacionGlobal(notificacion);
                expirados++;
            }
        }
        return expirados;
    }

    /**
     * Método para activar una alerta climática en el parque, utilizando el servicio de alertas para notificar a los visitantes sobre el tipo de clima adverso que se ha detectado.
     * @param tipo
     */
    public void activarAlertaClimatica(TipoClima tipo) {
        servicioAlertas.activarAlertaClimatica(parque, tipo);
    }
}

