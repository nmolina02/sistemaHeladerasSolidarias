package colaboraciones.colaboracionesJuridicas;

import colaboraciones.Colaboracion;
import colaboraciones.TipoColaboracion;
import lombok.Getter;
import persistencia.ClaseCRUD;
import premios.PremioColaboracion;

import javax.persistence.*;

@Entity
@Table(name = "ofrecer_producto_reconocimiento")
@Getter
public class OfrecerProductoReconocimiento extends Colaboracion {

    @OneToOne
    @JoinColumn(name = "prem_id")
    private PremioColaboracion producto;

    public OfrecerProductoReconocimiento(PremioColaboracion producto){
        super(TipoColaboracion.OFRECER_PRODUCTO_RECONOCIMIENTO);
        this.producto = producto;
        ClaseCRUD.getInstance().add(this);
    }

    public OfrecerProductoReconocimiento(){}

    @Override
    public void ejecutar_colaboracion(){
        //habria que tener en la base de datos o en el main una lista de premios
        //y se agregaria a esa lista
        System.out.println("Se ejeucto la colab");
    }

    @Override
    public double calcular_puntos(){
        return 0;
    }
}
