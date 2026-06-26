package receptorDeJSON.Receptores;

import Heladera.Heladera;
import colaboraciones.TipoColaboracion;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import io.javalin.http.Context;
import repository.RepositoryHeladera;

public class ViandasReceptor {
    public static void ejecutarViandasReceptor(Javalin app) {
        app.post("/viandas", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
            enviarCantidadViandas(ctx, jsonObject);
        });
    }

    // Método para enviar la cantidad de viandas de una heladera
    private static void enviarCantidadViandas(Context ctx, JsonObject jsonObject) {
        TipoColaboracion tipoColaboracion = TipoColaboracion.valueOf(jsonObject.get("tipoColaboracion").getAsString());
        String nombreHeladera = jsonObject.get("heladera").getAsString();
        Heladera heladera = RepositoryHeladera.getInstance().getHeladerasDelSistema().stream()
                .filter(h -> h.getNombreHeladera().equals(nombreHeladera))
                .findFirst()
                .orElse(null);

        if (heladera == null) {
            ctx.status(404).result("Heladera no encontrada");
        } else {
            int cantidadViandas = heladera.getGestorDeViandas().getViandas().size();
            JsonObject responseJson = new JsonObject();

            switch (tipoColaboracion) {
                case DONACION_DE_VIANDAS:
                    responseJson.addProperty("heladera", heladera.getNombreHeladera());
                    responseJson.addProperty("cantidadViandas", cantidadViandas);
                    break;
                case DISTRIBUCION_DE_VIANDAS:
                    responseJson.addProperty("heladera", heladera.getNombreHeladera());
                    responseJson.addProperty("cantidadViandas", cantidadViandas);
                    String nombreHeladera2 = jsonObject.get("heladera2").getAsString();
                    Heladera heladera2 = RepositoryHeladera.getInstance().getHeladerasDelSistema().stream()
                            .filter(h -> h.getNombreHeladera().equals(nombreHeladera2))
                            .findFirst()
                            .orElse(null);
                    responseJson.addProperty("heladera", heladera.getNombreHeladera());
                    responseJson.addProperty("cantidadViandas", cantidadViandas);
                    responseJson.addProperty("heladera2", heladera2.getNombreHeladera());
                    responseJson.addProperty("cantidadViandas2", cantidadViandas);
                    break;
            }
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(responseJson));
        }
    }
}
