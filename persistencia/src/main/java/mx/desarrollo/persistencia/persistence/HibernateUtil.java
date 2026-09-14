package mx.desarrollo.persistencia.persistence;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Singleton que provee el EntityManagerFactory.
 * Se crea UNA sola vez en toda la aplicación.
 */
public class HibernateUtil {

    private static final String PERSISTENCE_UNIT = "persistencia_PU";
    private static final EntityManagerFactory emf;

    static {
        try {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
            System.out.println(">>> HibernateUtil: EntityManagerFactory creado correctamente.");
        } catch (Throwable ex) {
            System.err.println(">>> Error al crear EntityManagerFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private HibernateUtil() {
        // Constructor privado para evitar instanciación
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    public static void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            System.out.println(">>> HibernateUtil: EntityManagerFactory cerrado.");
        }
    }
}