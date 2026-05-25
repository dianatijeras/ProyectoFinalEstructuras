package techpark;

import techpark.controladores.ControladorAdministrador;
import techpark.controladores.ControladorOperador;
import techpark.controladores.ControladorVisitante;
import techpark.datos.DatosIniciales;
import techpark.enums.TipoClima;
import techpark.enums.TipoTicket;
import techpark.estructuras.grafo.NodoGrafo;
import techpark.model.parque.Atraccion;
import techpark.model.parque.Parque;
import techpark.model.reportes.ResultadoAcceso;
import techpark.model.usuarios.Operador;
import techpark.model.usuarios.Visitante;
import techpark.servicios.acceso.ServicioAcceso;
import techpark.servicios.alertas.ServicioAlertas;
import techpark.servicios.colas.ServicioColas;
import techpark.servicios.operador.ServicioOperador;
import techpark.servicios.parque.ServicioParque;
import techpark.servicios.reportes.ServicioReportes;
import techpark.utilidades.Consola;

import java.util.List;

/**
 * Clase principal para ejecutar la consola de apoyo del parque.
 */
public class MainConsola {
    public static void main(String[] args) {
        DatosIniciales datos = new DatosIniciales();
        datos.cargar();
        Parque parque = datos.getParque();

        ServicioAlertas servicioAlertas = new ServicioAlertas();
        ServicioParque servicioParque = new ServicioParque(parque, servicioAlertas);
        ServicioColas servicioColas = new ServicioColas();
        ServicioAcceso servicioAcceso = new ServicioAcceso();
        ServicioOperador servicioOperador = new ServicioOperador(servicioAcceso, servicioColas, servicioAlertas);
        ServicioReportes servicioReportes = new ServicioReportes(parque, servicioAlertas);

        ControladorVisitante cv = new ControladorVisitante(parque, servicioParque, servicioColas);
        ControladorOperador co = new ControladorOperador(parque, servicioOperador);
        ControladorAdministrador ca = new ControladorAdministrador(parque, servicioParque, servicioReportes);

        Consola.imprimirSeparador("TECH-PARK UQ - Consola de apoyo");
        Visitante ana = datos.getVisitantes().get(0);
        Visitante carlos = datos.getVisitantes().get(3);
        Operador operadorAventura = datos.getOperadores().get(0);

        cv.comprarTicketYEntrar(ana, TipoTicket.GENERAL, parque.getZonas().get(0));
        cv.comprarTicketYEntrar(carlos, TipoTicket.FAST_PASS, parque.getZonas().get(0));
        System.out.println(cv.unirseAColaDeAtraccion(ana, "ATR-001"));
        System.out.println(cv.unirseAColaDeAtraccion(carlos, "ATR-001"));
        ResultadoAcceso resultado = co.procesarSiguienteEnCola(operadorAventura, "ATR-001");
        System.out.println("Primer procesado: " + resultado);

        Consola.imprimirSeparador("Ruta optima ATR-001 a ATR-006");
        List<NodoGrafo<Atraccion>> ruta = cv.calcularRutaOptima("ATR-001", "ATR-006");
        for (NodoGrafo<Atraccion> nodo : ruta) System.out.println("- " + nodo.getDato().getNombre());

        Consola.imprimirSeparador("Alerta climatica");
        ca.activarAlertaClimatica(TipoClima.LLUVIA_FUERTE);
        System.out.println("Estado ATR-003: " + parque.buscarAtraccion("ATR-003").getEstado());
        Consola.imprimirSeparador("Reporte");
        System.out.println(ca.generarReporteDiario());
    }
}
