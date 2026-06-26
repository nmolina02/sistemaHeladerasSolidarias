package repository;

import lombok.Getter;
import lombok.Setter;
import persona.roles.personaEnSituacionVulnerable.PersonaEnSituacionVulnerable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RepositoryPersonasVulnerables {
   @Setter private List<PersonaEnSituacionVulnerable> personasVulnerables;
   private static RepositoryPersonasVulnerables instance = null;

    private RepositoryPersonasVulnerables(){
        this.personasVulnerables = new ArrayList<>();
    }

    public static RepositoryPersonasVulnerables getInstance() {
        if (instance == null) {
            instance = new RepositoryPersonasVulnerables();
        }
        return instance;
    }

    public void agregarPersonaVulnerable(PersonaEnSituacionVulnerable personasVulnerables) {
       this.personasVulnerables.add(personasVulnerables);
    }
}
