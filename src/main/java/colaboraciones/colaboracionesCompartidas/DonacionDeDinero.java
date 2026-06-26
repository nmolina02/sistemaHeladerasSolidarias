package colaboraciones.colaboracionesCompartidas;

import colaboraciones.Colaboracion;
import colaboraciones.TipoColaboracion;
import lombok.Getter;
import persistencia.ClaseCRUD;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "donacion_de_dinero")
@Getter
public class DonacionDeDinero extends Colaboracion {
    @Column(name = "dona_dinero_fecha")
    private LocalDate fechaDeDonacion;

    @Column(name = "dona_dinero_monto")
    private double monto;

    @Column(name = "dona_dinero_frecuencia")
    @Enumerated(EnumType.STRING)
    private Frecuencia frecuencia;

    public DonacionDeDinero(LocalDate fechaDeDonacion, double monto, Frecuencia frecuencia){
        super(TipoColaboracion.DONACION_DE_DINERO);
        this.fechaDeDonacion = fechaDeDonacion;
        this.monto = monto;
        this.frecuencia = frecuencia;
        ClaseCRUD.getInstance().add(this);
    }

    public DonacionDeDinero(){}

    @Override public void ejecutar_colaboracion(){
        System.out.println("Se ha realizado la colaboracion donar dinero");
        //donde se dona??
        //entrega numero 6
    }

    @Override public double calcular_puntos(){
        return this.monto;
    }
}
