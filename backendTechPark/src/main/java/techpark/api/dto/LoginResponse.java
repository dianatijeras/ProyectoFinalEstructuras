package techpark.api.dto;

/**
 * DTO para la respuesta del login, contiene el id, nombre, documento y rol del usuario.
 * @param id
 * @param nombre
 * @param documento
 * @param rol
 */
public record LoginResponse(String id, String nombre, String documento, String rol) {}
