package suscripciones;

import Heladera.Heladera;
import Heladera.EstadoHeladera;
import lombok.Getter;
import lombok.Setter;
import persona.roles.colaborador.OpcionesSuscripciones.OpcionSuscripcion;
import persona.roles.colaborador.OpcionesSuscripciones.OpcionesSuscripcion;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GestorSuscripcionesHeladeras {
    private static GestorSuscripcionesHeladeras instancia = null;
    @Setter private List<Suscripcion> suscripciones = new ArrayList<>();

    private GestorSuscripcionesHeladeras() {}

    public static GestorSuscripcionesHeladeras getInstance() {
        if(instancia == null)
            instancia = new GestorSuscripcionesHeladeras();
        return instancia;
    }

    public void recibir_aviso(Heladera heladera) {
        for (Suscripcion suscripcion : this.suscripciones) {
            for (OpcionSuscripcion opcion : suscripcion.getColaborador().getGestorSuscripciones().getOpcionesSuscripcion()) {
                if (suscripcion.getHeladeras().contains(heladera)){
                    if (opcion.getOpcion().equals(OpcionesSuscripcion.CANT_VIANDAS_DISP) &&
                            opcion.getValor() == heladera.getGestorDeViandas().getViandas().size()) {
                        suscripcion.notificar_suscripcion();
                        break;
                    }
                    else if (opcion.getOpcion().equals(OpcionesSuscripcion.CANT_VIANDAS_PARA_LLENAR) && opcion.getValor() == heladera.getGestorDeViandas().capacidad_disponible()) {
                        suscripcion.notificar_suscripcion();
                        break;
                    }
                    else if (opcion.getOpcion().equals(OpcionesSuscripcion.DESPERFECTO_HELADERA)
                            && heladera.getEstadoHeladera().equals(EstadoHeladera.DE_BAJA)) {
                        suscripcion.notificar_suscripcion();
                        break;
                    }
                }
            }
        }
    }
}
