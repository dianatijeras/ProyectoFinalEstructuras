package techpark.datos;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResultadoCargaCsv {
    private boolean exitoso = true;
    private String mensaje = "CSV procesado";
    private int registrosProcesados;
    private int registrosExitosos;
    private int registrosConError;
    private final List<String> erroresPorFila = new ArrayList<>();
    private final Map<String, Integer> resumen = new LinkedHashMap<>();

    public void registrarExito(String tipo) {
        registrosProcesados++;
        registrosExitosos++;
        resumen.put(tipo, resumen.getOrDefault(tipo, 0) + 1);
    }

    public void registrarError(int fila, String tipo, String mensajeError) {
        registrosProcesados++;
        registrosConError++;
        exitoso = false;
        String etiqueta = tipo == null || tipo.isBlank() ? "DESCONOCIDO" : tipo;
        erroresPorFila.add("Linea " + fila + " [" + etiqueta + "]: " + mensajeError);
    }

    public boolean isExitoso() { return exitoso; }
    public String getMensaje() { return registrosConError == 0 ? mensaje : "CSV procesado con errores"; }
    public int getRegistrosProcesados() { return registrosProcesados; }
    public int getRegistrosExitosos() { return registrosExitosos; }
    public int getRegistrosConError() { return registrosConError; }
    public List<String> getErroresPorFila() { return erroresPorFila; }
    public Map<String, Integer> getResumen() { return resumen; }
}

