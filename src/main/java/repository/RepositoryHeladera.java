package repository;

import Heladera.Heladera;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RepositoryHeladera {
    private static RepositoryHeladera instancia = null;
    @Setter private List<Heladera> heladerasDelSistema = new ArrayList<Heladera>();

    private RepositoryHeladera() {}

    public static RepositoryHeladera getInstance() {
        if(instancia == null)
            instancia = new RepositoryHeladera();
        return instancia;
    }
}
