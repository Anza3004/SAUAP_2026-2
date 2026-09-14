package mx.desarrollo.negocio.delegate;

import mx.desarrollo.entity.Profesor;
import mx.desarrollo.persistencia.integration.ServiceLocator;

import java.util.List;

/**
 * Delegado para operaciones de Profesor.
 * Su única responsabilidad es llamar al DAO correspondiente.
 */
public class DelegateProfesor {

    private final ServiceLocator locator = ServiceLocator.getInstance();

    public Profesor altaProfesor(Profesor profesor) {
        return locator.getProfesorDAO().guardar(profesor);
    }

    public Profesor modificarProfesor(Profesor profesor) {
        return locator.getProfesorDAO().guardar(profesor);
    }

    public void eliminarProfesor(Profesor profesor) {
        locator.getProfesorDAO().eliminar(profesor);
    }

    public Profesor buscarPorId(Integer id) {
        return locator.getProfesorDAO().buscarPorId(id);
    }

    public Profesor buscarPorRFC(String rfc) {
        return locator.getProfesorDAO().buscarPorRFC(rfc);
    }

    public List<Profesor> consultarProfesores() {
        return locator.getProfesorDAO().listarTodos();
    }

    public List<Profesor> consultarProfesoresOrdenados() {
        return locator.getProfesorDAO().listarOrdenados();
    }
}