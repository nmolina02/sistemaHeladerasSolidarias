package repository;

import colaboraciones.Colaboracion;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class RepositoryColaboracion {
    private static RepositoryColaboracion instancia = null;
    @Setter private List<Colaboracion> colaboraciones;

    private RepositoryColaboracion() {
        this.colaboraciones = Collections.synchronizedList(new ArrayList<>());
    }

    public static synchronized RepositoryColaboracion getInstance() {
        if(instancia == null){
            instancia = new RepositoryColaboracion();
        }
        return instancia;
    }

    public synchronized void agregarColaboracion(Colaboracion colaboracion) {
        colaboraciones.add(colaboracion);
    }
}