package mx.desarrollo.ui;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import mx.desarrollo.entity.Asignacion;
import mx.desarrollo.entity.Profesor;
import mx.desarrollo.entity.UnidadAprendizaje;
import mx.desarrollo.negocio.facade.FacadeAsignacion;
import mx.desarrollo.negocio.facade.FacadeProfesor;
import mx.desarrollo.negocio.facade.FacadeUnidad;
import mx.desarrollo.negocio.integration.ValidacionException;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Named("consultaBean")
@SessionScoped
public class ConsultaBean implements Serializable {

    private final FacadeAsignacion facade = new FacadeAsignacion();
    private final FacadeProfesor facadeProfesor = new FacadeProfesor();
    private final FacadeUnidad facadeUnidad = new FacadeUnidad();

    // Modo de búsqueda
    private String tipoBusqueda = "PROFESOR"; // "PROFESOR" o "UNIDAD"

    // Selecciones
    private Integer idProfesorSeleccionado;
    private Integer idUnidadSeleccionada;

    // Datos para combos
    private List<Profesor> profesores;
    private List<UnidadAprendizaje> unidades;

    // Resultados
    private List<Asignacion> resultados;

    // Para modificar
    private Asignacion asignacionSeleccionada;
    private String diaModificar;
    private String horaInicioModificar;
    private String horaFinModificar;

    @PostConstruct
    public void init() {
        cargarDatos();
    }

    public void cargarDatos() {
        try {
            profesores = facadeProfesor.consultarProfesores();
            unidades = facadeUnidad.consultarUnidades();
        } catch (Exception e) {
            addError("Error al cargar datos: " + e.getMessage());
        }
    }

    // ============ BÚSQUEDA ============

    public void buscar() {
        try {
            if ("PROFESOR".equals(tipoBusqueda)) {
                if (idProfesorSeleccionado == null) {
                    addError("Debe seleccionar un profesor.");
                    return;
                }
                resultados = facade.consultarPorProfesor(idProfesorSeleccionado);
            } else {
                if (idUnidadSeleccionada == null) {
                    addError("Debe seleccionar una unidad.");
                    return;
                }
                resultados = facade.consultarPorUnidad(idUnidadSeleccionada);
            }
            System.out.println(">>> Consulta: " + resultados.size() + " resultados");
        } catch (Exception e) {
            addError("Error al buscar: " + e.getMessage());
        }
    }

    public void limpiar() {
        resultados = null;
        idProfesorSeleccionado = null;
        idUnidadSeleccionada = null;
    }

    // ============ MODIFICAR ============

    public void abrirModificar(Asignacion a) {
        this.asignacionSeleccionada = a;
        this.diaModificar = a.getDiaSemana();
        this.horaInicioModificar = a.getHoraInicio() != null ? a.getHoraInicio().toString() : null;
        this.horaFinModificar = a.getHoraFin() != null ? a.getHoraFin().toString() : null;
    }

    public void guardarModificacion() {
        try {
            if (asignacionSeleccionada == null) {
                addError("No hay asignación seleccionada.");
                return;
            }

            // Actualizar la asignación
            asignacionSeleccionada.setDiaSemana(diaModificar);
            asignacionSeleccionada.setHoraInicio(parseHora(horaInicioModificar));
            asignacionSeleccionada.setHoraFin(parseHora(horaFinModificar));

            facade.modificarAsignacion(asignacionSeleccionada);
            addInfo("✅ Asignación modificada correctamente.");

            // Recargar resultados
            buscar();

        } catch (ValidacionException e) {
            addError(e.getMessage());
        } catch (Exception e) {
            addError("Error inesperado: " + e.getMessage());
        }
    }

    // ============ ELIMINAR ============

    public void eliminar(Asignacion a) {
        try {
            facade.eliminarAsignacion(a);
            addInfo("✅ Asignación eliminada correctamente.");
            buscar(); // Recargar
        } catch (ValidacionException e) {
            addError(e.getMessage());
        } catch (Exception e) {
            addError("Error inesperado: " + e.getMessage());
        }
    }

    // ============ AUXILIARES ============

    private LocalTime parseHora(String hora) {
        if (hora == null || hora.isBlank()) return null;
        String horaNormalizada = hora.contains(":") ? hora : hora + ":00";
        try {
            return LocalTime.parse(horaNormalizada);
        } catch (Exception e) {
            return null;
        }
    }

    private void addInfo(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, mensaje, null));
    }

    private void addError(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, mensaje, null));
    }

    // ============ GETTERS Y SETTERS ============

    public String getTipoBusqueda() { return tipoBusqueda; }
    public void setTipoBusqueda(String tipoBusqueda) { this.tipoBusqueda = tipoBusqueda; }

    public Integer getIdProfesorSeleccionado() { return idProfesorSeleccionado; }
    public void setIdProfesorSeleccionado(Integer idProfesorSeleccionado) { this.idProfesorSeleccionado = idProfesorSeleccionado; }

    public Integer getIdUnidadSeleccionada() { return idUnidadSeleccionada; }
    public void setIdUnidadSeleccionada(Integer idUnidadSeleccionada) { this.idUnidadSeleccionada = idUnidadSeleccionada; }

    public List<Profesor> getProfesores() { return profesores; }
    public void setProfesores(List<Profesor> profesores) { this.profesores = profesores; }

    public List<UnidadAprendizaje> getUnidades() { return unidades; }
    public void setUnidades(List<UnidadAprendizaje> unidades) { this.unidades = unidades; }

    public List<Asignacion> getResultados() { return resultados; }
    public void setResultados(List<Asignacion> resultados) { this.resultados = resultados; }

    public Asignacion getAsignacionSeleccionada() { return asignacionSeleccionada; }
    public void setAsignacionSeleccionada(Asignacion asignacionSeleccionada) { this.asignacionSeleccionada = asignacionSeleccionada; }

    public String getDiaModificar() { return diaModificar; }
    public void setDiaModificar(String diaModificar) { this.diaModificar = diaModificar; }

    public String getHoraInicioModificar() { return horaInicioModificar; }
    public void setHoraInicioModificar(String horaInicioModificar) { this.horaInicioModificar = horaInicioModificar; }

    public String getHoraFinModificar() { return horaFinModificar; }
    public void setHoraFinModificar(String horaFinModificar) { this.horaFinModificar = horaFinModificar; }
}