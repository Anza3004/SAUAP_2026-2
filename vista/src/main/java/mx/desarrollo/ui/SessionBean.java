package mx.desarrollo.ui;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import mx.desarrollo.entity.Usuario;

import java.io.Serializable;

@Named("sessionBean")
@SessionScoped
public class SessionBean implements Serializable {

    private Usuario usuarioLogueado;

    public boolean isLogueado() {
        return usuarioLogueado != null;
    }

    /**
     * Cierra la sesión del usuario.
     * IMPORTANTE: NO invalidamos la sesión HTTP para evitar ViewExpiredException.
     * Solo limpiamos el usuario logueado.
     * El redirect con faces-redirect=true limpia el ViewState.
     */
    public void cerrarSesion() {
        this.usuarioLogueado = null;
    }

    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public void setUsuarioLogueado(Usuario usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }
}