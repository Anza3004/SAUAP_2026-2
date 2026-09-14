package mx.desarrollo.negocio.facade;

import mx.desarrollo.entity.Alumno;
import mx.desarrollo.negocio.delegate.DelegateAlumno;
import mx.desarrollo.negocio.integration.ValidacionException;

import java.util.List;

public class FacadeAlumno {

    private final DelegateAlumno delegate;

    public FacadeAlumno() {
        this.delegate = new DelegateAlumno();
    }

    public Alumno altaAlumno(Alumno alumno) {
        if (alumno == null) {
            throw new ValidacionException("El alumno no puede ser nulo.");
        }
        if (alumno.getMatricula() == null) {
            throw new ValidacionException("La matrícula es obligatoria.");
        }
        if (alumno.getNombre() == null || alumno.getNombre().isBlank()) {
            throw new ValidacionException("El nombre es obligatorio.");
        }
        if (alumno.getApellidos() == null || alumno.getApellidos().isBlank()) {
            throw new ValidacionException("Los apellidos son obligatorios.");
        }
        return delegate.altaAlumno(alumno);
    }

    public Alumno buscarPorId(Integer id) {
        return delegate.buscarPorId(id);
    }

    public List<Alumno> consultarAlumnos() {
        return delegate.consultarAlumnos();
    }
}