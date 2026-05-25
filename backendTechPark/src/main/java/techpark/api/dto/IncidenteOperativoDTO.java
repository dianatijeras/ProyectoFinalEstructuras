package techpark.api.dto;

/**
 * DTO para representar un incidente operativo en el parque de atracciones.
 * @param id
 * @param atraccionId
 * @param atraccionNombre
 * @param descripcion
 * @param gravedad
 * @param fechaHora
 * @param resuelto
 * @param solucion
 */
public record IncidenteOperativoDTO(String id, String atraccionId, String atraccionNombre, String descripcion, String gravedad, String fechaHora, boolean resuelto, String solucion) {}
