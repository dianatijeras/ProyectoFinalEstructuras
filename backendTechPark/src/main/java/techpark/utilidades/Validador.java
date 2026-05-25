package techpark.utilidades;

/**
 * Clase de utilidad para validar datos de entrada en el sistema.
 */
public class Validador {
    public static void validarNoNulo(Object objeto, String campo) {
        if (objeto == null) throw new IllegalArgumentException(campo + " no puede ser nulo");
    }

    /**
     * Valida que un texto no sea nulo ni esté vacío. 
     * @param texto
     * @param campo
     */
    public static void validarNoVacio(String texto, String campo) {
        if (texto == null || texto.isBlank()) throw new IllegalArgumentException(campo + " no puede estar vacio");
    }

    /**
     * Valida que un número sea positivo.
     * @param numero
     * @param campo
     */
    public static void validarPositivo(double numero, String campo) {
        if (numero < 0) throw new IllegalArgumentException(campo + " debe ser positivo");
    }

    /**
     * Valida que una edad esté dentro de un rango razonable .
     * @param edad
     */
    public static void validarRangoEdad(int edad) {
        if (edad < 0 || edad > 120) throw new IllegalArgumentException("Edad invalida");
    }

    /**
     * Valida que una estatura esté dentro de un rango razonable.
     * @param estatura
     */
    public static void validarEstatura(double estatura) {
        if (estatura <= 0 || estatura > 2.50) throw new IllegalArgumentException("Estatura invalida");
    }
}
