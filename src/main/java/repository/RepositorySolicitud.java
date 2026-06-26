package repository;

import lombok.Getter;
import java.util.ArrayList;
import java.util.List;


import Heladera.controladoresHeladera.Solicitud;
@Getter
public class RepositorySolicitud {
    private static RepositorySolicitud instancia = null;
    private List<Solicitud> solicitudes = new ArrayList<Solicitud>();

    private RepositorySolicitud() {}

    public static RepositorySolicitud getInstance() {
        if(instancia == null)
            instancia = new RepositorySolicitud();
        return instancia;
    }

    public void addSolicitud(Solicitud solicitud) {
        solicitudes.add(solicitud);
    }
}