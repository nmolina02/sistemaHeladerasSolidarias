package receptorDeJSON.Receptores;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.javalin.Javalin;
import premios.PremioColaboracion;
import repository.RepositoryPremios;

public class SolicitudPremiosReceptor {
    public static void ejecutarSolicitudPremiosReceptor(Javalin app) {
        app.post("/premios", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            if (RepositoryPremios.getInstance().getPremios().isEmpty()) {
                ctx.contentType("application/json");
                ctx.result(new Gson().toJson("No hay premios cargados"));
            }
            else {
                JsonObject premios = new JsonObject();
                for (PremioColaboracion premio : RepositoryPremios.getInstance().getPremios()) {
                    JsonObject premioJson = new JsonObject();
                    premioJson.addProperty("id", premio.getId());
                    premioJson.addProperty("nombre", premio.getNombre());
                    premioJson.addProperty("puntos_necesarios", premio.getPuntos_necesarios());
                    premioJson.addProperty("imagen", premio.getImagen());
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
