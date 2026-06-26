package reportes;

import Heladera.Heladera;
import lombok.Getter;
import lombok.Setter;
import persistencia.ClaseCRUD;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_heladera")
@Getter
public class MovimientosHeladera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movi_hela_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "repo_id")
    @Setter private ReporteMovimientosHeladera reporte;

    @ManyToOne
    @JoinColumn(name = "hela_id")
    private Heladera heladera;

    @Column(name = "movi_hela_viandas_ingresadas")
    public int cantViandasColocadas = 0;

    @Column(name = "movi_hela_viandas_retiradas")
    public int cantViandasRetiradas = 0;

    @Column(name = "movi_hela_horario")
    LocalDateTime horario;

    public MovimientosHeladera(Heladera heladera, int movimientos){
        this.heladera = heladera;
        if(movimientos<0){
            this.cantViandasRetiradas -= movimientos;
        }else{
            this.cantViandasColocadas += movimientos;
        }
        this.horario = LocalDateTime.now();
        ClaseCRUD.getInstance().add(this);
    }

    public MovimientosHeladera() {}
}
