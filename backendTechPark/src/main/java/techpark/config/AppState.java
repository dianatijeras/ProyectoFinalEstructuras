package techpark.config;

import org.springframework.stereotype.Component;
import techpark.datos.CargadorArchivo;
import techpark.datos.DatosIniciales;
import techpark.datos.ResultadoCargaCsv;
import techpark.model.parque.Parque;
import techpark.model.usuarios.Administrador;
import techpark.model.usuarios.Operador;
import techpark.model.usuarios.Visitante;
import techpark.servicios.acceso.ServicioAcceso;
import techpark.servicios.alertas.ServicioAlertas;
import techpark.servicios.colas.ServicioColas;
import techpark.servicios.operador.ServicioOperador;
import techpark.servicios.parque.ServicioAdministracion;
import techpark.servicios.parque.ServicioParque;
import techpark.servicios.reportes.ServicioReportes;
import techpark.servicios.shows.ServicioShows;
import techpark.servicios.usuarios.ServicioAutenticacion;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * AppState es un componente de Spring que mantiene el estado global de la aplicación, incluyendo el parque, los usuarios y los servicios.
 */
@Component
public class AppState {
    private Parque parque;
    private List<Visitante> visitantes;
    private List<Operador> operadores;
    private Administrador administrador;

    private ServicioAlertas servicioAlertas;
    private ServicioParque servicioParque;
    private ServicioColas servicioColas;
    private ServicioAcceso servicioAcceso;
    private ServicioOperador servicioOperador;
    private ServicioReportes servicioReportes;
    private ServicioAutenticacion servicioAutenticacion;
    private ServicioShows servicioShows;
    private ServicioAdministracion servicioAdministracion;
    private ResultadoCargaCsv ultimoResultadoCargaCsv;

    /**
     * Constructor de la clase AppState, que carga los datos iniciales del parque y sus usuarios al iniciar la aplicación.
      *
     */
    public AppState() {
        cargarDatosIniciales();
    }

    /**
     * Método sincronizado que carga los datos iniciales del parque, incluyendo la configuración del parque, los visitantes, los operadores y el administrador.
      *
     */
    public synchronized void cargarDatosIniciales() {
        DatosIniciales datos = new DatosIniciales();
        datos.cargar();
        inicializarDesde(datos.getParque(), datos.getVisitantes(), datos.getOperadores(), datos.getAdministrador());
    }

    /**
     * Método sincronizado que carga los datos del parque desde un archivo CSV especificado, actualizando el estado de la aplicación con la nueva configuración del parque, los usuarios y las alertas.
      *
     * @param archivo
     * @return
     * @throws IOException
     */
    public synchronized ResultadoCargaCsv cargarDesdeArchivo(File archivo) throws IOException {
        CargadorArchivo cargador = new CargadorArchivo();
        cargador.cargar(archivo);
        inicializarDesde(cargador.getParque(), cargador.getVisitantes(), cargador.getOperadores(), cargador.getAdministrador());
        for (var alerta : cargador.getAlertasClima()) {
            this.servicioAlertas.registrarAlertaClimaticaPrecargada(alerta);
        }
        for (var alerta : cargador.getAlertasMantenimiento()) {
            this.servicioAlertas.registrarAlertaMantenimientoPrecargada(alerta);
        }
        this.ultimoResultadoCargaCsv = cargador.getResultado();
        return this.ultimoResultadoCargaCsv;
    }

    /**
     * Método privado que inicializa el estado de la aplicación a partir de los datos proporcionados, configurando el parque, los usuarios y los servicios necesarios para el funcionamiento del sistema.
     * @param parque
     * @param visitantes
     * @param operadores
     * @param administrador
     */
    private void inicializarDesde(Parque parque, List<Visitante> visitantes, List<Operador> operadores, Administrador administrador) {
        this.parque = parque;
        this.visitantes = visitantes;
        this.operadores = operadores;
        this.administrador = administrador;

        this.servicioAlertas = new ServicioAlertas();
        this.servicioParque = new ServicioParque(parque, servicioAlertas);
        this.servicioColas = new ServicioColas(parque);
        this.servicioAcceso = new ServicioAcceso();
        this.servicioOperador = new ServicioOperador(servicioAcceso, servicioColas, servicioAlertas, parque);
        this.servicioReportes = new ServicioReportes(parque, servicioAlertas);
        this.servicioAutenticacion = new ServicioAutenticacion(parque);
        this.servicioShows = new ServicioShows(parque);
        this.servicioAdministracion = new ServicioAdministracion(parque, visitantes, operadores, servicioParque);
    }

    /**
     * Método que verifica la expiración de los tickets activos en el parque, utilizando el servicio de parque para expirar aquellos tickets que hayan alcanzado su tiempo de expiración según la hora actual.
      *
     */
    public void verificarExpiracionTickets() {
        if (servicioParque != null) servicioParque.expirarTicketsActivosSiCorresponde(java.time.LocalTime.now());
    }

    /**
     * Métodos getter para acceder al estado del parque, los usuarios y los servicios desde otras partes de la aplicación, como los controladores REST.
     * @return
     */
    public Parque getParque() {
        return parque;
    }

    public List<Visitante> getVisitantes() {
        return visitantes;
    }

    public List<Operador> getOperadores() {
        return operadores;
    }

    public Administrador getAdministrador() {
        return administrador;
    }

    public ServicioAlertas getServicioAlertas() {
        return servicioAlertas;
    }
    public ServicioParque getServicioParque() {
        return servicioParque;
    }
    public ServicioColas getServicioColas() {
        return servicioColas;
    }

    public ServicioAcceso getServicioAcceso() {
        return servicioAcceso;
    }

    public ServicioOperador getServicioOperador() {
        return servicioOperador;
    }

    public ServicioReportes getServicioReportes() {
        return servicioReportes;
    }

    public ServicioAutenticacion getServicioAutenticacion() {
        return servicioAutenticacion;
    }

    public ServicioShows getServicioShows() {
        return servicioShows;
    }

    public ServicioAdministracion getServicioAdministracion() {
        return servicioAdministracion;
    }

    public ResultadoCargaCsv getUltimoResultadoCargaCsv() {
        return ultimoResultadoCargaCsv;
    }
}

