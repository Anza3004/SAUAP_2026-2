package mx.desarrollo.ui;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import mx.desarrollo.entity.Usuario;
import mx.desarrollo.helper.LoginHelper;

import java.io.IOException;
import java.io.Serializable;

@Named("loginUI")
@SessionScoped
public class LoginBeanUI implements Serializable {

    private final LoginHelper loginHelper = new LoginHelper();
    private Usuario usuario;

    @Inject
    private SessionBean sessionBean;

    @PostConstruct
    public void init() {
        usuario = new Usuario();
    }

    public void login() throws IOException {
        Usuario us = loginHelper.login(usuario.getCorreo(), usuario.getContrasena());

        FacesContext fc = FacesContext.getCurrentInstance();

        if (us != null && us.getId() != null) {
            // Guardar en sesión
            sessionBean.setUsuarioLogueado(us);
            usuario = us;

            // Redirigir
            String ctx = fc.getExternalContext().getRequestContextPath();
            fc.getExternalContext().redirect(ctx + "/dashboard.xhtml");

        } else {
            fc.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    "Credenciales incorrectas",
                    "Verifique su correo y contraseña"));
        }
    }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}