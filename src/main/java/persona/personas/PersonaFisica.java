package persona.personas;

import localizacion.Ubicacion;
import lombok.Getter;
import lombok.Setter;
import medioDeContacto.MedioDeContacto;
import persistencia.ClaseCRUD;
import persona.documentacion.Documentacion;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "persona_fisica")
@Getter
public class PersonaFisica extends Persona {
    @Column(name = "pers_fisi_nombre")
    @Setter private String nombre;

    @Column(name = "pers_fisi_apellido")
    @Setter private String apellido;

    @Column(name = "pers_fisi_fecha_nacimiento")
    @Setter private LocalDate fechaNacimiento;

    @OneToOne
    @JoinColumn(name = "docu_id")
    private Documentacion documento;

    public PersonaFisica(List<MedioDeContacto> mediosDeContacto, Ubicacion direccion, String nombre, String apellido, LocalDate fechaNacimiento, Documentacion documento){
        super(mediosDeContacto, direccion);
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.documento = documento;
        ClaseCRUD.getInstance().add(this);
    }

    public PersonaFisica(){}
}
