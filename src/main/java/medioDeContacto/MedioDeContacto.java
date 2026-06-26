package medioDeContacto;

import lombok.Getter;
import lombok.Setter;
import persona.personas.Persona;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "medio_de_contacto")
@DiscriminatorColumn(name = "medi_descripcion", discriminatorType = DiscriminatorType.STRING)
@Getter
public abstract class MedioDeContacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medi_id")
    private Long id;

    @Transient
    private TipoMedioContacto tipoMedioContacto;

    @ManyToOne
    @JoinColumn(name = "pers_id")
    @Setter private Persona persona;

    public MedioDeContacto(TipoMedioContacto tipoMedioContacto){
        this.tipoMedioContacto = tipoMedioContacto;
    }

    public MedioDeContacto() {}
}
