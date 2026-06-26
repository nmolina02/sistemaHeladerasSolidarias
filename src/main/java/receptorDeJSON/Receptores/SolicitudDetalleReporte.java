package receptorDeJSON.Receptores;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import persona.personas.PersonaFisica;
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

public class SolicitudDetalleReporte {
    public static void ejecutarSolicitudDetalleReporte(Javalin app) {
        app.post("/solicitarDetalleReporte", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            // Parsear el JSON a un objeto genérico usando Gson o JsonParser
            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
            String solicitante = jsonObject.get("solicitante").getAsString();
            JsonObject respuesta = new JsonObject();
            JsonObject jsonReporte = new JsonObject();
            int index = 1;

            if (solicitante.equals("Semanal")) {
                String tipoReporte = jsonObject.get("tipoReporte").getAsString();
                respuesta.addProperty("solicitante", "Semanal");

                Reporte reporte = RepositoryReportes.getInstance().getReportesHistoricos().get(jsonObject.get("idReporte").getAsInt() - 1);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                respuesta.addProperty("fechaSolicitud", reporte.getFechaInicial().format(formatter));
                respuesta.addProperty("fechaVencimiento", reporte.getFechaInicial().format(formatter));

                switch (tipoReporte) {
                    case "Fallas de Heladera":
                        respuesta.addProperty("tipoReporte", "Fallas de Heladera");
                        for (FallasHeladera fallasHeladera : ((ReporteFallasHeladera) reporte).getFallasPorHeladera()) {
                            JsonObject jsonFalla = new JsonObject();
                            jsonFalla.addProperty("id", index);
                            jsonFalla.addProperty("heladera", fallasHeladera.getHeladera().getNombreHeladera());
                            jsonFalla.addProperty("cantidadFallas", fallasHeladera.getCantidadFallas());
                            jsonReporte.add(String.valueOf(index), jsonFalla);
                            index++;
                        }
                        break;
                    case "Movimientos de Heladera":
                        respuesta.addProperty("tipoReporte", "Movimientos de Heladera");
                        for (MovimientosHeladera movimientosHeladera : ((ReporteMovimientosHeladera) reporte).getMovimientosPorHeladera()) {
                            JsonObject jsonFalla = new JsonObject();
                            jsonFalla.addProperty("id", index);
                            jsonFalla.addProperty("heladera", movimientosHeladera.getHeladera().getNombreHeladera());
                            jsonFalla.addProperty("cantidadViandasIngresadas", movimientosHeladera.getCantViandasColocadas());
                            jsonFalla.addProperty("cantidadViandasRetiradas", movimientosHeladera.getCantViandasRetiradas());
                            jsonReporte.add(String.valueOf(index), jsonFalla);
                            index++;
                        }
                        break;
                    case "Viandas Donadas por Colaborador":
                        respuesta.addProperty("tipoReporte", "Viandas Donadas por Colaborador");
                        for (ViandasPorColaborador viandasPorColaborador : ((ReporteViandasPorColaborador) reporte).getViandasPorColaborador()) {
                            JsonObject jsonFalla = new JsonObject();
                            jsonFalla.addProperty("id", index);
                            PersonaFisica personaFisica = (PersonaFisica) viandasPorColaborador.getColaboradorHumano().getPersona();
                            Usuario usuario = RepositoryUsuario.getInstance().getUsuarios().stream()
                                    .skip(1)
                                    .filter(u -> u.getPersona().equals(personaFisica))
                                    .findFirst()
                                    .orElse(null);
                            String nombreApellidoPersona = personaFisica.getNombre() + " " + personaFisica.getApellido();
                            String nombreUsuario = usuario.getUsername();
                            String ejecutor = nombreApellidoPersona + " (" + nombreUsuario + ")";
                            jsonFalla.addProperty("colaborador", ejecutor);
                            jsonFalla.addProperty("cantidadViandasDonadas", viandasPorColaborador.getViandasDonadas());
                            jsonReporte.add(String.valueOf(index), jsonFalla);
                            index++;
                        }
                        break;
                    default:
                        break;
                }
                respuesta.add("detalleReporte", jsonReporte);
            } else {
                String username = jsonObject.get("solicitante").getAsString();
                respuesta.addProperty("solicitante", username);
                String tipoReporte = jsonObject.get("tipoReporte").getAsString();
                TipoReporte tipoReporteEnum = null;
                Usuario usuario = RepositoryUsuario.getInstance().getUsuarios().stream()
                        .filter(u -> u.getUsername().equals(username))
                        .findFirst()
                        .orElse(null);
                SolicitudReporteIndividual solicitudReporteIndividual = RepositorySolicitudReporte.getInstance().getSolicitudReporteIndividuales().stream()
                        .filter(s -> s.getUsuario().equals(usuario) && s.getId().equals(jsonObject.get("idReporte").getAsString()))
                        .findFirst()
                        .orElse(null);
                switch (tipoReporte) {
                    case "Fallas de Heladera":
                        tipoReporteEnum = TipoReporte.FALLAS_HELADERA;
                        break;
                    case "Movimientos de Heladera":
                        tipoReporteEnum = TipoReporte.MOVIMIENTOS_HELADERA;
                        break;
                    case "Viandas Donadas por Colaborador":
                        tipoReporteEnum = TipoReporte.VIANDAS_COLABORADOR;
                        break;
                    default:
                        break;
                }
                List<?> hechos = GeneradorDeReportes.getInstance().generar_reporte_personalizado(tipoReporteEnum.toString(), solicitudReporteIndividual.getFechaSolicitud());
                respuesta.addProperty("fechaSolicitud", solicitudReporteIndividual.getFechaSolicitud().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                LocalDateTime primerDomingo = solicitudReporteIndividual.getFechaSolicitud().with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
                respuesta.addProperty("fechaVencimiento", primerDomingo.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

                switch (tipoReporte) {
                    case "Fallas de Heladera":
                        respuesta.addProperty("tipoReporte", "Fallas de Heladera");
                        for (FallasHeladera fallasHeladera : ((List<FallasHeladera>) hechos)) {
                            JsonObject jsonFalla = new JsonObject();
                            jsonFalla.addProperty("id", index);
                            jsonFalla.addProperty("heladera", fallasHeladera.getHeladera().getNombreHeladera());
                            jsonFalla.addProperty("cantidadFallas", fallasHeladera.getCantidadFallas());
                            jsonReporte.add(String.valueOf(index), jsonFalla);
                            index++;
                        }
                        break;
                    case "Movimientos de Heladera":
                        respuesta.addProperty("tipoReporte", "Movimientos de Heladera");
                        for (MovimientosHeladera movimientosHeladera : ((List<MovimientosHeladera>) hechos)) {
                            JsonObject jsonFalla = new JsonObject();
                            jsonFalla.addProperty("id", index);
                            jsonFalla.addProperty("heladera", movimientosHeladera.getHeladera().getNombreHeladera());
                            jsonFalla.addProperty("cantidadViandasIngresadas", movimientosHeladera.getCantViandasColocadas());
                            jsonFalla.addProperty("cantidadViandasRetiradas", movimientosHeladera.getCantViandasRetiradas());
                            jsonReporte.add(String.valueOf(index), jsonFalla);
                            index++;
                        }
                        break;
                    case "Viandas Donadas por Colaborador":
                        respuesta.addProperty("tipoReporte", "Viandas Donadas por Colaborador");
                        for (ViandasPorColaborador viandasPorColaborador : ((List<ViandasPorColaborador>) hechos)) {
                            JsonObject jsonFalla = new JsonObject();
                            jsonFalla.addProperty("id", index);
                            PersonaFisica personaFisica = (PersonaFisica) viandasPorColaborador.getColaboradorHumano().getPersona();
                            Usuario usuarioColaborador = RepositoryUsuario.getInstance().getUsuarios().stream()
                                    .skip(1)
                                    .filter(u -> u.getPersona().equals(personaFisica))
                                    .findFirst()
                                    .orElse(null);
                            String nombreApellidoPersona = personaFisica.getNombre() + " " + personaFisica.getApellido();
                            String nombreUsuario = usuarioColaborador.getUsername();
                            String ejecutor = nombreApellidoPersona + " (" + nombreUsuario + ")";
                            jsonFalla.addProperty("colaborador", ejecutor);
                            jsonFalla.addProperty("cantidadViandasDonadas", viandasPorColaborador.getViandasDonadas());
                            jsonReporte.add(String.valueOf(index), jsonFalla);
                            index++;
                        }
                        break;
                    default:
                        break;
                }
                respuesta.add("detalleReporte", jsonReporte);
            }

            // Respuesta al cliente
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(respuesta));
        });
    }
}
