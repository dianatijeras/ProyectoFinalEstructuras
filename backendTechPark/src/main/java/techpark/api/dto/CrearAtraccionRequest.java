package techpark.api.dto;

import java.util.List;

/**
 * DTO para la creación de una atracción.
 * @param id
 * @param nombre
 * @param tipo
 * @param zonaId
 * @param capacidadMaximaPorCiclo
 * @param alturaMinima
 * @param edadMinima
 * @param costoAdicional
 * @param estadoInicial
 * @param tiempoEstimadoEspera
 * @param motivoCierre
 * @param aristas
 */
public record CrearAtraccionRequest(
    String id,
    String nombre,
    String tipo,
    String zonaId,
    int capacidadMaximaPorCiclo,
    double alturaMinima,
    int edadMinima,
    double costoAdicional,
    String estadoInicial,
    Integer tiempoEstimadoEspera,
    String motivoCierre,
    List<AristaAtraccionRequest> aristas
) {}
