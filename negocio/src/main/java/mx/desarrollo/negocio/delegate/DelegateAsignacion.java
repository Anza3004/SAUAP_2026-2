package mx.desarrollo.negocio.delegate;

import mx.desarrollo.entity.Asignacion;
import mx.desarrollo.persistencia.integration.ServiceLocator;

import java.time.LocalTime;
import java.util.List;

public class DelegateAsignacion {

    private final ServiceLocator locator = ServiceLocator.getInstance();

    public Asignacion altaAsignacion(Asignacion asignacion) {
        return locator.getAsignacionDAO().guardar(asignacion);
    }

    public Asignacion modificarAsignacion(Asignacion asignacion) {
        return locator.getAsignacionDAO().guardar(asignacion);
    }

    public void eliminarAsignacion(Asignacion asignacion) {
        locator.getAsignacionDAO().eliminar(asignacion);
    }

    public Asignacion buscarPorId(Integer id) {
        return locator.getAsignacionDAO().buscarPorId(id);
    }

    public List<Asignacion> consultarAsignaciones() {
        return locator.getAsignacionDAO().listarTodos();
    }

    public List<Asignacion> buscarTraslapes(Integer idProfesor, String dia,
                                            LocalTime horaInicio, LocalTime horaFin) {
        return locator.getAsignacionDAO()
                .buscarTraslapes(idProfesor, dia, horaInicio, horaFin);
    }

    public List<Asignacion> buscarTraslapes(Integer idProfesor, String dia,
                                            LocalTime horaInicio, LocalTime horaFin,
                                            Integer idExcluir) {
        return locator.getAsignacionDAO()
                .buscarTraslapes(idProfesor, dia, horaInicio, horaFin, idExcluir);
    }

    public mx.desarrollo.entity.UnidadAprendizaje buscarUnidad(Integer idUnidad) {
        return locator.getUnidadDAO().buscarPorId(idUnidad);
    }

    public List<Asignacion> consultarPorProfesor(Integer idProfesor) {
        return locator.getAsignacionDAO().listarPorProfesor(idProfesor);
    }

    public List<Asignacion> consultarPorUnidad(Integer idUnidad) {
        return locator.getAsignacionDAO().listarPorUnidad(idUnidad);
    }
    public Long sumarMinutosAsignados(Integer idUnidad) {
        return locator.getAsignacionDAO().sumarMinutosAsignados(idUnidad);
    }

    public Long sumarMinutosAsignadosExcluyendo(Integer idUnidad, Integer idAsignacionExcluir) {
        return locator.getAsignacionDAO()
                .sumarMinutosAsignadosExcluyendo(idUnidad, idAsignacionExcluir);
    }
    /**
     * Elimina todas las asignaciones con el mismo grupo.
     */
    public int eliminarPorGrupo(String grupo) {
        return locator.getAsignacionDAO().eliminarPorGrupo(grupo);
    }
}