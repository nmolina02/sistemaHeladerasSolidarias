package medioDeContacto;

import lombok.Getter;
import lombok.Setter;
import persistencia.ClaseCRUD;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("WHATSAPP")
@Getter
public class Whatsapp extends MedioDeContacto{
    @Column(name = "medi_contacto")
    @Setter private String numero;

    public Whatsapp(String numero){
        super(TipoMedioContacto.WHATSAPP);
        this.numero = numero;
        ClaseCRUD.getInstance().add(this);
    }

    public Whatsapp() {
        super(TipoMedioContacto.WHATSAPP);
    }
}
