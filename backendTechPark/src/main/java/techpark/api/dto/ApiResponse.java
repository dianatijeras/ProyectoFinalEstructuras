package techpark.api.dto;

/**
 * Clase genérica para representar la respuesta de una API REST.
 * @param ok
 * @param mensaje
 * @param data
 * @param <T>
 */
public record ApiResponse<T>(boolean ok, String mensaje, T data) {
    public static <T> ApiResponse<T> ok(T data) { return new ApiResponse<>(true, "OK", data); }
    public static <T> ApiResponse<T> ok(String mensaje, T data) { return new ApiResponse<>(true, mensaje, data); }
    public static <T> ApiResponse<T> error(String mensaje) { return new ApiResponse<>(false, mensaje, null); }
}
