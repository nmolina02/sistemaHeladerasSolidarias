package Heladera.incidente.Alerta;


import Heladera.Heladera;
import Heladera.incidente.Incidente;
import lombok.Getter;
import persistencia.ClaseCRUD;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerta_temperatura")
@Getter
public class AlertaTemperatura extends Incidente {
    @Column(name = "aler_temperatura_temperatura")
    private Float temperatura;

    @Column(name = "aler_temperatura_diferencia_temperatura")
    private Float difTemperatura = 0.0f;

    public AlertaTemperatura(LocalDateTime fechaIncidente, Heladera heladera, String descripcion, Float temperatura) {
        super(fechaIncidente, heladera, descripcion);
        this.temperatura = temperatura;
        ClaseCRUD.getInstance().add(this);
    }

    public AlertaTemperatura() {
        super();
    }

    @Override
    public void determinarGravedad() {
        difTemperatura = this.calcular_dif_temperatura();
        if (difTemperatura > 5.0f) {
            setGravedad(TipoGravedad.ALTA);
        } else if (difTemperatura > 2.0f) {
            setGravedad(TipoGravedad.MEDIA);
        } else {
            setGravedad(TipoGravedad.BAJA);
        }
    }

    public Float calcular_dif_temperatura(){
        Boolean hayQueCompararConTempMax = this.getHeladera().getController().getTempMaxUser() + this.getHeladera().getController().getTempMinUser() / 2 < this.getHeladera().getController().getTempActual();

        if(hayQueCompararConTempMax){
            difTemperatura = this.getHeladera().getController().getTempActual() - this.getHeladera().getController().getTempMaxUser();
        }else{
            difTemperatura = this.getHeladera().getController().getTempMinUser() - this.getHeladera().getController().getTempActual();
        }
        return difTemperatura;
    }
}
