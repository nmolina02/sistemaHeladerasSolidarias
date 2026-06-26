package tarjetas;
import lombok.Getter;
import Heladera.Heladera;
import persistencia.ClaseCRUD;
import persona.roles.colaborador.Colaborador;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tarjeta_colaborador")
@Getter
public class TarjetaColaborador extends Tarjeta {
    @OneToOne
    @JoinColumn(name = "rol_id")
    private Colaborador colaborador;

    @OneToMany(mappedBy = "tarjetaColaborador", cascade = CascadeType.ALL)
    private List<Operacion> operacionesRealizadas;

    public TarjetaColaborador(Colaborador unColaborador, String codigo){
        super(unColaborador, codigo);
        this.colaborador = unColaborador;
        this.operacionesRealizadas = new ArrayList<>();
        ClaseCRUD.getInstance().add(this);
    }

    public TarjetaColaborador() {}

    public void registrar_operacion(Heladera unaHeladera){
        LocalDate fechaHoraActual = LocalDate.now();
        Operacion nuevaOperacion = new Operacion(unaHeladera, fechaHoraActual);
        this.operacionesRealizadas.add(nuevaOperacion);
    }
}
