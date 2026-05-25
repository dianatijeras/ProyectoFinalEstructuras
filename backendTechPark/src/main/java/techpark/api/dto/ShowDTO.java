package techpark.api.dto;

/**
 * DTO para representar la información de un show en la respuesta de la API.
 * @param id
 * @param nombre
 * @param horario
 * @param duracion
 * @param estado
 * @param mensaje
 */
public record ShowDTO(String id, String nombre, String horario, int duracion, String estado, String mensaje) {}
