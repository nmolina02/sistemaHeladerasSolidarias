package medioDeContacto;

import lombok.Getter;
import lombok.Setter;
import persistencia.ClaseCRUD;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("TELEFONO")
@Getter
public class Telefono extends MedioDeContacto{
    @Column(name = "medi_contacto")
    @Setter private String numero;

    public Telefono(String numero){
        super(TipoMedioContacto.TELEFONO);
        this.numero = numero;
        ClaseCRUD.getInstance().add(this);
    }

    public Telefono() {
        super(TipoMedioContacto.TELEFONO);
    }
}
