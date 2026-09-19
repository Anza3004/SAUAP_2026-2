package mx.desarrollo.ui;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

@Named("dashboardBean")
@SessionScoped
public class DashboardBean implements Serializable {

    @Inject
    private SessionBean sessionBean;

    @Inject
    private ConsultaBean consultaBean;

    @Inject
    private AsignacionBean asignacionBean;

    private String vistaActual = "inicio";

    // ============ NAVEGACIÓN ============

    public void mostrarInicio() {
        vistaActual = "inicio";
    }

    public void mostrarProfesor() {
        vistaActual = "profesor";
    }

    public void mostrarUnidad() {
        vistaActual = "unidad";
    }

    public void mostrarAsignacion() {
        asignacionBean.cargarDatos();  // Recarga los combos
        vistaActual = "asignacion";
    }

    public void mostrarConsultas() {
        consultaBean.cargarDatos();    // Recarga los combos
        vistaActual = "consultas";
    }

    // ============ CERRAR SESIÓN ============

    public String cerrarSesion() {
        sessionBean.cerrarSesion();
        this.vistaActual = "inicio";
        return "/login.xhtml?faces-redirect=true";
    }

    // ============ GETTERS / SETTERS ============

    public String getVistaActual() {
        return vistaActual;
    }

    public void setVistaActual(String vistaActual) {
        this.vistaActual = vistaActual;
    }

    public boolean isVistaInicio() {
        return "inicio".equals(vistaActual);
    }

    public boolean isVistaAsignacion() {
        return "asignacion".equals(vistaActual);
    }

    public boolean isVistaProfesor() {
        return "profesor".equals(vistaActual);
    }

    public boolean isVistaUnidad() {
        return "unidad".equals(vistaActual);
    }

    public boolean isVistaConsultas() {
        return "consultas".equals(vistaActual);
    }
}