package receptorDeJSON.Receptores;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import persona.roles.colaborador.Colaborador;
import premios.PremioColaboracion;
import persona.roles.Usuario;
import repository.RepositoryColaborador;
import repository.RepositoryPremios;
import repository.RepositoryUsuario;

public class CanjearPremioReceptor {
    public static void ejecutarCanjearPremioReceptor(Javalin app) {
        app.post("/canjeoProducto", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            // Parsear el JSON a un objeto genérico usando Gson o JsonParser
            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
            JsonObject colaboradorRecibido = jsonObject.get("colaborador").getAsJsonObject();
            JsonObject premioRecibido = jsonObject.get("premio").getAsJsonObject();

            Usuario usuarioColaborador = RepositoryUsuario.getInstance().getUsuarios().get(colaboradorRecibido.get("userId").getAsInt() - 1);
            Colaborador colaborador = RepositoryColaborador.getInstance().getColaboradoresDelSistema().stream()
                    .filter(c -> c.getPersona().equals(usuarioColaborador.getPersona()))
                    .findFirst()
                    .orElse(null);

            PremioColaboracion premio = RepositoryPremios.getInstance().getPremios().stream()
                    .filter(p -> p.getId().equals(premioRecibido.get("id").getAsString()))
                    .findFirst()
                    .orElse(null);

            if (colaborador != null && premio != null){
                try {
                    colaborador.canjear_premio(premio);
                } catch (Exception e) {
                    System.out.println("No se pudo canjear el premio: " + e.getMessage());
                    ctx.contentType("application/json");
                    ctx.result(new Gson().toJson("No hay suficientes puntos"));
                    return;
                }
                JsonObject respuesta = new JsonObject();
                respuesta.addProperty("username", usuarioColaborador.getUsername());
                respuesta.addProperty("points", colaborador.getGestorDePuntaje().getPuntosTotales());
                respuesta.addProperty("userImage", usuarioColaborador.getImagen());
                respuesta.addProperty("id", premio.getId());
                System.out.println("respuesta: " + respuesta);
                ctx.contentType("application/json");
                ctx.result(new Gson().toJson(respuesta));
            }
            else {
                ctx.contentType("application/json");
                ctx.result(new Gson().toJson("No se pudo canjear el premio"));
            }
        });
    }
}
