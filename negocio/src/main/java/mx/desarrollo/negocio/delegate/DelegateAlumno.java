package mx.desarrollo.negocio.delegate;

import mx.desarrollo.entity.Alumno;
import mx.desarrollo.persistencia.integration.ServiceLocator;

import java.util.List;

public class DelegateAlumno {

    private final ServiceLocator locator = ServiceLocator.getInstance();

    public Alumno altaAlumno(Alumno alumno) {
        return locator.getAlumnoDAO().guardar(alumno);
    }

    public Alumno buscarPorId(Integer id) {
        return locator.getAlumnoDAO().buscarPorId(id);
    }

    public List<Alumno> consultarAlumnos() {
        return locator.getAlumnoDAO().listarTodos();
    }
}