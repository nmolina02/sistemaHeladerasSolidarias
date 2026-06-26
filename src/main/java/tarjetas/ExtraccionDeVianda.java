package tarjetas;

import Heladera.Vianda;
import Heladera.Heladera;
import lombok.Getter;
import persistencia.ClaseCRUD;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "extraccion_de_vianda")
@Getter
public class ExtraccionDeVianda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "extr_id")
    private Long id;

    @Column(name = "extr_fecha")
    private LocalDate fecha;

    @ManyToOne
    @JoinColumn(name = "hela_id")
    private Heladera heladera;

    @ManyToOne
    @JoinColumn(name = "tarj_id")
    private TarjetaPersonaVulnerable tarjeta;

    //tener cuidado con el formato al momento de insertar una fecha
    public ExtraccionDeVianda(LocalDate fecha, Heladera heladera) {
        this.fecha = fecha;
        this.heladera = heladera;
        ClaseCRUD.getInstance().add(this);
    }

    public ExtraccionDeVianda() {}

    public void extraer_vianda(Vianda vianda) {
        heladera.getGestorDeViandas().sacar_vianda(vianda);
    }
}