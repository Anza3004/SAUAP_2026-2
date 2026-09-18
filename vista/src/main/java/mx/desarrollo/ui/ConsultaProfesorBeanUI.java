package mx.desarrollo.ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.desarrollo.entity.Profesor;
import mx.desarrollo.negocio.facade.FacadeProfesor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("consultaProfesorUI")
@ViewScoped
public class ConsultaProfesorBeanUI implements Serializable {

    private List<Profesor> profesores;
    private String textoBusqueda;

    private Profesor profesorSeleccionado;
    private boolean mostrarModificar;
    private boolean mostrarEliminar;

    private final FacadeProfesor facadeProfesor = new FacadeProfesor();


    @PostConstruct
    public void init() {

        cargarProfesores();
    }

    public void cargarProfesores() {

        profesores = facadeProfesor.consultarProfesoresOrdenados();
    }

    public void buscar() {  // --------

        List<Profesor> todosLosProfesores =
                facadeProfesor.consultarProfesoresOrdenados();

        if (textoBusqueda == null || textoBusqueda.trim().isEmpty()) {

            profesores = todosLosProfesores;
            return;
        }

        String texto = textoBusqueda.trim().toLowerCase();

        profesores = new ArrayList<>();

        for (Profesor profesor : todosLosProfesores) {

            String nombreCompleto =
                    profesor.getNombre() + " " +
                            profesor.getApellidoPaterno() + " " +
                            profesor.getApellidoMaterno();

            String rfc = profesor.getRfc();

            if (nombreCompleto.toLowerCase().contains(texto)
                    || rfc.toLowerCase().contains(texto)) {

                profesores.add(profesor);
            }
        }
    }

    public void mostrarTodos() { //--------
        textoBusqueda = "";
        cargarProfesores();
    }


    public void abrirModificar(Profesor profesor) {
        profesorSeleccionado = profesor;
        mostrarModificar = true;
        mostrarEliminar = false;
    }

    public void cancelarModificar() {
        mostrarModificar = false;
        profesorSeleccionado = null;
    }
    public void guardarCambios() {
        if (profesorSeleccionado == null) {
            return;
        }

        facadeProfesor.modificarProfesor(profesorSeleccionado);

        mostrarModificar = false;
        profesorSeleccionado = null;

        cargarProfesores();
    }

    public void abrirEliminar(Profesor profesor) {
        profesorSeleccionado = profesor;
        mostrarEliminar = true;
        mostrarModificar = false;
    }

    public void cancelarEliminar() {
        mostrarEliminar = false;
        profesorSeleccionado = null;
    }

    public void eliminarProfesor() {
        if (profesorSeleccionado == null) {
            return;
        }

        facadeProfesor.eliminarProfesor(profesorSeleccionado);

        mostrarEliminar = false;
        profesorSeleccionado = null;

        cargarProfesores();
    }


    public List<Profesor> getProfesores() {
        return profesores;
    }

    public String getTextoBusqueda() {
        return textoBusqueda;
    }

    public void setTextoBusqueda(String textoBusqueda) {
        this.textoBusqueda = textoBusqueda;
    }
    public Profesor getProfesorSeleccionado() {
        return profesorSeleccionado;
    }

    public void setProfesorSeleccionado(Profesor profesorSeleccionado) {
        this.profesorSeleccionado = profesorSeleccionado;
    }

    public boolean isMostrarModificar() {
        return mostrarModificar;
    }

    public boolean isMostrarEliminar() {
        return mostrarEliminar;
    }
}