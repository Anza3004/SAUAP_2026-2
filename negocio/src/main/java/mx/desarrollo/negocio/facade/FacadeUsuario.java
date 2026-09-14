package mx.desarrollo.negocio.facade;

import mx.desarrollo.entity.Usuario;
import mx.desarrollo.negocio.delegate.DelegateUsuario;
import mx.desarrollo.negocio.integration.ValidacionException;

public class FacadeUsuario {

    private final DelegateUsuario delegate;

    public FacadeUsuario() {
        this.delegate = new DelegateUsuario();
    }

    /**
     * Autentica un usuario. Devuelve el Usuario si las credenciales son válidas.
     * Lanza excepción si son inválidas.
     */
    public Usuario autenticar(String correo, String contrasena) {
        if (correo == null || correo.isBlank()) {
            throw new ValidacionException("El correo es obligatorio.");
        }
        if (contrasena == null || contrasena.isBlank()) {
            throw new ValidacionException("La contraseña es obligatoria.");
        }

        Usuario usuario = delegate.validarLogin(correo.trim(), contrasena);
        if (usuario == null) {
            throw new ValidacionException("Credenciales inválidas. Verifique su correo y contraseña.");
        }
        return usuario;
    }
}