package receptorDeJSON.Receptores;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import persona.roles.Usuario;
import reportes.*;
import repository.RepositoryReportes;
import repository.RepositorySolicitudReporte;
import repository.RepositoryUsuario;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

public class SolicitudReportesExistentesReceptor {
    public static void ejecutarSolicitudReportesExistentesReceptor(Javalin app) {
        app.post("/solicitarReportesExistentes", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            // Parsear el JSON a un objeto genérico usando Gson o JsonParser
            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();

            JsonObject listaReportes = new JsonObject();
            for (Reporte reporte : RepositoryReportes.getInstance().getReportesHistoricos()) {
                JsonObject jsonReporte = new JsonObject();
                jsonReporte.addProperty("id", reporte.getId());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                jsonReporte.addProperty("fechaSolicitud", reporte.getFechaInicial().format(formatter));
                jsonReporte.addProperty("fechaVencimiento", reporte.getFechaFinal().format(formatter));
                jsonReporte.addProperty("solicitante", "Semanal");
                if (reporte instanceof ReporteFallasHeladera) {
                    jsonReporte.addProperty("tipoReporte", "Fallas de Heladera");
                } else if (reporte instanceof ReporteMovimientosHeladera) {
                    jsonReporte.addProperty("tipoReporte", "Movimientos de Heladera");
                } else if (reporte instanceof ReporteViandasPorColaborador) {
                    jsonReporte.addProperty("tipoReporte", "Viandas Donadas por Colaborador");
                }
                jsonReporte.addProperty("path", reporte.getPathArchivo());

                listaReportes.add(String.valueOf(RepositoryReportes.getInstance().getReportesHistoricos().indexOf(reporte) + 1), jsonReporte);
            }

            Usuario usuario = RepositoryUsuario.getInstance().getUsuarios().get(jsonObject.get("userId").getAsInt() - 1);
            List<SolicitudReporteIndividual> solicitudesReporteIndividuales = RepositorySolicitudReporte.getInstance().getSolicitudReporteIndividuales().stream()
                    .filter(solicitud -> solicitud.getUsuario().equals(usuario))
                    .collect(Collectors.toList());

            for (SolicitudReporteIndividual solicitudReporteIndividual : solicitudesReporteIndividuales) {
                JsonObject jsonReporte = new JsonObject();
                jsonReporte.addProperty("id", solicitudReporteIndividual.getId());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                jsonReporte.addProperty("fechaSolicitud", solicitudReporteIndividual.getFechaSolicitud().format(formatter));
                LocalDateTime primerDomingo = solicitudReporteIndividual.getFechaSolicitud().with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
                jsonReporte.addProperty("fechaVencimiento", primerDomingo.format(formatter));
                jsonReporte.addProperty("solicitante", usuario.getUsername());
                TipoReporte tipoReporte = TipoReporte.valueOf(solicitudReporteIndividual.getTipoSolicitud());
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
                listaReportes.add(solicitudReporteIndividual.getId(), jsonReporte);
            }

            // Respuesta al cliente
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(listaReportes));
        });
    }
}
