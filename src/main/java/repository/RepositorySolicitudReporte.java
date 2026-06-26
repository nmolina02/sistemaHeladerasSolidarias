package repository;

import lombok.Getter;
import lombok.Setter;
import reportes.SolicitudReporteIndividual;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RepositorySolicitudReporte {
    private static RepositorySolicitudReporte instancia = null;
    @Setter private List<SolicitudReporteIndividual> solicitudReporteIndividuales = new ArrayList<>();

    private RepositorySolicitudReporte(){}

    public static RepositorySolicitudReporte getInstance() {
        if(instancia == null)
            instancia = new RepositorySolicitudReporte();
        return instancia;
    }

    public void agregarSolicitudReporteIndividual(SolicitudReporteIndividual solicitudReporteIndividual) {
        solicitudReporteIndividuales.add(solicitudReporteIndividual);
    }
}