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
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Named("consultaBean")
@SessionScoped
public class ConsultaBean implements Serializable {

    private static final String POR_PROFESOR = "PROFESOR";
    private static final String POR_UNIDAD = "UNIDAD";

    /** Orden de los días para mostrar los resultados de lunes a viernes. */
    private static final List<String> DIAS =
            List.of("LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES");

    /** Igual que el grid de Asignaciones: inicios de 07:00 a 20:00, el último bloque termina a las 21:00. */
    private static final int HORA_PRIMERA = 7;
    private static final int HORA_LIMITE = 21;

    /** Profesor, unidad, bloque, día y hora: así los registros de un mismo bloque quedan juntos. */
    private static final Comparator<Asignacion> ORDEN_RESULTADOS = Comparator
            .comparing((Asignacion a) -> nombreProfesor(a), String.CASE_INSENSITIVE_ORDER)
            .thenComparing(a -> nombreUnidad(a), String.CASE_INSENSITIVE_ORDER)
            .thenComparing(a -> a.getGrupo() != null ? a.getGrupo() : "")
            .thenComparingInt(a -> indiceDia(a.getDiaSemana()))
            .thenComparing(Asignacion::getHoraInicio, Comparator.nullsLast(Comparator.naturalOrder()));

    private final FacadeAsignacion facade = new FacadeAsignacion();
    private final FacadeProfesor facadeProfesor = new FacadeProfesor();
    private final FacadeUnidad facadeUnidad = new FacadeUnidad();

    // Selecciones (una por cada combo, independientes entre sí)
    private Integer idProfesorSeleccionado;
    private Integer idUnidadSeleccionada;

    // Última búsqueda ejecutada. Sirve para recargar la tabla después de
    // eliminar o modificar, sin depender de lo que estén mostrando los combos.
    private String ultimoTipo;   // POR_PROFESOR | POR_UNIDAD | null
    private Integer ultimoId;

    // Datos para combos
    private List<Profesor> profesores;
    private List<UnidadAprendizaje> unidades;
    private List<String> horasInicio;

    // Resultados
    private List<Asignacion> resultados;

    // Para modificar (día y hora de inicio; la duración se conserva)
    private Asignacion asignacionSeleccionada;
    private String diaModificar;
    private String horaInicioModificar;

    // Mensajes para mostrar como alert en el cliente
    private String mensajeAlerta;
    private String tipoAlerta; // "success" | "error"

    @PostConstruct
    public void init() {
        horasInicio = new ArrayList<>();
        for (int h = HORA_PRIMERA; h < HORA_LIMITE; h++) {
            horasInicio.add(String.format("%02d:00", h));
        }
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

    public void buscarPorProfesor() {
        limpiarAlerta();

        if (idProfesorSeleccionado == null) {
            setAlerta("Debe seleccionar un profesor.", "error");
            return;
        }

        cancelarModificacion();
        idUnidadSeleccionada = null; // deja claro cuál filtro produjo los resultados
        ultimoTipo = POR_PROFESOR;
        ultimoId = idProfesorSeleccionado;
        ejecutarBusqueda();
    }

    public void buscarPorUnidad() {
        limpiarAlerta();

        if (idUnidadSeleccionada == null) {
            setAlerta("Debe seleccionar una unidad.", "error");
            return;
        }

        cancelarModificacion();
        idProfesorSeleccionado = null;
        ultimoTipo = POR_UNIDAD;
        ultimoId = idUnidadSeleccionada;
        ejecutarBusqueda();
    }

    /** Ejecuta la última búsqueda y avisa cuántos resultados hubo. */
    private void ejecutarBusqueda() {
        try {
            resultados = consultarUltimaBusqueda();

            if (resultados == null || resultados.isEmpty()) {
                setAlerta("No se encontraron resultados.", "error");
            } else {
                setAlerta("Se encontraron " + resultados.size() + " asignaciones.", "success");
            }
        } catch (Exception e) {
            setAlerta("Error al buscar: " + e.getMessage(), "error");
        }
    }

    /**
     * Refresca la tabla con la última búsqueda SIN tocar la alerta,
     * para que no pise el mensaje de "eliminada" o "modificada".
     */
    private void recargarResultados() {
        if (ultimoTipo == null || ultimoId == null) return;
        try {
            resultados = consultarUltimaBusqueda();
        } catch (Exception e) {
            setAlerta("Error al recargar resultados: " + e.getMessage(), "error");
        }
    }

    private List<Asignacion> consultarUltimaBusqueda() throws Exception {
        List<Asignacion> lista = POR_PROFESOR.equals(ultimoTipo)
                ? facade.consultarPorProfesor(ultimoId)
                : facade.consultarPorUnidad(ultimoId);

        if (lista == null) return null;

        List<Asignacion> ordenada = new ArrayList<>(lista);
        ordenada.sort(ORDEN_RESULTADOS);
        return ordenada;
    }

    public void limpiar() {
        resultados = null;
        idProfesorSeleccionado = null;
        idUnidadSeleccionada = null;
        ultimoTipo = null;
        ultimoId = null;
        cancelarModificacion();
        limpiarAlerta();
    }

    // ============ MODIFICAR ============

    public void abrirModificar(Asignacion a) {
        limpiarAlerta();
        this.asignacionSeleccionada = a;
        this.diaModificar = a.getDiaSemana();
        this.horaInicioModificar = a.getHoraInicio() != null ? a.getHoraInicio().toString() : null;
    }

    public void cancelarModificacion() {
        this.asignacionSeleccionada = null;
        this.diaModificar = null;
        this.horaInicioModificar = null;
    }

    /**
     * Reprograma la asignación seleccionada a otro día y/u hora de inicio.
     * La duración se conserva para no romper el total de horas de la unidad
     * (que se validó al hacer la asignación).
     */
    public void guardarModificacion() {
        limpiarAlerta();

        try {
            if (asignacionSeleccionada == null) {
                setAlerta("No hay asignación seleccionada.", "error");
                return;
            }

            LocalTime nuevoInicio = parseHora(horaInicioModificar);
            if (diaModificar == null || diaModificar.isBlank() || nuevoInicio == null) {
                setAlerta("Debe seleccionar el día y la hora de inicio.", "error");
                return;
            }

            LocalTime inicioActual = asignacionSeleccionada.getHoraInicio();
            LocalTime finActual = asignacionSeleccionada.getHoraFin();

            if (diaModificar.equals(asignacionSeleccionada.getDiaSemana())
                    && nuevoInicio.equals(inicioActual)) {
                setAlerta("No hiciste ningún cambio en el horario.", "error");
                return;
            }

            long duracionMin = Duration.between(inicioActual, finActual).toMinutes();
            long finMin = nuevoInicio.toSecondOfDay() / 60 + duracionMin;
            if (finMin > HORA_LIMITE * 60L) {
                setAlerta("Con inicio a las " + nuevoInicio + " el horario (" + formatearDuracion(duracionMin)
                        + ") terminaría después de las " + HORA_LIMITE + ":00. Elige una hora más temprana.", "error");
                return;
            }
            LocalTime nuevoFin = nuevoInicio.plusMinutes(duracionMin);

            // Se valida y guarda una COPIA: si hay traslape, la fila que se ve en la tabla no cambia.
            Asignacion copia = new Asignacion();
            copia.setId(asignacionSeleccionada.getId());
            copia.setProfesor(asignacionSeleccionada.getProfesor());
            copia.setUnidad(asignacionSeleccionada.getUnidad());
            copia.setGrupo(asignacionSeleccionada.getGrupo());
            copia.setDiaSemana(diaModificar);
            copia.setHoraInicio(nuevoInicio);
            copia.setHoraFin(nuevoFin);

            facade.modificarAsignacion(copia);
            setAlerta("✅ Asignación modificada: " + diaModificar + " " + nuevoInicio + " - " + nuevoFin + ".", "success");

            cancelarModificacion();
            recargarResultados();

        } catch (ValidacionException e) {
            setAlerta("⚠️ " + e.getMessage(), "error");
        } catch (Exception e) {
            setAlerta("Error inesperado: " + e.getMessage(), "error");
        }
    }

    // ============ ELIMINAR ============

    /**
     * Elimina la asignación COMPLETA (todos sus horarios de clase, taller y laboratorio),
     * sin importar en qué fila se pulsó. La regla vive en FacadeAsignacion: una unidad
     * debe tener el 100% de sus horas asignadas, así que nunca queda una asignación a medias.
     */
    public void eliminar(Asignacion a) {
        limpiarAlerta();

        try {
            int eliminados = facade.eliminarAsignacion(a);

            if (eliminados == 0) {
                setAlerta("La asignación ya no existe.", "error");
            } else {
                setAlerta("✅ Asignación eliminada por completo (" + eliminados + " horario(s)).", "success");
            }

            cancelarModificacion();
            recargarResultados();
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

    private void limpiarAlerta() {
        this.mensajeAlerta = null;
        this.tipoAlerta = null;
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

    private static String formatearDuracion(long minutos) {
        long h = minutos / 60;
        long m = minutos % 60;
        if (h == 0) return m + " min";
        if (m == 0) return h + " h";
        return h + " h " + m + " min";
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String nombreProfesor(Asignacion a) {
        Profesor p = a.getProfesor();
        if (p == null) return "";
        return safe(p.getNombre()) + " " + safe(p.getApellidoPaterno()) + " " + safe(p.getApellidoMaterno());
    }

    private static String nombreUnidad(Asignacion a) {
        UnidadAprendizaje u = a.getUnidad();
        return u == null ? "" : safe(u.getNombre());
    }

    private static int indiceDia(String dia) {
        if (dia == null) return DIAS.size();
        int i = DIAS.indexOf(dia.toUpperCase());
        return i < 0 ? DIAS.size() : i;
    }

    // ============ TEXTOS PARA EL PANEL DE EDICIÓN ============

    public String getResumenModificar() {
        if (asignacionSeleccionada == null) return "";
        Asignacion a = asignacionSeleccionada;
        return nombreProfesor(a).trim() + " · " + nombreUnidad(a) + " · "
                + a.getDiaSemana() + " " + a.getHoraInicio() + " - " + a.getHoraFin();
    }

    public String getDuracionModificar() {
        if (asignacionSeleccionada == null
                || asignacionSeleccionada.getHoraInicio() == null
                || asignacionSeleccionada.getHoraFin() == null) {
            return "";
        }
        return formatearDuracion(Duration.between(
                asignacionSeleccionada.getHoraInicio(),
                asignacionSeleccionada.getHoraFin()).toMinutes());
    }

    // ============ CÓDIGO DE GRUPO PARA LA TABLA ============

    /**
     * Código corto que se muestra en la columna "Grupo": los últimos 3 caracteres del grupo.
     *
     * Si esos 3 caracteres son una letra seguida de dos números (por ejemplo "b00"),
     * la letra se cambia por un dígito. El dígito sale del grupo completo, así que es
     * "aleatorio" pero siempre el mismo para el mismo grupo: todas las filas de una
     * asignación siguen mostrando el mismo código y no cambia al recargar la página.
     * Solo es visual; el borrado y las consultas usan el grupo completo.
     */
    public String codigoGrupo(Asignacion a) {
        String grupo = a.getGrupo();
        if (grupo == null || grupo.isBlank()) return "—";

        String codigo = grupo.length() > 3 ? grupo.substring(grupo.length() - 3) : grupo;

        if (codigo.matches("[A-Za-z]\\d\\d")) {
            codigo = Math.floorMod(grupo.hashCode(), 10) + codigo.substring(1);
        }
        return codigo;
    }

    // ============ GETTERS Y SETTERS ============

    public Integer getIdProfesorSeleccionado() { return idProfesorSeleccionado; }
    public void setIdProfesorSeleccionado(Integer idProfesorSeleccionado) { this.idProfesorSeleccionado = idProfesorSeleccionado; }

    public Integer getIdUnidadSeleccionada() { return idUnidadSeleccionada; }
    public void setIdUnidadSeleccionada(Integer idUnidadSeleccionada) { this.idUnidadSeleccionada = idUnidadSeleccionada; }

    public List<Profesor> getProfesores() { return profesores; }
    public void setProfesores(List<Profesor> profesores) { this.profesores = profesores; }

    public List<UnidadAprendizaje> getUnidades() { return unidades; }
    public void setUnidades(List<UnidadAprendizaje> unidades) { this.unidades = unidades; }

    public List<String> getHorasInicio() { return horasInicio; }

    public List<Asignacion> getResultados() { return resultados; }
    public void setResultados(List<Asignacion> resultados) { this.resultados = resultados; }

    public Asignacion getAsignacionSeleccionada() { return asignacionSeleccionada; }
    public void setAsignacionSeleccionada(Asignacion asignacionSeleccionada) { this.asignacionSeleccionada = asignacionSeleccionada; }

    public String getDiaModificar() { return diaModificar; }
    public void setDiaModificar(String diaModificar) { this.diaModificar = diaModificar; }

    public String getHoraInicioModificar() { return horaInicioModificar; }
    public void setHoraInicioModificar(String horaInicioModificar) { this.horaInicioModificar = horaInicioModificar; }

    public String getMensajeAlerta() { return mensajeAlerta; }
    public void setMensajeAlerta(String mensajeAlerta) { this.mensajeAlerta = mensajeAlerta; }

    public String getTipoAlerta() { return tipoAlerta; }
    public void setTipoAlerta(String tipoAlerta) { this.tipoAlerta = tipoAlerta; }
}