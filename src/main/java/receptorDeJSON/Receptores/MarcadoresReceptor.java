package receptorDeJSON.Receptores;

import Heladera.Heladera;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.javalin.Javalin;
import persona.personas.PersonaJuridica;
import repository.RepositoryHeladera;

public class MarcadoresReceptor {
    public static void ejecutarMarcadoresReceptor(Javalin app) {
        app.post("/heladera", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            if (RepositoryHeladera.getInstance().getHeladerasDelSistema().isEmpty()) {
                ctx.contentType("application/json");
                ctx.result(new Gson().toJson("No hay heladeras cargadas en el sistema"));
            }
            else {
                System.out.println("contenedor de heladeras para marc: " + RepositoryHeladera.getInstance().getHeladerasDelSistema());

                JsonObject listaHeladeras = new JsonObject();
                for (Heladera heladera : RepositoryHeladera.getInstance().getHeladerasDelSistema()) {
                    if (heladera.getDireccion() == null) {
                        continue;
                    }
                    JsonObject jsonHeladera = new JsonObject();
                    PersonaJuridica propietario = (PersonaJuridica) heladera.getPropietario().getPersona();
                    jsonHeladera.addProperty("nombreHeladera", heladera.getNombreHeladera());
                    jsonHeladera.addProperty("latitud", heladera.getDireccion().getLatitud());
                    jsonHeladera.addProperty("longitud", heladera.getDireccion().getLongitud());
                    jsonHeladera.addProperty("calle", heladera.getDireccion().getCalle());
                    jsonHeladera.addProperty("altura", heladera.getDireccion().getAltura());
                    jsonHeladera.addProperty("fechaInauguracion", heladera.getFechaInauguracion().toString());
                    jsonHeladera.addProperty("capacidadMaxima", heladera.getModelo().getCapacidadMaxima());
                    jsonHeladera.addProperty("propietario", propietario.getRazonSocial());
                    jsonHeladera.addProperty("estadoHeladera", heladera.getEstadoHeladera().toString());
                    jsonHeladera.addProperty("viandas", heladera.getGestorDeViandas().getViandas().size());
                    jsonHeladera.addProperty("alertas", heladera.getGestorDeAlertas().getAlertaActual());
                    jsonHeladera.addProperty("img", heladera.getImagen());
                    listaHeladeras.add(heladera.getNombreHeladera(), jsonHeladera);
                }

                // Respuesta al cliente
                ctx.contentType("application/json");
                ctx.result(new Gson().toJson(listaHeladeras));
            }
        });
    }
}
