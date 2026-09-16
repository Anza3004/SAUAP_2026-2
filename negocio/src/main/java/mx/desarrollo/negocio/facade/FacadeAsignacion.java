package mx.desarrollo.negocio.facade;

import mx.desarrollo.entity.Asignacion;
import mx.desarrollo.entity.UnidadAprendizaje;
import mx.desarrollo.negocio.delegate.DelegateAsignacion;
import mx.desarrollo.negocio.integration.TraslapeValidator;
import mx.desarrollo.negocio.integration.ValidacionException;

import java.time.Duration;
import java.util.List;

public class FacadeAsignacion {

    private final DelegateAsignacion delegate;
    private final TraslapeValidator traslapeValidator;

    public FacadeAsignacion() {
        this.delegate = new DelegateAsignacion();
        this.traslapeValidator = new TraslapeValidator();
    }

    public Asignacion altaAsignacion(Asignacion asignacion) {
        validarAsignacion(asignacion, null);
        return delegate.altaAsignacion(asignacion);
    }

    public Asignacion modificarAsignacion(Asignacion asignacion) {
        if (asignacion.getId() == null) {
            throw new ValidacionException("La asignación debe tener ID para modificar.");
        }
        validarAsignacion(asignacion, asignacion.getId());
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

    public String obtenerResumenHoras(Integer idUnidad) {
        UnidadAprendizaje unidad = delegate.buscarUnidad(idUnidad);
        if (unidad == null) {
            throw new ValidacionException("No existe la unidad con ID: " + idUnidad);
        }
        int horasReq = calcularHorasRequeridas(unidad);
        Long minutosAsignados = delegate.sumarMinutosAsignados(idUnidad);
        return formatearMinutos(minutosAsignados.intValue()) + " / " + horasReq + "h";
    }

    public Long minutosAsignados(Integer idUnidad) {
        return delegate.sumarMinutosAsignados(idUnidad);
    }

    public int minutosRequeridos(Integer idUnidad) {
        UnidadAprendizaje unidad = delegate.buscarUnidad(idUnidad);
        if (unidad == null) return 0;
        return calcularHorasRequeridas(unidad) * 60;
    }

    private void validarAsignacion(Asignacion asignacion, Integer idExcluir) {
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

        boolean hayTraslape = traslapeValidator.hayTraslape(
                asignacion.getProfesor().getId(),
                asignacion.getDiaSemana(),
                asignacion.getHoraInicio(),
                asignacion.getHoraFin(),
                idExcluir);

        if (hayTraslape) {
            throw new ValidacionException(
                    "Existe un traslape de horario para el profesor en el día "
                            + asignacion.getDiaSemana()
                            + " entre las " + asignacion.getHoraInicio()
                            + " y las " + asignacion.getHoraFin() + ".");
        }

        validarHorasUnidad(asignacion, idExcluir);
    }

    private void validarHorasUnidad(Asignacion asignacion, Integer idExcluir) {
        UnidadAprendizaje unidad = delegate.buscarUnidad(asignacion.getUnidad().getId());
        if (unidad == null) {
            throw new ValidacionException("No existe la unidad seleccionada.");
        }

        long minutosNueva = Duration.between(
                asignacion.getHoraInicio(),
                asignacion.getHoraFin()
        ).toMinutes();

        int minutosReq = calcularHorasRequeridas(unidad) * 60;
        if (minutosReq == 0) {
            throw new ValidacionException(
                    "La unidad '" + unidad.getNombre() + "' no tiene horas configuradas.");
        }

        Long minutosYaAsignados;
        if (idExcluir != null) {
            minutosYaAsignados = delegate.sumarMinutosAsignadosExcluyendo(unidad.getId(), idExcluir);
        } else {
            minutosYaAsignados = delegate.sumarMinutosAsignados(unidad.getId());
        }

        long totalPropuesto = minutosYaAsignados + minutosNueva;

        if (totalPropuesto > minutosReq) {
            long exceso = totalPropuesto - minutosReq;
            throw new ValidacionException(
                    "No se puede asignar: excede el total de horas. " +
                            "Unidad: " + unidad.getNombre() + ". " +
                            "Requeridas: " + (minutosReq / 60) + "h. " +
                            "Ya asignadas: " + formatearMinutos(minutosYaAsignados.intValue()) + ". " +
                            "Nueva: " + formatearMinutos((int) minutosNueva) + ". " +
                            "Exceso: " + formatearMinutos((int) exceso) + ".");
        }

        if (totalPropuesto < minutosReq) {
            long faltante = minutosReq - totalPropuesto;
            System.out.println("ℹ️ Unidad '" + unidad.getNombre() + "': " +
                    formatearMinutos((int) totalPropuesto) + " de " + (minutosReq / 60) + "h. " +
                    "Faltan " + formatearMinutos((int) faltante) + ".");
        }
    }

    private int calcularHorasRequeridas(UnidadAprendizaje unidad) {
        int clase = unidad.getHorasClase() != null ? unidad.getHorasClase() : 0;
        int taller = unidad.getHorasTaller() != null ? unidad.getHorasTaller() : 0;
        int lab = unidad.getHorasLaboratorio() != null ? unidad.getHorasLaboratorio() : 0;
        return clase + taller + lab;
    }

    private String formatearMinutos(int minutos) {
        int h = minutos / 60;
        int m = minutos % 60;
        if (m == 0) return h + "h";
        return h + "h " + m + "min";
    }
}