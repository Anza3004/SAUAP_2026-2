package mx.desarrollo.helper;

import mx.desarrollo.entity.Usuario;
import mx.desarrollo.negocio.facade.FacadeUsuario;
import mx.desarrollo.negocio.integration.ValidacionException;

import java.io.Serializable;

public class LoginHelper implements Serializable {

    private final FacadeUsuario facadeUsuario = new FacadeUsuario();

    /**
     * Metodo para hacer login, llama a la capa de negocio (FacadeUsuario).
     * @param correo    correo del usuario
     * @param password  contrasena del usuario
     * @return el Usuario autenticado, o null si las credenciales son invalidas
     */
    public Usuario login(String correo, String password) {
        try {
            return facadeUsuario.autenticar(correo, password);
        } catch (ValidacionException e) {
            return null;
        }
    }
}
