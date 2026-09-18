package mx.desarrollo.ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.desarrollo.entity.Asignacion;
import mx.desarrollo.entity.Profesor;
import mx.desarrollo.negocio.facade.FacadeAsignacion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Named("consultaAsignacionUI")
@ViewScoped
public class ConsultaAsignacionBeanUI implements Serializable {

    private List<Asignacion> asignaciones;
    private List<Profesor> profesores;
    private String textoBusqueda;

    private Profesor profesorSeleccionado;
    private List<Asignacion> asignacionesProfesor;
    private boolean mostrarDetalles;

    private final FacadeAsignacion facadeAsignacion =
            new FacadeAsignacion();

    @PostConstruct
    public void init() {
        cargarAsignaciones();
    }

    public void cargarAsignaciones() {
        asignaciones = facadeAsignacion.consultarAsignacionesConDetalles();
        cargarProfesoresUnicos(asignaciones);
    }

    private void cargarProfesoresUnicos(List<Asignacion> listaAsignaciones) {

        profesores = new ArrayList<>();
        Set<Integer> idsAgregados = new HashSet<>();

        for (Asignacion asignacion : listaAsignaciones) {

            Profesor profesor = asignacion.getProfesor();

            if (profesor != null
                    && profesor.getId() != null && !idsAgregados.contains(profesor.getId())) {

                profesores.add(profesor);
                idsAgregados.add(profesor.getId());
            }
        }
    }

    public void buscar() {

        List<Asignacion> todasLasAsignaciones =
                facadeAsignacion.consultarAsignacionesConDetalles();

        if (textoBusqueda == null || textoBusqueda.trim().isEmpty()) {
            asignaciones = todasLasAsignaciones;
            cargarProfesoresUnicos(asignaciones);
            return;
        }

        String texto = textoBusqueda.trim().toLowerCase();

        asignaciones = new ArrayList<>();

        for (Asignacion asignacion : todasLasAsignaciones) {

            String nombreProfesor =
                    asignacion.getProfesor().getNombre() + " " +
                            asignacion.getProfesor().getApellidoPaterno() + " " +
                            asignacion.getProfesor().getApellidoMaterno();

            String rfc = asignacion.getProfesor().getRfc();

            if (nombreProfesor.toLowerCase().contains(texto)
                    || rfc.toLowerCase().contains(texto)) {

                asignaciones.add(asignacion);
            }
        }

        cargarProfesoresUnicos(asignaciones);
    }

    public int contarUnidadesAsignadas(Profesor profesor) {

        if (profesor == null || profesor.getId() == null) {
            return 0;
        }

        Set<Integer> unidades = new HashSet<>();

        for (Asignacion asignacion : asignaciones) {

            if (asignacion.getProfesor().getId().equals(profesor.getId())
                    && asignacion.getUnidad() != null
                    && asignacion.getUnidad().getId() != null) {

                unidades.add(asignacion.getUnidad().getId());
            }
        }

        return unidades.size();
    }

    public void abrirDetalles(Profesor profesor) {
        profesorSeleccionado = profesor;
        asignacionesProfesor = new ArrayList<>();

        for (Asignacion asignacion : asignaciones) {
            if (asignacion.getProfesor().getId().equals(profesor.getId())) {
                asignacionesProfesor.add(asignacion);
            }
        }

        mostrarDetalles = true;
    }

    public void cerrarDetalles() {
        mostrarDetalles = false;
        profesorSeleccionado = null;
        asignacionesProfesor = null;
    }



    public void mostrarTodas() {
        textoBusqueda = "";
        cargarAsignaciones();
    }

    public List<Asignacion> getAsignaciones() {
        return asignaciones;
    }

    public List<Profesor> getProfesores() {
        return profesores;
    }

    public String getTextoBusqueda() {
        return textoBusqueda;
    }

    public Profesor getProfesorSeleccionado() {
        return profesorSeleccionado;
    }

    public List<Asignacion> getAsignacionesProfesor() {
        return asignacionesProfesor;
    }

    public boolean isMostrarDetalles() {
        return mostrarDetalles;
    }



    public void setTextoBusqueda(String textoBusqueda) {
        this.textoBusqueda = textoBusqueda;
    }
}