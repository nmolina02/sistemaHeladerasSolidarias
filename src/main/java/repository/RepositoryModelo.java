package repository;

import Heladera.Modelo;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class RepositoryModelo {
    private static RepositoryModelo instancia = null;
    @Setter private List<Modelo> modelos;

    private RepositoryModelo() {
        this.modelos = Collections.synchronizedList(new ArrayList<>());
    }

    public static synchronized RepositoryModelo getInstance() {
        if(instancia == null){
            instancia = new RepositoryModelo();
        }
        return instancia;
    }

    public synchronized void agregarModelo(Modelo modelo) {
        modelos.add(modelo);
    }
}