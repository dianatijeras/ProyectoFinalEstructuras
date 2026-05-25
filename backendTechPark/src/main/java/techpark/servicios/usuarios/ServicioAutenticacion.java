package techpark.servicios.usuarios;

import techpark.model.parque.Parque;
import techpark.model.usuarios.Usuario;

/**
 * Servicio para manejar la autenticación de usuarios en el parque.
 */
public class ServicioAutenticacion {
    private final Parque parque;

    /**
     * Constructor del servicio de autenticación.
     * @param parque
     */
    public ServicioAutenticacion(Parque parque) {
        this.parque = parque;
    }

    /**
     * Método para iniciar sesión de un usuario, verifica el documento y la contraseña.
     * @param documento
     * @param password
     * @return
     */
    public Usuario iniciarSesion(String documento, String password) {
        if (documento == null || documento.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Debe ingresar documento y contraseña");
        }
        Usuario usuario = parque.buscarUsuarioPorDocumento(documento.trim());
        if (usuario == null || !password.equals(usuario.getPassword())) {
            throw new SecurityException("Documento o contraseña incorrectos");
        }
        return usuario;
    }
}

