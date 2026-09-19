package mx.desarrollo.ui;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
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

    // Mensajes para mostrar como alert en el cliente
    private String mensajeAlerta;
    private String tipoAlerta; // "success" | "error"

    @PostConstruct
    public void init() {
        cargarDatos();
    }

    public void cargarDatos() {
        try {
            profesores = facadeProfesor.consultarProfesores();
            unidades = facadeUnidad.consultarUnidades();
        } catch (Exception e) {
            setAlerta("Error al cargar datos: " + e.getMessage(), "error");
        }
    }

    // ============ BÚSQUEDA ============

    public void buscar() {
        mensajeAlerta = null;
        tipoAlerta = null;

        try {
            if ("PROFESOR".equals(tipoBusqueda)) {
                if (idProfesorSeleccionado == null) {
                    setAlerta("Debe seleccionar un profesor.", "error");
                    return;
                }
                resultados = facade.consultarPorProfesor(idProfesorSeleccionado);
            } else {
                if (idUnidadSeleccionada == null) {
                    setAlerta("Debe seleccionar una unidad.", "error");
                    return;
                }
                resultados = facade.consultarPorUnidad(idUnidadSeleccionada);
            }

            if (resultados == null || resultados.isEmpty()) {
                setAlerta("No se encontraron resultados.", "error");
            } else {
                setAlerta("Se encontraron " + resultados.size() + " asignaciones.", "success");
            }

        } catch (Exception e) {
            setAlerta("Error al buscar: " + e.getMessage(), "error");
        }
    }

    public void limpiar() {
        resultados = null;
        idProfesorSeleccionado = null;
        idUnidadSeleccionada = null;
        mensajeAlerta = null;
        tipoAlerta = null;
    }

    // ============ MODIFICAR ============

    public void abrirModificar(Asignacion a) {
        this.asignacionSeleccionada = a;
        this.diaModificar = a.getDiaSemana();
        this.horaInicioModificar = a.getHoraInicio() != null ? a.getHoraInicio().toString() : null;
        this.horaFinModificar = a.getHoraFin() != null ? a.getHoraFin().toString() : null;
    }

    public void guardarModificacion() {
        mensajeAlerta = null;
        tipoAlerta = null;

        try {
            if (asignacionSeleccionada == null) {
                setAlerta("No hay asignación seleccionada.", "error");
                return;
            }

            // Actualizar la asignación
            asignacionSeleccionada.setDiaSemana(diaModificar);
            asignacionSeleccionada.setHoraInicio(parseHora(horaInicioModificar));
            asignacionSeleccionada.setHoraFin(parseHora(horaFinModificar));

            facade.modificarAsignacion(asignacionSeleccionada);
            setAlerta("✅ Asignación modificada correctamente.", "success");

            // Recargar resultados
            buscar();

        } catch (ValidacionException e) {
            setAlerta("⚠️ " + e.getMessage(), "error");
        } catch (Exception e) {
            setAlerta("Error inesperado: " + e.getMessage(), "error");
        }
    }

    // ============ ELIMINAR ============

    public void eliminar(Asignacion a) {
        mensajeAlerta = null;
        tipoAlerta = null;

        try {
            facade.eliminarAsignacion(a);
            setAlerta("✅ Asignación eliminada correctamente.", "success");
            buscar(); // Recargar
        } catch (ValidacionException e) {
            setAlerta("⚠️ " + e.getMessage(), "error");
        } catch (Exception e) {
            setAlerta("Error inesperado: " + e.getMessage(), "error");
        }
    }

    // ============ AUXILIARES ============

    private void setAlerta(String mensaje, String tipo) {
        this.mensajeAlerta = mensaje;
        this.tipoAlerta = tipo;
        System.out.println(">>> Alerta [" + tipo + "]: " + mensaje);
    }

    private LocalTime parseHora(String hora) {
        if (hora == null || hora.isBlank()) return null;
        String horaNormalizada = hora.contains(":") ? hora : hora + ":00";
        try {
            return LocalTime.parse(horaNormalizada);
        } catch (Exception e) {
            return null;
        }
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

    public String getMensajeAlerta() { return mensajeAlerta; }
    public void setMensajeAlerta(String mensajeAlerta) { this.mensajeAlerta = mensajeAlerta; }

    public String getTipoAlerta() { return tipoAlerta; }
    public void setTipoAlerta(String tipoAlerta) { this.tipoAlerta = tipoAlerta; }
}