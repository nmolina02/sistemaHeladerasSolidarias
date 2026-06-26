package persona.personas;

import localizacion.Ubicacion;
import lombok.Getter;
import lombok.Setter;
import medioDeContacto.*;
import persistencia.ClaseCRUD;

import javax.persistence.*;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "persona")
@Getter
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pers_id")
    private Long id;

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL)
    private List<MedioDeContacto> mediosDeContacto;

    @ManyToOne
    @JoinColumn(name = "ubic_id")
    @Setter private Ubicacion direccion;

    public Persona(List<MedioDeContacto> mediosDeContacto, Ubicacion direccion){
        this.mediosDeContacto = mediosDeContacto;
        this.direccion = direccion;
        ClaseCRUD.getInstance().add(this);
    }

    public Persona(){}

    public void agregar_medio_de_contacto(MedioDeContacto medioDeContacto){
        this.mediosDeContacto.add(medioDeContacto);
        medioDeContacto.setPersona(this);
    }

    public void quitar_medio_contacto(MedioDeContacto medioDeContacto){
        this.mediosDeContacto.remove(medioDeContacto);
    }
}
