package mx.desarrollo.negocio.facade;

import mx.desarrollo.entity.Asignacion;
import mx.desarrollo.negocio.delegate.DelegateAsignacion;
import mx.desarrollo.negocio.integration.TraslapeValidator;
import mx.desarrollo.negocio.integration.ValidacionException;

import java.util.List;

public class FacadeAsignacion {

    private final DelegateAsignacion delegate;
    private final TraslapeValidator traslapeValidator;

    public FacadeAsignacion() {
        this.delegate = new DelegateAsignacion();
        this.traslapeValidator = new TraslapeValidator();
    }

    /**
     * Da de alta una asignación validando:
     * - Campos obligatorios
     * - Rango horario válido (inicio < fin)
     * - Sin traslapes con otras asignaciones del mismo profesor
     */
    public Asignacion altaAsignacion(Asignacion asignacion) {
        validarAsignacion(asignacion);
        return delegate.altaAsignacion(asignacion);
    }

    public Asignacion modificarAsignacion(Asignacion asignacion) {
        if (asignacion.getId() == null) {
            throw new ValidacionException("La asignación debe tener ID para modificar.");
        }
        validarAsignacion(asignacion);
        return delegate.modificarAsignacion(asignacion);
    }

    public void eliminarAsignacion(Asignacion asignacion) {
        if (asignacion == null || asignacion.getId() == null) {
            throw new ValidacionException("Debe seleccionar una asignación válida.");
        }
        delegate.eliminarAsignacion(asignacion);
    }

    public Asignacion buscarPorId(Integer id) {
        return delegate.buscarPorId(id);
    }

    public List<Asignacion> consultarAsignaciones() {
        return delegate.consultarAsignaciones();
    }

    public List<Asignacion> consultarPorProfesor(Integer idProfesor) {
        return delegate.consultarPorProfesor(idProfesor);
    }

    public List<Asignacion> consultarPorUnidad(Integer idUnidad) {
        return delegate.consultarPorUnidad(idUnidad);
    }

    /**
     * Valida todos los aspectos de una asignación.
     */
    private void validarAsignacion(Asignacion asignacion) {
        if (asignacion == null) {
            throw new ValidacionException("La asignación no puede ser nula.");
        }
        if (asignacion.getProfesor() == null || asignacion.getProfesor().getId() == null) {
            throw new ValidacionException("Debe seleccionar un profesor.");
        }
        if (asignacion.getUnidad() == null || asignacion.getUnidad().getId() == null) {
            throw new ValidacionException("Debe seleccionar una unidad de aprendizaje.");
        }
        if (asignacion.getDiaSemana() == null || asignacion.getDiaSemana().isBlank()) {
            throw new ValidacionException("Debe seleccionar un día de la semana.");
        }
        if (asignacion.getHoraInicio() == null || asignacion.getHoraFin() == null) {
            throw new ValidacionException("Las horas de inicio y fin son obligatorias.");
        }

        String errorRango = TraslapeValidator.getMensajeErrorRango(
                asignacion.getHoraInicio(), asignacion.getHoraFin());
        if (errorRango != null) {
            throw new ValidacionException(errorRango);
        }

        // Validar traslape
        boolean hayTraslape = traslapeValidator.hayTraslape(
                asignacion.getProfesor().getId(),
                asignacion.getDiaSemana(),
                asignacion.getHoraInicio(),
                asignacion.getHoraFin());

        if (hayTraslape) {
            throw new ValidacionException(
                    "Existe un traslape de horario para el profesor en el día "
                            + asignacion.getDiaSemana()
                            + " entre las " + asignacion.getHoraInicio()
                            + " y las " + asignacion.getHoraFin() + ".");
        }
    }
}