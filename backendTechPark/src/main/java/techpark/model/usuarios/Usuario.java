package techpark.model.usuarios;

import techpark.enums.Rol;

/**
 * Clase base para todos los tipos de usuarios en el sistema.
 */
public abstract class Usuario {
    protected String id;
    protected String nombre;
    protected String documento;
    protected int edad;
    protected String password;
    protected Rol rol;

    /**
     * Constructor para crear un nuevo usuario.
     * @param id
     * @param nombre
     * @param documento
     * @param edad
     * @param password
     * @param rol
     */
    public Usuario(String id, String nombre, String documento, int edad, String password, Rol rol) {
        this.id = id;
        this.nombre = nombre;
        this.documento = documento;
        this.edad = edad;
        this.password = password;
        this.rol = rol;
    }

    /**
     * Actualiza los datos básicos del usuario. El nombre no puede ser nulo o vacío, y la edad no puede ser negativa.
     * @param nombre
     * @param edad
     */
    public void actualizarDatosBasicos(String nombre, int edad) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre es obligatorio");
        if (edad < 0) throw new IllegalArgumentException("La edad no puede ser negativa");
        this.nombre = nombre;
        this.edad = edad;
    }

    /**
     * Cambia la contraseña del usuario. El nuevo hash de contraseña no puede ser nulo o vacío.
     * @param password
     */
    public void cambiarPassword(String password) {
        if (password != null && !password.isBlank()) this.password = password;
    }

    /**
     * Metodo que devuelve el id del usuario
     * @return
     */
    public String getId(){
        return id;
    }

    /**
     * Metodo que devuelve el nombre del usuario
     * @return
     */
    public String getNombre(){
        return nombre;
    }

    /**
     * Metodo que devuelve el documento del usuario
     * @return
     */
    public String getDocumento(){
        return documento;
    }

    /**
     * Metodo que devuelve la edad del usuario
     * @return
     */
    public int getEdad(){
        return edad;
    }

    /**
     * Metodo que devuelve la contraseña del usuario
     * @return
     */
    public String getPassword(){
        return password;
    }

    /**
     * Metodo que devuelve el rol del usuario
     * @return
     */
    public Rol getRol(){
        return rol;
    }

    /**

    /**
     * Metodo que devuelve una representación en cadena del usuario, mostrando su nombre y rol.
     * @return
     */
    public String toString(){
        return nombre + " (" + rol + ")";
    }
}

