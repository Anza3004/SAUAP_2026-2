package mx.desarrollo.test;

import mx.desarrollo.entity.Profesor;
import mx.desarrollo.persistencia.integration.ServiceLocator;
import mx.desarrollo.persistencia.persistence.HibernateUtil;

import java.util.List;

public class TestDAO {

    public static void main(String[] args) {
        System.out.println(">>> Iniciando prueba de DAOs...");

        ServiceLocator locator = ServiceLocator.getInstance();

        // 1. CREAR un profesor de prueba
        Profesor p = new Profesor();
        p.setNombre("Juan");
        p.setApellidoPaterno("Perez");
        p.setApellidoMaterno("Lopez");
        p.setRfc("PELJ850101ABC");

        System.out.println(">>> Guardando profesor...");
        Profesor guardado = locator.getProfesorDAO().guardar(p);
        System.out.println(">>> Profesor guardado con ID: " + guardado.getId());

        // 2. BUSCAR por RFC
        System.out.println(">>> Buscando por RFC...");
        Profesor encontrado = locator.getProfesorDAO().buscarPorRFC("PELJ850101ABC");
        if (encontrado != null) {
            System.out.println(">>> Encontrado: " + encontrado.getNombre()
                    + " " + encontrado.getApellidoPaterno()
                    + " (RFC: " + encontrado.getRfc() + ")");
        } else {
            System.out.println(">>> NO encontrado");
        }

        // 3. LISTAR todos
        System.out.println(">>> Listando todos los profesores...");
        List<Profesor> lista = locator.getProfesorDAO().listarTodos();
        System.out.println(">>> Total profesores: " + lista.size());
        lista.forEach(prof ->
                System.out.println("   - ID " + prof.getId() + ": " + prof.getNombre())
        );

        // 4. ELIMINAR
        System.out.println(">>> Eliminando profesor de prueba...");
        locator.getProfesorDAO().eliminar(guardado);
        System.out.println(">>> Eliminado");

        // 5. LISTAR de nuevo para verificar
        List<Profesor> listaFinal = locator.getProfesorDAO().listarTodos();
        System.out.println(">>> Total profesores después de eliminar: " + listaFinal.size());

        // Cerrar
        HibernateUtil.shutdown();
        System.out.println("=== FIN DE LA PRUEBA ===");
    }
}