package receptorDeJSON.Receptores;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import medioDeContacto.Mail;
import medioDeContacto.Telefono;
import medioDeContacto.Telegram;
import medioDeContacto.Whatsapp;
import persona.personas.Persona;
import persona.personas.PersonaFisica;
import persona.personas.PersonaJuridica;
import persona.roles.colaborador.Colaborador;
import persona.roles.tecnico.Tecnico;
import persona.roles.Usuario;
import repository.RepositoryColaborador;
import repository.RepositoryTecnicos;
import repository.RepositoryUsuario;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PerfilReceptor {
    public static void ejecutarPerfilReceptor(Javalin app) {
        app.post("/miPerfil", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            // Parsear el JSON a un objeto genérico usando Gson o JsonParser
            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();

            String rolActivo = null;

            if (jsonObject.get("userId").getAsInt() == 1) {
                JsonObject respuesta = new JsonObject();
                respuesta.addProperty("username", "administrador01");
                respuesta.addProperty("userImage", "../images/users_images/imagen_registro.png");
                respuesta.addProperty("description", "Administrador del sistema");
                respuesta.addProperty("userType", "Administrador");
                DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");
                respuesta.addProperty("registerDate", LocalDateTime.now().format(formatter2));

                // Respuesta al cliente
                ctx.contentType("application/json");
                ctx.result(new Gson().toJson(respuesta));
                return;
            }

            if (jsonObject.get("userColaboradorRol").getAsString().equals("Colaborador") && jsonObject.get("userTecnicoRol").getAsString().equals("Técnico")) {
                rolActivo = jsonObject.get("rolActivo").getAsString();
            }
            System.out.println("userId: " + jsonObject.get("userId").getAsInt());

            Usuario usuarioColaborador = RepositoryUsuario.getInstance().getUsuarios().get(jsonObject.get("userId").getAsInt() - 1);
            Persona personaColaborador = usuarioColaborador.getPersona();
            Colaborador colaborador = RepositoryColaborador.getInstance().getColaboradoresDelSistema().stream()
                    .filter(c -> c.getPersona().equals(personaColaborador))
                    .findFirst()
                    .orElse(null);

            Tecnico tecnico = RepositoryTecnicos.getInstance().getTecnicos().stream()
                    .filter(c -> c.getPersona().equals(personaColaborador))
                    .findFirst()
                    .orElse(null);

            if (colaborador != null && colaborador.getPersona() instanceof PersonaFisica && (rolActivo == null || rolActivo.equals("Colaborador"))) {
                // significa que es un colaborador fisico
                JsonObject infoColaborador = new JsonObject();
                PersonaFisica personaFisica = (PersonaFisica) colaborador.getPersona();
                infoColaborador.addProperty("name", personaFisica.getNombre());
                infoColaborador.addProperty("surname", personaFisica.getApellido());
                infoColaborador.addProperty("userType", "Persona Humana");
                Mail mail = colaborador.getPersona().getMediosDeContacto().stream()
                        .filter(m -> m instanceof Mail)
                        .map(m -> (Mail) m)
                        .findFirst()
                        .orElse(null);
                if (mail != null) {
                    infoColaborador.addProperty("email", mail.getCasilla());
                } else {
                    infoColaborador.addProperty("email", "null");
                }
                Telefono telefono = colaborador.getPersona().getMediosDeContacto().stream()
                        .filter(t -> t instanceof Telefono)
                        .map(t -> (Telefono) t)
                        .findFirst()
                        .orElse(null);
                if (telefono != null) {
                    infoColaborador.addProperty("phone", telefono.getNumero());
                } else {
                    infoColaborador.addProperty("phone", "null");
                }
                Whatsapp whatsapp = colaborador.getPersona().getMediosDeContacto().stream()
                        .filter(w -> w instanceof Whatsapp)
                        .map(w -> (Whatsapp) w)
                        .findFirst()
                        .orElse(null);
                if (whatsapp != null) {
                    infoColaborador.addProperty("whatsapp", whatsapp.getNumero());
                } else {
                    infoColaborador.addProperty("whatsapp", "null");
                }
                Telegram telegram = colaborador.getPersona().getMediosDeContacto().stream()
                        .filter(t -> t instanceof Telegram)
                        .map(t -> (Telegram) t)
                        .findFirst()
                        .orElse(null);
                if (telegram != null) {
                    infoColaborador.addProperty("telegram", telegram.getNumero());
                } else {
                    infoColaborador.addProperty("telegram", "null");
                }
                String numeroDni = personaFisica.getDocumento().getNumero();
                String tipoDni = String.valueOf(personaFisica.getDocumento().getTipoDocumentacion());
                infoColaborador.addProperty("dni", numeroDni + " (" + tipoDni + ")");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String formattedDate = personaFisica.getFechaNacimiento().format(formatter);
                infoColaborador.addProperty("birthdate", formattedDate);
                String domicilio = personaFisica.getDireccion().getCalle() + " "
                        + personaFisica.getDireccion().getAltura() +
                        ", " + personaFisica.getDireccion().getCiudad().getNombre() +
                        ", " + personaFisica.getDireccion().getCiudad().getPais().getNombre();
                infoColaborador.addProperty("address", domicilio);
                DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");
                infoColaborador.addProperty("registerDate", colaborador.getFechaIngreso().format(formatter2));
                infoColaborador.addProperty("points", colaborador.getPuntos_acumulados());
                infoColaborador.addProperty("userImage", usuarioColaborador.getImagen());
                infoColaborador.addProperty("description", usuarioColaborador.getDescripcion());
                infoColaborador.addProperty("username", usuarioColaborador.getUsername());
                if (colaborador.getTarjeta() != null) {
                    infoColaborador.addProperty("card", colaborador.getTarjeta().getCodigo());
                } else {
                    infoColaborador.addProperty("card", "null");
                }
                // Respuesta al cliente
                ctx.contentType("application/json");
                ctx.result(new Gson().toJson(infoColaborador));
            }

            else if (colaborador != null && colaborador.getPersona() instanceof PersonaJuridica) {
                // significa que es un colaborador juridico
                JsonObject infoColaborador = new JsonObject();
                PersonaJuridica personaJuridica = (PersonaJuridica) colaborador.getPersona();
                infoColaborador.addProperty("razonSocial", personaJuridica.getRazonSocial());
                infoColaborador.addProperty("companyType", String.valueOf(personaJuridica.getTipoJuridico()));
                infoColaborador.addProperty("rubro", personaJuridica.getRubro());
                infoColaborador.addProperty("cuit", personaJuridica.getCuit());
                infoColaborador.addProperty("userType", "Persona Jurídica");
                Mail mail = colaborador.getPersona().getMediosDeContacto().stream()
                        .filter(m -> m instanceof Mail)
                        .map(m -> (Mail) m)
                        .findFirst()
                        .orElse(null);
                if (mail != null) {
                    infoColaborador.addProperty("email", mail.getCasilla());
                } else {
                    infoColaborador.addProperty("email", "null");
                }
                Telefono telefono = colaborador.getPersona().getMediosDeContacto().stream()
                        .filter(t -> t instanceof Telefono)
                        .map(t -> (Telefono) t)
                        .findFirst()
                        .orElse(null);
                if (telefono != null) {
                    infoColaborador.addProperty("phone", telefono.getNumero());
                } else {
                    infoColaborador.addProperty("phone", "null");
                }
                Whatsapp whatsapp = colaborador.getPersona().getMediosDeContacto().stream()
                        .filter(w -> w instanceof Whatsapp)
                        .map(w -> (Whatsapp) w)
                        .findFirst()
                        .orElse(null);
                if (whatsapp != null) {
                    infoColaborador.addProperty("whatsapp", whatsapp.getNumero());
                } else {
                    infoColaborador.addProperty("whatsapp", "null");
                }
                Telegram telegram = colaborador.getPersona().getMediosDeContacto().stream()
                        .filter(t -> t instanceof Telegram)
                        .map(t -> (Telegram) t)
                        .findFirst()
                        .orElse(null);
                if (telegram != null) {
                    infoColaborador.addProperty("telegram", telegram.getNumero());
                } else {
                    infoColaborador.addProperty("telegram", "null");
                }
                String domicilio = personaJuridica.getDireccion().getCalle() + " "
                        + personaJuridica.getDireccion().getAltura() +
                        ", " + personaJuridica.getDireccion().getCiudad().getNombre() +
                        ", " + personaJuridica.getDireccion().getCiudad().getPais().getNombre();
                infoColaborador.addProperty("address", domicilio);
                DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");
                infoColaborador.addProperty("registerDate", colaborador.getFechaIngreso().format(formatter2));
                infoColaborador.addProperty("points", colaborador.getPuntos_acumulados());
                infoColaborador.addProperty("userImage", usuarioColaborador.getImagen());
                infoColaborador.addProperty("description", usuarioColaborador.getDescripcion());
                infoColaborador.addProperty("username", usuarioColaborador.getUsername());
                // Respuesta al cliente
                ctx.contentType("application/json");
                ctx.result(new Gson().toJson(infoColaborador));
            }

            else if (tecnico != null && (rolActivo == null || rolActivo.equals("Técnico"))) {
                JsonObject infoTecnico = new JsonObject();
                PersonaFisica personaFisica = (PersonaFisica) tecnico.getPersona();
                infoTecnico.addProperty("name", personaFisica.getNombre());
                infoTecnico.addProperty("surname", personaFisica.getApellido());
                infoTecnico.addProperty("userType", "Técnico");
                Mail mail = tecnico.getPersona().getMediosDeContacto().stream()
                        .filter(m -> m instanceof Mail)
                        .map(m -> (Mail) m)
                        .findFirst()
                        .orElse(null);
                if (mail != null) {
                    infoTecnico.addProperty("email", mail.getCasilla());
                } else {
                    infoTecnico.addProperty("email", "null");
                }
                Telefono telefono = tecnico.getPersona().getMediosDeContacto().stream()
                        .filter(t -> t instanceof Telefono)
                        .map(t -> (Telefono) t)
                        .findFirst()
                        .orElse(null);
                if (telefono != null) {
                    infoTecnico.addProperty("phone", telefono.getNumero());
                } else {
                    infoTecnico.addProperty("phone", "null");
                }
                Whatsapp whatsapp = tecnico.getPersona().getMediosDeContacto().stream()
                        .filter(w -> w instanceof Whatsapp)
                        .map(w -> (Whatsapp) w)
                        .findFirst()
                        .orElse(null);
                if (whatsapp != null) {
                    infoTecnico.addProperty("whatsapp", whatsapp.getNumero());
                } else {
                    infoTecnico.addProperty("whatsapp", "null");
                }
                Telegram telegram = tecnico.getPersona().getMediosDeContacto().stream()
                        .filter(t -> t instanceof Telegram)
                        .map(t -> (Telegram) t)
                        .findFirst()
                        .orElse(null);
                if (telegram != null) {
                    infoTecnico.addProperty("telegram", telegram.getNumero());
                } else {
                    infoTecnico.addProperty("telegram", "null");
                }
                String numeroDni = personaFisica.getDocumento().getNumero();
                String tipoDni = String.valueOf(personaFisica.getDocumento().getTipoDocumentacion());
                infoTecnico.addProperty("dni", numeroDni + " (" + tipoDni + ")");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String formattedDate = personaFisica.getFechaNacimiento().format(formatter);
                infoTecnico.addProperty("birthdate", formattedDate);
                String domicilio = personaFisica.getDireccion().getCalle() + " "
                        + personaFisica.getDireccion().getAltura() +
                        ", " + personaFisica.getDireccion().getCiudad().getNombre() +
                        ", " + personaFisica.getDireccion().getCiudad().getPais().getNombre();
                infoTecnico.addProperty("address", domicilio);
                DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");
                infoTecnico.addProperty("registerDate", tecnico.getFechaIngreso().format(formatter2));
                infoTecnico.addProperty("userImage", usuarioColaborador.getImagen());
                infoTecnico.addProperty("description", usuarioColaborador.getDescripcion());
                infoTecnico.addProperty("username", usuarioColaborador.getUsername());
                // Respuesta al cliente
                ctx.contentType("application/json");
                ctx.result(new Gson().toJson(infoTecnico));
            }

            else {
                System.out.println("No se encontró el usuario");
                String mensajeRespuesta = "No se encontró el usuario";
                // Respuesta al cliente
                ctx.contentType("application/json");
                ctx.result(new Gson().toJson(mensajeRespuesta));
            }
        });
    }

    public static void ejecutarActualizacionDescripcion(Javalin app) {
        app.post("/actualizacionDescripcion", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            // Parsear el JSON a un objeto genérico usando Gson o JsonParser
            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
            JsonObject jsonUsuario = jsonObject.get("usuario").getAsJsonObject();

            Usuario usuarioColaborador = RepositoryUsuario.getInstance().getUsuarios().get(jsonUsuario.get("userId").getAsInt() - 1);
            usuarioColaborador.setDescripcion(jsonObject.get("descripcion").getAsString());

            // Respuesta al cliente
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson("Descripción actualizada con éxito"));
        });
    }
}
