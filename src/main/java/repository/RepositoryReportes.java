package repository;
import lombok.Getter;
import lombok.Setter;
import reportes.Reporte;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RepositoryReportes {
    private static RepositoryReportes instancia = null;
    @Setter private List<Reporte> reportesHistoricos = new ArrayList<>();

    private RepositoryReportes(){}

    public static RepositoryReportes getInstance() {
        if(instancia == null)
            instancia = new RepositoryReportes();
        return instancia;
    }
}