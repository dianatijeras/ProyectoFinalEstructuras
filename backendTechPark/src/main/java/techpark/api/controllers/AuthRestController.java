package techpark.api.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import techpark.api.dto.ApiResponse;
import techpark.api.dto.LoginRequest;
import techpark.api.dto.LoginResponse;
import techpark.api.mappers.ApiMapper;
import techpark.config.AppState;
import techpark.model.usuarios.Usuario;

/**
 * Controlador REST para la autenticación de usuarios en el parque de atracciones. Proporciona un endpoint para iniciar sesión
 */
@RestController
@RequestMapping("/api/auth")
public class AuthRestController {
    private final AppState appState;

    /**
     * Constructor de la clase AuthRestController.
     * @param appState
     */
    public AuthRestController(AppState appState) { this.appState = appState; }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        Usuario usuario = appState.getServicioAutenticacion().iniciarSesion(request.documento(), request.password());
        return ApiResponse.ok("Inicio de sesion correcto", ApiMapper.toLoginResponse(usuario));
    }
}