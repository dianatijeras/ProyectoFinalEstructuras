package techpark.model.parque;

import techpark.enums.EstadoAtraccion;
import techpark.enums.TipoAtraccion;
import techpark.estructuras.cola.ColaPrioridad;
import techpark.estructuras.lista.ListaEnlazada;
import techpark.model.tickets.EntradaEnCola;
import techpark.model.usuarios.Operador;

/**
 * Clase que representa una atracción dentro del parque.
 */
public class Atraccion {
    private String id;
    private String nombre;
    private TipoAtraccion tipo;
    private int capacidadMaximaPorCiclo;
    private double alturaMinima;
    private int edadMinima;
    private double costoAdicional;
    private int contadorAcumuladoVisitantes;
    private int tiempoEstimadoEspera;
    private EstadoAtraccion estado = EstadoAtraccion.ACTIVA;
    private String motivoCierre;
    private Zona zona;
    private final ColaPrioridad<EntradaEnCola> colaVirtual = new ColaPrioridad<>();
    private int incidentesOperativos;
    private int visitantesCicloActual;
    private final ListaEnlazada<Operador> operadoresResponsables = new ListaEnlazada<>();

    /**
     * Constructor de la clase Atraccion.
     * @param id
     * @param nombre
     * @param tipo
     * @param capacidadMaximaPorCiclo
     * @param alturaMinima
     * @param edadMinima
     * @param costoAdicional
     */
    public Atraccion(String id, String nombre, TipoAtraccion tipo, int capacidadMaximaPorCiclo, double alturaMinima, int edadMinima, double costoAdicional) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.capacidadMaximaPorCiclo = capacidadMaximaPorCiclo;
        this.alturaMinima = alturaMinima;
        this.edadMinima = edadMinima;
        this.costoAdicional = costoAdicional;
    }

    /**
     * Metodo para incrementar el contador de visitantes acumulados y del ciclo actual, y actualizar el tiempo estimado de espera.
     */
    public void incrementarVisitantes(){
        contadorAcumuladoVisitantes++; visitantesCicloActual++; actualizarTiempoEspera();
    }

    /**
     * Metodo que devuelve true si hay capacidad para más visitantes en el ciclo actual, false en caso contrario.
     * @return
     */
    public boolean hayCapacidadEnCiclo(){
        return visitantesCicloActual < capacidadMaximaPorCiclo;
    }

    /**
     * Metodo para reiniciar el contador de visitantes del ciclo actual, generalmente llamado al finalizar un ciclo de la atracción.
     */
    public void reiniciarCiclo(){
        visitantesCicloActual = 0;
    }

    /**
     * Metodo que devuelve true si la atracción requiere mantenimiento debido a que ha alcanzado o superado el umbral de visitantes acumulados, false en caso contrario.
     * @return
     */
    public boolean requiereMantenimiento(){
        return contadorAcumuladoVisitantes >= 500;
    }

    /**
     * Metodo para actualizar el tiempo estimado de espera basado en el tamaño de la cola virtual.
     * El tiempo se calcula como el número de personas en la cola multiplicado por 5 minutos, con un mínimo de 1 minuto.
     */
    public void actualizarTiempoEspera(){
        tiempoEstimadoEspera = Math.max(1, colaVirtual.getTamanio()) * 5;
    }

    /**
     * Metodo para cambiar el estado de la atracción.
     * Si el nuevo estado es CERRADA, se requiere un motivo de cierre no nulo ni vacío.
     * @param estado
     * @param motivo
     */
    public void cambiarEstado(EstadoAtraccion estado, String motivo) {
        if (estado == EstadoAtraccion.CERRADA && (motivo == null || motivo.isBlank())) throw new IllegalArgumentException("Debe indicar motivo de cierre");
        this.estado = estado; this.motivoCierre = motivo;
    }

    /**
     * Metodo para registrar un incidente operativo en la atracción, incrementando el contador de incidentes operativos.
     */
    public void registrarIncidente(){
        incidentesOperativos++;
    }

    /**
     * Metodo para actualizar los datos de la atracción.
     * El nombre no puede ser nulo o vacío, la capacidad por ciclo debe ser mayor que cero, la altura mínima no puede ser negativa, la edad mínima no puede ser negativa y el costo adicional no puede ser negativo.
     * @param nombre
     * @param tipo
     * @param capacidadMaximaPorCiclo
     * @param alturaMinima
     * @param edadMinima
     * @param costoAdicional
     * @param tiempoEstimadoEspera
     */
    public void actualizarDatos(String nombre, TipoAtraccion tipo, int capacidadMaximaPorCiclo, double alturaMinima, int edadMinima, double costoAdicional, int tiempoEstimadoEspera) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre de atraccion obligatorio");
        if (capacidadMaximaPorCiclo <= 0) throw new IllegalArgumentException("La capacidad por ciclo debe ser mayor que cero");
        if (alturaMinima < 0) throw new IllegalArgumentException("La altura minima no puede ser negativa");
        if (edadMinima < 0) throw new IllegalArgumentException("La edad minima no puede ser negativa");
        if (costoAdicional < 0) throw new IllegalArgumentException("El costo adicional no puede ser negativo");
        this.nombre = nombre;
        this.tipo = tipo;
        this.capacidadMaximaPorCiclo = capacidadMaximaPorCiclo;
        this.alturaMinima = alturaMinima;
        this.edadMinima = edadMinima;
        this.costoAdicional = costoAdicional;
        setTiempoEstimadoEspera(tiempoEstimadoEspera);
    }

    /**
     * Metodo para asignar un operador responsable a la atracción.
     * El operador no puede ser nulo y debe pertenecer a la misma zona que la atracción si esta tiene una zona asignada.
     * Si el operador ya es responsable de la atracción
     * @param operador
     */
    public void asignarOperadorResponsable(Operador operador) {
        if (operador == null) throw new IllegalArgumentException("Operador invalido");
        if (zona != null && operador.getZonaAsignada() != null && operador.getZonaAsignada() != zona) {
            throw new IllegalStateException("El operador no pertenece a la zona de la atraccion");
        }
        if (!operadoresResponsables.contiene(operador)) operadoresResponsables.agregarFinal(operador);
    }

    /**
     * Metodo para remover un operador responsable de la atracción.
     * El operador no puede ser nulo y debe ser actualmente responsable de la atracción.
     * @return
     */
    public boolean tieneOperadorResponsable() {
        return operadoresResponsables.getTamanio() > 0 || (zona != null && zona.tieneOperadores());
    }

    /**
     * Metodo que devuelve el id de la atracción
     * @return
     */
    public String getId(){
        return id;
    }

    /**
     * Metodo que devuelve el nombre de la atracción
     * @return
     */
    public String getNombre(){
        return nombre;
    }

    /**
     * Metodo que devuelve el tipo de la atracción
     * @return
     */
    public TipoAtraccion getTipo(){
        return tipo;
    }

    /**
     * Metodo que devuelve la capacidad máxima de visitantes por ciclo de la atracción
     * @return
     */
    public int getCapacidadMaximaPorCiclo(){
        return capacidadMaximaPorCiclo;
    }

    /**
     * Metodo que devuelve la altura mínima requerida para acceder a la atracción
     * @return
     */
    public double getAlturaMinima(){
        return alturaMinima;
    }

    /**
     * Metodo que devuelve la edad mínima requerida para acceder a la atracción
     * @return
     */
    public int getEdadMinima(){
        return edadMinima;
    }

    /**
     * Metodo que devuelve el costo adicional para acceder a la atracción, además del costo de entrada al parque
     * @return
     */
    public double getCostoAdicional(){
        return costoAdicional;
    }

    /**
     * Metodo que devuelve el contador acumulado de visitantes que han accedido a la atracción desde su última revisión o mantenimiento
     * @return
     */
    public int getContadorAcumuladoVisitantes(){
        return contadorAcumuladoVisitantes;
    }

    /**
     * Metodo para establecer el contador acumulado de visitantes, utilizado principalmente para reiniciar el contador después de una revisión o mantenimiento.
     * El contador no puede ser negativo.
     * @param contador
     */
    public void setContadorAcumuladoVisitantes(int contador){
        this.contadorAcumuladoVisitantes = contador;
    }

    /**
     * Metodo que devuelve el tiempo estimado de espera para acceder a la atracción, basado en el tamaño de la cola virtual y otros factores operativos.
     * @return
     */
    public int getTiempoEstimadoEspera(){
        return tiempoEstimadoEspera;
    }

    /**
     * Metodo para establecer el tiempo estimado de espera, utilizado principalmente para actualizar el tiempo después de cambios en la cola virtual o incidentes operativos.
     * @param tiempoEstimadoEspera
     */
    public void setTiempoEstimadoEspera(int tiempoEstimadoEspera){
        this.tiempoEstimadoEspera = Math.max(0, tiempoEstimadoEspera);
    }

    /**
     * Metodo que devuelve el estado actual de la atracción, indicando si está activa, en mantenimiento o cerrada.
     * @return
     */
    public EstadoAtraccion getEstado(){
        return estado;
    }

    /**
     * Metodo que devuelve el motivo de cierre de la atracción, si el estado es CERRADA.
     * Devuelve null o una cadena vacía si la atracción no está cerrada o si no se ha proporcionado un motivo.
     * @return
     */
    public String getMotivoCierre(){
        return motivoCierre;
    }

    /**
     * Metodo que devuelve la zona a la que pertenece la atracción, o null si la atracción no tiene una zona asignada.
     * @return
     */
    public Zona getZona(){
        return zona;
    }

    /**
     * Metodo para asignar una zona a la atracción.
     * @param zona
     */
    public void setZona(Zona zona){
        this.zona = zona;
    }

    /**
     * Metodo que devuelve la cola virtual de la atracción, que representa a los visitantes que están esperando para acceder a la atracción.
     * @return
     */
    public ColaPrioridad<EntradaEnCola> getColaVirtual(){
        return colaVirtual;
    }

    /**
     * Metodo que devuelve el número de incidentes operativos registrados en la atracción, utilizado para monitorear el desempeño operativo y la necesidad de mantenimiento.
     * @return
     */
    public int getIncidentesOperativos(){
        return incidentesOperativos;
    }

    /**
     * Metodo que devuelve el número de visitantes que han accedido a la atracción en el ciclo actual, utilizado para controlar la capacidad y el flujo de visitantes durante cada ciclo de operación.
     * @return
     */
    public int getVisitantesCicloActual(){
        return visitantesCicloActual;
    }

    /**
     * Metodo que devuelve la lista de operadores responsables asignados a la atracción, quienes son responsables de su operación y mantenimiento.
     * @return
     */
    public ListaEnlazada<Operador> getOperadoresResponsables(){
        return operadoresResponsables;
    }

    /**
     * Metodo que devuelve una representación en cadena de la atracción, mostrando su nombre y estado actual.
     * @return
     */
    public String toString(){ return nombre + " [" + estado + "]";
    }
}

