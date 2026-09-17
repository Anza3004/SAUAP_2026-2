package mx.desarrollo.ui;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import mx.desarrollo.entity.Profesor;
import mx.desarrollo.negocio.facade.FacadeProfesor;
import mx.desarrollo.negocio.integration.ValidacionException;

import java.io.Serializable;
import java.util.List;

@Named("profesorBean")
@SessionScoped
public class ProfesorBean implements Serializable {

    private final FacadeProfesor facade = new FacadeProfesor();

    private Profesor nuevoProfesor = new Profesor();
    private List<Profesor> profesores;

    @PostConstruct
    public void init() {
        cargarProfesores();
    }

    public void guardar() {
        try {
            facade.altaProfesor(nuevoProfesor);
            nuevoProfesor = new Profesor(); // limpia el formulario
            cargarProfesores();
        } catch (ValidacionException e) {
            // aquí podrías guardar e.getMessage() en un campo mensajeAlerta,
            // igual que hace AsignacionBean
        }
    }

    private void cargarProfesores() {
        profesores = facade.consultarProfesoresOrdenados();
    }

    public Profesor getNuevoProfesor() { return nuevoProfesor; }
    public List<Profesor> getProfesores() { return profesores; }
}