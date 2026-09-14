package mx.desarrollo.persistencia.integration;

import mx.desarrollo.persistencia.dao.*;

/**
 * ServiceLocator: provee instancias únicas de los DAOs.
 */
public class ServiceLocator {

    private static ServiceLocator instance;

    private final ProfesorDAO profesorDAO;
    private final UnidadAprendizajeDAO unidadDAO;
    private final AsignacionDAO asignacionDAO;
    private final AlumnoDAO alumnoDAO;
    private final UsuarioDAO usuarioDAO;

    private ServiceLocator() {
        this.profesorDAO = new ProfesorDAO();
        this.unidadDAO = new UnidadAprendizajeDAO();
        this.asignacionDAO = new AsignacionDAO();
        this.alumnoDAO = new AlumnoDAO();
        this.usuarioDAO = new UsuarioDAO();
    }

    public static synchronized ServiceLocator getInstance() {
        if (instance == null) {
            instance = new ServiceLocator();
        }
        return instance;
    }

    public ProfesorDAO getProfesorDAO() { return profesorDAO; }
    public UnidadAprendizajeDAO getUnidadDAO() { return unidadDAO; }
    public AsignacionDAO getAsignacionDAO() { return asignacionDAO; }
    public AlumnoDAO getAlumnoDAO() { return alumnoDAO; }
    public UsuarioDAO getUsuarioDAO() { return usuarioDAO; }
}