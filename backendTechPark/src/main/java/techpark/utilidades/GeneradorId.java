package techpark.utilidades;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * GeneradorId es una clase de utilidad para generar identificadores únicos para diferentes entidades del parque temático.
 */
public class GeneradorId {
    private static final Map<String, Integer> contadores = new HashMap<>();

    /**
     * Genera un ID único basado en un prefijo específico. El ID se forma concatenando el prefijo con un número secuencial de tres dígitos.
     * @param prefijo
     * @return
     */
    public static String generarId(String prefijo) {
        int nuevo = contadores.getOrDefault(prefijo, 0) + 1;
        contadores.put(prefijo, nuevo);
        return prefijo + String.format("%03d", nuevo);
    }

    /**
     * Genera un UUID.
     * @return
     */
    public static String generarUUID() {
        return UUID.randomUUID().toString();
    }
}
