package colaboraciones;

import lombok.Getter;
import lombok.Setter;
import persona.roles.colaborador.Colaborador;
import repository.RepositoryColaboracion;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "colaboracion")
public abstract class Colaboracion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "colab_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rol_id")
    @Setter private Colaborador colaborador;

    @Column(name = "colab_tipo")
    @Enumerated(EnumType.STRING)
    @Getter private TipoColaboracion tipoColaboracion;

    @Column(name = "colab_fecha")
    @Getter private LocalDateTime fechaDeEjecucion;

    public Colaboracion(TipoColaboracion tipoColaboracion){
        this.tipoColaboracion = tipoColaboracion;
        this.fechaDeEjecucion = LocalDateTime.now();
        RepositoryColaboracion.getInstance().agregarColaboracion(this);
    }

    public Colaboracion(){}

    public abstract void ejecutar_colaboracion();
    public abstract double calcular_puntos();
}
