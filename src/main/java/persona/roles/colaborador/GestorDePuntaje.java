package persona.roles.colaborador;

import colaboraciones.Colaboracion;
import lombok.Getter;
import lombok.Setter;
import premios.PremioColaboracion;

@Getter
public class GestorDePuntaje {
    private final Colaborador colaborador;
    @Setter private double puntosTotales;

    public GestorDePuntaje(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public void actualizar_puntaje(Colaboracion colaboracion){
        puntosTotales += calcular_puntos_por_tipo_colaboracion(colaboracion);
    }

    private double calcular_puntos_por_tipo_colaboracion(Colaboracion colaboracion){
        return colaboracion.calcular_puntos() * ConfigCoeficientes.getInstance().getCoeficiente(colaboracion.getTipoColaboracion());
    }

    public boolean puede_realizar_el_canje(PremioColaboracion premio){
        int puntos_necesarios = premio.getPuntos_necesarios();
        premio.setColaboradorAdquirido(this.colaborador);
        colaborador.getPremios().add(premio);
        return puntos_necesarios <= this.puntosTotales;
    }
}
