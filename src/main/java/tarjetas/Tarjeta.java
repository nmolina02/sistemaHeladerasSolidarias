package tarjetas;

import lombok.Getter;

import persistencia.ClaseCRUD;
import persona.roles.Rol;

import javax.persistence.*;


@MappedSuperclass
@Table(name = "rol")
@Getter
public abstract class Tarjeta {
    @Id
    @Column(name = "tarj_id")
    private String codigo;

    @Transient
    private Rol rolPersona;

    public Tarjeta(Rol rolPersona, String codigo) {
        this.rolPersona = rolPersona;
        if(codigo.length() == 11) {
            this.codigo = codigo;
        }
        else throw new RuntimeException("El codigo no tiene 11 caracteres.");
    }

    public Tarjeta() {}
}