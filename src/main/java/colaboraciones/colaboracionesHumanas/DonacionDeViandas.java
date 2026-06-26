package colaboraciones.colaboracionesHumanas;

import Heladera.Heladera;
import Heladera.Vianda;
import colaboraciones.Colaboracion;
import colaboraciones.TipoColaboracion;
import lombok.Getter;
import persistencia.ClaseCRUD;

import javax.persistence.*;

@Entity
@Table(name = "donacion_de_viandas")
@Getter
public class DonacionDeViandas extends Colaboracion {
    @Transient
    private Vianda vianda;

    @ManyToOne
    @JoinColumn(name = "hela_id")
    private Heladera heladera;

    public DonacionDeViandas(Vianda vianda, Heladera heladera){
        super(TipoColaboracion.DONACION_DE_VIANDAS);
        this.vianda = vianda;
        this.heladera = heladera;
        if (vianda != null)
            vianda.setHeladera(heladera);
        ClaseCRUD.getInstance().add(this);
    }

    public DonacionDeViandas(){}

    @Override
    public void ejecutar_colaboracion(){
        System.out.println("Se ha realizado la colaboracion donar vianda");
        if(heladera.getGestorDeViandas().getViandas().size() == heladera.getModelo().getCapacidadMaxima()){
            throw new RuntimeException("La heladera seleccionada ya alcanzó su capacidad maxima");
        }else {
            heladera.getGestorDeViandas().agregar_vianda(vianda);
            vianda.agregar_a(heladera);
            heladera.getGestorDeViandas().setViandasUltimoRegistro(heladera.getGestorDeViandas().getViandas().size());
        }
    }

    @Override
    public double calcular_puntos(){
        return 1;
    }
}
