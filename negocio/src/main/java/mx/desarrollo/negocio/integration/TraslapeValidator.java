package mx.desarrollo.negocio.integration;

import mx.desarrollo.entity.Asignacion;
import mx.desarrollo.negocio.delegate.DelegateAsignacion;

import java.time.LocalTime;
import java.util.List;

/**
 * Valida que no exista traslape de horarios para un profesor.
 * El traslape ocurre si dos asignaciones se solapan en el mismo día.
 */
public class TraslapeValidator {

    private final DelegateAsignacion delegateAsignacion;

    public TraslapeValidator() {
        this.delegateAsignacion = new DelegateAsignacion();
    }

    /**
     * Valida si hay traslape para un profesor en un día y rango horario.
     * @param idProfesor ID del profesor
     * @param diaSemana Día de la semana (LUNES, MARTES, ...)
     * @param horaInicio Hora de inicio
     * @param horaFin Hora de fin
     * @return true si HAY traslape, false si NO hay
     */
    public boolean hayTraslape(Integer idProfesor, String diaSemana,
                               LocalTime horaInicio, LocalTime horaFin) {
        if (idProfesor == null || diaSemana == null
                || horaInicio == null || horaFin == null) {
            return false;
        }

        List<Asignacion> traslapes = delegateAsignacion
                .buscarTraslapes(idProfesor, diaSemana, horaInicio, horaFin);

        return !traslapes.isEmpty();
    }

    /**
     * Valida el rango horario: horaInicio debe ser antes de horaFin.
     */
    public static boolean rangoValido(LocalTime horaInicio, LocalTime horaFin) {
        if (horaInicio == null || horaFin == null) return false;
        return horaInicio.isBefore(horaFin);
    }

    public static String getMensajeErrorRango(LocalTime horaInicio, LocalTime horaFin) {
        if (horaInicio == null || horaFin == null) {
            return "Las horas de inicio y fin son obligatorias.";
        }
        if (!rangoValido(horaInicio, horaFin)) {
            return "La hora de inicio debe ser anterior a la hora de fin.";
        }
        return null;
    }
}