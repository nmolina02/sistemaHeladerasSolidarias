package receptorDeJSON.Receptores;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import enviadoresNotificaciones.EnviadorDeMails;
import enviadoresNotificaciones.EnviadorWhatsapp;
import io.javalin.Javalin;
import medioDeContacto.Mail;
import medioDeContacto.Whatsapp;
import persona.personas.PersonaFisica;
import persona.roles.Usuario;
import repository.RepositoryUsuario;

import java.util.Random;

public class RecuperarContrasenia {
    public static void ejecutarRecuperarContrasenia(Javalin app) {
        app.post("/solicitudRecuperoIngreso", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            // Parsear el JSON a un objeto genérico usando Gson o JsonParser
            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();

            Usuario usuario = RepositoryUsuario.getInstance().getUsuarios().stream()
                    .filter(u -> u.getUsername().equals(jsonObject.get("username").getAsString()))
                    .findFirst()
                    .orElse(null);

            if (usuario != null) {
                PersonaFisica personaFisica = (PersonaFisica) usuario.getPersona();
                asignarCodigoDeRecupero(usuario);
                Mail mail = personaFisica.getMediosDeContacto().stream()
                        .filter(m -> m instanceof Mail)
                        .map(m -> (Mail) m)
                        .findFirst()
                        .orElse(null);
                if (mail != null) {
                    String remitente = "heladeras.solidarias@gmail.com";
                    String casillaDestino = mail.getCasilla();
                    String asunto = "Recuperación de contraseña";
                    String texto = "Hola " + personaFisica.getNombre() + "\n\n" +
                            "Recibimos una solicitud para recuperar tu contraseña. Para confirmar el " +
                            "cambio, ingrese el código de seguridad en el apartado indicado.\n\n" +
                            "Código de seguridad: " + usuario.getCodigoRecuperacion() + "\n\n" +
                            "Saludos,\n" +
                            "Heladeras Solidarias";
                    EnviadorDeMails.getInstance().enviar_email(remitente, casillaDestino, asunto, texto);
                    System.out.println("Se envió un mail a " + mail.getCasilla());
                    ctx.contentType("application/json");
                    ctx.result(new Gson().toJson("Envio solicitud por mail"));
                    return;
                }

                Whatsapp whatsapp = personaFisica.getMediosDeContacto().stream()
                        .filter(m -> m instanceof Whatsapp)
                        .map(m -> (Whatsapp) m)
                        .findFirst()
                        .orElse(null);
                if (whatsapp != null) {
                    String numero = whatsapp.getNumero();
                    String mensaje = "Hola " + personaFisica.getNombre() + "\n\n" +
                            "Recibimos una solicitud para recuperar tu contraseña. Para confirmar el " +
                            "cambio, ingrese el código de seguridad en el apartado indicado.\n\n" +
                            "Código de seguridad: " + usuario.getCodigoRecuperacion() + "\n\n" +
                            "Saludos,\n" +
                            "Heladeras Solidarias";
                    EnviadorWhatsapp.getInstance().enviar_whatsapp(numero, mensaje);
                    System.out.println("Se envió un mensaje de WhatsApp al número " + numero);
                    ctx.contentType("application/json");
                    ctx.result(new Gson().toJson("Envio solicitud por whatsapp"));
                    return;
                }
            }

            ctx.contentType("application/json");
            ctx.result(new Gson().toJson("Error en el envío de la solicitud de recuperación"));
        });
    }

    public static void ejecutarConfirmacionContrasenia(Javalin app) {
        app.post("/confirmacionContrasenia", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            // Parsear el JSON a un objeto genérico usando Gson o JsonParser
            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();

            Usuario usuario = RepositoryUsuario.getInstance().getUsuarios().stream()
                    .filter(u -> u.getUsername().equals(jsonObject.get("username").getAsString()))
                    .findFirst()
                    .orElse(null);

            if (usuario != null) {
                if (usuario.getCodigoRecuperacion().equals(jsonObject.get("codigoRecuperacion").getAsString())) {
                    usuario.setPassword(jsonObject.get("password").getAsString());
                    usuario.setCodigoRecuperacion(null);
                    ctx.contentType("application/json");
                    ctx.result(new Gson().toJson("Recuperación exitosa"));
                    return;
                }
            }

            ctx.contentType("application/json");
            ctx.result(new Gson().toJson("Fallo en la recuperación"));
        });
    }

    private static void asignarCodigoDeRecupero(Usuario usuario) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(11);
        String nuevoCodigo;
        for (int i = 0; i < 11; i++) {
            int index = random.nextInt(caracteres.length());
            sb.append(caracteres.charAt(index));
        }
        nuevoCodigo = sb.toString();
        usuario.setCodigoRecuperacion(nuevoCodigo);
        sb.setLength(0);
    }
}