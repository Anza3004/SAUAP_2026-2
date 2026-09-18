package mx.desarrollo.ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.desarrollo.entity.UnidadAprendizaje;
import mx.desarrollo.negocio.facade.FacadeUnidad;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

@Named("consultaKBean")
@ViewScoped
public class ConsultaKBean implements Serializable {

    private List<UnidadAprendizaje> materias;
    private List<UnidadAprendizaje> materiasFiltradas;
    private String filtroNombre;

    private UnidadAprendizaje materiaSeleccionada;
    private boolean mostrarModificar = false;
    private boolean mostrarEliminar = false;

    public void seleccionarParaModificar(UnidadAprendizaje materia) {
        this.materiaSeleccionada = materia;
        this.mostrarModificar = true;
        this.mostrarEliminar = false;
    }

    public void seleccionarParaEliminar(UnidadAprendizaje materia) {
        this.materiaSeleccionada = materia;
        this.mostrarModificar = false;
        this.mostrarEliminar = true;
    }

    public void cancelar() {
        this.materiaSeleccionada = null;
        this.mostrarModificar = false;
        this.mostrarEliminar = false;
    }
    public void guardarCambios() {

        if (materiaSeleccionada == null) {
            return;
        }

        FacadeUnidad facadeUnidad = new FacadeUnidad();

        facadeUnidad.modificarUnidad(materiaSeleccionada);

        // Volvemos a consultar para actualizar la tabla
        materias = facadeUnidad.consultarUnidades();

        if (materias == null) {
            materias = new ArrayList<>();
        }

        materiasFiltradas = new ArrayList<>(materias);

        materiaSeleccionada = null;
        mostrarModificar = false;
    }

    public void eliminar() {

        if (materiaSeleccionada == null) {
            return;
        }

        FacadeUnidad facadeUnidad = new FacadeUnidad();

        facadeUnidad.eliminarUnidad(materiaSeleccionada);

        //se actualiza la tabla una vez que se elimina
        materias = facadeUnidad.consultarUnidades();

        if (materias == null) {
            materias = new ArrayList<>();
        }

        materiasFiltradas = new ArrayList<>(materias);

        materiaSeleccionada = null;
        mostrarEliminar = false;
    }

    @PostConstruct
    public void init() {

        FacadeUnidad facadeUnidad = new FacadeUnidad();

        materias = facadeUnidad.consultarUnidades();

        if (materias == null) {
            materias = new ArrayList<>();
        }

        materiasFiltradas = new ArrayList<>(materias);
    }

    public void buscar() {

        if (filtroNombre == null || filtroNombre.isBlank()) {
            materiasFiltradas = new ArrayList<>(materias);
            return;
        }

        String texto = filtroNombre.trim().toLowerCase();

        materiasFiltradas = materias.stream()
                .filter(materia ->
                        materia.getNombre() != null &&
                                materia.getNombre().toLowerCase().contains(texto))
                .toList();
    }

    public void limpiarFiltro() {
        filtroNombre = "";
        materiasFiltradas = new ArrayList<>(materias);
    }

    public List<UnidadAprendizaje> getMaterias() {
        return materias;
    }

    public List<UnidadAprendizaje> getMateriasFiltradas() {
        return materiasFiltradas;
    }

    public String getFiltroNombre() {
        return filtroNombre;
    }

    public void setFiltroNombre(String filtroNombre) {
        this.filtroNombre = filtroNombre;
    }
    public UnidadAprendizaje getMateriaSeleccionada() {
        return materiaSeleccionada;
    }

    public void setMateriaSeleccionada(UnidadAprendizaje materiaSeleccionada) {
        this.materiaSeleccionada = materiaSeleccionada;
    }

    public boolean isMostrarModificar() {
        return mostrarModificar;
    }

    public boolean isMostrarEliminar() {
        return mostrarEliminar;
    }
}