package techpark.api.dto;

public record ActualizarVisitanteRequest(String nombre, Integer edad, Double estatura, Double saldoVirtual, String password) {
}
