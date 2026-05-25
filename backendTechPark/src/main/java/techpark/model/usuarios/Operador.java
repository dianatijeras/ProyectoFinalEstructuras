package techpark.model.usuarios;

import techpark.enums.Rol;
import techpark.model.parque.Zona;

/**
 * Clase que representa a un operador del parque. Un operador tiene una zona asignada que supervisa.
 */
public class Operador extends Usuario {
    private Zona zonaAsignada;

    /**
     * Constructor para crear un nuevo operador. El rol se asigna automáticamente como OPERADOR.
     * @param id
     * @param nombre
     * @param documento
     * @param edad
     * @param password
     */
    public Operador(String id, String nombre, String documento, int edad, String password) {
        super(id, nombre, documento, edad, password, Rol.OPERADOR);
    }

    /**
     * Metodo que devuelve la zona asignada al operador
     * @return
     */
    public Zona getZonaAsignada(){
        return zonaAsignada;
    }

    /**
     * Metodo que asigna una zona al operador
     * @param zonaAsignada
     */
    public void setZonaAsignada(Zona zonaAsignada){
        this.zonaAsignada = zonaAsignada;
    }
}
