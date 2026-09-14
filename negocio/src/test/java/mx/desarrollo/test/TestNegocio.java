package mx.desarrollo.test;

import mx.desarrollo.entity.Profesor;
import mx.desarrollo.entity.UnidadAprendizaje;
import mx.desarrollo.negocio.facade.FacadeProfesor;
import mx.desarrollo.negocio.facade.FacadeUnidad;
import mx.desarrollo.negocio.integration.ValidacionException;

public class TestNegocio {

    public static void main(String[] args) {
        FacadeProfesor facadeProfesor = new FacadeProfesor();
        FacadeUnidad facadeUnidad = new FacadeUnidad();

        System.out.println("=== PRUEBA 1: Guardar profesor válido ===");
        try {
            Profesor p = new Profesor();
            p.setNombre("Maria");
            p.setApellidoPaterno("Garcia");
            p.setApellidoMaterno("Ruiz");
            p.setRfc("GARM900101XYZ");

            Profesor guardado = facadeProfesor.altaProfesor(p);
            System.out.println("✅ Profesor guardado: " + guardado.getNombre()
                    + " (ID: " + guardado.getId() + ")");
        } catch (ValidacionException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        System.out.println("\n=== PRUEBA 2: RFC inválido ===");
        try {
            Profesor p = new Profesor();
            p.setNombre("Pedro");
            p.setApellidoPaterno("Lopez");
            p.setApellidoMaterno("Diaz");
            p.setRfc("123"); // RFC inválido
            facadeProfesor.altaProfesor(p);
            System.out.println("❌ NO debió guardar (RFC inválido)");
        } catch (ValidacionException e) {
            System.out.println("✅ Validación funcionó: " + e.getMessage());
        }

        System.out.println("\n=== PRUEBA 3: RFC duplicado ===");
        try {
            Profesor p = new Profesor();
            p.setNombre("Otra");
            p.setApellidoPaterno("Persona");
            p.setApellidoMaterno("Test");
            p.setRfc("GARM900101XYZ"); // RFC ya usado
            facadeProfesor.altaProfesor(p);
            System.out.println("❌ NO debió guardar (RFC duplicado)");
        } catch (ValidacionException e) {
            System.out.println("✅ Validación funcionó: " + e.getMessage());
        }

        System.out.println("\n=== PRUEBA 4: Unidad con horas inválidas ===");
        try {
            UnidadAprendizaje u = new UnidadAprendizaje();
            u.setNombre("Programación");
            u.setHorasClase((byte) 10); // inválido, max 4
            u.setHorasTaller((byte) 2);
            u.setHorasLaboratorio((byte) 2);
            facadeUnidad.altaUnidad(u);
            System.out.println("❌ NO debió guardar (horas > 4)");
        } catch (ValidacionException e) {
            System.out.println("✅ Validación funcionó: " + e.getMessage());
        }

        System.out.println("\n=== PRUEBA 5: Unidad válida ===");
        try {
            UnidadAprendizaje u = new UnidadAprendizaje();
            u.setNombre("Programación Web");
            u.setHorasClase((byte) 3);
            u.setHorasTaller((byte) 1);
            u.setHorasLaboratorio((byte) 2);
            UnidadAprendizaje guardada = facadeUnidad.altaUnidad(u);
            System.out.println("✅ Unidad guardada: " + guardada.getNombre()
                    + " (ID: " + guardada.getId() + ")");
        } catch (ValidacionException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }

        System.out.println("\n=== FIN DE PRUEBAS ===");
    }
}