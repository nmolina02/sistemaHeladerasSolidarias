package receptorDeJSON.Receptores;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import persona.roles.Usuario;
import reportes.GeneradorDeReportes;
import reportes.Reporte;
import reportes.SolicitudReporteIndividual;
import repository.RepositoryReportes;
import repository.RepositorySolicitudReporte;
import repository.RepositoryUsuario;

public class ExportarReportePDF {
    public static void ejecutarExportarReportePDF(Javalin app) {
        app.post("/exportarReportePdf", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            // Parsear el JSON a un objeto genérico usando Gson o JsonParser
            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
            JsonObject solicitante = jsonObject.get("solicitante").getAsJsonObject();
            if (solicitante.equals("Semanal")) {
                int idReporteRecibido = Integer.parseInt(jsonObject.get("reporte").getAsString());
                Reporte reporte = RepositoryReportes.getInstance().getReportesHistoricos().get(idReporteRecibido - 1);

                // Generar el PDF y obtener el nombre del archivo generado
                reporte.generarReporteFisico();
                reporte.generarNombreArchivo();
            } else {
                Usuario usuario = RepositoryUsuario.getInstance().getUsuarios().get(solicitante.get("userId").getAsInt() - 1);
                SolicitudReporteIndividual solicitudReporteIndividual = RepositorySolicitudReporte.getInstance().getSolicitudReporteIndividuales()
                        .stream().filter(solicitud -> solicitud.getUsuario().equals(usuario)
                                && solicitud.getId().equals(jsonObject.get("reporte").getAsString()))
                        .findFirst().orElse(null);
                GeneradorDeReportes.getInstance().generarSolicitudReporteFisico(solicitudReporteIndividual);
            }
            // Respuesta al cliente
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson("Reporte exportado con éxito."));
        });
    }
}
