package techpark.model.reportes;

import techpark.enums.Resultado;

/**
 * Clase que representa el resultado de un intento de acceso a una zona del parque.
 */
public class ResultadoAcceso {
    private final Resultado resultado;
    private final String motivo;
    private final double costoDeducido;

    /**
     * Constructor para crear un nuevo resultado de acceso.
     * @param resultado
     * @param motivo
     * @param costoDeducido
     */
    public ResultadoAcceso(Resultado resultado, String motivo, double costoDeducido) {
        this.resultado = resultado;
        this.motivo = motivo;
        this.costoDeducido = costoDeducido;
    }

    /**
     * Metodo que devuelve un resultado de acceso autorizado con el costo deducido.
     * @param costo
     * @return
     */
    public static ResultadoAcceso autorizado(double costo){
        return new ResultadoAcceso(Resultado.AUTORIZADO, "Acceso autorizado", costo);
    }

    /**
     * Metodo que devuelve un resultado de acceso denegado con el motivo especificado.
     * @param motivo
     * @return
     */
    public static ResultadoAcceso denegado(String motivo){
        return new ResultadoAcceso(Resultado.DENEGADO, motivo, 0);
    }

    /**
     * Metodo que devuelve true si el acceso fue autorizado, false en caso contrario.
     * @return
     */
    public boolean fueAutorizado(){
        return resultado == Resultado.AUTORIZADO;
    }

    /**
     * Metodo que devuelve el resultado del intento de acceso.
     * @return
     */
    public Resultado getResultado(){
        return resultado;
    }

    /**
     * Metodo que devuelve el motivo del resultado del intento de acceso.
     * @return
     */
    public String getMotivo(){
        return motivo;
    }

    /**
     * Metodo que devuelve el costo deducido en caso de acceso autorizado, o 0 en caso de acceso denegado.
     * @return
     */
    public double getCostoDeducido(){
        return costoDeducido;
    }

    /**
     * Metodo que devuelve una representación en cadena del resultado de acceso, incluyendo el resultado y el motivo.
     * @return
     */
    public String toString(){
        return resultado + " - " + motivo;
    }
}
