package mx.desarrollo.persistencia.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import mx.desarrollo.entity.Usuario;
import mx.desarrollo.persistencia.persistence.AbstractDAO;
import mx.desarrollo.persistencia.persistence.HibernateUtil;

public class UsuarioDAO extends AbstractDAO<Usuario> {

    public UsuarioDAO() {
        super(Usuario.class);
    }

    /**
     * Busca un usuario por correo. Devuelve null si no existe.
     */
    public Usuario buscarPorCorreo(String correo) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Usuario> q = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.correo = :correo", Usuario.class);
            q.setParameter("correo", correo);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Valida login: devuelve el usuario si el correo y contraseña coinciden.
     */
    public Usuario validarLogin(String correo, String contrasena) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Usuario> q = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.correo = :correo AND u.contrasena = :pass",
                    Usuario.class);
            q.setParameter("correo", correo);
            q.setParameter("pass", contrasena);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}