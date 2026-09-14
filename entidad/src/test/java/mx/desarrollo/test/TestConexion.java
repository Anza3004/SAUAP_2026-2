package mx.desarrollo.test;

import jakarta.persistence.*;
import mx.desarrollo.entity.Profesor;

public class TestConexion {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("entidad_PU");
        EntityManager em = emf.createEntityManager();

        System.out.println("=== Conexión exitosa ===");

        // Contar profesores
        Long count = em.createQuery("SELECT COUNT(p) FROM Profesor p", Long.class)
                .getSingleResult();
        System.out.println("Profesores en BD: " + count);

        // Listar profesores
        em.createQuery("SELECT p FROM Profesor p", Profesor.class)
                .getResultList()
                .forEach(p -> System.out.println("  - " + p.getNombre() + " " + p.getApellidoPaterno()));

        em.close();
        emf.close();
        System.out.println("=== OK ===");
    }
}