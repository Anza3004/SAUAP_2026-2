package mx.desarrollo.negocio.integration;

import java.util.regex.Pattern;

/**
 * Validador de RFC mexicano.
 * Formato: 3-4 letras + 6 dígitos (fecha) + 3 caracteres alfanuméricos (homoclave)
 */
public class RFCValidator {

    private static final Pattern RFC_PATTERN =
            Pattern.compile("^[A-ZÑ&]{3,4}\\d{6}[A-Z0-9]{3}$");

    /**
     * Valida si un RFC tiene el formato correcto.
     * @param rfc RFC a validar
     * @return true si es válido, false si no
     */
    public static boolean esValido(String rfc) {
        if (rfc == null || rfc.isBlank()) {
            return false;
        }
        return RFC_PATTERN.matcher(rfc.toUpperCase().trim()).matches();
    }

    /**
     * Devuelve el mensaje de error si el RFC no es válido.
     */
    public static String getMensajeError(String rfc) {
        if (rfc == null || rfc.isBlank()) {
            return "El RFC no puede estar vacío.";
        }
        if (!esValido(rfc)) {
            return "El RFC '" + rfc + "' no tiene un formato válido. " +
                    "Debe tener 13 caracteres: 4 letras + 6 dígitos + 3 caracteres.";
        }
        return null; // Sin error
    }
}