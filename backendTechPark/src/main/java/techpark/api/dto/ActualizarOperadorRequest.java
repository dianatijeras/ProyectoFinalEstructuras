package techpark.api.dto;

/**
 * DTO para actualizar un operador.
 * @param nombre
 * @param edad
 * @param password
 */
public record ActualizarOperadorRequest(String nombre, Integer edad, String password) {
}
