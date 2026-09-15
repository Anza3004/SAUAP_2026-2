package mx.desarrollo.ui;

import mx.desarrollo.helper.LoginHelper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import mx.desarrollo.entity.Usuario;

import java.io.IOException;
import java.io.Serializable;

@Named("loginUI")
@SessionScoped
public class LoginBeanUI implements Serializable {

    private final LoginHelper loginHelper = new LoginHelper();
    private Usuario usuario;

    /**
     * Metodo postconstructor, todo lo que este dentro de este metodo
     * sera lo primero que se ejecute cuando cargue la pagina
     */
    @PostConstruct
    public void init() {
        usuario = new Usuario();
    }

    public void login() throws IOException {
        String appURL = "/index.xhtml";
        // los atributos de usuario vienen del xhtml
        Usuario us = loginHelper.login(usuario.getCorreo(), usuario.getContrasena());
        if (us != null && us.getId() != null) {
            // asigno el usuario encontrado al usuario de esta clase para que
            // se muestre correctamente en la pagina de informacion
            usuario = us;
            FacesContext.getCurrentInstance().getExternalContext().redirect(
                    FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + appURL);
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Usuario o contraseña incorrecta:", "Intente de nuevo"));
        }
    }

    /* getters y setters */

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}