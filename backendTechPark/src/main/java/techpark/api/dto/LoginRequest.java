package techpark.api.dto;

/**
 * DTO para la solicitud de inicio de sesión.
 * @param documento
 * @param password
 */
public record LoginRequest(String documento, String password) {}
