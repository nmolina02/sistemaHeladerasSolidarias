package receptorDeJSON.Receptores;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import localizacion.Ubicacion;
import medioDeContacto.*;
import persona.personas.PersonaFisica;
import persona.roles.Usuario;
import persona.roles.colaborador.Colaborador;
import persona.roles.tecnico.Tecnico;
import receptorDeJSON.UsuariosRecibidos;
import repository.*;

public class ActualizacionConfiguraciones {
    public static void ejecutarActualizacionConfiguraciones(Javalin app) {
        app.post("/configuracion", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            // Parsear el JSON a un objeto genérico usando Gson o JsonParser
            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
            JsonObject jsonUsuarioRecibido = jsonObject.get("usuario").getAsJsonObject();

            String mensajeRespuesta = null;

            Usuario usuario = RepositoryUsuario.getInstance().getUsuarios().get(jsonUsuarioRecibido.get("userId").getAsInt() - 1);
            Colaborador colaborador = RepositoryColaborador.getInstance().getColaboradoresDelSistema().stream()
                    .filter(c -> c.getPersona().equals(usuario.getPersona()))
                    .findFirst()
                    .orElse(null);

            if (colaborador != null) {
                PersonaFisica personaFisica = (PersonaFisica) colaborador.getPersona();
                mensajeRespuesta = actualizarDatos(jsonObject, usuario, personaFisica);
                ctx.contentType("application/json");
                ctx.result(new Gson().toJson(mensajeRespuesta));
                return;
            }

            Tecnico tecnico = RepositoryTecnicos.getInstance().getTecnicos().stream()
                    .filter(c -> c.getPersona().equals(usuario.getPersona()))
                    .findFirst()
                    .orElse(null);

            if (tecnico != null) {
                PersonaFisica personaFisica = (PersonaFisica) tecnico.getPersona();
                mensajeRespuesta = actualizarDatos(jsonObject, usuario, personaFisica);
                ctx.contentType("application/json");
                ctx.result(new Gson().toJson(mensajeRespuesta));
                return;
            }

            ctx.contentType("application/json");
            ctx.result(new Gson().toJson("No hay datos para actualizar"));
        });
    }

    private static String actualizarMediosDeContacto(PersonaFisica personaFisica, JsonObject jsonObject) {
        String mensajeRespuesta = "No hay datos que cambiar";
        JsonObject mediosDeContacto = jsonObject.get("dato").getAsJsonObject();
        if (!mediosDeContacto.get("email").getAsString().isEmpty()) {
            Mail mail = personaFisica.getMediosDeContacto().stream()
                    .filter(m -> m instanceof Mail)
                    .map(m -> (Mail) m)
                    .findFirst()
                    .orElse(null);
            if (mail != null) {
                mail.setCasilla(mediosDeContacto.get("email").getAsString());
                System.out.println("Mail cambiado con éxito");
            } else {
                personaFisica.agregar_medio_de_contacto(new Mail(mediosDeContacto.get("email").getAsString()));
                System.out.println("Mail creado con éxito");
            }
        }

        if (!mediosDeContacto.get("phone").getAsString().equals("0")) {
            Telefono telefono = personaFisica.getMediosDeContacto().stream()
                    .filter(m -> m instanceof Telefono)
                    .map(m -> (Telefono) m)
                    .findFirst()
                    .orElse(null);
            if (telefono != null) {
                telefono.setNumero(mediosDeContacto.get("phone").getAsString());
                System.out.println("Teléfono cambiado con éxito");
            } else {
                personaFisica.agregar_medio_de_contacto(new Telefono(mediosDeContacto.get("phone").getAsString()));
                System.out.println("Teléfono creado con éxito");
            }
        }

        Whatsapp whatsapp = personaFisica.getMediosDeContacto().stream()
                .filter(m -> m instanceof Whatsapp)
                .map(m -> (Whatsapp) m)
                .findFirst()
                .orElse(null);
        if (whatsapp != null) {
            whatsapp.setNumero(mediosDeContacto.get("phone").getAsString());
            System.out.println("Whatsapp cambiado con éxito");
        } else if (mediosDeContacto.get("whatsapp").getAsString().equals("true")) {
            personaFisica.agregar_medio_de_contacto(new Whatsapp(mediosDeContacto.get("phone").getAsString()));
            System.out.println("Whatsapp creado con éxito");
        }

        Telegram telegram = personaFisica.getMediosDeContacto().stream()
                .filter(m -> m instanceof Telegram)
                .map(m -> (Telegram) m)
                .findFirst()
                .orElse(null);
        if (telegram != null) {
            telegram.setNumero(mediosDeContacto.get("phone").getAsString());
            System.out.println("Telegram cambiado con éxito");
        } else if (mediosDeContacto.get("telegram").getAsString().equals("true")) {
            personaFisica.agregar_medio_de_contacto(new Telegram(mediosDeContacto.get("phone").getAsString()));
            System.out.println("Telegram creado con éxito");
        }

        mensajeRespuesta = "Datos actualizados correctamente";

        return mensajeRespuesta;
    }

    private static String actualizarDatos(JsonObject jsonObject, Usuario usuario, PersonaFisica personaFisica) {
        String mensajeRespuesta = "";
        switch (jsonObject.get("tipoDato").getAsString()) {
            case "username":
                if (RepositoryUsuario.getInstance().usernameExistente(jsonObject.get("dato").getAsString())) {
                    System.out.println("El nombre de usuario ya existe");
                    mensajeRespuesta = "Error al cambiar el dato";
                } else {
                    usuario.setUsername(jsonObject.get("dato").getAsString());
                    System.out.println("Usuario cambiado con éxito");
                    mensajeRespuesta = "Dato actualizado correctamente";
                }
                break;
            case "contactData":
                if(actualizarMediosDeContacto(personaFisica, jsonObject).equals("Datos actualizados correctamente")) {
                    System.out.println("Medios de contacto cambiados con éxito");
                    mensajeRespuesta = "Dato actualizado correctamente";
                } else {
                    System.out.println("Error al cambiar los medios de contacto");
                    mensajeRespuesta = "Error al cambiar el dato";
                }
                break;
            case "address":
                UsuariosRecibidos usuariosRecibidos = new UsuariosRecibidos();
                Ubicacion ubicacion = usuariosRecibidos.solicitarUbicacion(jsonObject.get("dato").getAsString());
                personaFisica.setDireccion(ubicacion);
                System.out.println("Ubicación cambiada con éxito");
                mensajeRespuesta = "Dato actualizado correctamente";
                break;
            case "dataKey":
                usuario.setPassword(jsonObject.get("dato").getAsString());
                System.out.println("Contraseña cambiada con éxito");
                mensajeRespuesta = "Dato actualizado correctamente";
                break;
            default:
                System.out.println("No se ha encontrado el tipo de dato a actualizar");
                break;
        }
        return mensajeRespuesta;
    }
}