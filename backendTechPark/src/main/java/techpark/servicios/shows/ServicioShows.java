package techpark.servicios.shows;

import techpark.enums.EstadoShow;
import techpark.enums.TipoNotif;
import techpark.model.eventos.Notificacion;
import techpark.model.eventos.Show;
import techpark.model.parque.Parque;
import techpark.model.parque.Zona;
import techpark.model.usuarios.Visitante;
import techpark.utilidades.GeneradorId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ServicioShows es la clase encargada de gestionar los shows del parque.
 */
public class ServicioShows {
    private final Parque parque;
    private final Set<String> eventosNotificados = new HashSet<>();

    /**
     * Constructor de la clase ServicioShows.
     * @param parque
     */
    public ServicioShows(Parque parque) {
        this.parque = parque;
        asegurarShowsFijos();
    }

    /**
     * Método que devuelve la lista de shows programados para el día actual, asegurándose de que los shows fijos estén presentes y actualizando sus estados según el horario.
     * @return
     */
    public List<Show> listarShowsDelDia() {
        asegurarShowsFijos();
        actualizarEstadosYNotificar();
        return parque.getShows();
    }

    /**
     * Método que devuelve la lista de shows que están programados para el día actual, filtrando solo aquellos que aún no han comenzado.
     * @return
     */
    public List<Show> listarShowsProgramados() {
        List<Show> programados = new ArrayList<>();
        for (Show show : listarShowsDelDia()) {
            if (show.getEstado() == EstadoShow.PROGRAMADO) programados.add(show);
        }
        return programados;
    }

    /**
     * Método que devuelve un mensaje indicando el estado actual del show (próximo, en 10 minutos, en curso o finalizado) según el horario actual y el horario del show.
     * @param show
     * @return
     */
    public String mensajeEstado(Show show) {
        LocalTime ahora = LocalTime.now();
        LocalTime inicio = show.getHorario().toLocalTime();
        LocalTime aviso = inicio.minusMinutes(10);
        LocalTime fin = inicio.plusMinutes(show.getDuracion() <= 0 ? 30 : show.getDuracion());

        if (ahora.isBefore(aviso)) return "Próximo show: " + formatearHora(inicio);
        if (!ahora.isBefore(aviso) && ahora.isBefore(inicio)) return "En 10 minutos empieza el Show del Café de las " + formatearHora(inicio);
        if (!ahora.isBefore(inicio) && ahora.isBefore(fin)) return "En curso";
        return "Finalizado";
    }

    /**
     * Método que inicia un show específico, cambiando su estado a "En curso" y enviando una notificación a los visitantes con ticket activo.
     * @param idShow
     * @return
     */
    public Notificacion iniciarShow(String idShow) {
        Show show = buscarShow(idShow);
        if (show == null) throw new IllegalArgumentException("Show no encontrado: " + idShow);
        show.setEstado(EstadoShow.EN_CURSO);
        return notificar(show, "inicio", "El Show del Café está en curso");
    }

    /**
     * Método que finaliza un show específico, cambiando su estado a "Finalizado" y enviando una notificación a los visitantes con ticket activo.
     * @param idShow
     */
    public void finalizarShow(String idShow) {
        Show show = buscarShow(idShow);
        if (show == null) throw new IllegalArgumentException("Show no encontrado: " + idShow);
        show.setEstado(EstadoShow.FINALIZADO);
        notificar(show, "fin", "El Show del Café ha finalizado");
    }

    /**
     * Método que programa un nuevo show, registrándolo en el parque y asegurándose de que no sea nulo.
     * Devuelve el show programado.
     * @param show
     * @return
     */
    public Show programarShow(Show show) {
        if (show == null) throw new IllegalArgumentException("Show invalido");
        parque.registrarShow(show);
        return show;
    }

    /**
     * Método privado que actualiza el estado de cada show según el horario actual y el horario programado del show, y envía notificaciones a los visitantes con ticket activo cuando un show está próximo a comenzar, en curso o ha finalizado.
     */
    private void actualizarEstadosYNotificar() {
        LocalTime ahora = LocalTime.now();
        for (Show show : parque.getShows()) {
            LocalTime inicio = show.getHorario().toLocalTime();
            LocalTime aviso = inicio.minusMinutes(10);
            LocalTime fin = inicio.plusMinutes(show.getDuracion() <= 0 ? 30 : show.getDuracion());

            if (!ahora.isBefore(aviso) && ahora.isBefore(inicio)) {
                notificar(show, "aviso", "En 10 minutos empieza el Show del Café de las " + formatearHora(inicio));
            } else if (!ahora.isBefore(inicio) && ahora.isBefore(fin)) {
                show.setEstado(EstadoShow.EN_CURSO);
                notificar(show, "inicio", "El Show del Café está en curso");
            } else if (!ahora.isBefore(fin)) {
                show.setEstado(EstadoShow.FINALIZADO);
                notificar(show, "fin", "El Show del Café ha finalizado");
            } else {
                show.setEstado(EstadoShow.PROGRAMADO);
            }
        }
    }

    /**
     * Método privado que envía una notificación a los visitantes con ticket activo sobre un evento específico relacionado con un show.
     * @param show
     * @param evento
     * @param mensaje
     * @return
     */
    private Notificacion notificar(Show show, String evento, String mensaje) {
        String clave = show.getId() + "-" + evento + "-" + LocalDate.now();
        if (eventosNotificados.contains(clave)) return null;
        eventosNotificados.add(clave);
        Notificacion notificacion = new Notificacion(GeneradorId.generarId("NOT-"), mensaje, TipoNotif.SHOW);
        parque.registrarNotificacionGlobal(notificacion);
        for (Visitante visitante : parque.getVisitantesConTicketActivo()) notificacion.agregarDestinatario(visitante);
        return notificacion;
    }

    /**
     * Método privado que asegura que los shows fijos del día (Show del Café a las 10:00 a. m. y a las 3:00 p. m.) estén presentes en la lista de shows del parque, y si no es así, los registra automáticamente con el horario correspondiente.
     */
    private void asegurarShowsFijos() {
        LocalDate hoy = LocalDate.now();
        boolean formatoCorrecto = parque.getShows().size() == 2;
        if (formatoCorrecto) {
            boolean tiene10 = false;
            boolean tiene15 = false;
            for (Show show : parque.getShows()) {
                LocalTime hora = show.getHorario().toLocalTime();
                boolean nombreCorrecto = "Show del Café".equals(show.getNombre());
                if (!nombreCorrecto) {
                    formatoCorrecto = false;
                    break;
                }
                if (hora.getHour() == 10 && hora.getMinute() == 0) tiene10 = true;
                if (hora.getHour() == 15 && hora.getMinute() == 0) tiene15 = true;
            }
            formatoCorrecto = formatoCorrecto && tiene10 && tiene15;
        }

        if (!formatoCorrecto) {
            parque.getShows().clear();
            Zona zona = parque.getZonas().isEmpty() ? null : parque.getZonas().get(0);
            parque.registrarShow(new Show("SHO-CAFE-10", "Show del Café", zona, LocalDateTime.of(hoy, LocalTime.of(10, 0)), 30));
            parque.registrarShow(new Show("SHO-CAFE-15", "Show del Café", zona, LocalDateTime.of(hoy, LocalTime.of(15, 0)), 30));
        }
    }

    /**
     * Método privado que formatea la hora de un show en un formato legible para los visitantes, mostrando "10:00 a. m." para las 10:00 y "3:00 p. m." para las 15:00, y el formato estándar para otras horas.
     * @param hora
     * @return
     */
    private String formatearHora(LocalTime hora) {
        if (hora.getHour() == 10) return "10:00 a. m.";
        if (hora.getHour() == 15) return "3:00 p. m.";
        return hora.toString();
    }

    /**
     * Método privado que busca un show específico por su id en la lista de shows del parque, y devuelve el show si se encuentra o null si no se encuentra.
     * @param idShow
     * @return
     */
    private Show buscarShow(String idShow) {
        for (Show show : parque.getShows()) {
            if (show.getId().equalsIgnoreCase(idShow)) return show;
        }
        return null;
    }
}

