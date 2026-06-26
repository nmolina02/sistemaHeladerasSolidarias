package receptorDeJSON.Receptores;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import io.javalin.Javalin;
import medioDeContacto.TipoMedioContacto;
import persona.personas.Persona;
import persona.roles.colaborador.Colaborador;
import persona.roles.colaborador.OpcionesSuscripciones.OpcionSuscripcion;
import persona.roles.Usuario;
import persona.roles.colaborador.OpcionesSuscripciones.OpcionesSuscripcion;
import repository.RepositoryColaborador;
import repository.RepositoryUsuario;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SuscripcionesReceptor {
    public static void ejecutarSuscripcionesReceptor(Javalin app) {
        app.post("/suscripcion", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            // Parsear el JSON a un objeto genérico usando Gson o JsonParser
            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();

            JsonObject jsonColaborador = jsonObject.get("colaborador").getAsJsonObject();

            Usuario usuarioColaborador = RepositoryUsuario.getInstance().getUsuarios().get(jsonColaborador.get("userId").getAsInt() - 1);
            Persona personaColaborador = usuarioColaborador.getPersona();
            Colaborador colaborador = RepositoryColaborador.getInstance().getColaboradoresDelSistema().stream()
                    .filter(c -> c.getPersona().equals(personaColaborador))
                    .findFirst()
                    .orElse(null);

            JsonArray mediosContactoOptionsArray = jsonObject.get("messaggeOptions").getAsJsonArray();
            JsonArray suscriptionsOptionsArray = jsonObject.get("suscriptionsOptions").getAsJsonArray();
            List<OpcionSuscripcion> opcionesSuscripcion = new ArrayList<>();
            List<TipoMedioContacto> tiposMediosDeContacto = new ArrayList<>();

            for (int i = 0; i < suscriptionsOptionsArray.size(); i++) {
                OpcionesSuscripcion opcion = OpcionesSuscripcion.valueOf(suscriptionsOptionsArray.get(i).getAsString());
                if (opcion.equals(OpcionesSuscripcion.CANT_VIANDAS_DISP)) {
                    if (jsonObject.get("cantViandasDisp") == null) {
                        opcionesSuscripcion.add(new OpcionSuscripcion(opcion, 0));
                    } else {
                        int valorOpcion1 = jsonObject.get("cantViandasDisp").getAsInt();
                        opcionesSuscripcion.add(new OpcionSuscripcion(opcion, valorOpcion1));
                    }
                } else if (opcion.equals(OpcionesSuscripcion.CANT_VIANDAS_PARA_LLENAR)) {
                    if (jsonObject.get("cantViandasLlevar") == null) {
                        opcionesSuscripcion.add(new OpcionSuscripcion(opcion, 0));
                    } else {
                        int valorOpcion2 = jsonObject.get("cantViandasLlevar").getAsInt();
                        opcionesSuscripcion.add(new OpcionSuscripcion(opcion, valorOpcion2));
                    }
                } else if (opcion.equals(OpcionesSuscripcion.DESPERFECTO_HELADERA)) {
                    opcionesSuscripcion.add(new OpcionSuscripcion(opcion, 0));
                }
            }

            for (int i = 0; i < mediosContactoOptionsArray.size(); i++) {
                TipoMedioContacto tipoMedioContacto = TipoMedioContacto.valueOf(mediosContactoOptionsArray.get(i).getAsString());
                if (colaborador.getPersona().getMediosDeContacto().stream().anyMatch(medioDeContacto -> medioDeContacto.getTipoMedioContacto().equals(tipoMedioContacto))) {
                    tiposMediosDeContacto.add(tipoMedioContacto);
                }
            }

            String mensajeRespuesta = "";

            if (colaborador != null && colaborador.getGestorSuscripciones().getEntidadNotificadora() != null) {
                colaborador.getGestorSuscripciones().actualizarMediosDeContactoSuscripciones(tiposMediosDeContacto);
                colaborador.getGestorSuscripciones().actualizarOpcionesSuscripcion(opcionesSuscripcion);
                System.out.println("Suscripción actualizada correctamente");
                mensajeRespuesta = "Suscripción actualizada correctamente";
            } else if (colaborador != null) {
                colaborador.getGestorSuscripciones().suscribirse_a_notificaciones(opcionesSuscripcion, tiposMediosDeContacto);
                System.out.println("Suscripción realizada correctamente");
                mensajeRespuesta = "Suscripción realizada correctamente";
            } else {
                System.out.println("No se encontró el colaborador");
                mensajeRespuesta = "No se encontró el colaborador";
            }
            // Respuesta al cliente
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(mensajeRespuesta));
        });
    }

    public static void ejecutarSuscripcionUsuarioExistente(Javalin app) {
        app.post("/solicitudSuscripcionExistenteUsuario", ctx -> {
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

            if (colaborador != null && colaborador.getGestorSuscripciones().getEntidadNotificadora() != null) {
                mensajeRespuesta = "Colaborador Suscripto";
            } else {
                mensajeRespuesta = "No hay suscripcion existente";
            }
            // Respuesta al cliente
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(mensajeRespuesta));
        });
    }
}
