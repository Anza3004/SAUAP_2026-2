package mx.desarrollo.negocio.delegate;

import mx.desarrollo.entity.UnidadAprendizaje;
import mx.desarrollo.persistencia.integration.ServiceLocator;

import java.util.List;

public class DelegateUnidad {

    private final ServiceLocator locator = ServiceLocator.getInstance();

    public UnidadAprendizaje altaUnidad(UnidadAprendizaje unidad) {
        return locator.getUnidadDAO().guardar(unidad);
    }

    public UnidadAprendizaje modificarUnidad(UnidadAprendizaje unidad) {
        return locator.getUnidadDAO().guardar(unidad);
    }

    public void eliminarUnidad(UnidadAprendizaje unidad) {
        locator.getUnidadDAO().eliminar(unidad);
    }

    public List<String> consultarProfesoresAsignados(Integer idUnidad) {
        return locator.getAsignacionDAO().listarNombresProfesoresPorUnidad(idUnidad);
    }

    public UnidadAprendizaje buscarPorId(Integer id) {
        return locator.getUnidadDAO().buscarPorId(id);
    }

    public List<UnidadAprendizaje> consultarUnidades() {
        return locator.getUnidadDAO().listarTodos();
    }
}