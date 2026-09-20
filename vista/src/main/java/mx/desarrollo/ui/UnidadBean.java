package mx.desarrollo.ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mx.desarrollo.entity.UnidadAprendizaje;
import mx.desarrollo.negocio.facade.FacadeUnidad;
import mx.desarrollo.negocio.integration.ValidacionException;

@Named("unidadBean")
@ViewScoped
public class UnidadBean implements Serializable {

    private UnidadAprendizaje unidad;
    private FacadeUnidad facade;
    private List<Byte> cantidadesHoras;
    private List<UnidadAprendizaje> listaUnidades;

    @PostConstruct
    public void init() {
        facade = new FacadeUnidad();
        unidad = new UnidadAprendizaje();
        cantidadesHoras = List.of((byte)0, (byte)1, (byte)2, (byte)3, (byte)4);
        cargarListaUnidades();
    }

    public void cargarListaUnidades() {
        try {
            listaUnidades = facade.consultarUnidades();
        } catch (Exception e) {
            listaUnidades = new ArrayList<>();
        }
    }

    public void guardar() {
        try {
            facade.altaUnidad(unidad);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito", "Unidad registrada correctamente."));
            unidad = new UnidadAprendizaje();
            cargarListaUnidades();
        } catch (ValidacionException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de validacion", e.getMessage()));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error inesperado."));
        }
    }

    public void eliminarUnidad(UnidadAprendizaje u) {
        try {
            facade.eliminarUnidad(u);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito", "Unidad '" + u.getNombre() + "' eliminada."));
            cargarListaUnidades();
        } catch (ValidacionException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar la unidad."));
        }
    }

    public void limpiar() {
        unidad = new UnidadAprendizaje();
        unidad.setHorasClase((byte) 0);
        unidad.setHorasTaller((byte) 0);
        unidad.setHorasLaboratorio((byte) 0);
    }

    public UnidadAprendizaje getUnidad() { return unidad; }
    public void setUnidad(UnidadAprendizaje unidad) { this.unidad = unidad; }
    public List<Byte> getCantidadesHoras() { return cantidadesHoras; }
    public List<UnidadAprendizaje> getListaUnidades() { return listaUnidades; }
    public void setListaUnidades(List<UnidadAprendizaje> listaUnidades) { this.listaUnidades = listaUnidades; }
}