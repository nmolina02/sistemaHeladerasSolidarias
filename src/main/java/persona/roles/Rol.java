package persona.roles;

import lombok.Getter;
import persona.personas.Persona;

import javax.persistence.*;

@MappedSuperclass
@Table(name = "rol")
@Getter
public abstract class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "pers_id")
    public Persona persona;

    public Rol(Persona persona){
        this.persona = persona;
    }

    public Rol() {}
}
