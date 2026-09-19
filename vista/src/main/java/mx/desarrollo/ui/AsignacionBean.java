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
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
@Named("asignacionBean")
@ViewScoped
public class AsignacionBean implements Serializable {

    private final FacadeAsignacion facade = new FacadeAsignacion();
    private final FacadeProfesor facadeProfesor = new FacadeProfesor();
    private final FacadeUnidad facadeUnidad = new FacadeUnidad();

    private Asignacion asignacionActual;
    private List<Asignacion> asignaciones;
    private List<Profesor> profesores;
    private List<UnidadAprendizaje> unidades;

    private Integer idProfesorSeleccionado;
    private Integer idUnidadSeleccionada;

    private String gridData;

    private String mensajeAlerta;
    private String tipoAlerta; // "success" | "error"

    private Integer horasClaseSeleccionada = 0;
    private Integer horasTallerSeleccionada = 0;
    private Integer horasLabSeleccionada = 0;

    @PostConstruct
    public void init() {
        asignacionActual = new Asignacion();
        cargarDatos();
    }

    public void cargarDatos() {
        try {
            asignaciones = facade.consultarAsignaciones();
            profesores = facadeProfesor.consultarProfesores();
            unidades = facadeUnidad.consultarUnidades();
        } catch (Exception e) {
            setAlerta("Error al cargar datos: " + e.getMessage(), "error");
        }
    }

    public void onUnidadChange() {
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
        }
    }

    public void guardarDesdeGrid() {
        mensajeAlerta = null;
        tipoAlerta = null;

        try {
            if (idProfesorSeleccionado == null) {
                setAlerta("Debe seleccionar un profesor.", "error");
                return;
            }
            if (idUnidadSeleccionada == null) {
                setAlerta("Debe seleccionar una unidad de aprendizaje.", "error");
                return;
            }
            if (gridData == null || gridData.isBlank() || gridData.equals("{}")) {
                setAlerta("Debe pintar al menos una celda en el horario.", "error");
                return;
            }

            Profesor p = encontrarProfesor(idProfesorSeleccionado);
            UnidadAprendizaje u = encontrarUnidad(idUnidadSeleccionada);

            if (p == null || u == null) {
                setAlerta("Profesor o unidad no válidos.", "error");
                return;
            }

            List<Asignacion> asignacionesNuevas = parsearGridData(gridData, p, u);

            if (asignacionesNuevas.isEmpty()) {
                setAlerta("No se pudieron generar asignaciones del grid.", "error");
                return;
            }

            int minutosRequeridos = calcularMinutosRequeridos(u);
            long minutosPintados = calcularMinutosDelBloque(asignacionesNuevas);

            if (minutosPintados != minutosRequeridos) {
                String mensaje = minutosPintados < minutosRequeridos
                        ? "Faltan horas por asignar.\n\nUnidad: " + u.getNombre() +
                        "\nRequeridas: " + (minutosRequeridos / 60) + "h" +
                        "\nPintadas: " + formatearMinutos((int) minutosPintados) +
                        "\nFaltan: " + formatearMinutos((int) (minutosRequeridos - minutosPintados))
                        : "Se excede el total de horas.\n\nUnidad: " + u.getNombre() +
                        "\nRequeridas: " + (minutosRequeridos / 60) + "h" +
                        "\nPintadas: " + formatearMinutos((int) minutosPintados) +
                        "\nExceso: " + formatearMinutos((int) (minutosPintados - minutosRequeridos));
                setAlerta(mensaje, "error");
                return;
            }

            // Validar TODAS las asignaciones ANTES de guardar
            List<String> erroresValidacion = new ArrayList<>();
            for (Asignacion a : asignacionesNuevas) {
                try {
                    facade.validarAsignacionSinGuardar(a);
                } catch (ValidacionException e) {
                    erroresValidacion.add(a.getDiaSemana() + " " +
                            a.getHoraInicio() + "-" + a.getHoraFin() + ": " + e.getMessage());
                }
            }

            if (!erroresValidacion.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append("No se guardó ninguna asignación.\n");
                sb.append("Se encontraron ").append(erroresValidacion.size()).append(" errores:\n\n");
                for (String err : erroresValidacion) {
                    sb.append("• ").append(err).append("\n");
                }
                setAlerta(sb.toString(), "error");
                return;
            }

            // Guardar todas
            int guardadas = 0;
            for (Asignacion a : asignacionesNuevas) {
                facade.altaAsignacion(a);
                guardadas++;
            }

            setAlerta("✅ " + guardadas + " asignación(es) guardada(s) correctamente.", "success");

            cargarDatos();
            gridData = null;
            idProfesorSeleccionado = null;
            idUnidadSeleccionada = null;
            horasClaseSeleccionada = 0;
            horasTallerSeleccionada = 0;
            horasLabSeleccionada = 0;

        } catch (Exception e) {
            setAlerta("Error inesperado: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    private void setAlerta(String mensaje, String tipo) {
        this.mensajeAlerta = mensaje;
        this.tipoAlerta = tipo;
        System.out.println(">>> Alerta [" + tipo + "]: " + mensaje);
    }

    private int calcularMinutosRequeridos(UnidadAprendizaje u) {
        int clase = u.getHorasClase() != null ? u.getHorasClase() : 0;
        int taller = u.getHorasTaller() != null ? u.getHorasTaller() : 0;
        int lab = u.getHorasLaboratorio() != null ? u.getHorasLaboratorio() : 0;
        return (clase + taller + lab) * 60;
    }

    private long calcularMinutosDelBloque(List<Asignacion> asignaciones) {
        long total = 0;
        for (Asignacion a : asignaciones) {
            total += Duration.between(a.getHoraInicio(), a.getHoraFin()).toMinutes();
        }
        return total;
    }

    private String formatearMinutos(int minutos) {
        int h = minutos / 60;
        int m = minutos % 60;
        if (m == 0) return h + "h";
        return h + "h " + m + "min";
    }

    /**
     * Parsea el grid y genera UN grupo UUID para TODAS las asignaciones del bloque.
     */
    private List<Asignacion> parsearGridData(String json, Profesor p, UnidadAprendizaje u) {
        List<Asignacion> resultado = new ArrayList<>();

        // 🆕 Generar un grupo único para TODAS las asignaciones del bloque
        String grupo = UUID.randomUUID().toString();

        json = json.replace("&quot;", "\"")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&#39;", "'");

        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);

        if (json.isBlank()) return resultado;

        String[] entradas = json.split(",");
        java.util.Map<String, String> mapa = new java.util.HashMap<>();

        for (String entrada : entradas) {
            entrada = entrada.trim();
            int idx = entrada.lastIndexOf(":");
            if (idx < 0) continue;

            String key = entrada.substring(0, idx).trim().replace("\"", "");
            String tipo = entrada.substring(idx + 1).trim().replace("\"", "");
            mapa.put(key, tipo);
        }

        java.util.Map<String, java.util.Map<String, List<String>>> porDia = new java.util.LinkedHashMap<>();

        for (java.util.Map.Entry<String, String> entry : mapa.entrySet()) {
            String key = entry.getKey();
            String tipo = entry.getValue();

            int guion = key.indexOf("-");
            if (guion < 0) continue;

            String dia = key.substring(0, guion);
            String hora = key.substring(guion + 1);

            porDia.computeIfAbsent(dia, k -> new java.util.LinkedHashMap<>())
                    .computeIfAbsent(tipo, k -> new ArrayList<>())
                    .add(hora);
        }

        for (java.util.Map.Entry<String, java.util.Map<String, List<String>>> diaEntry : porDia.entrySet()) {
            String dia = diaEntry.getKey();

            for (java.util.Map.Entry<String, List<String>> tipoEntry : diaEntry.getValue().entrySet()) {
                List<String> horas = tipoEntry.getValue();
                java.util.Collections.sort(horas);

                int i = 0;
                while (i < horas.size()) {
                    String horaInicio = horas.get(i);
                    String horaFin = sumarUnaHora(horaInicio);

                    int j = i + 1;
                    while (j < horas.size() && horas.get(j).equals(horaFin)) {
                        horaFin = sumarUnaHora(horaFin);
                        j++;
                    }

                    Asignacion a = new Asignacion();
                    a.setProfesor(p);
                    a.setUnidad(u);
                    a.setDiaSemana(dia);
                    a.setHoraInicio(parseHoraSegura(horaInicio));
                    a.setHoraFin(parseHoraSegura(horaFin));
                    a.setGrupo(grupo);  // 🆕 MISMO grupo para todas

                    resultado.add(a);
                    i = j;
                }
            }
        }

        System.out.println(">>> Grupo generado: " + grupo + " (" + resultado.size() + " asignaciones)");
        return resultado;
    }

    private String sumarUnaHora(String hora) {
        if (hora == null || hora.isBlank()) return hora;
        String horaNormalizada = hora.contains(":") ? hora : hora + ":00";
        try {
            LocalTime t = LocalTime.parse(horaNormalizada);
            return t.plusHours(1).toString();
        } catch (Exception e) {
            return hora;
        }
    }

    private LocalTime parseHoraSegura(String hora) {
        if (hora == null || hora.isBlank()) return null;
        String horaNormalizada = hora.contains(":") ? hora : hora + ":00";
        try {
            return LocalTime.parse(horaNormalizada);
        } catch (Exception e) {
            return null;
        }
    }

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

    public String getMensajeAlerta() { return mensajeAlerta; }
    public void setMensajeAlerta(String mensajeAlerta) { this.mensajeAlerta = mensajeAlerta; }

    public String getTipoAlerta() { return tipoAlerta; }
    public void setTipoAlerta(String tipoAlerta) { this.tipoAlerta = tipoAlerta; }

    public Integer getHorasClaseSeleccionada() { return horasClaseSeleccionada; }
    public void setHorasClaseSeleccionada(Integer horasClaseSeleccionada) { this.horasClaseSeleccionada = horasClaseSeleccionada; }

    public Integer getHorasTallerSeleccionada() { return horasTallerSeleccionada; }
    public void setHorasTallerSeleccionada(Integer horasTallerSeleccionada) { this.horasTallerSeleccionada = horasTallerSeleccionada; }

    public Integer getHorasLabSeleccionada() { return horasLabSeleccionada; }
    public void setHorasLabSeleccionada(Integer horasLabSeleccionada) { this.horasLabSeleccionada = horasLabSeleccionada; }
}