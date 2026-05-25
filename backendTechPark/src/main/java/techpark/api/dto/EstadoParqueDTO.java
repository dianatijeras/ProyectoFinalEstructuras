package techpark.api.dto;
import java.util.List;

/**
 * DTO que representa el estado actual del parque, incluyendo un resumen general y el estado de cada zona.
 * @param resumen
 * @param zonas
 */
public record EstadoParqueDTO(ParqueResumenDTO resumen, List<ZonaEstadoDTO> zonas) {
    public record ZonaEstadoDTO(ZonaDTO zona, java.util.List<AtraccionDTO> atracciones) {}
}
