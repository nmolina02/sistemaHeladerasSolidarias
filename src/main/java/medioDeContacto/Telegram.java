package medioDeContacto;

import lombok.Getter;
import lombok.Setter;
import persistencia.ClaseCRUD;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("TELEGRAM")
@Getter
public class Telegram extends MedioDeContacto{
    @Column(name = "medi_contacto")
    @Setter private String numero;

    public Telegram(String numero){
        super(TipoMedioContacto.TELEGRAM);
        this.numero = numero;
        ClaseCRUD.getInstance().add(this);
    }

    public Telegram() {
        super(TipoMedioContacto.TELEGRAM);
    }
}