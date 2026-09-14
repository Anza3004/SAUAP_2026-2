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

    public List<Asignacion> consultarPorProfesor(Integer idProfesor) {
        return locator.getAsignacionDAO().listarPorProfesor(idProfesor);
    }

    public List<Asignacion> consultarPorUnidad(Integer idUnidad) {
        return locator.getAsignacionDAO().listarPorUnidad(idUnidad);
    }
}