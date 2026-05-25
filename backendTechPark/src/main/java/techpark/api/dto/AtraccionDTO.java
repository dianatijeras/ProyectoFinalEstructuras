package techpark.api.dto;

/**
 * DTO para representar la información de una atracción en el parque de diversiones.
 * @param id
 * @param nombre
 * @param tipo
 * @param capacidadMaximaPorCiclo
 * @param alturaMinima
 * @param edadMinima
 * @param costoAdicional
 * @param contadorAcumuladoVisitantes
 * @param visitantesCicloActual
 * @param tiempoEstimadoEspera
 * @param estado
 * @param motivoCierre
 * @param zonaId
 * @param zonaNombre
 * @param tamanioCola
 * @param incidentesOperativos
 * @param operadoresResponsables
 */
public record AtraccionDTO(String id, String nombre, String tipo, int capacidadMaximaPorCiclo, double alturaMinima, int edadMinima, double costoAdicional, int contadorAcumuladoVisitantes, int visitantesCicloActual, int tiempoEstimadoEspera, String estado, String motivoCierre, String zonaId, String zonaNombre, int tamanioCola, int incidentesOperativos, int operadoresResponsables) {}
