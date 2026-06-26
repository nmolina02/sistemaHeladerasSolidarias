package receptorDeJSON.Receptores;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.javalin.Javalin;
import persona.personas.PersonaFisica;
import persona.roles.personaEnSituacionVulnerable.PersonaEnSituacionVulnerable;
import repository.RepositoryPersonasVulnerables;

public class CargaDePersonasVulnerables {
    public static void ejecutarCargaDePersonasVulnerables(Javalin app) {
        app.post("/listadoPersonasVulnerables", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            JsonObject jsonListPersonasVulnerables = new JsonObject();

            for (PersonaEnSituacionVulnerable personaEnSituacionVulnerable : RepositoryPersonasVulnerables.getInstance().getPersonasVulnerables()) {
                JsonObject jsonPersonaVulnerable = new JsonObject();
                int posicionPersonaVulnerable = RepositoryPersonasVulnerables.getInstance().getPersonasVulnerables().indexOf(personaEnSituacionVulnerable);
                jsonPersonaVulnerable.addProperty("id", String.valueOf(posicionPersonaVulnerable + 1));
                PersonaFisica personaFisica = (PersonaFisica) personaEnSituacionVulnerable.getPersona();
                jsonPersonaVulnerable.addProperty("nombre", personaFisica.getNombre());
                jsonPersonaVulnerable.addProperty("apellido", personaFisica.getApellido());
                if (personaFisica.getDocumento() == null) {
                    jsonPersonaVulnerable.addProperty("dni", "Indefinido");
                    jsonListPersonasVulnerables.add(String.valueOf(posicionPersonaVulnerable + 1), jsonPersonaVulnerable);
                    continue;
                }
                String numeroDocumento = personaFisica.getDocumento().getNumero();
                String tipoDocumento = String.valueOf(personaFisica.getDocumento().getTipoDocumentacion());
                String documento = numeroDocumento + " (" + tipoDocumento + ")";
                jsonPersonaVulnerable.addProperty("dni", documento);
                jsonListPersonasVulnerables.add(String.valueOf(posicionPersonaVulnerable + 1), jsonPersonaVulnerable);
            }
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(jsonListPersonasVulnerables));
        });
    }
}