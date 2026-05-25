package techpark.model.usuarios;

import techpark.enums.Rol;
import techpark.model.parque.Parque;

/**
 * Clase que representa a un administrador del parque. Un administrador tiene un parque asignado que gestiona.
 */
public class Administrador extends Usuario {
    private Parque parqueGestionado;

    /**
     * Constructor para crear un nuevo administrador. El rol se asigna automáticamente como ADMINISTRADOR.
     * @param id
     * @param nombre
     * @param documento
     * @param edad
     * @param password
     */
    public Administrador(String id, String nombre, String documento, int edad, String password) {
        super(id, nombre, documento, edad, password, Rol.ADMINISTRADOR);
    }
    public Parque getParqueGestionado(){
        return parqueGestionado;
    }

    public void setParqueGestionado(Parque parqueGestionado){
        this.parqueGestionado = parqueGestionado;
    }
}
