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
     * Valida si hay traslape (sin exclusión, para altas).
     */
    public boolean hayTraslape(Integer idProfesor, String diaSemana,
                               LocalTime horaInicio, LocalTime horaFin) {
        return hayTraslape(idProfesor, diaSemana, horaInicio, horaFin, null);
    }

    /**
     * Valida si hay traslape para un profesor en un día y rango horario.
     * @param idExcluir si no es null, excluye esa asignación (para modificar)
     */
    public boolean hayTraslape(Integer idProfesor, String diaSemana,
                               LocalTime horaInicio, LocalTime horaFin,
                               Integer idExcluir) {
        if (idProfesor == null || diaSemana == null
                || horaInicio == null || horaFin == null) {
            return false;
        }

        List<Asignacion> traslapes = delegateAsignacion
                .buscarTraslapes(idProfesor, diaSemana, horaInicio, horaFin, idExcluir);

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