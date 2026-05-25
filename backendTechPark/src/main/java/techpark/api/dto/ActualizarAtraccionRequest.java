package techpark.api.dto;

import java.util.List;

/**
 * DTO para actualizar una atracción existente.
 * @param nombre
 * @param tipo
 * @param zonaId
 * @param capacidadMaximaPorCiclo
 * @param alturaMinima
 * @param edadMinima
 * @param costoAdicional
 * @param estado
 * @param motivoCierre
 * @param tiempoEstimadoEspera
 * @param aristas
 */
public record ActualizarAtraccionRequest(
        String nombre,
        String tipo,
        String zonaId,
        Integer capacidadMaximaPorCiclo,
        Double alturaMinima,
        Integer edadMinima,
        Double costoAdicional,
        String estado,
        String motivoCierre,
        Integer tiempoEstimadoEspera,
        List<AristaAtraccionRequest> aristas
) {}
