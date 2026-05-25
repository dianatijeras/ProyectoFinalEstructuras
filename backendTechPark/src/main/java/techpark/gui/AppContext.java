package techpark.gui;


import techpark.datos.DatosIniciales;
import techpark.model.parque.Parque;
import techpark.model.usuarios.Operador;
import techpark.model.usuarios.Visitante;
import techpark.servicios.acceso.ServicioAcceso;
import techpark.servicios.alertas.ServicioAlertas;
import techpark.servicios.colas.ServicioColas;
import techpark.servicios.operador.ServicioOperador;
import techpark.servicios.parque.ServicioParque;
import techpark.servicios.reportes.ServicioReportes;
import techpark.servicios.usuarios.ServicioAutenticacion;

import java.util.List;

/**
 * AppContext es una clase centralizada que mantiene el estado global de la aplicación, incluyendo el parque, los visitantes, los operadores y los servicios principales.
 */
public class AppContext {
    private Parque parque;
    private List<Visitante> visitantes;
    private List<Operador> operadores;
    private ServicioAlertas servicioAlertas;
    private ServicioParque servicioParque;
    private ServicioColas servicioColas;
    private ServicioAcceso servicioAcceso;
    private ServicioOperador servicioOperador;
    private ServicioReportes servicioReportes;
    private ServicioAutenticacion servicioAutenticacion;

    /**
     * Constructor de la clase AppContext. Carga los datos iniciales del parque, visitantes y operadores, y configura los servicios necesarios para la aplicación.
     */
    public AppContext() {
        DatosIniciales datos = new DatosIniciales();
        datos.cargar();
        configurar(datos.getParque(), datos.getVisitantes(), datos.getOperadores());
    }

    /**
     * Configura el estado de la aplicación con los datos proporcionados y crea las instancias de los servicios necesarios.
     * @param parque
     * @param visitantes
     * @param operadores
     */
    public void configurar(Parque parque, List<Visitante> visitantes, List<Operador> operadores) {
        this.parque = parque;
        this.visitantes = visitantes;
        this.operadores = operadores;
        this.servicioAlertas = new ServicioAlertas();
        this.servicioParque = new ServicioParque(parque, servicioAlertas);
        this.servicioColas = new ServicioColas();
        this.servicioAcceso = new ServicioAcceso();
        this.servicioOperador = new ServicioOperador(servicioAcceso, servicioColas, servicioAlertas);
        this.servicioReportes = new ServicioReportes(parque, servicioAlertas);
        this.servicioAutenticacion = new ServicioAutenticacion(parque);
    }

    /**
     * Métodos getter para acceder al estado global de la aplicación, incluyendo el parque, los visitantes, los operadores y los servicios principales.
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

    public ServicioAlertas getServicioAlertas() {
        return servicioAlertas;
    }

    public ServicioParque getServicioParque() {
        return servicioParque;
    }

    public ServicioColas getServicioColas() {
        return servicioColas;
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
}

