package mx.desarrollo.persistencia.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import mx.desarrollo.entity.Asignacion;
import mx.desarrollo.persistencia.persistence.AbstractDAO;
import mx.desarrollo.persistencia.persistence.HibernateUtil;
import jakarta.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

public class AsignacionDAO extends AbstractDAO<Asignacion> {

    public AsignacionDAO() {
        super(Asignacion.class);
    }

    /**
     * Busca traslapes de un profesor en un día específico.
     * Si idExcluir no es null, excluye esa asignación (para modificar).
     */
    public List<Asignacion> buscarTraslapes(Integer idProfesor, String diaSemana,
                                            LocalTime horaInicio, LocalTime horaFin) {
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

    public List<String> listarNombresUnidadesPorProfesor(Integer idProfesor) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<String> q = em.createQuery(
                    "SELECT DISTINCT a.unidad.nombre FROM Asignacion a " +
                            "WHERE a.profesor.id = :id ORDER BY a.unidad.nombre", String.class);
            q.setParameter("id", idProfesor);
            return q.getResultList();
        }finally {
            em.close();
        }
    }

    public List<String> listarNombresProfesoresPorUnidad(Integer idUnidad) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<String> q = em.createQuery(
                    "SELECT DISTINCT CONCAT(a.profesor.nombre, ' ', a.profesor.apellidoPaterno) " +
                            "FROM Asignacion a WHERE a.unidad.id = :id", String.class);
            q.setParameter("id", idUnidad);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Suma el total de minutos de todas las asignaciones de una unidad.
     * ⚠️ Ya NO se usa para validar, solo para mostrar info.
     */
    public Long sumarMinutosAsignados(Integer idUnidad) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<BigDecimal> q = em.createQuery(
                    "SELECT COALESCE(SUM(FUNCTION('TIMESTAMPDIFF', MINUTE, a.horaInicio, a.horaFin)), 0) " +
                            "FROM Asignacion a WHERE a.unidad.id = :idUnidad", BigDecimal.class);
            q.setParameter("idUnidad", idUnidad);
            BigDecimal total = q.getSingleResult();
            return total != null ? total.longValue() : 0L;
        } finally {
            em.close();
        }
    }

    public Long sumarMinutosAsignadosExcluyendo(Integer idUnidad, Integer idAsignacionExcluir) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<BigDecimal> q = em.createQuery(
                    "SELECT COALESCE(SUM(FUNCTION('TIMESTAMPDIFF', MINUTE, a.horaInicio, a.horaFin)), 0) " +
                            "FROM Asignacion a " +
                            "WHERE a.unidad.id = :idUnidad " +
                            "AND a.id <> :idExcluir", BigDecimal.class);
            q.setParameter("idUnidad", idUnidad);
            q.setParameter("idExcluir", idAsignacionExcluir);
            BigDecimal total = q.getSingleResult();
            return total != null ? total.longValue() : 0L;
        } finally {
            em.close();
        }
    }
    /**
     * Elimina todas las asignaciones que tengan el mismo grupo.
     * Se usa para eliminar un bloque completo (clase + taller + lab).
     */
    public int eliminarPorGrupo(String grupo) {
        if (grupo == null || grupo.isBlank()) {
            return 0;
        }

        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            int eliminadas = em.createQuery(
                            "DELETE FROM Asignacion a WHERE a.grupo = :grupo")
                    .setParameter("grupo", grupo)
                    .executeUpdate();
            tx.commit();
            return eliminadas;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    public int eliminarPorProfesorUnidadSinGrupo(Integer idProfesor, Integer idUnidad) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            int eliminadas = em.createQuery(
                            "DELETE FROM Asignacion a " +
                                    "WHERE a.profesor.id = :idProfesor " +
                                    "AND a.unidad.id = :idUnidad " +
                                    "AND (a.grupo IS NULL OR a.grupo = '')")
                    .setParameter("idProfesor", idProfesor)
                    .setParameter("idUnidad", idUnidad)
                    .executeUpdate();
            tx.commit();
            return eliminadas;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}