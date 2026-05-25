package techpark.datos;

import techpark.enums.TipoAtraccion;
import techpark.model.eventos.Show;
import techpark.model.parque.Atraccion;
import techpark.model.parque.Parque;
import techpark.model.parque.Zona;
import techpark.model.usuarios.Administrador;
import techpark.model.usuarios.Operador;
import techpark.model.usuarios.Visitante;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase para cargar datos iniciales al sistema, creando un parque con zonas, atracciones, visitantes, operadores y un administrador.
 */
public class DatosIniciales {
    private Parque parque;
    private final List<Visitante> visitantes = new ArrayList<>();
    private final List<Operador> operadores = new ArrayList<>();
    private Administrador administrador;

    /**
     * Método para cargar los datos iniciales del parque, incluyendo la creación de un parque, zonas, atracciones, visitantes, operadores y un administrador. También se registran las atracciones en el parque y se asignan operadores responsables a cada atracción.
     */
    public void cargar() {
        parque = new Parque("PAR-001", "Tech-Park UQ", 1000);

        Zona aventura = new Zona("ZON-001", "Zona Aventura", 300);
        Zona agua = new Zona("ZON-002", "Zona Acuática", 250);
        Zona familiar = new Zona("ZON-003", "Zona Familiar", 400);
        parque.agregarZona(aventura); parque.agregarZona(agua); parque.agregarZona(familiar);

        Atraccion montana = new Atraccion("ATR-001", "yippe", TipoAtraccion.MECANICA_ALTURA, 20, 1.35, 12, 10000);
        Atraccion torre = new Atraccion("ATR-002", "la cumbre", TipoAtraccion.MECANICA_ALTURA, 12, 1.40, 14, 8000);
        Atraccion rapidos = new Atraccion("ATR-003", "rápidos", TipoAtraccion.ACUATICA, 16, 1.20, 10, 5000);
        Atraccion lago = new Atraccion("ATR-004", "montaña acuática", TipoAtraccion.ACUATICA, 10, 1.00, 6, 0);
        Atraccion carrusel = new Atraccion("ATR-005", "carrusel", TipoAtraccion.OTRA, 25, 0.80, 3, 0);
        Atraccion simulador = new Atraccion("ATR-006", "carros chocones", TipoAtraccion.OTRA, 15, 1.10, 8, 4000);
        Atraccion rueda = new Atraccion("ATR-007", "la rueda", TipoAtraccion.OTRA, 30, 0.90, 4, 0);
        Atraccion tunel = new Atraccion("ATR-008", "túnel", TipoAtraccion.OTRA, 18, 1.20, 10, 3000);

        aventura.agregarAtraccion(montana); aventura.agregarAtraccion(torre); aventura.agregarAtraccion(tunel);
        agua.agregarAtraccion(rapidos); agua.agregarAtraccion(lago);
        familiar.agregarAtraccion(carrusel); familiar.agregarAtraccion(simulador); familiar.agregarAtraccion(rueda);

        for (Zona z : parque.getZonas()) for (Atraccion a : z.getAtracciones()) parque.registrarAtraccion(a);

        parque.getMapa().agregarArista("ATR-001", "ATR-002", 4);
        parque.getMapa().agregarArista("ATR-002", "ATR-003", 6);
        parque.getMapa().agregarArista("ATR-003", "ATR-004", 3);
        parque.getMapa().agregarArista("ATR-004", "ATR-005", 5);
        parque.getMapa().agregarArista("ATR-005", "ATR-006", 2);
        parque.getMapa().agregarArista("ATR-006", "ATR-007", 4);
        parque.getMapa().agregarArista("ATR-007", "ATR-008", 3);
        parque.getMapa().agregarArista("ATR-001", "ATR-008", 9);
        parque.getMapa().agregarArista("ATR-002", "ATR-008", 5);
        parque.getMapa().agregarArista("ATR-008", "ATR-003", 7);

        visitantes.add(new Visitante("VIS-001", "Ana Perez", "1001", 20, "123", 1.60, 30000, null));
        visitantes.add(new Visitante("VIS-002", "Luis Gomez", "1002", 11, "123", 1.30, 20000, null));
        visitantes.add(new Visitante("VIS-003", "Marta Diaz", "1003", 35, "123", 1.70, 5000, null));
        visitantes.add(new Visitante("VIS-004", "Carlos Ruiz", "1004", 18, "123", 1.80, 100000, null));
        visitantes.add(new Visitante("VIS-005", "Sofia Lopez", "1005", 9, "123", 1.15, 15000, null));
        visitantes.forEach(parque::registrarUsuario);

        operadores.add(new Operador("OPE-001", "Operador Aventura", "2001", 30, "123"));
        operadores.add(new Operador("OPE-002", "Operador Agua", "2002", 31, "123"));
        operadores.add(new Operador("OPE-003", "Operador Familiar", "2003", 28, "123"));
        aventura.agregarOperador(operadores.get(0)); agua.agregarOperador(operadores.get(1)); familiar.agregarOperador(operadores.get(2));
        for (Atraccion a : aventura.getAtracciones()) a.asignarOperadorResponsable(operadores.get(0));
        for (Atraccion a : agua.getAtracciones()) a.asignarOperadorResponsable(operadores.get(1));
        for (Atraccion a : familiar.getAtracciones()) a.asignarOperadorResponsable(operadores.get(2));
        operadores.forEach(parque::registrarUsuario);

        parque.registrarShow(new Show("SHO-001", "Show de Luces Binarias", aventura, LocalDateTime.now().plusHours(2), 30));
        parque.registrarShow(new Show("SHO-002", "Magia Algoritmica", familiar, LocalDateTime.now().plusHours(4), 25));

        administrador = new Administrador("ADM-001", "Admin Principal", "3001", 40, "123");
        administrador.setParqueGestionado(parque);
        parque.registrarUsuario(administrador);
    }

    /**
     * Método getter para obtener el parque con los datos iniciales cargados
     * @return
     */
    public Parque getParque(){
        return parque;
    }

    /**
     * Método getter para obtener la lista de visitantes creados en los datos iniciales
     * @return
     */
    public List<Visitante> getVisitantes(){
        return visitantes;
    }

    /**
     * Método getter para obtener la lista de operadores creados en los datos iniciales
     * @return
     */
    public List<Operador> getOperadores(){
        return operadores;
    }

    /**
     * Método getter para obtener el administrador creado en los datos iniciales
     * @return
     */
    public Administrador getAdministrador(){
        return administrador;
    }

}

