package repository;

import java.util.ArrayList;
import java.util.List;

import Heladera.VisitaHeladera;
import lombok.Getter;
import lombok.Setter;

@Getter
public class RepositoryVisitaHeladera {
    private static RepositoryVisitaHeladera instancia;
    @Setter private List<VisitaHeladera> visitas;

    private RepositoryVisitaHeladera() {
        this.visitas = new ArrayList<>();
    }

    public static RepositoryVisitaHeladera getInstance() {
        if (instancia == null) {
            instancia = new RepositoryVisitaHeladera();
        }
        return instancia;
    }
}