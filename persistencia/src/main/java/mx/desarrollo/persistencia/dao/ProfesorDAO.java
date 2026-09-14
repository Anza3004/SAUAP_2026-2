package mx.desarrollo.persistencia.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import mx.desarrollo.entity.Profesor;
import mx.desarrollo.persistencia.persistence.AbstractDAO;
import mx.desarrollo.persistencia.persistence.HibernateUtil;

import java.util.List;

public class ProfesorDAO extends AbstractDAO<Profesor> {

    public ProfesorDAO() {
        super(Profesor.class);
    }

    /**
     * Busca un profesor por su RFC. Devuelve null si no existe.
     */
    public Profesor buscarPorRFC(String rfc) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Profesor> q = em.createQuery(
                    "SELECT p FROM Profesor p WHERE p.rfc = :rfc", Profesor.class);
            q.setParameter("rfc", rfc);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Lista profesores ordenados por apellido paterno.
     */
    public List<Profesor> listarOrdenados() {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM Profesor p ORDER BY p.apellidoPaterno ASC, p.apellidoMaterno ASC",
                    Profesor.class).getResultList();
        } finally {
            em.close();
        }
    }
}