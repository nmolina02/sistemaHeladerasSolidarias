package Heladera.incidente.Alerta;

import Heladera.Heladera;
import Heladera.incidente.GestorIncidentes;
import Heladera.incidente.Incidente;
import lombok.Getter;
import persistencia.ClaseCRUD;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerta_fraude")
@Getter
public class AlertaFraude extends Incidente {
    @Column(name = "aler_fraude_viandas_atracadas")
    private int viandasAtracadas;

    public AlertaFraude(LocalDateTime fechaIncidente, Heladera heladera, String descripcion){
        super(fechaIncidente, heladera, descripcion);
        viandasAtracadas = this.getHeladera().getGestorDeViandas().estimar_cantidad_viandas_atracadas();
        ClaseCRUD.getInstance().add(this);
    }

    public AlertaFraude() {
        super();
    }

    @Override
    public void determinarGravedad() {
        float promedioPerdida = this.calcular_promedio_perdida();
        if (promedioPerdida > 50.0f){
            this.setGravedad(TipoGravedad.ALTA);
        }else if (promedioPerdida > 25.0f){
            this.setGravedad(TipoGravedad.MEDIA);
        }else this.setGravedad(TipoGravedad.BAJA);
    }

    public Float calcular_promedio_perdida(){
        int viandasAntesAtraco = this.getHeladera().getGestorDeViandas().getViandas().size();
        int viandasRestantes = viandasAntesAtraco - this.getHeladera().getGestorDeViandas().estimar_cantidad_viandas_atracadas();
        return (viandasRestantes * 100.0f) / viandasAntesAtraco;
    }
}
