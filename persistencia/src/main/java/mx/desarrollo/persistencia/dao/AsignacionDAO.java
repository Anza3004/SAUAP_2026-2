package mx.desarrollo.persistencia.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import mx.desarrollo.entity.Asignacion;
import mx.desarrollo.persistencia.persistence.AbstractDAO;
import mx.desarrollo.persistencia.persistence.HibernateUtil;

import java.time.LocalTime;
import java.util.List;

public class AsignacionDAO extends AbstractDAO<Asignacion> {

    public AsignacionDAO() {
        super(Asignacion.class);
    }

    /**
     * Busca traslapes de un profesor en un día específico.
     * Devuelve asignaciones que se solapan con el rango [horaInicio, horaFin].
     */
    public List<Asignacion> buscarTraslapes(Integer idProfesor, String diaSemana,
                                            LocalTime horaInicio, LocalTime horaFin) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Asignacion> q = em.createQuery(
                    "SELECT a FROM Asignacion a " +
                            "WHERE a.profesor.id = :idProfesor " +
                            "AND a.diaSemana = :dia " +
                            "AND a.horaInicio < :horaFin " +
                            "AND a.horaFin > :horaInicio",
                    Asignacion.class);
            q.setParameter("idProfesor", idProfesor);
            q.setParameter("dia", diaSemana);
            q.setParameter("horaInicio", horaInicio);
            q.setParameter("horaFin", horaFin);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Lista asignaciones de un profesor.
     */
    public List<Asignacion> listarPorProfesor(Integer idProfesor) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Asignacion> q = em.createQuery(
                    "SELECT a FROM Asignacion a WHERE a.profesor.id = :id", Asignacion.class);
            q.setParameter("id", idProfesor);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Lista asignaciones de una unidad de aprendizaje.
     */
    public List<Asignacion> listarPorUnidad(Integer idUnidad) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Asignacion> q = em.createQuery(
                    "SELECT a FROM Asignacion a WHERE a.unidad.id = :id", Asignacion.class);
            q.setParameter("id", idUnidad);
            return q.getResultList();
        } finally {
            em.close();
        }
    }
}