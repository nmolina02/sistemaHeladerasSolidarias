package reportes;

import lombok.Getter;
import lombok.Setter;
import persistencia.ClaseCRUD;
import persona.roles.colaborador.Colaborador;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Comparator;

@Entity
@Table(name = "viandas_por_colaborador")
@Getter
public class ViandasPorColaborador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vian_cola_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "repo_id")
    @Setter private Reporte reporte;

    @ManyToOne
    @JoinColumn(name = "cola_id")
    private Colaborador colaboradorHumano;

    @Column(name = "vian_cola_cantidad_viandas")
    public int viandasDonadas;

    @Column(name = "vian_cola_horario")
    LocalDateTime horario;

    public ViandasPorColaborador(Colaborador colaboradorHumano, int viandasDonadas){
        this.colaboradorHumano = colaboradorHumano;
        this.viandasDonadas = viandasDonadas;
        this.horario = LocalDateTime.now();
        ClaseCRUD.getInstance().add(this);
    }

    public ViandasPorColaborador() {}
}
