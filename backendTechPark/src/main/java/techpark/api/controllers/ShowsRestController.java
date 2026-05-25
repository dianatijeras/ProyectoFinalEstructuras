package techpark.api.controllers;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import techpark.api.dto.ApiResponse;
import techpark.api.dto.ShowDTO;
import techpark.config.AppState;
import techpark.model.eventos.Show;

/**
 * Controlador REST para gestionar los shows del parque de diversiones. Proporciona endpoints para listar los shows programados para el día actual, incluyendo su estado y mensajes relacionados.
 */
@RestController
@RequestMapping("/api/shows")
public class ShowsRestController {
    private final AppState appState;

    /**
     * Constructor para inyectar el estado de la aplicación, que proporciona acceso a los servicios y datos relacionados con los shows del parque.
     * @param appState
     */
    public ShowsRestController(AppState appState) {
        this.appState = appState;
    }

    /**
     * Devuelve una lista de los shows programados para el día actual, incluyendo su ID, nombre, horario, duración, estado actual y un mensaje relacionado con su estado.
     * @return
     */
    @GetMapping
    public ApiResponse<List<ShowDTO>> listar() {
        List<ShowDTO> respuesta = new ArrayList<>();
        for (Show show : appState.getServicioShows().listarShowsDelDia()) {
            respuesta.add(new ShowDTO(
                    show.getId(),
                    show.getNombre(),
                    show.getHorario().toString(),
                    show.getDuracion(),
                    show.getEstado().name(),
                    appState.getServicioShows().mensajeEstado(show)
            ));
        }
        return ApiResponse.ok(respuesta);
    }
}
