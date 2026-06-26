package receptorDeJSON.Receptores;

import Heladera.incidente.Alerta.AlertaConexion;
import Heladera.incidente.Alerta.AlertaFraude;
import Heladera.incidente.Alerta.AlertaTemperatura;
import Heladera.incidente.FallaTecnica;
import Heladera.incidente.Incidente;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.javalin.Javalin;
import persona.personas.PersonaFisica;
import repository.RepositoryIncidente;

import java.time.format.DateTimeFormatter;

public class IncidentesOcurridos {
    public static void ejecutarIncidentesOcurridos(Javalin app) {
        app.post("/incidentesOcurridos", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            JsonObject jsonListIncidentes = new JsonObject();
            int index = 0;
            for (Incidente incidente : RepositoryIncidente.getInstance().getIncidentes()) {
                JsonObject jsonIncidente = new JsonObject();

                jsonIncidente.addProperty("id", String.valueOf(index + 1));
                jsonIncidente.addProperty("tipo", incidente.getGravedad().toString());
                jsonIncidente.addProperty("heladera", incidente.getHeladera().getNombreHeladera());

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                jsonIncidente.addProperty("fecha", incidente.getFechaIncidente().format(formatter));

                if (incidente.isSolucionado()){
                    jsonIncidente.addProperty("estadoIncidente", "Solucionado");
                } else {
                    jsonIncidente.addProperty("estadoIncidente", "Pendiente");
                }

                if (incidente.getClass().equals(FallaTecnica.class)) {
                    FallaTecnica fallaTecnica = (FallaTecnica) incidente;
                    jsonIncidente.addProperty("tipo", "Falla Técnica");
                    jsonIncidente.addProperty("colaborador", fallaTecnica.getColaborador().getUsuario().getUsername());
                } else if (incidente.getClass().equals(AlertaConexion.class)) {
                    AlertaConexion alertaConexion = (AlertaConexion) incidente;
                    jsonIncidente.addProperty("tipo", "Alerta Falla en la Conexión");
                    jsonIncidente.addProperty("ultimaTemperatura", alertaConexion.getUltimaTemperatura());
                } else if (incidente.getClass().equals(AlertaFraude.class)) {
                    AlertaFraude alertaFraude = (AlertaFraude) incidente;
                    jsonIncidente.addProperty("tipo", "Alerta de Fraude");
                    jsonIncidente.addProperty("viandasAtracadas", - alertaFraude.getViandasAtracadas());
                } else if (incidente.getClass().equals(AlertaTemperatura.class)) {
                    AlertaTemperatura alertaTemperatura = (AlertaTemperatura) incidente;
                    jsonIncidente.addProperty("tipo", "Alerta de Temperatura");
                    jsonIncidente.addProperty("temepraturaRegistrada", alertaTemperatura.getTemperatura());
                    jsonIncidente.addProperty("diferenciaTemperatura", alertaTemperatura.getDifTemperatura());
                }

                jsonIncidente.addProperty("visitasRealizadas", incidente.buscarVisitasAsociadas().size());
                PersonaFisica personaFisica = (PersonaFisica) incidente.getTecnico().getPersona();
                jsonIncidente.addProperty("tecnico", personaFisica.getNombre() + " " + personaFisica.getApellido());

                jsonListIncidentes.add("incidente_" + index, jsonIncidente);
                index++;
            }
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(jsonListIncidentes));
        });
    }
}