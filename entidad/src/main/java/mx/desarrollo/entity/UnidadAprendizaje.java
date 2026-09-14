package mx.desarrollo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "unidad_aprendizaje")
public class UnidadAprendizaje implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unidad", nullable = false)
    private Integer id;

    @Size(max = 50)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Min(0) @Max(4)
    @NotNull
    @Column(name = "horas_clase", nullable = false)
    private Byte horasClase;

    @Min(0) @Max(4)
    @NotNull
    @Column(name = "horas_taller", nullable = false)
    private Byte horasTaller;

    @Min(0) @Max(4)
    @NotNull
    @Column(name = "horas_laboratorio", nullable = false)
    private Byte horasLaboratorio;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Byte getHorasClase() { return horasClase; }
    public void setHorasClase(Byte horasClase) { this.horasClase = horasClase; }

    public Byte getHorasTaller() { return horasTaller; }
    public void setHorasTaller(Byte horasTaller) { this.horasTaller = horasTaller; }

    public Byte getHorasLaboratorio() { return horasLaboratorio; }
    public void setHorasLaboratorio(Byte horasLaboratorio) { this.horasLaboratorio = horasLaboratorio; }
}