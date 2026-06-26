package colaboraciones.colaboracionesHumanas;

import Heladera.Heladera;
import colaboraciones.Colaboracion;
import Heladera.Vianda;
import colaboraciones.TipoColaboracion;
import lombok.Getter;
import persistencia.ClaseCRUD;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "distribucion_de_viandas")
@Getter
public class DistribucionDeViandas extends Colaboracion {
    @ManyToOne
    @JoinColumn(name = "hela_origen_id")
    private Heladera heladeraOrigen;

    @ManyToOne
    @JoinColumn(name = "hela_destino_id")
    private Heladera heladeraDestino;

    @Column(name = "dist_cantidad_viandas")
    private int cantidadDeViandas = 0;

    @Column(name = "dist_motivo")
    @Enumerated(EnumType.STRING)
    private MotivoDistribucion motivoDistribucion;

    @Column(name = "dist_fecha")
    private LocalDate fecha;

    public DistribucionDeViandas(Heladera heladeraOrigen, Heladera heladeraDestino, int cantidadDeViandas, MotivoDistribucion motivoDistribucion, LocalDate fecha){
        super(TipoColaboracion.DISTRIBUCION_DE_VIANDAS);
        this.heladeraOrigen = heladeraOrigen;
        this.heladeraDestino = heladeraDestino;
        this.cantidadDeViandas = cantidadDeViandas;
        this.motivoDistribucion = motivoDistribucion;
        this.fecha = fecha;
        ClaseCRUD.getInstance().add(this);
    }

    public DistribucionDeViandas(){}

    private void intercambiarViandas(){
        List<Vianda> viandasOrigen = heladeraOrigen.getGestorDeViandas().getViandas();
        List<Vianda> viandasDestino = heladeraDestino.getGestorDeViandas().getViandas();

        for (int i = 0; i < this.cantidadDeViandas; i++) {
            Vianda vianda = viandasOrigen.remove(0);
            viandasDestino.add(vianda);
            vianda.setHeladera(heladeraDestino);
        }

        heladeraOrigen.getGestorDeViandas().setViandas(viandasOrigen);
        heladeraDestino.getGestorDeViandas().setViandas(viandasDestino);

        heladeraOrigen.getGestorDeViandas().setViandasUltimoRegistro(heladeraOrigen.getGestorDeViandas().getViandas().size());
        heladeraDestino.getGestorDeViandas().setViandasUltimoRegistro(heladeraDestino.getGestorDeViandas().getViandas().size());
    }

    @Override
    public void ejecutar_colaboracion() {
        intercambiarViandas();
    }

    @Override
    public double calcular_puntos(){
        return this.cantidadDeViandas;
    }
}
