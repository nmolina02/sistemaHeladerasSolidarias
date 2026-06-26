package receptorDeJSON.Receptores;

import Heladera.Heladera;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.javalin.Javalin;
import repository.RepositoryHeladera;

public class EstadoHeladeraReceptor {
    public static void ejecutarEstadoHeladeraReceptor(Javalin app) {
        app.post("/estadoHeladera", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            if (RepositoryHeladera.getInstance().getHeladerasDelSistema().isEmpty()) {
                ctx.contentType("application/json");
                ctx.result(new Gson().toJson("No hay heladeras cargadas en el sistema"));
            }
            else {
                JsonObject listaHeladeras = new JsonObject();
                for (Heladera heladera : RepositoryHeladera.getInstance().getHeladerasDelSistema()) {
                    if (heladera.getDireccion() == null) {
                        continue;
                    }
                    JsonObject jsonHeladera = new JsonObject();
                    jsonHeladera.addProperty("nombre", heladera.getNombreHeladera());
                    jsonHeladera.addProperty("estado", heladera.getEstadoHeladera().toString());
                    listaHeladeras.add(heladera.getNombreHeladera(), jsonHeladera);
                }

                ctx.contentType("application/json");
                ctx.result(new Gson().toJson(listaHeladeras));
            }
        });
    }
}
