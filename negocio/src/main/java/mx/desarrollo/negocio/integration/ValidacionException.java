package mx.desarrollo.negocio.integration;

/**
 * Excepción lanzada cuando una regla de negocio no se cumple.
 */
public class ValidacionException extends RuntimeException {

    public ValidacionException(String mensaje) {
        super(mensaje);
    }

    public ValidacionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}