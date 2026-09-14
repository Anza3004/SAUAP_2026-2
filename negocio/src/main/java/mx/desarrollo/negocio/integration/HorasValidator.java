package mx.desarrollo.negocio.integration;

/**
 * Validador para las horas de una unidad de aprendizaje.
 * Deben estar entre 0 y 4.
 */
public class HorasValidator {

    private static final int MIN_HORAS = 0;
    private static final int MAX_HORAS = 4;

    public static boolean esValido(Byte horas) {
        if (horas == null) return false;
        return horas >= MIN_HORAS && horas <= MAX_HORAS;
    }

    public static String getMensajeError(String nombreCampo, Byte horas) {
        if (horas == null) {
            return "Las " + nombreCampo + " no pueden estar vacías.";
        }
        if (!esValido(horas)) {
            return "Las " + nombreCampo + " deben estar entre " +
                    MIN_HORAS + " y " + MAX_HORAS + ".";
        }
        return null;
    }
}