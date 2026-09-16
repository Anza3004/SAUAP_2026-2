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
        // Llamada sin exclusión (alta nueva)
        return buscarTraslapes(idProfesor, diaSemana, horaInicio, horaFin, null);
    }

    public List<Asignacion> buscarTraslapes(Integer idProfesor, String diaSemana,
                                            LocalTime horaInicio, LocalTime horaFin,
                                            Integer idExcluir) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            String jpql = "SELECT a FROM Asignacion a " +
                    "WHERE a.profesor.id = :idProfesor " +
                    "AND a.diaSemana = :dia " +
                    "AND a.horaInicio < :horaFin " +
                    "AND a.horaFin > :horaInicio";

            if (idExcluir != null) {
                jpql += " AND a.id <> :idExcluir";
            }

            TypedQuery<Asignacion> q = em.createQuery(jpql, Asignacion.class);
            q.setParameter("idProfesor", idProfesor);
            q.setParameter("dia", diaSemana);
            q.setParameter("horaInicio", horaInicio);
            q.setParameter("horaFin", horaFin);

            if (idExcluir != null) {
                q.setParameter("idExcluir", idExcluir);
            }

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
    /**
     * Suma el total de minutos de todas las asignaciones de una unidad.
     */
    public Long sumarMinutosAsignados(Integer idUnidad) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Long> q = em.createQuery(
                    "SELECT COALESCE(SUM(FUNCTION('TIMESTAMPDIFF', MINUTE, a.horaInicio, a.horaFin)), 0) " +
                            "FROM Asignacion a WHERE a.unidad.id = :idUnidad", Long.class);
            q.setParameter("idUnidad", idUnidad);
            Long total = q.getSingleResult();
            return total != null ? total : 0L;
        } finally {
            em.close();
        }
    }

    /**
     * Suma los minutos asignados a una unidad, EXCLUYENDO una asignación (para modificar).
     */
    public Long sumarMinutosAsignadosExcluyendo(Integer idUnidad, Integer idAsignacionExcluir) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Long> q = em.createQuery(
                    "SELECT COALESCE(SUM(FUNCTION('TIMESTAMPDIFF', MINUTE, a.horaInicio, a.horaFin)), 0) " +
                            "FROM Asignacion a " +
                            "WHERE a.unidad.id = :idUnidad " +
                            "AND a.id <> :idExcluir", Long.class);
            q.setParameter("idUnidad", idUnidad);
            q.setParameter("idExcluir", idAsignacionExcluir);
            Long total = q.getSingleResult();
            return total != null ? total : 0L;
        } finally {
            em.close();
        }
    }
}