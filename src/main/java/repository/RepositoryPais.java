package repository;

import localizacion.Pais;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class RepositoryPais {
    private static RepositoryPais instancia = null;
    @Setter private List<Pais> paises;

    private RepositoryPais() {
        this.paises = Collections.synchronizedList(new ArrayList<>());
    }

    public static synchronized RepositoryPais getInstance() {
        if(instancia == null){
            instancia = new RepositoryPais();
        }
        return instancia;
    }

    public synchronized void agregarPais(Pais pais) {
        paises.add(pais);
    }
}