package receptorDeJSON.Receptores;

import Heladera.Heladera;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import persona.roles.colaborador.Colaborador;
import persona.roles.Usuario;
import repository.RepositoryColaborador;
import repository.RepositoryHeladera;
import repository.RepositoryUsuario;

public class SolicitudAperturaHeladeraReceptor {
    public static void ejecutarSolicitudAperturaHeladeraReceptor(Javalin app) {
        app.post("/solicitudAperturaHeladera", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            // Parsear el JSON a un objeto genérico usando Gson o JsonParser
            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
            JsonObject colaboradorJson = jsonObject.get("colaborador").getAsJsonObject();
            String nombreHeladera = jsonObject.get("heladera").getAsString();

            Usuario usuarioColaborador = RepositoryUsuario.getInstance().getUsuarios().get(colaboradorJson.get("userId").getAsInt() - 1);
            Colaborador colaborador = RepositoryColaborador.getInstance().getColaboradoresDelSistema().stream()
                    .filter(c -> c.getPersona().equals(usuarioColaborador.getPersona()))
                    .findFirst()
                    .orElse(null);

            Heladera heladera = RepositoryHeladera.getInstance().getHeladerasDelSistema().stream()
                    .filter(h -> h.getNombreHeladera().equals(nombreHeladera))
                    .findFirst()
                    .orElse(null);

            String mensajeRespuesta = "";

            if (colaborador != null && heladera != null) {
                colaborador.solicitar_apertura_heladera(heladera);
                mensajeRespuesta = "Se ha solicitado el acceso a la heladera correctamente";
            } else if (colaborador == null) {
                System.out.println("No se encontró el colaborador");
                mensajeRespuesta = "No se encontró el colaborador";
            } else {
                System.out.println("No se encontró la heladera");
                mensajeRespuesta = "No se encontró la heladera";
            }
            // Respuesta al cliente
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(mensajeRespuesta));
        });
    }
}
