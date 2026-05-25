package techpark.api.dto;

import java.util.List;

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
