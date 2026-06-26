package medioDeContacto;

import lombok.Getter;
import lombok.Setter;
import persistencia.ClaseCRUD;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("MAIL")
@Getter
public class Mail extends MedioDeContacto{
    @Column(name = "medi_contacto")
    @Setter private String casilla;

    public Mail(String casilla){
        super(TipoMedioContacto.MAIL);
        this.casilla = casilla;
        ClaseCRUD.getInstance().add(this);
    }

    public Mail() {
        super(TipoMedioContacto.MAIL);
    }
}
