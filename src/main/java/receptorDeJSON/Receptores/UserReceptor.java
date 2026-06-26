package receptorDeJSON.Receptores;

import localizacion.APIUbicacion.APIUbicacion;
import localizacion.APIUbicacion.Punto;
import com.google.gson.Gson;
import io.javalin.Javalin;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import localizacion.Ubicacion;
import medioDeContacto.MedioDeContacto;
import persona.documentacion.Documentacion;
import persona.documentacion.TipoDocumentacion;
import persona.personas.Persona;
import persona.personas.PersonaFisica;
import persona.personas.PersonaJuridica;
import persona.personas.TipoJuridico;
import persona.roles.colaborador.Colaborador;
import persona.roles.tecnico.Tecnico;
import persona.roles.Usuario;
import receptorDeJSON.UsuariosRecibidos;
import repository.RepositoryColaborador;
import repository.RepositoryTecnicos;
import repository.RepositoryUsuario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserReceptor {
    public static void ejecutarUserReceptor(Javalin app) {
        app.post("/user", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            // Parsear el JSON a un objeto genérico usando Gson o JsonParser
            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();

            // Extraer el tipo de usuario directamente desde el JSON
            String userType = jsonObject.get("userType").getAsString();

            if(RepositoryUsuario.getInstance().usernameExistente(jsonObject.get("username").getAsString())){
                ctx.contentType("application/json");
                ctx.result(new Gson().toJson("El nombre de usuario ya existe"));
                return;
            }

            Colaborador colaboradorCreado;
            Usuario usuarioColaboradorCreado;
            Tecnico tecnicoCreado;
            Usuario usuarioTecnicoCreado;

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

            if (userType.equals("Técnico") || userType.equals("Persona Humana")) {
                String nameRecibido = jsonObject.get("name").getAsString();
                String surnameRecibido = jsonObject.get("surname").getAsString();
                String dniRecibido = jsonObject.get("dni").getAsString();

                Persona personaExistente = colaboradorExistente(nameRecibido, surnameRecibido, dniRecibido);
                Usuario usuarioExistente;
                int posicionUsuario;
                // existe como colaborador, creamos el tecnico
                if (personaExistente != null) {
                    String direccionRecibida = jsonObject.get("address").getAsString();
                    APIUbicacion apiUbicacion = APIUbicacion.getInstance();
                    String latitudRecibida = apiUbicacion.buscar_latitud_lugar(direccionRecibida);
                    String longitudRecibida = apiUbicacion.buscar_longitud_lugar(direccionRecibida);
                    Punto punto = new Punto(latitudRecibida, longitudRecibida, "5000");
                    PersonaFisica personaFisica = (PersonaFisica) personaExistente;
                    tecnicoCreado = new Tecnico(personaFisica, punto);
                    tecnicoCreado.setFechaIngreso(localDateTime);
                    Colaborador colaboradorExistente = RepositoryColaborador.getInstance().getColaboradoresDelSistema().stream()
                                                        .filter(c -> c.getPersona().equals(personaFisica))
                                                        .findFirst()
                                                        .orElse(null);
                    tecnicoCreado.setUsuario(colaboradorExistente.getUsuario());
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

            // sino, significa que es un usuario nuevo
            // sin embargo, si es una persona, revisamos si ya existe con el dni
            // en cambio, si es una empresa, revisamos si ya existe con el cuit
            if (userType.equals("Persona Humana") || userType.equals("Técnico")) {
                String dniRecibido = jsonObject.get("dni").getAsString();
                String numeroDni = dniRecibido.split(" \\(")[0];
                TipoDocumentacion tipoDni = TipoDocumentacion.valueOf(dniRecibido.split(" \\(")[1].split("\\)")[0]);
                PersonaFisica personaFisica = RepositoryColaborador.getInstance().getColaboradoresDelSistema().stream()
                        .filter(c -> c.getPersona() instanceof PersonaFisica)
                        .map(c -> (PersonaFisica) c.getPersona())
                        .filter(p -> p.getDocumento().getNumero().equals(numeroDni) && p.getDocumento().getTipoDocumentacion().equals(tipoDni))
                        .findFirst()
                        .orElse(null);
                if (personaFisica != null) {
                    ctx.contentType("application/json");
                    ctx.result(new Gson().toJson("La documentación ya fue registrada"));
                    return;
                }
                personaFisica = RepositoryTecnicos.getInstance().getTecnicos().stream()
                        .filter(c -> c.getPersona() instanceof PersonaFisica)
                        .map(c -> (PersonaFisica) c.getPersona())
                        .filter(p -> p.getDocumento().getNumero().equals(numeroDni) && p.getDocumento().getTipoDocumentacion().equals(tipoDni))
                        .findFirst()
                        .orElse(null);
                if (personaFisica != null) {
                    ctx.contentType("application/json");
                    ctx.result(new Gson().toJson("La documentación ya fue registrada"));
                    return;
                }
            } else if (userType.equals("Persona Jurídica")) {
                String cuitRecibido = jsonObject.get("cuit").getAsString();
                PersonaJuridica personaJuridica = RepositoryColaborador.getInstance().getColaboradoresDelSistema().stream()
                        .filter(c -> c.getPersona() instanceof PersonaJuridica)
                        .map(c -> (PersonaJuridica) c.getPersona())
                        .filter(p -> p.getCuit().equals(cuitRecibido))
                        .findFirst()
                        .orElse(null);
                if (personaJuridica != null) {
                    ctx.contentType("application/json");
                    ctx.result(new Gson().toJson("El CUIT ya fue registrado"));
                    return;
                }
            }
            UsuariosRecibidos usuario = new UsuariosRecibidos(
                    jsonObject.get("address").getAsString(),
                    jsonObject.get("username").getAsString(),
                    jsonObject.get("password").getAsString(),
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
                    String nameFisico = jsonObject.get("name").getAsString();
                    String surnameFisico = jsonObject.get("surname").getAsString();
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

                    colaboradorCreado = new Colaborador(personaFisica);
                    colaboradorCreado.setFechaIngreso(localDateTime);

                    usuarioColaboradorCreado = new Usuario(usuario.getUsername(), usuario.getPassword(), descripcion, imagen, personaFisica);
                    colaboradorCreado.setUsuario(usuarioColaboradorCreado);
                    mensajeRespuesta.addProperty("id", String.valueOf(RepositoryUsuario.getInstance().getUsuarios().size()));
                    mensajeRespuesta.addProperty("tipo", "H");
                    break;
                case "Persona Jurídica":
                    // Extraer datos específicos para una persona jurídica directamente desde el JSON
                    String razonSocial = jsonObject.get("razonSocial").getAsString();
                    String companyType = jsonObject.get("companyType").getAsString();
                    String rubro = jsonObject.get("rubro").getAsString();
                    String cuit = jsonObject.get("cuit").getAsString();

                    PersonaJuridica personaJuridica = new PersonaJuridica(mediosDeContactoUsuario, ubicacion, razonSocial, TipoJuridico.valueOf(companyType.toUpperCase()), rubro, cuit);

                    for (MedioDeContacto medioDeContacto : mediosDeContacto) {
                        personaJuridica.agregar_medio_de_contacto(medioDeContacto);
                    }

                    colaboradorCreado = new Colaborador(personaJuridica);
                    colaboradorCreado.setFechaIngreso(localDateTime);
                    usuarioColaboradorCreado = new Usuario(usuario.getUsername(), usuario.getPassword(), descripcion, imagen, personaJuridica);
                    colaboradorCreado.setUsuario(usuarioColaboradorCreado);
                    mensajeRespuesta.addProperty("id", String.valueOf(RepositoryUsuario.getInstance().getUsuarios().size()));
                    mensajeRespuesta.addProperty("tipo", "J");
                    break;
                case "Técnico":
                    // Extraer datos específicos para un técnico directamente desde el JSON
                    String nameTecnico = jsonObject.get("name").getAsString();
                    String surnameTecnico = jsonObject.get("surname").getAsString();
                    String birthdateTecnico = jsonObject.get("birthdate").getAsString();
                    String dniTecnico = jsonObject.get("dni").getAsString();

                    LocalDate birthdatePersonaTecnico = LocalDate.parse(birthdateTecnico);
                    String numeroDniTecnico = dniTecnico.split(" \\(")[0];
                    TipoDocumentacion tipoDniTecnico = TipoDocumentacion.valueOf(dniTecnico.split(" \\(")[1].split("\\)")[0]);
                    Documentacion documentoTecnico = new Documentacion(tipoDniTecnico, numeroDniTecnico);

                    PersonaFisica personaFisicaTecnico = new PersonaFisica(mediosDeContactoUsuario, ubicacion, nameTecnico, surnameTecnico, birthdatePersonaTecnico, documentoTecnico);

                    for (MedioDeContacto medioDeContacto : mediosDeContacto) {
                        personaFisicaTecnico.agregar_medio_de_contacto(medioDeContacto);
                    }

                    Punto punto = new Punto(ubicacion.getLatitud(), ubicacion.getLongitud(), "5000");
                    tecnicoCreado = new Tecnico(personaFisicaTecnico, punto);
                    tecnicoCreado.setFechaIngreso(localDateTime);
                    usuarioTecnicoCreado = new Usuario(usuario.getUsername(), usuario.getPassword(), descripcion, imagen, personaFisicaTecnico);
                    tecnicoCreado.setUsuario(usuarioTecnicoCreado);
                    mensajeRespuesta.addProperty("id", String.valueOf(RepositoryUsuario.getInstance().getUsuarios().size()));
                    mensajeRespuesta.addProperty("tipo", "H");
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

    public static Persona tecnicoExistente(String name, String surname, String dni) {
        for (Tecnico tecnico : RepositoryTecnicos.getInstance().getTecnicos()) {
            PersonaFisica persona = (PersonaFisica) tecnico.getPersona();
            return existePersonaFisica(persona, name, surname, dni);
        }
        return null;
    }

    public static Persona colaboradorExistente(String name, String surname, String dni) {
        for (Colaborador colaborador : RepositoryColaborador.getInstance().getColaboradoresDelSistema()) {
            if (colaborador.getPersona() instanceof PersonaJuridica) {
                continue;
            }
            PersonaFisica persona = (PersonaFisica) colaborador.getPersona();
            return existePersonaFisica(persona, name, surname, dni);
        }
        return null;
    }

    public static Persona empresaColaboradoraExistente(String razonSocial, String cuit) {
        for (Colaborador colaborador : RepositoryColaborador.getInstance().getColaboradoresDelSistema()) {
            if (colaborador.getPersona() instanceof PersonaFisica) {
                continue;
            }
            PersonaJuridica persona = (PersonaJuridica) colaborador.getPersona();
            return existePersonaJuridica(persona, razonSocial, cuit);
        }
        return null;
    }

    private static Persona existePersonaFisica(PersonaFisica personaFisica, String name, String surname, String dni) {
        String numeroDni = dni.split(" \\(")[0];
        TipoDocumentacion tipoDni = TipoDocumentacion.valueOf(dni.split(" \\(")[1].split("\\)")[0]);
        if (personaFisica.getDocumento().getNumero().equals(numeroDni)
                && personaFisica.getDocumento().getTipoDocumentacion().equals(tipoDni)
                && personaFisica.getNombre().equals(name)
                && personaFisica.getApellido().equals(surname)) {
            return personaFisica;
        }
        return null;
    }

    private static Persona existePersonaJuridica(PersonaJuridica personaJuridica, String razonSocial, String cuit) {
        if (personaJuridica.getCuit().equals(cuit) && personaJuridica.getRazonSocial().equals(razonSocial)) {
            return personaJuridica;
        }
        return null;
    }
}

