package techpark.gui;

import techpark.datos.CargadorArchivo;
import techpark.enums.*;
import techpark.estructuras.grafo.NodoGrafo;
import techpark.model.parque.Atraccion;
import techpark.model.parque.Zona;
import techpark.model.reportes.ReporteJornada;
import techpark.model.reportes.ResultadoAcceso;
import techpark.model.usuarios.Operador;
import techpark.model.usuarios.Usuario;
import techpark.model.usuarios.Visitante;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Ventana principal de la aplicación, con pestañas para cada rol y funcionalidades correspondientes.
 */
public class VentanaPrincipal extends JFrame {
    private final AppContext contexto;
    private final Usuario usuario;
    private final JTextArea salida = new JTextArea();
    private JComboBox<String> cbVisitantes;
    private JComboBox<String> cbAtracciones;
    private JComboBox<String> cbZonas;
    private JComboBox<TipoTicket> cbTicket;
    private JComboBox<String> cbOperadores;

    /**
     * Constructor de la ventana principal.
     */
    public VentanaPrincipal(AppContext contexto, Usuario usuario) {
        this.contexto = contexto;
        this.usuario = usuario;
        setTitle("Tech-Park UQ - " + usuario.getNombre() + " [" + usuario.getRol() + "]");
        setSize(900, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        crearContenido();
        refrescarCombos();
        imprimirEstadoGeneral();
    }

    /**
     * Método para crear el contenido de la ventana.
     * Cada pestaña contiene botones y campos específicos para las acciones permitidas según el rol del usuario.
     */
    private void crearContenido() {
        salida.setEditable(false);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Inicio", crearPanelInicio());
        tabs.addTab("Visitante", crearPanelVisitante());
        tabs.addTab("Operador", crearPanelOperador());
        tabs.addTab("Administrador", crearPanelAdministrador());
        tabs.addTab("Mapa y Reportes", crearPanelReportes());
        add(tabs, BorderLayout.NORTH);
        add(new JScrollPane(salida), BorderLayout.CENTER);
    }

    /**
     * Método para crear el panel de inicio, que muestra información general del parque y botones para acciones comunes como ver el estado del parque, limpiar la salida o cerrar sesión.
     * @return
     */
    private JPanel crearPanelInicio() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 8, 8));
        JButton btnEstado = new JButton("Ver estado del parque");
        JButton btnLimpiar = new JButton("Limpiar salida");
        JButton btnSalir = new JButton("Cerrar sesión");
        btnEstado.addActionListener(e -> imprimirEstadoGeneral());
        btnLimpiar.addActionListener(e -> salida.setText(""));
        btnSalir.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); });
        panel.add(new JLabel("Rol actual: " + usuario.getRol()));
        panel.add(btnEstado);
        panel.add(btnLimpiar);
        panel.add(new JLabel("Documento: " + usuario.getDocumento()));
        panel.add(btnSalir);
        return panel;
    }

    /**
     * Método para crear el panel de visitante.
     * @return
     */
    private JPanel crearPanelVisitante() {
        JPanel panel = new JPanel(new GridLayout(4, 4, 8, 8));
        cbVisitantes = new JComboBox<>();
        cbAtracciones = new JComboBox<>();
        cbZonas = new JComboBox<>();
        cbTicket = new JComboBox<>(TipoTicket.values());

        JButton btnComprar = new JButton("Comprar ticket");
        JButton btnCola = new JButton("Unirse a cola");
        JButton btnFavorito = new JButton("Agregar favorito");
        JButton btnHistorial = new JButton("Ver historial");
        JButton btnRuta = new JButton("Ruta ATR-001 → seleccionada");

        btnComprar.addActionListener(e -> comprarTicket());
        btnCola.addActionListener(e -> unirseACola());
        btnFavorito.addActionListener(e -> agregarFavorito());
        btnHistorial.addActionListener(e -> verHistorial());
        btnRuta.addActionListener(e -> calcularRuta());

        panel.add(new JLabel("Visitante")); panel.add(cbVisitantes);
        panel.add(new JLabel("Tipo ticket")); panel.add(cbTicket);
        panel.add(new JLabel("Zona entrada")); panel.add(cbZonas);
        panel.add(btnComprar); panel.add(btnCola);
        panel.add(new JLabel("Atracción")); panel.add(cbAtracciones);
        panel.add(btnFavorito); panel.add(btnHistorial);
        panel.add(btnRuta);
        return panel;
    }

    /**
     * Método para crear el panel de operador
     * @return
     */
    private JPanel crearPanelOperador() {
        JPanel panel = new JPanel(new GridLayout(3, 3, 8, 8));
        cbOperadores = new JComboBox<>();
        JButton btnProcesar = new JButton("Procesar siguiente cola");
        JButton btnCerrar = new JButton("Cerrar atracción");
        JButton btnActivar = new JButton("Activar atracción");
        JButton btnRevision = new JButton("Registrar revisión satisfactoria");
        btnProcesar.addActionListener(e -> procesarSiguiente());
        btnCerrar.addActionListener(e -> cambiarEstado(EstadoAtraccion.CERRADA));
        btnActivar.addActionListener(e -> cambiarEstado(EstadoAtraccion.ACTIVA));
        btnRevision.addActionListener(e -> registrarRevision());
        panel.add(new JLabel("Operador")); panel.add(cbOperadores); panel.add(new JLabel("Usa la atracción del panel Visitante"));
        panel.add(btnProcesar); panel.add(btnCerrar); panel.add(btnActivar);
        panel.add(btnRevision);
        return panel;
    }

    /**
     * Método para crear el panel de administrador
     * @return
     */
    private JPanel crearPanelAdministrador() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 8, 8));
        JButton btnClima = new JButton("Activar lluvia fuerte");
        JButton btnTormenta = new JButton("Activar tormenta eléctrica");
        JButton btnCsv = new JButton("Cargar datos CSV/TXT");
        btnClima.addActionListener(e -> activarClima(TipoClima.LLUVIA_FUERTE));
        btnTormenta.addActionListener(e -> activarClima(TipoClima.TORMENTA_ELECTRICA));
        btnCsv.addActionListener(e -> cargarArchivo());
        panel.add(btnClima); panel.add(btnTormenta); panel.add(btnCsv);
        panel.add(new JLabel("Formato CSV explicado en README y archivo ejemplo."));
        return panel;
    }

    /**
     * Método para crear el panel de reportes y mapa, accesible para el administrador.
     * @return
     */
    private JPanel crearPanelReportes() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 8, 8));
        JButton btnMapa = new JButton("Ver mapa textual");
        JButton btnReporte = new JButton("Generar reporte jornada");
        JButton btnAlertas = new JButton("Ver alertas mantenimiento");
        btnMapa.addActionListener(e -> imprimirMapa());
        btnReporte.addActionListener(e -> generarReporte());
        btnAlertas.addActionListener(e -> salida.append("\nAlertas pendientes/históricas: " + contexto.getServicioAlertas().getHistorialMantenimiento().size() + "\n"));
        panel.add(btnMapa); panel.add(btnReporte); panel.add(btnAlertas);
        return panel;
    }

    /**
     * Método para refrescar los combos de selección de visitantes, operadores, atracciones y zonas, cargando los datos actuales del contexto.
     */
    private void refrescarCombos() {
        if (cbVisitantes != null) {
            cbVisitantes.removeAllItems();
            for (Visitante v : contexto.getVisitantes()) cbVisitantes.addItem(v.getDocumento() + " - " + v.getNombre());
        }
        if (cbOperadores != null) {
            cbOperadores.removeAllItems();
            for (Operador o : contexto.getOperadores()) cbOperadores.addItem(o.getDocumento() + " - " + o.getNombre());
        }
        if (cbAtracciones != null) {
            cbAtracciones.removeAllItems();
            for (Atraccion a : contexto.getParque().getCatalogoAtracciones().inorden()) cbAtracciones.addItem(a.getId() + " - " + a.getNombre());
        }
        if (cbZonas != null) {
            cbZonas.removeAllItems();
            for (Zona z : contexto.getParque().getZonas()) cbZonas.addItem(z.getId() + " - " + z.getNombre());
        }
    }

    /**
     * Método para obtener el visitante seleccionado en el combo, buscando por documento en el contexto del parque.
     * @return
     */
    private Visitante visitanteSeleccionado() {
        String item = (String) cbVisitantes.getSelectedItem();
        if (item == null) return null;
        Usuario u = contexto.getParque().buscarUsuarioPorDocumento(item.split(" - ")[0]);
        return u instanceof Visitante ? (Visitante) u : null;
    }

    /**
     * Método para obtener el operador seleccionado en el combo, buscando por documento en el contexto del parque.
     * @return
     */
    private Operador operadorSeleccionado() {
        String item = (String) cbOperadores.getSelectedItem();
        if (item == null) return null;
        Usuario u = contexto.getParque().buscarUsuarioPorDocumento(item.split(" - ")[0]);
        return u instanceof Operador ? (Operador) u : null;
    }

    /**
     * Método para obtener la atracción seleccionada en el combo, buscando por ID en el catálogo de atracciones del parque.
     * @return
     */
    private Atraccion atraccionSeleccionada() {
        String item = (String) cbAtracciones.getSelectedItem();
        if (item == null) return null;
        return contexto.getParque().buscarAtraccion(item.split(" - ")[0]);
    }

    /**
     * Método para obtener la zona seleccionada en el combo, buscando por ID en las zonas del parque.
     * @return
     */
    private Zona zonaSeleccionada() {
        String item = (String) cbZonas.getSelectedItem();
        if (item == null) return null;
        String id = item.split(" - ")[0];
        for (Zona z : contexto.getParque().getZonas()) if (z.getId().equals(id)) return z;
        return null;
    }

    /**
     * Método para comprar un ticket, validando que el usuario tenga el rol adecuado, obteniendo los datos seleccionados en los combos y llamando al servicio de parque para vender el ticket y registrar el ingreso.
     */
    private void comprarTicket() {
        try {
            validarRolVisitanteOAdmin();
            Visitante v = visitanteSeleccionado();
            TipoTicket tipo = (TipoTicket) cbTicket.getSelectedItem();
            int grupo = 1;
            if (tipo == TipoTicket.FAMILIAR) {
                String texto = JOptionPane.showInputDialog(this, "Cantidad de personas del grupo familiar:", "4");
                grupo = Integer.parseInt(texto);
            }
            var ticket = contexto.getServicioParque().venderTicket(v, tipo, zonaSeleccionada(), grupo);
            contexto.getServicioParque().registrarIngreso(zonaSeleccionada());
            salida.append("\nTicket vendido a " + v.getNombre() + ": " + ticket.getTipo() + " - $" + ticket.getPrecio() + "\n");
        } catch (Exception ex) { mostrarError(ex); }
    }

    /**
     * Método para unirse a la cola de una atracción, validando que el usuario tenga el rol adecuado, obteniendo los datos seleccionados en los combos y llamando al servicio de colas para registrar la solicitud y mostrar el mensaje resultante.
     */
    private void unirseACola() {
        try {
            validarRolVisitanteOAdmin();
            String msg = contexto.getServicioColas().unirseACola(visitanteSeleccionado(), atraccionSeleccionada());
            salida.append("\n" + msg + "\n");
        } catch (Exception ex) { mostrarError(ex); }
    }

    /**
     * Método para agregar una atracción a favoritos, validando que el usuario tenga el rol adecuado, obteniendo los datos seleccionados en los combos y llamando al método del visitante para agregar el favorito, mostrando un mensaje según el resultado.
     */
    private void agregarFavorito() {
        try {
            Visitante v = visitanteSeleccionado();
            Atraccion a = atraccionSeleccionada();
            boolean ok = v.agregarFavorito(a);
            salida.append("\nFavorito " + (ok ? "agregado: " : "ya existía: ") + a.getNombre() + "\n");
        } catch (Exception ex) { mostrarError(ex); }
    }

    /**
     * Método para ver el historial de visitas de un visitante, validando que el usuario tenga el rol adecuado, obteniendo el visitante seleccionado en el combo y mostrando su historial de visitas registradas, o un mensaje si no tiene visitas.
     */
    private void verHistorial() {
        try {
            Visitante v = visitanteSeleccionado();
            salida.append("\nHistorial de " + v.getNombre() + ":\n");
            if (v.getHistorialVisitas().estaVacia()) salida.append("Sin visitas registradas.\n");
            for (var r : v.getHistorialVisitas()) salida.append("- " + r + "\n");
        } catch (Exception ex) { mostrarError(ex); }
    }

    /**
     * Método para calcular la ruta sugerida desde la atracción ATR-001 (puerta de entrada) hasta la atracción seleccionada.
     */
    private void calcularRuta() {
        try {
            Atraccion destino = atraccionSeleccionada();
            List<NodoGrafo<Atraccion>> ruta = contexto.getParque().getMapa().dijkstra("ATR-001", destino.getId());
            salida.append("\nRuta sugerida:\n");
            for (NodoGrafo<Atraccion> n : ruta) salida.append("- " + n.getDato().getNombre() + "\n");
        } catch (Exception ex) { mostrarError(ex); }
    }

    /**
     * Método para procesar el siguiente visitante en la cola de una atracción.
     */
    private void procesarSiguiente() {
        try {
            validarRolOperadorOAdmin();
            ResultadoAcceso r = contexto.getServicioOperador().procesarSiguienteEnCola(operadorSeleccionado(), atraccionSeleccionada());
            salida.append("\nResultado acceso: " + r + "\n");
        } catch (Exception ex) { mostrarError(ex); }
    }

    /**
     * Método para cambiar el estado de una atracción (cerrada o activa).
     * @param estado
     */
    private void cambiarEstado(EstadoAtraccion estado) {
        try {
            validarRolOperadorOAdmin();
            String motivo = estado == EstadoAtraccion.CERRADA ? "Cierre manual por operador" : null;
            contexto.getServicioOperador().cambiarEstadoAtraccion(operadorSeleccionado(), atraccionSeleccionada(), estado, motivo);
            salida.append("\nEstado actualizado: " + atraccionSeleccionada().getNombre() + " -> " + estado + "\n");
        } catch (Exception ex) { mostrarError(ex); }
    }

    /**
     * Método para registrar una revisión técnica satisfactoria en una atracción, lo que la activa si estaba cerrada por mantenimiento.
     */
    private void registrarRevision() {
        try {
            validarRolOperadorOAdmin();
            contexto.getServicioOperador().registrarRevisionTecnica(operadorSeleccionado(), atraccionSeleccionada(), "Revisión técnica desde GUI", ResultadoRevision.SATISFACTORIA);
            salida.append("\nRevisión satisfactoria registrada. Atracción activa.\n");
        } catch (Exception ex) { mostrarError(ex); }
    }

    /**
     * Método para activar una alerta climática, lo que cierra automáticamente las atracciones afectadas por el clima seleccionado.
     * @param tipo
     */
    private void activarClima(TipoClima tipo) {
        try {
            validarAdmin();
            var alerta = contexto.getServicioAlertas().activarAlertaClimatica(contexto.getParque(), tipo);
            salida.append("\nAlerta climática activada: " + tipo + ". Atracciones afectadas: " + alerta.getAtraccionesAfectadas().size() + "\n");
        } catch (Exception ex) { mostrarError(ex); }
    }

    /**
     * Método para generar un reporte de la jornada.
     */
    private void generarReporte() {
        try {
            validarAdmin();
            ReporteJornada r = contexto.getServicioReportes().generarReporteJornada();
            salida.append("\n" + r + "\n");
            salida.append("Atracciones más visitadas:\n");
            for (Atraccion a : r.getAtraccionesMasVisitadas()) salida.append("- " + a.getNombre() + ": " + a.getContadorAcumuladoVisitantes() + " visitas\n");
            salida.append("Tiempos promedio/estimados:\n");
            for (String t : r.getTiemposPromedioEspera()) salida.append("- " + t + "\n");
            salida.append("Atracciones con incidentes: " + r.getAtraccionesConMasIncidentes().size() + "\n");
        } catch (Exception ex) { mostrarError(ex); }
    }

    /**
     * Método para cargar datos desde un archivo CSV o TXT, utilizando un JFileChooser para seleccionar el archivo, y luego configurando el contexto del parque con los datos cargados, refrescando los combos y mostrando un mensaje con el resultado.
     */
    private void cargarArchivo() {
        try {
            validarAdmin();
            JFileChooser chooser = new JFileChooser();
            int respuesta = chooser.showOpenDialog(this);
            if (respuesta != JFileChooser.APPROVE_OPTION) return;
            File archivo = chooser.getSelectedFile();
            CargadorArchivo cargador = new CargadorArchivo();
            cargador.cargar(archivo);
            contexto.configurar(cargador.getParque(), cargador.getVisitantes(), cargador.getOperadores());
            refrescarCombos();
            salida.append("\nDatos cargados desde archivo: " + archivo.getName() + "\n");
            imprimirEstadoGeneral();
        } catch (Exception ex) { mostrarError(ex); }
    }

    /**
     * Método para imprimir el estado general del parque, incluyendo el nombre del parque, su aforo actual y capacidad máxima, las zonas con su aforo y capacidad, y las atracciones con su estado y tamaño de cola virtual.
     */
    private void imprimirEstadoGeneral() {
        salida.append("\n=== Estado general del parque ===\n");
        salida.append("Parque: " + contexto.getParque().getNombre() + " | Aforo: " + contexto.getParque().getAforoActual() + "/" + contexto.getParque().getCapacidadMaxima() + "\n");
        for (Zona z : contexto.getParque().getZonas()) {
            salida.append("Zona: " + z.getNombre() + " | Aforo: " + z.getAforoActual() + "/" + z.getCapacidadMaxima() + "\n");
            for (Atraccion a : z.getAtracciones()) salida.append("  - " + a.getId() + " " + a.getNombre() + " [" + a.getEstado() + "] Cola: " + a.getColaVirtual().getTamanio() + "\n");
        }
    }

    /**
     * Método para imprimir un mapa textual del grafo de atracciones, mostrando cada nodo con su ID, nombre y sus vecinos con el peso de las aristas.
     */
    private void imprimirMapa() {
        salida.append("\n=== Mapa textual del grafo ===\n");
        for (NodoGrafo<Atraccion> nodo : contexto.getParque().getMapa().getNodos()) {
            salida.append(nodo.getId() + " " + nodo.getDato().getNombre() + " -> ");
            nodo.getVecinos().forEach(a -> salida.append(a.getDestino().getId() + "(" + a.getPeso() + ") "));
            salida.append("\n");
        }
    }

    /**
     * Método para validar que el usuario tenga el rol de administrador, lanzando una excepción de seguridad si no es así.
     */
    private void validarAdmin() {
        if (usuario.getRol() != Rol.ADMINISTRADOR) throw new SecurityException("Solo el administrador puede realizar esta acción");
    }

    /**
     * Método para validar que el usuario tenga el rol de operador o administrador, lanzando una excepción de seguridad si no es así.
     */
    private void validarRolOperadorOAdmin() {
        if (usuario.getRol() != Rol.OPERADOR && usuario.getRol() != Rol.ADMINISTRADOR) {
            throw new SecurityException("Acción permitida solo para operador o administrador");
        }
    }

    /**
     * Método para validar que el usuario tenga el rol de visitante o administrador.
     */
    private void validarRolVisitanteOAdmin() {
        if (usuario.getRol() != Rol.VISITANTE && usuario.getRol() != Rol.ADMINISTRADOR) {
            throw new SecurityException("Acción permitida solo para visitante o administrador");
        }
    }

    /**
     * Método para mostrar un mensaje de error en un cuadro de diálogo, utilizando el mensaje de la excepción proporcionada.
     * @param ex
     */
    private void mostrarError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}
