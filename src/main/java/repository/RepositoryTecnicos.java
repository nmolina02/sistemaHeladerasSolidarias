package repository;

import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import persona.roles.tecnico.Tecnico;

import java.util.List;
@Getter
@Setter
public class RepositoryTecnicos {
    private List<Tecnico> tecnicos;
    private static RepositoryTecnicos instancia = null;

    // Constructor privado para evitar instanciación directa
    private RepositoryTecnicos() {
        this.tecnicos = new ArrayList<>();
    }

    // Método estático para obtener la instancia única
    public static RepositoryTecnicos getInstance() {
        if (instancia == null) {
            instancia = new RepositoryTecnicos();
        }
        return instancia;
    }
}