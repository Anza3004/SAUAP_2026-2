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

@Named("asignacionBean")
@SessionScoped
public class AsignacionBean implements Serializable {

    private final FacadeAsignacion facade = new FacadeAsignacion();
    private final FacadeProfesor facadeProfesor = new FacadeProfesor();
    private final FacadeUnidad facadeUnidad = new FacadeUnidad();

    // Estado de la vista
    private Asignacion asignacionActual;
    private List<Asignacion> asignaciones;
    private List<Profesor> profesores;
    private List<UnidadAprendizaje> unidades;

    // Selecciones
    private Integer idProfesorSeleccionado;
    private Integer idUnidadSeleccionada;

    // Grid data (JSON serializado del JS)
    private String gridData;
    // Horas requeridas de la unidad seleccionada (para JS)
    private Integer horasClaseSeleccionada = 0;
    private Integer horasTallerSeleccionada = 0;
    private Integer horasLabSeleccionada = 0;
    @PostConstruct
    public void init() {
        asignacionActual = new Asignacion();
        cargarDatos();
    }

    // ============ MÉTODOS DE NEGOCIO ============

    public void cargarDatos() {
        try {
            asignaciones = facade.consultarAsignaciones();
            profesores = facadeProfesor.consultarProfesores();
            unidades = facadeUnidad.consultarUnidades();
        } catch (Exception e) {
            addError("Error al cargar datos: " + e.getMessage());
        }
    }
    /**
     * Se ejecuta cuando cambia la unidad seleccionada.
     * Actualiza las horas requeridas para que JS las lea.
     */
    public void onUnidadChange() {
        System.out.println(">>> onUnidadChange llamado. ID: " + idUnidadSeleccionada);

        if (idUnidadSeleccionada == null) {
            horasClaseSeleccionada = 0;
            horasTallerSeleccionada = 0;
            horasLabSeleccionada = 0;
            return;
        }

        UnidadAprendizaje u = encontrarUnidad(idUnidadSeleccionada);
        if (u != null) {
            horasClaseSeleccionada = u.getHorasClase() != null ? u.getHorasClase().intValue() : 0;
            horasTallerSeleccionada = u.getHorasTaller() != null ? u.getHorasTaller().intValue() : 0;
            horasLabSeleccionada = u.getHorasLaboratorio() != null ? u.getHorasLaboratorio().intValue() : 0;

            System.out.println(">>> Horas: Clase=" + horasClaseSeleccionada +
                    ", Taller=" + horasTallerSeleccionada +
                    ", Lab=" + horasLabSeleccionada);
        }
    }

    /**
     * Guarda las asignaciones desde el grid interactivo.
     * Recibe el JSON con las celdas pintadas, lo convierte en asignaciones,
     * las agrupa en bloques contiguos, y las guarda.
     */
    public void guardarDesdeGrid() {
        try {
            // 1. Validar selecciones
            if (idProfesorSeleccionado == null) {
                addError("Debe seleccionar un profesor.");
                return;
            }
            if (idUnidadSeleccionada == null) {
                addError("Debe seleccionar una unidad de aprendizaje.");
                return;
            }
            if (gridData == null || gridData.isBlank() || gridData.equals("{}")) {
                addError("Debe pintar al menos una celda en el horario.");
                return;
            }

            Profesor p = encontrarProfesor(idProfesorSeleccionado);
            UnidadAprendizaje u = encontrarUnidad(idUnidadSeleccionada);

            if (p == null || u == null) {
                addError("Profesor o unidad no válidos.");
                return;
            }

            // 2. Parsear el JSON y construir asignaciones
            List<Asignacion> asignacionesNuevas = parsearGridData(gridData, p, u);

            if (asignacionesNuevas.isEmpty()) {
                addError("No se pudieron generar asignaciones del grid.");
                return;
            }

            // 3. Guardar cada asignación (el Facade valida traslapes y horas)
            int guardadas = 0;
            for (Asignacion a : asignacionesNuevas) {
                try {
                    facade.altaAsignacion(a);
                    guardadas++;
                } catch (ValidacionException e) {
                    // Si una falla, mostrar el error específico
                    addError("Error al guardar " + a.getDiaSemana() + " " +
                            a.getHoraInicio() + "-" + a.getHoraFin() + ": " +
                            e.getMessage());
                    // No lanzar, seguir con las demás (opcional)
                }
            }

            if (guardadas > 0) {
                addInfo("✅ " + guardadas + " asignación(es) guardada(s) correctamente.");
                cargarDatos();
                gridData = null;
                idProfesorSeleccionado = null;
                idUnidadSeleccionada = null;
            }

        } catch (Exception e) {
            addError("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Parsea el JSON del grid y construye una lista de Asignaciones.
     * JSON esperado: { "LUNES-08:00": "CLASE", "LUNES-09:00": "CLASE", ... }
     *
     * Agrupa celdas contiguas del mismo tipo en una sola Asignación.
     */
    private List<Asignacion> parsearGridData(String json, Profesor p, UnidadAprendizaje u) {
        List<Asignacion> resultado = new ArrayList<>();

        // 1. Parsear JSON manualmente (sin librerías externas)
        // Eliminar { } y separar por comas
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);

        if (json.isBlank()) return resultado;

        // Cada entrada es: "LUNES-08:00":"CLASE"
        String[] entradas = json.split(",");
        java.util.Map<String, String> mapa = new java.util.HashMap<>();

        for (String entrada : entradas) {
            entrada = entrada.trim();
            // Separar por ":"
            int idx = entrada.indexOf(":");
            if (idx < 0) continue;

            String key = entrada.substring(0, idx).trim().replace("\"", "");
            String tipo = entrada.substring(idx + 1).trim().replace("\"", "");
            mapa.put(key, tipo);
        }

        // 2. Agrupar por día
        // Estructura: { "LUNES": { "CLASE": [07:00, 08:00, 09:00], ... } }
        java.util.Map<String, java.util.Map<String, List<String>>> porDia = new java.util.LinkedHashMap<>();

        for (java.util.Map.Entry<String, String> entry : mapa.entrySet()) {
            String key = entry.getKey(); // "LUNES-08:00"
            String tipo = entry.getValue(); // "CLASE"

            int guion = key.indexOf("-");
            if (guion < 0) continue;

            String dia = key.substring(0, guion);
            String hora = key.substring(guion + 1);

            porDia.computeIfAbsent(dia, k -> new java.util.LinkedHashMap<>())
                    .computeIfAbsent(tipo, k -> new ArrayList<>())
                    .add(hora);
        }

        // 3. Para cada día y tipo, agrupar horas contiguas
        for (java.util.Map.Entry<String, java.util.Map<String, List<String>>> diaEntry : porDia.entrySet()) {
            String dia = diaEntry.getKey();

            for (java.util.Map.Entry<String, List<String>> tipoEntry : diaEntry.getValue().entrySet()) {
                List<String> horas = tipoEntry.getValue();
                java.util.Collections.sort(horas);

                // Agrupar horas contiguas
                int i = 0;
                while (i < horas.size()) {
                    String horaInicio = horas.get(i);
                    String horaFin = sumarUnaHora(horaInicio);

                    // Ver cuántas horas contiguas hay
                    int j = i + 1;
                    while (j < horas.size() && horas.get(j).equals(horaFin)) {
                        horaFin = sumarUnaHora(horaFin);
                        j++;
                    }

                    // Crear Asignación
                    Asignacion a = new Asignacion();
                    a.setProfesor(p);
                    a.setUnidad(u);
                    a.setDiaSemana(dia);
                    a.setHoraInicio(LocalTime.parse(horaInicio));
                    a.setHoraFin(LocalTime.parse(horaFin));

                    resultado.add(a);
                    i = j;
                }
            }
        }

        return resultado;
    }

    /**
     * Suma una hora a una hora en formato HH:mm.
     * "08:00" → "09:00"
     */
    private String sumarUnaHora(String hora) {
        try {
            LocalTime t = LocalTime.parse(hora);
            return t.plusHours(1).toString();
        } catch (Exception e) {
            return hora;
        }
    }

    // ============ MÉTODOS AUXILIARES ============

    private Profesor encontrarProfesor(Integer id) {
        if (id == null || profesores == null) return null;
        for (Profesor p : profesores) {
            if (p.getId() != null && p.getId().equals(id)) return p;
        }
        return null;
    }

    private UnidadAprendizaje encontrarUnidad(Integer id) {
        if (id == null || unidades == null) return null;
        for (UnidadAprendizaje u : unidades) {
            if (u.getId() != null && u.getId().equals(id)) return u;
        }
        return null;
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

    public Asignacion getAsignacionActual() { return asignacionActual; }
    public void setAsignacionActual(Asignacion asignacionActual) { this.asignacionActual = asignacionActual; }

    public List<Asignacion> getAsignaciones() { return asignaciones; }
    public void setAsignaciones(List<Asignacion> asignaciones) { this.asignaciones = asignaciones; }

    public List<Profesor> getProfesores() { return profesores; }
    public void setProfesores(List<Profesor> profesores) { this.profesores = profesores; }

    public List<UnidadAprendizaje> getUnidades() { return unidades; }
    public void setUnidades(List<UnidadAprendizaje> unidades) { this.unidades = unidades; }

    public Integer getIdProfesorSeleccionado() { return idProfesorSeleccionado; }
    public void setIdProfesorSeleccionado(Integer idProfesorSeleccionado) { this.idProfesorSeleccionado = idProfesorSeleccionado; }

    public Integer getIdUnidadSeleccionada() { return idUnidadSeleccionada; }
    public void setIdUnidadSeleccionada(Integer idUnidadSeleccionada) { this.idUnidadSeleccionada = idUnidadSeleccionada; }

    public String getGridData() { return gridData; }
    public void setGridData(String gridData) { this.gridData = gridData; }
    public Integer getHorasClaseSeleccionada() {
        return horasClaseSeleccionada;
    }
    public void setHorasClaseSeleccionada(Integer horasClaseSeleccionada) {
        this.horasClaseSeleccionada = horasClaseSeleccionada;
    }

    public Integer getHorasTallerSeleccionada() {
        return horasTallerSeleccionada;
    }
    public void setHorasTallerSeleccionada(Integer horasTallerSeleccionada) {
        this.horasTallerSeleccionada = horasTallerSeleccionada;
    }

    public Integer getHorasLabSeleccionada() {
        return horasLabSeleccionada;
    }
    public void setHorasLabSeleccionada(Integer horasLabSeleccionada) {
        this.horasLabSeleccionada = horasLabSeleccionada;
    }
}