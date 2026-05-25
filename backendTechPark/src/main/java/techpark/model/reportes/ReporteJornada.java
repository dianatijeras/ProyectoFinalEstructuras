package techpark.model.reportes;

import techpark.model.eventos.AlertaMantenimiento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ReporteJornada representa un resumen diario de las operaciones del parque.
 */
public class ReporteJornada {
    private LocalDate fecha = LocalDate.now();
    private double ingresosDiarios;
    private final List<Atraccion> atraccionesMasVisitadas = new ArrayList<>();
    private final List<String> tiemposPromedioEspera = new ArrayList<>();
    private int cierresPorClima;
    private final List<AlertaMantenimiento> alertasMantenimiento = new ArrayList<>();
    private final List<Atraccion> atraccionesConMasIncidentes = new ArrayList<>();
    private final List<IncidenteOperativo> incidentesOperativos = new ArrayList<>();

    /**
     * Constructor para crear un nuevo reporte de jornada.
     * @param fecha
     * @param ingresosDiarios
     * @param cierresPorClima
     */
    public ReporteJornada(LocalDate fecha, double ingresosDiarios, int cierresPorClima) {
        this.fecha = fecha;
        this.ingresosDiarios = ingresosDiarios;
        this.cierresPorClima = cierresPorClima;
    }

    /**
     * Metodo que devuelve la fecha del reporte
     * @return
     */
    public LocalDate getFecha(){
        return fecha;
    }

    /**
     * Metodo que devuelve los ingresos diarios del parque
     * @return
     */
    public double getIngresosDiarios(){
        return ingresosDiarios;
    }

    /**
     * Metodo que establece los ingresos diarios del parque
     * @param ingresosDiarios
     */
    public void setIngresosDiarios(double ingresosDiarios){
        this.ingresosDiarios = ingresosDiarios;
    }

    /**
     * Metodo que devuelve la lista de las atracciones más visitadas del día
     * @return
     */
    public List<Atraccion> getAtraccionesMasVisitadas(){
        return atraccionesMasVisitadas;
    }

    /**
     * Metodo que devuelve la lista de los tiempos promedio de espera para las atracciones
     * @return
     */
    public List<String> getTiemposPromedioEspera(){
        return tiemposPromedioEspera;
    }

    /**
     * Metodo que devuelve el número de cierres por clima durante el día
     * @return
     */
    public int getCierresPorClima(){
        return cierresPorClima;
    }

    /**
     * Metodo que establece el número de cierres por clima durante el día
     * @param cierresPorClima
     */
    public void setCierresPorClima(int cierresPorClima){
        this.cierresPorClima = cierresPorClima;
    }

    /**
     * Metodo que devuelve la lista de alertas de mantenimiento registradas durante el día
     * @return
     */
    public List<AlertaMantenimiento> getAlertasMantenimiento(){
        return alertasMantenimiento;
    }

    /**
     * Metodo que devuelve la lista de atracciones con más incidentes registrados durante el día
     * @return
     */
    public List<Atraccion> getAtraccionesConMasIncidentes(){
        return atraccionesConMasIncidentes;
    }

    /**
     * Metodo que devuelve la lista de incidentes operativos registrados durante el día
     * @return
     */
    public List<IncidenteOperativo> getIncidentesOperativos(){
        return incidentesOperativos;
    }

    /**
     * Metodo que devuelve una representación en cadena del reporte de jornada, mostrando la fecha, ingresos diarios, cierres por clima y número de atracciones revisadas.
     * @return
     */
    public String toString() {
        return "Reporte " + fecha + "\nIngresos: " + ingresosDiarios + "\nCierres por clima: " + cierresPorClima + "\nAtracciones revisadas: " + atraccionesMasVisitadas.size();
    }
}
