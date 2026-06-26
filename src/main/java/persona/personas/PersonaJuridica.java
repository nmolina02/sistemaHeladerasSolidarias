package persona.personas;

import localizacion.Ubicacion;
import lombok.Getter;
import medioDeContacto.MedioDeContacto;
import persistencia.ClaseCRUD;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "persona_juridica")
@Getter
public class PersonaJuridica extends Persona {
    @Column(name = "pers_juri_razon_social")
    private String razonSocial;

    @Column(name = "pers_juri_tipo")
    @Enumerated(EnumType.STRING)
    private TipoJuridico tipoJuridico;

    @Column(name = "pers_juri_rubro")
    private String rubro;

    @Column(name = "pers_juri_cuit")
    private String cuit;

    public PersonaJuridica(List<MedioDeContacto> mediosDeContacto, Ubicacion direccion, String razonSocial, TipoJuridico tipoJuridico, String rubro, String cuit){
        super(mediosDeContacto, direccion);
        this.razonSocial = razonSocial;
        this.tipoJuridico = tipoJuridico;
        this.rubro = rubro;
        this.cuit = cuit;
        ClaseCRUD.getInstance().add(this);
    }

    public PersonaJuridica(){}
}
