package reportes;

import Heladera.Heladera;
import lombok.Getter;
import lombok.Setter;
import persistencia.ClaseCRUD;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fallas_heladera")
@Getter
public class FallasHeladera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "falla_hela_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "repo_id")
    @Setter private Reporte reporte;

    @ManyToOne
    @JoinColumn(name = "hela_id")
    private Heladera heladera;

    @Column(name = "fall_hela_cantidad_viandas")
    public int cantidadFallas = 1;

    @Column(name = "fall_hela_horario")
    LocalDateTime horario;

    public FallasHeladera(Heladera heladera){
        this.heladera = heladera;
        this.horario = LocalDateTime.now();
        ClaseCRUD.getInstance().add(this);
    }

    public FallasHeladera() {}
}

