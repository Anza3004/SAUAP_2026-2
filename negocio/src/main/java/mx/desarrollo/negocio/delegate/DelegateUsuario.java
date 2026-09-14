package mx.desarrollo.negocio.delegate;

import mx.desarrollo.entity.Usuario;
import mx.desarrollo.persistencia.integration.ServiceLocator;

public class DelegateUsuario {

    private final ServiceLocator locator = ServiceLocator.getInstance();

    public Usuario buscarPorCorreo(String correo) {
        return locator.getUsuarioDAO().buscarPorCorreo(correo);
    }

    public Usuario validarLogin(String correo, String contrasena) {
        return locator.getUsuarioDAO().validarLogin(correo, contrasena);
    }
}