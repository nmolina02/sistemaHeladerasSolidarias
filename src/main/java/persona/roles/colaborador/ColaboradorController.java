package persona.roles.colaborador;

import Heladera.Heladera;
import colaboraciones.colaboracionesHumanas.DonacionDeViandas;
import localizacion.APIUbicacion.Punto;
import colaboraciones.Colaboracion;
import colaboraciones.TipoColaboracion;
import colaboraciones.colaboracionesHumanas.DistribucionDeViandas;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ColaboradorController {
    private Colaborador colaborador;
    private List<Punto> zonasFrecuentes = new ArrayList<>();

    public ColaboradorController(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public void calcular_zonas_frecuentes(){ //este metodo lo vamos a utilizar para los colaboradores que sean personas fisicas
        List<Colaboracion> colaboraciones = colaborador.getColaboracionesRealizadas().stream()
                .filter(colaboracion -> colaboracion.getTipoColaboracion() == TipoColaboracion.DISTRIBUCION_DE_VIANDAS || colaboracion.getTipoColaboracion() == TipoColaboracion.DONACION_DE_VIANDAS)
                .collect(Collectors.toList());

        for (Colaboracion colaboracion : colaboraciones){
            if (colaboracion.getTipoColaboracion() == TipoColaboracion.DONACION_DE_VIANDAS){
                DonacionDeViandas donacionDeViandas = (DonacionDeViandas) colaboracion;
                if (donacionDeViandas.getHeladera() != null){
                    if (donacionDeViandas.getHeladera().getDireccion() != null){
                        this.agregar_punto_frecuente(donacionDeViandas.getHeladera());
                    }
                }
            } else {
                DistribucionDeViandas distribucionDeViandas = (DistribucionDeViandas) colaboracion;
                if (distribucionDeViandas.getHeladeraOrigen() != null ||
                        distribucionDeViandas.getHeladeraDestino() != null){
                    this.agregar_punto_frecuente(distribucionDeViandas.getHeladeraOrigen());
                    this.agregar_punto_frecuente(distribucionDeViandas.getHeladeraDestino());
                }
                }
        }
    }

    private void agregar_punto_frecuente(Heladera heladera){
        Punto puntoFrecuente = heladera.getController().calcular_punto_ubicacion();
        this.zonasFrecuentes.add(puntoFrecuente);
    }
}
