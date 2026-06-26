package repository;

import localizacion.Ubicacion;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class RepositoryUbicacion {
    private static RepositoryUbicacion instancia = null;
    @Setter private List<Ubicacion> ubicaciones;

    private RepositoryUbicacion() {
        this.ubicaciones = Collections.synchronizedList(new ArrayList<>());
    }

    public static synchronized RepositoryUbicacion getInstance() {
        if(instancia == null){
            instancia = new RepositoryUbicacion();
        }
        return instancia;
    }

    public synchronized void agregarUbicacion(Ubicacion ubicacion) {
        ubicaciones.add(ubicacion);
    }
}