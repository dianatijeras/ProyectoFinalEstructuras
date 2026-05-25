package techpark.controladores;

import techpark.enums.TipoClima;
import techpark.model.parque.Parque;
import techpark.model.parque.Zona;
import techpark.model.reportes.ReporteJornada;
import techpark.model.usuarios.Operador;
import techpark.servicios.parque.ServicioParque;
import techpark.servicios.reportes.ServicioReportes;

/**
 * Controlador para las operaciones administrativas del parque.
 */
public class ControladorAdministrador {
    private final ServicioParque servicioParque;
    private final ServicioReportes servicioReportes;

    /**
     * Constructor del controlador
     * @param parque
     * @param servicioParque
     * @param servicioReportes
     */
    public ControladorAdministrador(Parque parque, ServicioParque servicioParque, ServicioReportes servicioReportes) {
        this.servicioParque = servicioParque;
        this.servicioReportes = servicioReportes;
    }

    /**
     * Metodo para asignar un operador a una zona del parque.
     * @param operador
     * @param zona
     */
    public void asignarOperadorAZona(Operador operador, Zona zona){
        servicioParque.asignarOperador(operador, zona);
    }

    /**
     * Metodo para activar una alerta climatica en el parque, lo que puede afectar el funcionamiento de ciertas atracciones o zonas.
     * @param tipoClima
     */
    public void activarAlertaClimatica(TipoClima tipoClima){
        servicioParque.activarAlertaClimatica(tipoClima);
    }

    /**
     * Metodo para generar un reporte diario de la jornada.
     * @return
     */
    public ReporteJornada generarReporteDiario(){
        return servicioReportes.generarReporteJornada();
    }
}
