package receptorDeJSON.Receptores;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import persona.roles.Usuario;
import reportes.*;
import repository.RepositoryUsuario;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.UUID;

public class GenerarReporteIndividual {
    public static void ejecutarGenerarReporteIndividual(Javalin app) {
        app.post("/generarReporte", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            // Parsear el JSON a un objeto genérico usando Gson o JsonParser
            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
            JsonObject jsonUsuario = jsonObject.get("solicitante").getAsJsonObject();
            String tipoSolicitud = jsonObject.get("tipoSolicitud").getAsString();

            Usuario usuario = RepositoryUsuario.getInstance().getUsuarios().get(jsonUsuario.get("userId").getAsInt() - 1);
            String randomCode = UUID.randomUUID().toString().substring(0, 8);
            SolicitudReporteIndividual solicitudReporteIndividual = new SolicitudReporteIndividual(usuario, tipoSolicitud, randomCode);

            JsonObject jsonReporte = new JsonObject();
            jsonReporte.addProperty("id", solicitudReporteIndividual.getId());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            jsonReporte.addProperty("fechaSolicitud", solicitudReporteIndividual.getFechaSolicitud().format(formatter));
            LocalDateTime primerDomingo = solicitudReporteIndividual.getFechaSolicitud().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            jsonReporte.addProperty("fechaVencimiento", primerDomingo.format(formatter));
            jsonReporte.addProperty("solicitante", usuario.getUsername());
            TipoReporte tipoReporte = TipoReporte.valueOf(tipoSolicitud);
            switch (tipoReporte) {
                case FALLAS_HELADERA:
                    jsonReporte.addProperty("tipoReporte", "Fallas de Heladera");
                    break;
                case MOVIMIENTOS_HELADERA:
                    jsonReporte.addProperty("tipoReporte", "Movimientos de Heladera");
                    break;
                case VIANDAS_COLABORADOR:
                    jsonReporte.addProperty("tipoReporte", "Viandas Donadas por Colaborador");
                    break;
            }

            // Respuesta al cliente
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(jsonReporte));
        });
    }
}
