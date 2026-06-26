package repository;

import localizacion.Ciudad;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class RepositoryCiudad {
    private static RepositoryCiudad instancia = null;
    @Setter private List<Ciudad> ciudades;

    private RepositoryCiudad() {
        this.ciudades = Collections.synchronizedList(new ArrayList<>());
    }

    public static synchronized RepositoryCiudad getInstance() {
        if(instancia == null){
            instancia = new RepositoryCiudad();
        }
        return instancia;
    }

    public synchronized void agregarCiudad(Ciudad ciudad) {
        ciudades.add(ciudad);
    }
}