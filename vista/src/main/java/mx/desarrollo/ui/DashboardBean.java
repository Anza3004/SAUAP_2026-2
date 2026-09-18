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
    private ConsultaAsignacionBeanUI consultaAsignacionUI;

    private String vistaActual = "inicio";

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
        vistaActual = "asignacion";
    }


    public void mostrarConsultaProfesores() {
        vistaActual = "consultaProfesores";
    }

    public void mostrarConsultaAsignaciones() {
        consultaAsignacionUI.mostrarTodas();
        vistaActual = "consultaAsignaciones";
    }


    public String cerrarSesion() {
        // 1. Limpiar el usuario del bean de sesión
        sessionBean.cerrarSesion();

        // 2. Resetear la vista a "inicio" (para la próxima sesión)
        this.vistaActual = "inicio";

        // 3. Redirigir al login con faces-redirect=true
        //    El redirect fuerza una nueva petición HTTP que limpia el ViewState
        return "/login.xhtml?faces-redirect=true";
    }

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

    public boolean isVistaConsultaProfesores() {
        return "consultaProfesores".equals(vistaActual);
    }

    public boolean isVistaConsultaAsignaciones() {
        return "consultaAsignaciones".equals(vistaActual);
    }

    public boolean isVistaConsultas() {
        return isVistaConsultaProfesores() || isVistaConsultaAsignaciones();
    }
}