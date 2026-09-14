package mx.desarrollo.negocio.facade;

import mx.desarrollo.entity.Profesor;
import mx.desarrollo.negocio.delegate.DelegateProfesor;
import mx.desarrollo.negocio.integration.RFCValidator;
import mx.desarrollo.negocio.integration.ValidacionException;

import java.util.List;

/**
 * Fachada de Profesor: valida reglas de negocio antes de delegar al DAO.
 */
public class FacadeProfesor {

    private final DelegateProfesor delegate;

    public FacadeProfesor() {
        this.delegate = new DelegateProfesor();
    }

    /**
     * Da de alta un profesor validando:
     * - Campos obligatorios
     * - Formato de RFC
     * - RFC no duplicado
     */
    public Profesor altaProfesor(Profesor profesor) {
        validarProfesor(profesor);

        // Validar que el RFC no exista
        Profesor existente = delegate.buscarPorRFC(profesor.getRfc());
        if (existente != null) {
            throw new ValidacionException(
                    "Ya existe un profesor con el RFC: " + profesor.getRfc());
        }

        return delegate.altaProfesor(profesor);
    }

    /**
     * Modifica un profesor existente.
     */
    public Profesor modificarProfesor(Profesor profesor) {
        if (profesor.getId() == null) {
            throw new ValidacionException("El profesor debe tener un ID para modificar.");
        }
        validarProfesor(profesor);

        // Verificar que el RFC no esté en uso por OTRO profesor
        Profesor existente = delegate.buscarPorRFC(profesor.getRfc());
        if (existente != null && !existente.getId().equals(profesor.getId())) {
            throw new ValidacionException(
                    "Ya existe otro profesor con el RFC: " + profesor.getRfc());
        }

        return delegate.modificarProfesor(profesor);
    }

    public void eliminarProfesor(Profesor profesor) {
        if (profesor == null || profesor.getId() == null) {
            throw new ValidacionException("Debe seleccionar un profesor válido.");
        }
        delegate.eliminarProfesor(profesor);
    }

    public Profesor buscarPorId(Integer id) {
        return delegate.buscarPorId(id);
    }

    public List<Profesor> consultarProfesores() {
        return delegate.consultarProfesores();
    }

    public List<Profesor> consultarProfesoresOrdenados() {
        return delegate.consultarProfesoresOrdenados();
    }

    /**
     * Método privado que valida los campos del profesor.
     */
    private void validarProfesor(Profesor profesor) {
        if (profesor == null) {
            throw new ValidacionException("El profesor no puede ser nulo.");
        }
        if (profesor.getNombre() == null || profesor.getNombre().isBlank()) {
            throw new ValidacionException("El nombre es obligatorio.");
        }
        if (profesor.getApellidoPaterno() == null || profesor.getApellidoPaterno().isBlank()) {
            throw new ValidacionException("El apellido paterno es obligatorio.");
        }
        if (profesor.getApellidoMaterno() == null || profesor.getApellidoMaterno().isBlank()) {
            throw new ValidacionException("El apellido materno es obligatorio.");
        }
        if (profesor.getNombre().length() > 50) {
            throw new ValidacionException("El nombre no puede exceder 50 caracteres.");
        }
        if (profesor.getApellidoPaterno().length() > 50) {
            throw new ValidacionException("El apellido paterno no puede exceder 50 caracteres.");
        }
        if (profesor.getApellidoMaterno().length() > 50) {
            throw new ValidacionException("El apellido materno no puede exceder 50 caracteres.");
        }

        String errorRFC = RFCValidator.getMensajeError(profesor.getRfc());
        if (errorRFC != null) {
            throw new ValidacionException(errorRFC);
        }
    }
}