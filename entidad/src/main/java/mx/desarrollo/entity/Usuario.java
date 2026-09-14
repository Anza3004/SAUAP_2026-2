package mx.desarrollo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idusuario", nullable = false)
    private Integer id;

    @Size(max = 45)
    @NotNull
    @Column(name = "correo", nullable = false, length = 45)
    private String correo;

    @Size(max = 45)
    @NotNull
    @Column(name = "contrasena", nullable = false, length = 45)
    private String contrasena;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idAlumno", nullable = false)
    private Alumno idAlumno;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public Alumno getIdAlumno() { return idAlumno; }
    public void setIdAlumno(Alumno idAlumno) { this.idAlumno = idAlumno; }
}