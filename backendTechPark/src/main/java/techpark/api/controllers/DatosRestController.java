package techpark.api.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import techpark.api.dto.ApiResponse;
import techpark.api.dto.CargaCsvRequest;
import techpark.config.AppState;
import techpark.datos.ResultadoCargaCsv;

import java.io.File;
import java.io.IOException;

/**
 * Controlador REST para gestionar la carga de datos en el parque de atracciones.
 */
@RestController
@RequestMapping("/api/datos")
public class DatosRestController {
    private final AppState appState;

    /**
     * Constructor del controlador, recibe el estado de la aplicación para acceder a los servicios y datos del parque.
     * @param appState
     */
    public DatosRestController(AppState appState) { this.appState = appState; }

    /**
     * Endpoint para cargar datos de ejemplo en el parque.
     * @return
     */
    @PostMapping("/cargar-ejemplo")
    public ApiResponse<String> cargarEjemplo() {
        appState.cargarDatosIniciales();
        return ApiResponse.ok("Datos iniciales recargados", appState.getParque().getNombre());
    }

    /**
     * Endpoint para cargar datos desde un archivo CSV especificado por su ruta en el sistema de archivos.
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/cargar-csv")
    public ApiResponse<ResultadoCargaCsv> cargarCsvPorRuta(@RequestBody CargaCsvRequest request) throws IOException {
        File archivo = new File(request.ruta());
        if (!archivo.exists()) throw new IllegalArgumentException("Archivo no encontrado: " + request.ruta());
        ResultadoCargaCsv resultado = appState.cargarDesdeArchivo(archivo);
        return ApiResponse.ok("CSV cargado", resultado);
    }

    /**
     * Endpoint para cargar datos desde un archivo CSV subido a través de una solicitud multipart/form-data.
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/cargar-csv/upload")
    public ApiResponse<ResultadoCargaCsv> cargarCsvUpload(@RequestParam("file") MultipartFile file) throws IOException {
        File temporal = File.createTempFile("techpark-", ".csv");
        file.transferTo(temporal);
        ResultadoCargaCsv resultado = appState.cargarDesdeArchivo(temporal);
        temporal.delete();
        return ApiResponse.ok("CSV cargado", resultado);
    }
}

