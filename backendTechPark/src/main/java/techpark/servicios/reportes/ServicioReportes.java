package techpark.servicios.reportes;

import techpark.model.parque.Atraccion;
import techpark.model.parque.Parque;
import techpark.model.reportes.ReporteJornada;
import techpark.servicios.alertas.ServicioAlertas;

import java.util.Comparator;
import java.util.List;

/**
 * ServicioReportes es responsable de generar reportes diarios sobre el parque de diversiones.
 */
public class ServicioReportes {
    private final Parque parque;
    private final ServicioAlertas servicioAlertas;

    /**
     * Constructor de la clase ServicioReportes.
     * @param parque
     * @param servicioAlertas
     */
    public ServicioReportes(Parque parque, ServicioAlertas servicioAlertas) {
        this.parque = parque; this.servicioAlertas = servicioAlertas;
    }

    /**
     * Genera un reporte de jornada que incluye ingresos diarios, atracciones más visitadas, tiempos promedio de espera, cierres por clima, alertas de mantenimiento y atracciones con más incidentes operativos.
     * @return
     */
    public ReporteJornada generarReporteJornada() {
        ReporteJornada reporte = new ReporteJornada();
        reporte.setIngresosDiarios(parque.getIngresosDiarios());
        List<Atraccion> atracciones = parque.getCatalogoAtracciones().inorden();
        atracciones.sort(Comparator.comparingInt(Atraccion::getContadorAcumuladoVisitantes).reversed());
        for (Atraccion a : atracciones) {
            if (a.getContadorAcumuladoVisitantes() > 0) {
                reporte.getAtraccionesMasVisitadas().add(a);
            }
            reporte.getTiemposPromedioEspera().add(a.getNombre() + ": " + a.getTiempoEstimadoEspera() + " minutos");
            if (a.getIncidentesOperativos() > 0) reporte.getAtraccionesConMasIncidentes().add(a);
        }
        reporte.setCierresPorClima(servicioAlertas.getHistorialClima().stream().mapToInt(a -> a.getAtraccionesAfectadas().size()).sum());
        reporte.getAlertasMantenimiento().addAll(servicioAlertas.getHistorialMantenimiento());
        reporte.getIncidentesOperativos().addAll(parque.getIncidentesOperativos());
        return reporte;
    }
}
