package techpark.utilidades;

/**
 * Clase de utilidad para imprimir mensajes en la consola con formato.
 */
public class Consola {
    public static void imprimirSeparador(String titulo) {
        System.out.println("\n==== " + titulo + " ====");
    }

    /**
     * Imprime un mensaje de éxito en la consola con el formato [OK] mensaje.
     * @param mensaje
     */
    public static void imprimirExito(String mensaje) {
        System.out.println("[OK] " + mensaje);
    }

    /**
     * Imprime un mensaje de error en la consola con el formato [ERROR] mensaje.
     * @param mensaje
     */
    public static void imprimirError(String mensaje) {
        System.out.println("[ERROR] " + mensaje);
    }

    /**
     * Imprime un mensaje de alerta en la consola con el formato [ALERTA] mensaje.
     * @param mensaje
     */
    public static void imprimirAlerta(String mensaje) {
        System.out.println("[ALERTA] " + mensaje);
    }
}
