package Heladera.incidente;

import Heladera.Heladera;
import java.time.LocalDateTime;

import Heladera.incidente.Alerta.TipoGravedad;
import lombok.Getter;
import persistencia.ClaseCRUD;
import persona.roles.colaborador.Colaborador;
import reportes.GeneradorDeReportes;

import javax.persistence.*;

@Entity
@Table(name = "falla_tecnica")
@Getter
public class FallaTecnica extends Incidente {
    @ManyToOne
    @JoinColumn(name = "rol_id")
    private Colaborador colaborador;

    @Column(name = "fall_foto")
    private String foto;

    public FallaTecnica(Heladera heladera, Colaborador colaborador, String descripcion, String foto, TipoGravedad gravedad) {
        super(LocalDateTime.now(), heladera, descripcion);
        this.colaborador = colaborador;
        this.foto = foto;
        this.gravedad = gravedad;
        ClaseCRUD.getInstance().add(this);
    }

    public FallaTecnica() {
        super();
    }

    @Override
    public void determinarGravedad(){};
}