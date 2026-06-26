package receptorDeJSON.Receptores;

import Heladera.incidente.Incidente;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import persona.personas.Persona;
import persona.roles.Usuario;
import persona.roles.tecnico.Tecnico;
import repository.RepositoryIncidente;
import repository.RepositoryTecnicos;
import repository.RepositoryUsuario;

import java.util.List;
import java.util.stream.Collectors;

public class RealizarVisitaHeladera {
    public static void ejecutarRealizarVisitaHeladera(Javalin app) {
        app.post("/realizarVisitaHeladera", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            // Parsear el JSON a un objeto genérico usando Gson o JsonParser
            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
            JsonObject usuarioRecibido = jsonObject.get("usuario").getAsJsonObject();

            Usuario usuarioTecnico = RepositoryUsuario.getInstance().getUsuarios().get(usuarioRecibido.get("userId").getAsInt() - 1);
            Persona personaTecnico = usuarioTecnico.getPersona();
            Tecnico tecnico = RepositoryTecnicos.getInstance().getTecnicos().stream()
                    .filter(c -> c.getPersona().equals(personaTecnico))
                    .findFirst()
                    .orElse(null);

            List<Incidente> incidentesPendientes = RepositoryIncidente.getInstance().getIncidentes().stream()
                    .filter(incidente -> !incidente.isSolucionado() && incidente.getTecnico().equals(tecnico))
                    .collect(Collectors.toList());

            Incidente incidente = incidentesPendientes.get(jsonObject.get("incidente").getAsInt() - 1);
            tecnico.pasar_a_ocupado();
            tecnico.arreglar_heladera(incidente, jsonObject.get("imagen").getAsString());

            ctx.contentType("application/json");
            ctx.result(new Gson().toJson("Visita realizada con éxito"));
        });
    }
}