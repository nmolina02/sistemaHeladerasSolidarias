package receptorDeJSON.Receptores;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import persona.roles.Usuario;
import persona.roles.colaborador.Colaborador;
import premios.PremioColaboracion;
import repository.RepositoryColaborador;
import repository.RepositoryPremios;
import repository.RepositoryUsuario;

public class SolicitudPremiosExistentes {
    public static void ejecutarSolicitudPremiosExistentes(Javalin app) {
        app.post("/solicitarPremiosCanjeados", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            // Parsear el JSON a un objeto genérico usando Gson o JsonParser
            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
            Usuario usuario = RepositoryUsuario.getInstance().getUsuarios().get(jsonObject.get("userId").getAsInt() - 1);
            Colaborador colaborador = RepositoryColaborador.getInstance().getColaboradoresDelSistema().stream()
                    .filter(c -> c.getPersona().equals(usuario.getPersona()))
                    .findFirst()
                    .orElse(null);

            if (colaborador.getPremios().isEmpty()) {
                ctx.contentType("application/json");
                ctx.result(new Gson().toJson("No existen premios canjeados"));
            }
            else {
                JsonObject premios = new JsonObject();
                for (PremioColaboracion premio : colaborador.getPremios()) {
                    JsonObject premioJson = new JsonObject();
                    premioJson.addProperty("nombre", premio.getNombre());
                    premioJson.addProperty("puntos_necesarios", premio.getPuntos_necesarios());
                    premioJson.addProperty("categoria", premio.getCategoria().toString());
                    premioJson.addProperty("descripcion", premio.getDescripcion());
                    premios.add(premio.getId(), premioJson);
                }
                // Respuesta al cliente
                ctx.contentType("application/json");
                ctx.result(new Gson().toJson(premios));
            }
        });
    }
}
