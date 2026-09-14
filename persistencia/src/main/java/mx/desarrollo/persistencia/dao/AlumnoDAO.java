package mx.desarrollo.persistencia.dao;

import mx.desarrollo.entity.Alumno;
import mx.desarrollo.persistencia.persistence.AbstractDAO;

public class AlumnoDAO extends AbstractDAO<Alumno> {

    public AlumnoDAO() {
        super(Alumno.class);
    }
}