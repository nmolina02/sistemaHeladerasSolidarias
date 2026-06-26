package receptorDeJSON.Receptores;

import Heladera.VisitaHeladera;
import Heladera.incidente.Alerta.AlertaConexion;
import Heladera.incidente.Alerta.AlertaFraude;
import Heladera.incidente.Alerta.AlertaTemperatura;
import Heladera.incidente.FallaTecnica;
import Heladera.incidente.Incidente;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import persona.personas.Persona;
import persona.personas.PersonaFisica;
import persona.roles.Usuario;
import persona.roles.tecnico.Tecnico;
import repository.RepositoryIncidente;
import repository.RepositoryTecnicos;
import repository.RepositoryUsuario;
import repository.RepositoryVisitaHeladera;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class SolicitudDetalleVisitaHeladera {
    public static void ejecutarSolicitudDetalleVisitaHeladera(Javalin app) {
        app.post("/detalleVisitaHeladera", ctx -> {
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
            List<VisitaHeladera> visitasHeladeraIncidente = RepositoryVisitaHeladera.getInstance().getVisitas().stream()
                    .filter(visita -> visita.getIncidente().equals(incidente))
                    .collect(Collectors.toList());
            VisitaHeladera visitaHeladera = visitasHeladeraIncidente.get(jsonObject.get("visita").getAsInt() - 1);

            JsonObject respuesta = new JsonObject();
            respuesta.addProperty("id", jsonObject.get("visita").getAsInt());
            respuesta.addProperty("fecha", visitaHeladera.getFechaDeVisita().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
            respuesta.addProperty("descripcion", visitaHeladera.getDescripcionVisita());
            if (visitaHeladera.getFotoVisita().isEmpty()) {
                respuesta.addProperty("foto", "heladeraSinImagen.jpg");
            } else {
                respuesta.addProperty("foto", visitaHeladera.getFotoVisita());
            }
            respuesta.addProperty("heladera", incidente.getHeladera().getNombreHeladera());
            respuesta.addProperty("incidenteId", RepositoryIncidente.getInstance().getIncidentes().indexOf(incidente) + 1);

            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(respuesta));
        });
    }
}