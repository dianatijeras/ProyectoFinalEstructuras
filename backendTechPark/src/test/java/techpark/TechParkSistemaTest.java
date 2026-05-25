package techpark;

import org.junit.jupiter.api.Test;
import techpark.datos.DatosIniciales;
import techpark.enums.EstadoAtraccion;
import techpark.enums.TipoTicket;
import techpark.estructuras.grafo.NodoGrafo;
import techpark.model.eventos.IncidenteOperativo;
import techpark.model.parque.Atraccion;
import techpark.model.parque.Parque;
import techpark.model.reportes.ResultadoAcceso;
import techpark.model.tickets.EntradaEnCola;
import techpark.model.tickets.Ticket;
import techpark.model.usuarios.Visitante;
import techpark.servicios.acceso.ServicioAcceso;
import techpark.servicios.alertas.ServicioAlertas;
import techpark.servicios.colas.ServicioColas;
import techpark.servicios.parque.ServicioAdministracion;
import techpark.servicios.parque.ServicioParque;

import java.util.List;

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


    @Test
    void colaVirtualDebeProcesarPrimeroVisitanteFastPass() {
        DatosIniciales datos = cargarDatos();
        Parque parque = datos.getParque();
        ServicioParque servicioParque = new ServicioParque(parque, new ServicioAlertas());
        ServicioColas servicioColas = new ServicioColas(parque);

        Visitante general = datos.getVisitantes().get(0);
        Visitante fastPass = datos.getVisitantes().get(3);
        Atraccion atraccion = parque.buscarAtraccion("ATR-001");

        servicioParque.venderTicket(general, TipoTicket.GENERAL, parque.getZonas().get(0));
        servicioParque.venderTicket(fastPass, TipoTicket.FAST_PASS, parque.getZonas().get(0));

        servicioColas.unirseACola(general, atraccion);
        servicioColas.unirseACola(fastPass, atraccion);

        EntradaEnCola siguiente = servicioColas.llamarSiguiente(atraccion);

        assertNotNull(siguiente);
        assertEquals(fastPass, siguiente.getVisitante());
        assertEquals(TipoTicket.FAST_PASS, siguiente.getTicket().getTipo());
    }

    @Test
    void registrarAccesoDebeActualizarHistorialDelVisitante() {
        DatosIniciales datos = cargarDatos();
        Parque parque = datos.getParque();
        ServicioParque servicioParque = new ServicioParque(parque, new ServicioAlertas());
        ServicioAcceso servicioAcceso = new ServicioAcceso();

        Visitante visitante = datos.getVisitantes().get(0);
        Atraccion atraccion = parque.buscarAtraccion("ATR-001");

        servicioParque.venderTicket(visitante, TipoTicket.FAST_PASS, parque.getZonas().get(0));

        ResultadoAcceso resultado = servicioAcceso.validarYRegistrarAcceso(visitante, atraccion);

        assertTrue(resultado.fueAutorizado());
        assertEquals(1, visitante.getHistorialVisitas().getTamanio());
        assertEquals(atraccion, visitante.getHistorialVisitas().obtener(0).getAtraccion());
    }

    @Test
    void registrarAccesoDebeActualizarUbicacionActualDelVisitante() {
        DatosIniciales datos = cargarDatos();
        Parque parque = datos.getParque();
        ServicioParque servicioParque = new ServicioParque(parque, new ServicioAlertas());
        ServicioAcceso servicioAcceso = new ServicioAcceso();

        Visitante visitante = datos.getVisitantes().get(0);
        Atraccion atraccion = parque.buscarAtraccion("ATR-001");

        servicioParque.venderTicket(visitante, TipoTicket.FAST_PASS, parque.getZonas().get(0));

        ResultadoAcceso resultado = servicioAcceso.validarYRegistrarAcceso(visitante, atraccion);

        assertTrue(resultado.fueAutorizado());
        assertEquals(atraccion, visitante.getUltimaAtraccionVisitada());
        assertEquals("yippe", visitante.getNombreUbicacionActual());
    }

    @Test
    void registrarIncidenteDebeCerrarAtraccionAfectada() {
        DatosIniciales datos = cargarDatos();
        Parque parque = datos.getParque();
        ServicioParque servicioParque = new ServicioParque(parque, new ServicioAlertas());
        ServicioAdministracion servicioAdministracion = new ServicioAdministracion(
                parque,
                datos.getVisitantes(),
                datos.getOperadores(),
                servicioParque
        );
        Atraccion atraccion = parque.buscarAtraccion("ATR-001");

        IncidenteOperativo incidente = servicioAdministracion.registrarIncidente(
                atraccion,
                "Falla temporal en sensor de seguridad",
                "MEDIA"
        );

        assertNotNull(incidente);
        assertEquals(EstadoAtraccion.CERRADA, atraccion.getEstado());
        assertEquals("Cerrada por incidente operativo", atraccion.getMotivoCierre());
        assertTrue(parque.getIncidentesOperativos().contains(incidente));
    }

    @Test
    void grafoDebeCalcularRutaEntreAtraccionesConDijkstra() {
        DatosIniciales datos = cargarDatos();

        List<NodoGrafo<Atraccion>> ruta = datos.getParque().getMapa().dijkstra("ATR-001", "ATR-006");

        assertFalse(ruta.isEmpty());
        assertEquals("ATR-001", ruta.get(0).getId());
        assertEquals("ATR-006", ruta.get(ruta.size() - 1).getId());
    }

    private DatosIniciales cargarDatos() {
        DatosIniciales datos = new DatosIniciales();
        datos.cargar();
        return datos;
    }
}
