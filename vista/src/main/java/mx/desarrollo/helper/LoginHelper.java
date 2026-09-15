package mx.desarrollo.helper;

import mx.desarrollo.entity.Usuario;
import mx.desarrollo.negocio.facade.FacadeUsuario;
import mx.desarrollo.negocio.integration.ValidacionException;

import java.io.Serializable;

public class LoginHelper implements Serializable {

    private final FacadeUsuario facadeUsuario = new FacadeUsuario();

    public Usuario login(String correo, String password) {
        try {
            return facadeUsuario.autenticar(correo, password);
        } catch (ValidacionException e) {
            // Error de credenciales
            return null;
        } catch (Exception e) {
            // Error inesperado
            System.err.println("Error inesperado en login: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}