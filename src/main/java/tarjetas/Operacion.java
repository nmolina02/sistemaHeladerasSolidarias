package tarjetas;
import Heladera.Heladera;
import lombok.Getter;
import persistencia.ClaseCRUD;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "operacion")
@Getter
public class Operacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oper_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hela_id")
    private Heladera heladera;

    @Column(name = "oper_horario_apertura")
    private LocalDate fechaHoraApertura;

    @ManyToOne
    @JoinColumn(name = "tarj_id")
    private TarjetaColaborador tarjetaColaborador;

    public Operacion(Heladera heladera, LocalDate fechaHoraApertura){
        this.heladera = heladera;
        this.fechaHoraApertura = fechaHoraApertura;
        ClaseCRUD.getInstance().add(this);
    }

    public Operacion() {}
}
