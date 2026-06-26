package receptorDeJSON.Receptores;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import persona.personas.Persona;
import persona.roles.colaborador.Colaborador;
import persona.roles.Usuario;
import repository.RepositoryColaborador;
import repository.RepositoryUsuario;

public class SolicitudTarjetaReceptor {
    public static void ejecutarSolicitudTarjetaReceptor(Javalin app) {
        app.post("/solicitudTarjeta", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            // Parsear el JSON a un objeto genérico usando Gson o JsonParser
            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();

            Usuario usuarioColaborador = RepositoryUsuario.getInstance().getUsuarios().get(jsonObject.get("userId").getAsInt() - 1);
            Persona personaColaborador = usuarioColaborador.getPersona();
            Colaborador colaborador = RepositoryColaborador.getInstance().getColaboradoresDelSistema().stream()
                    .filter(c -> c.getPersona().equals(personaColaborador))
                    .findFirst()
                    .orElse(null);

            String mensajeRespuesta = "";

            if (colaborador != null) {
                colaborador.solicitar_tarjeta();
                System.out.println("tarjeta generada: " + colaborador.getTarjeta().getCodigo());
                mensajeRespuesta = colaborador.getTarjeta().getCodigo();
            } else {
                System.out.println("No se encontró el colaborador");
                mensajeRespuesta = "No se encontró el colaborador";
            }
            // Respuesta al cliente
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(mensajeRespuesta));
        });
    }
}
