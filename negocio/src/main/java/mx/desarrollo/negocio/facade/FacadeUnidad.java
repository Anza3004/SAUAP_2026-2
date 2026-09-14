package mx.desarrollo.negocio.facade;

import mx.desarrollo.entity.UnidadAprendizaje;
import mx.desarrollo.negocio.delegate.DelegateUnidad;
import mx.desarrollo.negocio.integration.HorasValidator;
import mx.desarrollo.negocio.integration.ValidacionException;

import java.util.List;

public class FacadeUnidad {

    private final DelegateUnidad delegate;

    public FacadeUnidad() {
        this.delegate = new DelegateUnidad();
    }

    public UnidadAprendizaje altaUnidad(UnidadAprendizaje unidad) {
        validarUnidad(unidad);
        return delegate.altaUnidad(unidad);
    }

    public UnidadAprendizaje modificarUnidad(UnidadAprendizaje unidad) {
        if (unidad.getId() == null) {
            throw new ValidacionException("La unidad debe tener un ID para modificar.");
        }
        validarUnidad(unidad);
        return delegate.modificarUnidad(unidad);
    }

    public void eliminarUnidad(UnidadAprendizaje unidad) {
        if (unidad == null || unidad.getId() == null) {
            throw new ValidacionException("Debe seleccionar una unidad válida.");
        }
        delegate.eliminarUnidad(unidad);
    }

    public UnidadAprendizaje buscarPorId(Integer id) {
        return delegate.buscarPorId(id);
    }

    public List<UnidadAprendizaje> consultarUnidades() {
        return delegate.consultarUnidades();
    }

    private void validarUnidad(UnidadAprendizaje unidad) {
        if (unidad == null) {
            throw new ValidacionException("La unidad no puede ser nula.");
        }
        if (unidad.getNombre() == null || unidad.getNombre().isBlank()) {
            throw new ValidacionException("El nombre de la unidad es obligatorio.");
        }
        if (unidad.getNombre().length() > 50) {
            throw new ValidacionException("El nombre no puede exceder 50 caracteres.");
        }

        String errorClase = HorasValidator.getMensajeError("horas clase", unidad.getHorasClase());
        if (errorClase != null) throw new ValidacionException(errorClase);

        String errorTaller = HorasValidator.getMensajeError("horas taller", unidad.getHorasTaller());
        if (errorTaller != null) throw new ValidacionException(errorTaller);

        String errorLab = HorasValidator.getMensajeError("horas laboratorio", unidad.getHorasLaboratorio());
        if (errorLab != null) throw new ValidacionException(errorLab);

        // Al menos una hora debe ser mayor a 0
        if ((unidad.getHorasClase() == null || unidad.getHorasClase() == 0)
                && (unidad.getHorasTaller() == null || unidad.getHorasTaller() == 0)
                && (unidad.getHorasLaboratorio() == null || unidad.getHorasLaboratorio() == 0)) {
            throw new ValidacionException(
                    "La unidad debe tener al menos 1 hora asignada (clase, taller o laboratorio).");
        }
    }
}