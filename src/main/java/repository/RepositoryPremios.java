package repository;

import lombok.Getter;
import lombok.Setter;
import premios.PremioColaboracion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class RepositoryPremios {
    private static RepositoryPremios instancia = null;
    @Setter private List<PremioColaboracion> premios;

    private RepositoryPremios() {
        this.premios = Collections.synchronizedList(new ArrayList<>());
    }

    public static synchronized RepositoryPremios getInstance() {
        if(instancia == null){
            instancia = new RepositoryPremios();
        }
        return instancia;
    }

    public synchronized void addPremio(PremioColaboracion premio) {
        premios.add(premio);
    }
}