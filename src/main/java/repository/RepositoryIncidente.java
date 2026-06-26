package repository;

import java.util.ArrayList;

import Heladera.incidente.Incidente;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class RepositoryIncidente {
    private List<Incidente> incidentes;

    // Instancia única de la clase
    private static RepositoryIncidente instancia;

    // Constructor privado para evitar instanciación directa
    private RepositoryIncidente() {
        this.incidentes = new ArrayList<>();
    }

    // Método estático para obtener la instancia única
    public static RepositoryIncidente getInstance() {
        if (instancia == null) {
            instancia = new RepositoryIncidente();
        }
        return instancia;
    }
}