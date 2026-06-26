package Heladera.controladoresHeladera;

import Heladera.Heladera;
import lombok.Getter;
import persona.roles.colaborador.Colaborador;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class GestorDeSolicitudes {
    private Heladera heladera;
    private List<Solicitud> solicitudesPendientes = new ArrayList<>();

    public GestorDeSolicitudes(Heladera heladera){
        this.heladera = heladera;
    }

    public Boolean existe_solicitud_en_tiempo(Colaborador colaborador){
        for (Solicitud solicitud : this.getSolicitudesPendientes()){
            if (solicitud.getSolicitante() == colaborador && solicitud.esta_dentro_del_horario(LocalDateTime.now())){
                return true;
            }
        }
        return false;
    }

    public void generar_solicitud_apertura(Colaborador colaborador){
        Solicitud solicitud = new Solicitud(colaborador, heladera, 3);
        this.getSolicitudesPendientes().add(solicitud);
    }
}
