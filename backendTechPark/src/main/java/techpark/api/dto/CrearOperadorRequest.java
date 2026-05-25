package techpark.api.dto;

/**
 * DTO para la creación de un nuevo operador.
 * @param nombre
 * @param documento
 * @param edad
 * @param password
 * @param zonaId
 */
public record CrearOperadorRequest(String nombre, String documento, int edad, String password, String zonaId) {}
