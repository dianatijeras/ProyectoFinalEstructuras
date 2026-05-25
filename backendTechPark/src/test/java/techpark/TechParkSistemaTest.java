package techpark;

import org.junit.jupiter.api.Test;
import techpark.datos.DatosIniciales;
import techpark.enums.TipoTicket;
import techpark.model.parque.Parque;
import techpark.model.tickets.Ticket;
import techpark.model.usuarios.Visitante;
import techpark.servicios.alertas.ServicioAlertas;
import techpark.servicios.parque.ServicioParque;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TechParkSistemaTest {

    @Test
    void debeCrearVisitanteConDatosValidos() {
        Visitante visitante = new Visitante("VIS-T01", "Juan Perez", "9001", 20, "123", 1.75, 50000, null);

        assertEquals("Juan Perez", visitante.getNombre());
        assertEquals("9001", visitante.getDocumento());
        assertEquals(20, visitante.getEdad());
        assertEquals(1.75, visitante.getEstatura());
        assertEquals(50000, visitante.getSaldoVirtual());
    }

    @Test
    void debeNormalizarSaldoNegativoDelVisitanteACero() {
        Visitante visitante = new Visitante("VIS-T02", "Ana Gomez", "9002", 18, "123", 1.60, -20000, null);

        assertEquals(0, visitante.getSaldoVirtual());
    }

    @Test
    void debeRechazarEdadYEstaturaNegativasAlActualizarVisitante() {
        Visitante visitante = new Visitante("VIS-T03", "Carlos Ruiz", "9003", 22, "123", 1.80, 40000, null);

        assertThrows(IllegalArgumentException.class,
                () -> visitante.actualizarPerfil("Carlos Ruiz", -1, 1.80, 40000));

        assertThrows(IllegalArgumentException.class,
                () -> visitante.actualizarPerfil("Carlos Ruiz", 22, -1.50, 40000));
    }

    @Test
    void debeComprarTicketGeneralCorrectamente() {
        DatosIniciales datos = cargarDatos();
        Parque parque = datos.getParque();
        ServicioParque servicioParque = new ServicioParque(parque, new ServicioAlertas());
        Visitante visitante = datos.getVisitantes().get(0);

        Ticket ticket = servicioParque.venderTicket(visitante, TipoTicket.GENERAL, parque.getZonas().get(0));

        assertNotNull(ticket);
        assertTrue(ticket.estaActivo());
        assertEquals(TipoTicket.GENERAL, ticket.getTipo());
        assertEquals(ticket, visitante.getTicketActivo());
        assertEquals(parque.getZonas().get(0), ticket.getZonaIngreso());
    }

    @Test
    void debeComprarTicketFastPassYConservarTipoYPrioridad() {
        DatosIniciales datos = cargarDatos();
        Parque parque = datos.getParque();
        ServicioParque servicioParque = new ServicioParque(parque, new ServicioAlertas());
        Visitante visitante = datos.getVisitantes().get(3);

        Ticket ticket = servicioParque.venderTicket(visitante, TipoTicket.FAST_PASS, parque.getZonas().get(0));

        assertEquals(TipoTicket.FAST_PASS, ticket.getTipo());
        assertEquals(1, ticket.getPrioridad());
        assertTrue(ticket.estaActivo());
    }

    private DatosIniciales cargarDatos() {
        DatosIniciales datos = new DatosIniciales();
        datos.cargar();
        return datos;
    }
}
