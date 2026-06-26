package receptorDeJSON.Receptores;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import localizacion.Ubicacion;
import medioDeContacto.Mail;
import medioDeContacto.MedioDeContacto;
import persona.documentacion.Documentacion;
import persona.documentacion.TipoDocumentacion;
import persona.personas.Persona;
import persona.personas.PersonaFisica;
import persona.personas.PersonaJuridica;
import persona.personas.TipoJuridico;
import persona.roles.Usuario;
import persona.roles.colaborador.Colaborador;
import persona.roles.tecnico.Tecnico;
import receptorDeJSON.UsuariosRecibidos;
import receptorDeJSON.UsuariosRecibidosSSO;
import repository.RepositoryColaborador;
import repository.RepositoryTecnicos;
import repository.RepositoryUsuario;
import repository.RepositoryUsuarioSSO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static receptorDeJSON.Receptores.UserReceptor.*;

public class UserSSOReceptor {
    public static void ejecutarUserSSOReceptor(Javalin app) {
        app.post("/userSSO", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            // Parsear el JSON a un objeto genérico usando Gson o JsonParser
            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();

            UsuariosRecibidosSSO usuarioRecibidoSSO = RepositoryUsuarioSSO.getInstance().buscarUserInfo(jsonObject.get("currentUserId").getAsString());
            System.out.println("Usuario recibido: " + usuarioRecibidoSSO.getUserInfo().getString("given_name") + " " + usuarioRecibidoSSO.getUserInfo().getString("family_name"));

            // Extraer el tipo de usuario directamente desde el JSON
            String userType = jsonObject.get("userType").getAsString();
            String usernameARegistrar = usuarioRecibidoSSO.getUserInfo().getString("email").split("@")[0];

            if(RepositoryUsuario.getInstance().usernameExistente(usernameARegistrar)){
                Random random = new Random();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 5; i++) {
                    char randomChar = (char) ('a' + random.nextInt(26));
                    sb.append(randomChar);
                }
                usernameARegistrar = usernameARegistrar + sb.toString();
            }

            Colaborador colaboradorCreado;
            Usuario usuarioColaboradorCreado;

            String[] parts = jsonObject.get("registerDate").getAsString().split(", ");
            String[] dateParts = parts[0].split("/");
            String[] timeParts = parts[1].split(":");

            int day = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]);
            int year = Integer.parseInt(dateParts[2]);
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            int second = Integer.parseInt(timeParts[2]);

            LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute, second);
            System.out.println("Converted LocalDateTime: " + localDateTime);

            Persona personaExistente;
            Usuario usuarioExistente;
            if (userType.equals("Persona Humana")) {
                String nameRecibido = usuarioRecibidoSSO.getUserInfo().getString("given_name");
                String surnameRecibido = usuarioRecibidoSSO.getUserInfo().getString("family_name");
                String dniRecibido = jsonObject.get("dni").getAsString();

                personaExistente = colaboradorExistente(nameRecibido, surnameRecibido, dniRecibido);

                int posicionUsuario;
                // si el usuario existe, no se cambia nada y se lo deja pasar porque ya es colaborador
                if (personaExistente != null) {
                    PersonaFisica personaFisica = (PersonaFisica) personaExistente;
                    Colaborador colaboradorExistente = RepositoryColaborador.getInstance().getColaboradoresDelSistema().stream()
                                                        .filter(c -> c.getPersona().equals(personaFisica))
                                                        .findFirst()
                                                        .orElse(null);
                    Mail mail = new Mail(usuarioRecibidoSSO.getUserInfo().getString("email"));
                    personaFisica.agregar_medio_de_contacto(mail);
                    usuarioExistente = colaboradorExistente.getUsuario();
                    posicionUsuario = RepositoryUsuario.getInstance().getUsuarios().indexOf(usuarioExistente);
                    // Respuesta al cliente
                    ctx.contentType("application/json");
                    ctx.result(new Gson().toJson(String.valueOf(posicionUsuario + 1)));
                    return;
                }
                // si llego aca, no existe como colaborador, revisamos si existe como tecnico
                personaExistente = tecnicoExistente(nameRecibido, surnameRecibido, dniRecibido);
                if (personaExistente != null) {
                    colaboradorCreado = new Colaborador(personaExistente);
                    PersonaFisica personaFisica = (PersonaFisica) personaExistente;
                    Tecnico tecnicoExistente = RepositoryTecnicos.getInstance().getTecnicos().stream()
                            .filter(t -> t.getPersona().equals(personaFisica))
                            .findFirst()
                            .orElse(null);
                    colaboradorCreado.setFechaIngreso(localDateTime);
                    colaboradorCreado.setUsuario(tecnicoExistente.getUsuario());
                    usuarioExistente = tecnicoExistente.getUsuario();
                    posicionUsuario = RepositoryUsuario.getInstance().getUsuarios().indexOf(usuarioExistente);
                    // Respuesta al cliente
                    ctx.contentType("application/json");
                    ctx.result(new Gson().toJson(String.valueOf(posicionUsuario + 1)));
                    return;
                }
            }
            // verifico tambien para una persona juridica
            else if (userType.equals("Persona Jurídica")) {
                String razonSocialRecibida = usuarioRecibidoSSO.getUserInfo().getString("name");
                String cuitRecibido = jsonObject.get("cuit").getAsString();

                personaExistente = empresaColaboradoraExistente(razonSocialRecibida, cuitRecibido);

                int posicionUsuario;
                // si el usuario existe, no se cambia nada y se lo deja pasar porque ya es colaborador
                if (personaExistente != null) {
                    PersonaJuridica personaJuridica = (PersonaJuridica) personaExistente;
                    Colaborador colaboradorExistente = RepositoryColaborador.getInstance().getColaboradoresDelSistema().stream()
                            .filter(c -> c.getPersona().equals(personaJuridica))
                            .findFirst()
                            .orElse(null);
                    Mail mail = new Mail(usuarioRecibidoSSO.getUserInfo().getString("email"));
                    personaJuridica.agregar_medio_de_contacto(mail);
                    usuarioExistente = colaboradorExistente.getUsuario();
                    posicionUsuario = RepositoryUsuario.getInstance().getUsuarios().indexOf(usuarioExistente);
                    // Respuesta al cliente
                    ctx.contentType("application/json");
                    ctx.result(new Gson().toJson(String.valueOf(posicionUsuario + 1)));
                    return;
                }
            }

            // sino, significa que es un usuario nuevo
            UsuariosRecibidos usuario = new UsuariosRecibidos(
                    jsonObject.get("address").getAsString(),
                    usernameARegistrar,
                    UUID.randomUUID().toString(),
                    jsonObject.get("email").getAsString(),
                    jsonObject.get("phone").getAsString(),
                    jsonObject.get("whatsapp").getAsString(),
                    jsonObject.get("telegram").getAsString()
            );
            List<MedioDeContacto> mediosDeContacto = new ArrayList<>();
            usuario.convertirMediosContactoAObjetos(mediosDeContacto);
            Ubicacion ubicacion = usuario.convertirUbicacionAObjeto(jsonObject.get("address").getAsString());

            JsonObject mensajeRespuesta = new JsonObject();
            String descripcion = "Desea agregar una descripción...";
            String imagen = "../images/users_images/imagen_registro.png";

            //empieza vacia, pero despues se llena
            List<MedioDeContacto> mediosDeContactoUsuario = new ArrayList<>();

            // Match directo con el tipo de usuario
            switch (userType) {
                case "Persona Humana":
                    // Extraer datos específicos para una persona física directamente desde el JSON
                    String nameFisico = usuarioRecibidoSSO.getUserInfo().getString("given_name");
                    String surnameFisico = usuarioRecibidoSSO.getUserInfo().getString("family_name");
                    String birthdateFisico = jsonObject.get("birthdate").getAsString();
                    String dniFisico = jsonObject.get("dni").getAsString();

                    LocalDate birthdatePersona = LocalDate.parse(birthdateFisico);
                    String numeroDni = dniFisico.split(" \\(")[0];
                    TipoDocumentacion tipoDni = TipoDocumentacion.valueOf(dniFisico.split(" \\(")[1].split("\\)")[0]);
                    Documentacion documento = new Documentacion(tipoDni, numeroDni);

                    PersonaFisica personaFisica = new PersonaFisica(mediosDeContactoUsuario, ubicacion, nameFisico, surnameFisico, birthdatePersona, documento);

                    for (MedioDeContacto medioDeContacto : mediosDeContacto) {
                        personaFisica.agregar_medio_de_contacto(medioDeContacto);
                    }

                    personaFisica.agregar_medio_de_contacto(new Mail(usuarioRecibidoSSO.getUserInfo().getString("email")));

                    colaboradorCreado = new Colaborador(personaFisica);
                    colaboradorCreado.setFechaIngreso(localDateTime);

                    usuarioColaboradorCreado = new Usuario(usuario.getUsername(), usuario.getPassword(), descripcion, imagen, personaFisica);
                    colaboradorCreado.setUsuario(usuarioColaboradorCreado);
                    mensajeRespuesta.addProperty("id", String.valueOf(RepositoryUsuario.getInstance().getUsuarios().size()));
                    mensajeRespuesta.addProperty("tipo", "H");
                    break;
                case "Persona Jurídica":
                    // Extraer datos específicos para una persona jurídica directamente desde el JSON
                    String razonSocial = usuarioRecibidoSSO.getUserInfo().getString("name");
                    String companyType = jsonObject.get("companyType").getAsString();
                    String rubro = jsonObject.get("rubro").getAsString();
                    String cuit = jsonObject.get("cuit").getAsString();

                    PersonaJuridica personaJuridica = new PersonaJuridica(mediosDeContactoUsuario, ubicacion, razonSocial, TipoJuridico.valueOf(companyType.toUpperCase()), rubro, cuit);

                    for (MedioDeContacto medioDeContacto : mediosDeContacto) {
                        personaJuridica.agregar_medio_de_contacto(medioDeContacto);
                    }

                    personaJuridica.agregar_medio_de_contacto(new Mail(usuarioRecibidoSSO.getUserInfo().getString("email")));

                    colaboradorCreado = new Colaborador(personaJuridica);
                    colaboradorCreado.setFechaIngreso(localDateTime);
                    usuarioColaboradorCreado = new Usuario(usuario.getUsername(), usuario.getPassword(), descripcion, imagen, personaJuridica);
                    colaboradorCreado.setUsuario(usuarioColaboradorCreado);
                    mensajeRespuesta.addProperty("id", String.valueOf(RepositoryUsuario.getInstance().getUsuarios().size()));
                    mensajeRespuesta.addProperty("tipo", "J");
                    break;
                default:
                    System.out.println("El usuario es un tipo desconocido");
                    mensajeRespuesta.addProperty("id", "-1");
                    mensajeRespuesta.addProperty("tipo", "Desconocido");
                    break;
            }

            // Respuesta al cliente
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(mensajeRespuesta));
        });
    }
}

