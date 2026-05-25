package techpark.api.dto;

import java.util.List;

/**
 * DTO para representar la información de un operador en el sistema.
 * @param id
 * @param nombre
 * @param documento
 * @param edad
 * @param zonaAsignadaId
 * @param zonaAsignadaNombre
 * @param atraccionesAsignadas
 */
public record OperadorDTO(String id, String nombre, String documento, int edad, String zonaAsignadaId, String zonaAsignadaNombre, List<String> atraccionesAsignadas) {}
