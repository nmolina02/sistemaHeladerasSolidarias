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
@Table(name = "alerta_conexion")
@Getter
public class AlertaConexion extends Incidente {
    @Column(name = "aler_conexion_ultima_temperatura")
    private Float ultimaTemperatura;

    public AlertaConexion(LocalDateTime fechaIncidente, Heladera heladera, String descripcion, Float ultimaTemperatura){
        super(fechaIncidente, heladera, descripcion);
        this.ultimaTemperatura = ultimaTemperatura;
        ClaseCRUD.getInstance().add(this);
    }

    public AlertaConexion() {
        super();
    }

    @Override
    public void determinarGravedad() {
        setGravedad(TipoGravedad.ALTA);
    }
}
