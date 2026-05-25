package techpark.api.dto;
import java.util.List;

/**
 * DTO para el reporte de la jornada, que incluye información sobre ingresos, atracciones, incidentes y alertas.
 * @param fecha
 * @param ingresosDiarios
 * @param atraccionesMasVisitadas
 * @param tiemposPromedioEspera
 * @param cierresPorClima
 * @param alertasMantenimiento
 * @param atraccionesConMasIncidentes
 * @param incidentesOperativos
 */
public record ReporteJornadaDTO(String fecha, double ingresosDiarios, List<AtraccionDTO> atraccionesMasVisitadas, List<String> tiemposPromedioEspera, int cierresPorClima, List<String> alertasMantenimiento, List<AtraccionDTO> atraccionesConMasIncidentes, List<IncidenteOperativoDTO> incidentesOperativos) {}
