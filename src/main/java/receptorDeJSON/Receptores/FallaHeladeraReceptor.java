package receptorDeJSON.Receptores;

import Heladera.Heladera;
import Heladera.incidente.Alerta.TipoGravedad;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import persona.personas.Persona;
import persona.roles.colaborador.Colaborador;
import persona.roles.Usuario;
import repository.RepositoryColaborador;
import repository.RepositoryHeladera;
import repository.RepositoryUsuario;

public class FallaHeladeraReceptor {
    public static void ejecutarFallaHeladeraReceptor(Javalin app) {
        app.post("/reportarFalla", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            // Parsear el JSON a un objeto genérico usando Gson o JsonParser
            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();

            JsonObject jsonColaborador = jsonObject.get("colaborador").getAsJsonObject();
            Usuario usuarioColaborador = RepositoryUsuario.getInstance().getUsuarios().get(jsonColaborador.get("userId").getAsInt() - 1);
            Persona personaColaborador = usuarioColaborador.getPersona();
            Colaborador colaborador = RepositoryColaborador.getInstance().getColaboradoresDelSistema().stream()
                    .filter(c -> c.getPersona().equals(personaColaborador))
                    .findFirst()
                    .orElse(null);

            String nombreHeladera = jsonObject.get("heladera").getAsString();
            Heladera heladera = RepositoryHeladera.getInstance().getHeladerasDelSistema().stream()
                    .filter(h -> h.getNombreHeladera().equals(nombreHeladera))
                    .findFirst()
                    .orElse(null);

            String mensajeRespuesta = "";

            if (colaborador != null && heladera != null) {
                TipoGravedad tipoGravedad = TipoGravedad.valueOf(jsonObject.get("tipoGravedad").getAsString());
                String descripcion = jsonObject.get("descripcion").getAsString();
                String imagen = "";
                if (!jsonObject.get("imagen").getAsString().isEmpty()) {
                    imagen = jsonObject.get("imagen").getAsString();
                }

                colaborador.reportar_falla_tecnica(heladera, descripcion, imagen, tipoGravedad);

                System.out.println("Falla reportada correctamente");
                mensajeRespuesta = "Falla reportada correctamente";
            } else {
                System.out.println("No se encontró el colaborador");
                mensajeRespuesta = "No se encontró el colaborador";
            }
            // Respuesta al cliente
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(mensajeRespuesta));
        });
    }
}
