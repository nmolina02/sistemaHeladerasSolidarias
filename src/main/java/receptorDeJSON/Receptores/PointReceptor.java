package receptorDeJSON.Receptores;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import Heladera.Heladera;
import Heladera.Modelo;
import Heladera.Vianda;
import colaboraciones.TipoColaboracion;
import colaboraciones.colaboracionesCompartidas.Frecuencia;
import colaboraciones.colaboracionesHumanas.MotivoDistribucion;
import com.google.gson.Gson;
import io.javalin.http.UploadedFile;
import localizacion.Ubicacion;
import medioDeContacto.MedioDeContacto;
import persistencia.ClaseCRUD;
import persona.documentacion.Documentacion;
import persona.documentacion.TipoDocumentacion;
import persona.personas.PersonaFisica;
import persona.roles.colaborador.Colaborador;
import persona.roles.personaEnSituacionVulnerable.PersonaEnSituacionVulnerable;
import premios.PremioColaboracion;
import premios.TipoCategoria;
import persona.roles.Usuario;
import receptorDeJSON.UsuariosRecibidos;
import repository.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static receptorDeJSON.Receptores.RecepcionDeArchivos.imagenDescargadaConExito;

public class PointReceptor {
    public static void ejecutarPointReceptor(Javalin app) {
        app.post("/points", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
            JsonObject colaboradorJson = jsonObject.get("colaborador").getAsJsonObject();

            Usuario usuarioColaborador = RepositoryUsuario.getInstance().getUsuarios().get(colaboradorJson.get("userId").getAsInt() - 1);
            Colaborador colaborador = RepositoryColaborador.getInstance().getColaboradoresDelSistema().stream()
                    .filter(c -> c.getPersona().equals(usuarioColaborador.getPersona()))
                    .findFirst()
                    .orElse(null);

            // Obtener el tipo de colaboración
            TipoColaboracion tipoColaboracion = TipoColaboracion.valueOf(jsonObject.get("tipoColaboracion").getAsString().toUpperCase());
            System.out.println("Tipo de colaboración: " + tipoColaboracion);
            switch (tipoColaboracion) {
                case DONACION_DE_DINERO:
                    double monto = jsonObject.get("monto").getAsDouble();
                    Frecuencia frecuencia = Frecuencia.valueOf(jsonObject.get("frecuencia").getAsString().toUpperCase());
                    colaborador.realizar_colaboracion(tipoColaboracion, LocalDate.now(), monto, frecuencia);
                    break;
                case DONACION_DE_VIANDAS:
                    Vianda vianda = convertirViandaAObjeto(jsonObject);
                    String nombreHeladeraDonacion = jsonObject.get("heladera").getAsString();
                    Heladera heladera = RepositoryHeladera.getInstance().getHeladerasDelSistema().stream()
                            .filter(h -> h.getNombreHeladera().equals(nombreHeladeraDonacion))
                            .findFirst()
                            .orElse(null);
                    colaborador.realizar_colaboracion(tipoColaboracion, vianda, heladera);
                    if (!colaborador.getColaboracionesRealizadas().get(colaborador.getColaboracionesRealizadas().size() - 1).getTipoColaboracion().equals(TipoColaboracion.DONACION_DE_VIANDAS)) {
                        ClaseCRUD.getInstance().getObjectList().remove(vianda);
                    }
                    break;
                case DISTRIBUCION_DE_VIANDAS:
                    String nombreHeladeraOrigen = jsonObject.get("heladeraOrigen").getAsString();
                    String nombreHeladeraDestino = jsonObject.get("heladeraDestino").getAsString();
                    Heladera heladeraOrigen = RepositoryHeladera.getInstance().getHeladerasDelSistema().stream()
                            .filter(h -> h.getNombreHeladera().equals(nombreHeladeraOrigen))
                            .findFirst()
                            .orElse(null);
                    Heladera heladeraDestino = RepositoryHeladera.getInstance().getHeladerasDelSistema().stream()
                            .filter(h -> h.getNombreHeladera().equals(nombreHeladeraDestino))
                            .findFirst()
                            .orElse(null);
                    int cantidadDeViandas = jsonObject.get("cantidadViandas").getAsInt();
                    MotivoDistribucion motivoDistribucion = MotivoDistribucion.valueOf(jsonObject.get("motivoDistribucion").getAsString().toUpperCase());
                    colaborador.realizar_colaboracion(tipoColaboracion, heladeraOrigen, heladeraDestino, cantidadDeViandas, motivoDistribucion, LocalDate.now());
                    break;
                case HACERSE_CARGO:
                    String fileName = "";
                    if (jsonObject.has("fileName") && !jsonObject.get("fileName").isJsonNull()) {
                        fileName = jsonObject.get("fileName").getAsString();
                    }
                    String nombreHeladera = jsonObject.get("nombreHeladera").getAsString();
                    if (RepositoryHeladera.getInstance().getHeladerasDelSistema().stream().anyMatch(h -> h.getNombreHeladera().equals(nombreHeladera))) {
                        ctx.contentType("application/json");
                        ctx.result(new Gson().toJson("Hay una heladera registrada con ese nombre"));
                        return;
                    }
                    String nombreModelo = jsonObject.get("modelo").getAsString();
                    String marcaModelo = jsonObject.get("marcaModelo").getAsString();
                    int capacidadHeladeraEnLitros = jsonObject.get("heladeraCapacity").getAsInt();
                    Modelo modelo = RepositoryModelo.getInstance().getModelos().stream()
                            .filter(m -> m.getNombre().equals(nombreModelo) && m.getMarca().equals(marcaModelo) && m.getCapacidadMaxima() == capacidadHeladeraEnLitros)
                            .findFirst()
                            .orElse(null);
                    if (modelo == null) {
                        modelo = new Modelo(nombreModelo, marcaModelo, capacidadHeladeraEnLitros, 10.0f, -10.0f);
                    }
                    String ubicacionRegistrada = jsonObject.get("heladeraLocation").getAsString();
                    UsuariosRecibidos usuarioAux = new UsuariosRecibidos();
                    Ubicacion ubicacion = usuarioAux.solicitarUbicacion(ubicacionRegistrada);
                    colaborador.realizar_colaboracion(tipoColaboracion, nombreHeladera, modelo, ubicacion, fileName);
                    if(imagenDescargadaConExito()) {
                        System.out.println("Imagen descargada con éxito");
                    } else {
                        System.out.println("Error al descargar la imagen");
                    }
                    break;
                case REGISTRO_DE_PERSONAS_VULNERABLES:
                    JsonObject personaVuln = jsonObject.get("personaVulnerable").getAsJsonObject();
                    UsuariosRecibidos usuario = new UsuariosRecibidos(
                            personaVuln.get("address").getAsString(),
                            "",
                            "",
                            personaVuln.get("email").getAsString(),
                            personaVuln.get("phone").getAsString(),
                            personaVuln.get("whatsapp").getAsString(),
                            personaVuln.get("telegram").getAsString()
                    );
                    List<MedioDeContacto> mediosDeContacto = new ArrayList<>();
                    Ubicacion ubicacionPersonaVulnerable = null;
                    usuario.convertirMediosContactoAObjetos(mediosDeContacto);

                    if (!usuario.getAddress().isEmpty()){
                        UsuariosRecibidos usuarioAuxVuln = new UsuariosRecibidos();
                        ubicacionPersonaVulnerable = usuarioAuxVuln.solicitarUbicacion(personaVuln.get("address").getAsString());
                    }

                    String nameFisico = personaVuln.get("name").getAsString();
                    String surnameFisico = personaVuln.get("surname").getAsString();
                    String birthdateFisico = personaVuln.get("birthdate").getAsString();
                    String dniFisico = personaVuln.get("dni").getAsString();

                    LocalDate birthdatePersona;
                    Documentacion documento = null;

                    if (birthdateFisico.isEmpty() || dniFisico.equals(" (0)")) {
                        System.out.println("No tiene documentacion");
                        String edad = personaVuln.get("age").getAsString();
                        // calculamos la fecha estimada de nacimiento
                        birthdatePersona = LocalDate.now().minusYears(Integer.parseInt(edad));
                    }
                    else {
                        birthdatePersona = LocalDate.parse(birthdateFisico);
                        String numeroDni = dniFisico.split(" \\(")[0];
                        TipoDocumentacion tipoDni = TipoDocumentacion.valueOf(dniFisico.split(" \\(")[1].split("\\)")[0]);
                        documento = new Documentacion(tipoDni, numeroDni);
                    }

                    boolean esMenor = Boolean.parseBoolean(jsonObject.get("esMenor").getAsString());
                    String tutorId = jsonObject.get("tutor").getAsString();

                    PersonaFisica personaFisica = new PersonaFisica(mediosDeContacto, ubicacionPersonaVulnerable, nameFisico, surnameFisico, birthdatePersona, documento);
                    PersonaEnSituacionVulnerable personaEnSituacionVulnerable = new PersonaEnSituacionVulnerable(personaFisica, esMenor, null);
                    colaborador.realizar_colaboracion(tipoColaboracion, personaEnSituacionVulnerable, tutorId);
                    break;
                case OFRECER_PRODUCTO_RECONOCIMIENTO:
                    int puntosNecesarios = Integer.parseInt(jsonObject.get("puntosNecesarios").getAsString()); //TODO: revisar!!!
                    String fotoPremio = jsonObject.get("productPhoto").getAsString();
                    String nombrePremio = jsonObject.get("productName").getAsString();
                    TipoCategoria categoria = TipoCategoria.valueOf(jsonObject.get("category").getAsString().toUpperCase());
                    String descripcion = jsonObject.get("descripcion").getAsString();
                    PremioColaboracion premioColaboracion = new PremioColaboracion(nombrePremio, puntosNecesarios, fotoPremio, categoria, descripcion);
                    colaborador.realizar_colaboracion(tipoColaboracion, premioColaboracion);
                    break;
                default:
                    System.out.println("El usuario es un tipo desconocido");
                    break;
            }

            JsonObject respuesta = new JsonObject();
            respuesta.addProperty("username", usuarioColaborador.getUsername());
            respuesta.addProperty("points", colaborador.getGestorDePuntaje().getPuntosTotales());
            respuesta.addProperty("userImage", usuarioColaborador.getImagen());

            System.out.println("Respuesta: " + respuesta);

            // Responder con tipo de contenido JSON
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(respuesta));
        });
    }

    private static Vianda convertirViandaAObjeto(JsonObject jsonObject) {
        if (jsonObject.get("calorias").getAsString().isEmpty() ||
                jsonObject.get("peso").getAsString().isEmpty()) {
            LocalDate fechaVencimientoVianda = LocalDate.parse(jsonObject.get("fechaVencimiento").getAsString());
            String comida = jsonObject.get("comida").getAsString();
            return new Vianda(comida, 0, 0, fechaVencimientoVianda);
        }
        int caloriasVianda = jsonObject.get("calorias").getAsInt();
        int pesoVianda = jsonObject.get("peso").getAsInt();
        LocalDate fechaVencimientoVianda = LocalDate.parse(jsonObject.get("fechaVencimiento").getAsString());
        String comida = jsonObject.get("comida").getAsString();
        return new Vianda(comida, caloriasVianda, pesoVianda, fechaVencimientoVianda);
    }
}
