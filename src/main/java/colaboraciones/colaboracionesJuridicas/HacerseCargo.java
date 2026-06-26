package colaboraciones.colaboracionesJuridicas;

import Heladera.Heladera;
import Heladera.Modelo;
import colaboraciones.Colaboracion;
import colaboraciones.TipoColaboracion;
import localizacion.Ubicacion;
import lombok.Getter;
import persistencia.ClaseCRUD;
import persona.roles.colaborador.Colaborador;

import javax.persistence.*;

@Entity
@Table(name = "hacerse_cargo")
@Getter
public class HacerseCargo extends Colaboracion{
    @OneToOne
    @JoinColumn(name = "hela_id")
    private Heladera heladeraParaHacerseCargo;

    @ManyToOne
    @JoinColumn(name = "ubic_id")
    private Ubicacion ubicacionNueva;

    public HacerseCargo(String nombreHeladera, Modelo modelo, Ubicacion ubicacionNueva, Colaborador colaborador, String imagenHeladera) {
        super(TipoColaboracion.HACERSE_CARGO);
        this.heladeraParaHacerseCargo = new Heladera(ubicacionNueva, nombreHeladera, modelo, colaborador);
        this.heladeraParaHacerseCargo.setImagen(imagenHeladera);
        this.ubicacionNueva = ubicacionNueva;
        ClaseCRUD.getInstance().add(this);
    }

    public HacerseCargo(){}

    @Override
    public void ejecutar_colaboracion() {
        this.heladeraParaHacerseCargo.setDireccion(ubicacionNueva);
    }

    @Override
    public double calcular_puntos() {
        this.heladeraParaHacerseCargo.getController().calcular_meses_activa();
        return this.heladeraParaHacerseCargo.getController().getMesesActiva();
    }
}
