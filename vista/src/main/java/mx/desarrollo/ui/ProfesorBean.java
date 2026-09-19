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
    private String mensajeAlerta;
    private String tipoAlerta;

    @PostConstruct
    public void init() {
        cargarProfesores();
    }

    public void guardar() {
        try {
            facade.altaProfesor(nuevoProfesor);
            nuevoProfesor = new Profesor(); // limpia el formulario
            cargarProfesores();
            mensajeAlerta = "Profesor creado exitosamente";
            tipoAlerta = "sucess";
        } catch (ValidacionException e) {
            mensajeAlerta = e.getMessage();
            tipoAlerta = "error";
        }
    }
    public void eliminar(Profesor profesor) {
        try {
            facade.eliminarProfesor(profesor);
            cargarProfesores();
            mensajeAlerta = "Profesor eliminado";
            tipoAlerta = "sucess";
        }catch (ValidacionException e) {
            mensajeAlerta = e.getMessage();
            tipoAlerta = "error";
        }
    }

    private void cargarProfesores() {
        profesores = facade.consultarProfesoresOrdenados();
    }

    public Profesor getNuevoProfesor() { return nuevoProfesor; }
    public List<Profesor> getProfesores() { return profesores; }
    public String getMensajeAlerta() { return mensajeAlerta; }
    public void setMensajeAlerta(String mensajeAlerta) {this.mensajeAlerta = mensajeAlerta;}
    public String getTipoAlerta() { return tipoAlerta; }
    public void setTipoAlerta(String tipoAlerta) { this.tipoAlerta = tipoAlerta; }
}