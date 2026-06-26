package receptorDeJSON.Receptores;

import colaboraciones.Colaboracion;
import colaboraciones.colaboracionesCompartidas.DonacionDeDinero;
import colaboraciones.colaboracionesHumanas.DistribucionDeViandas;
import colaboraciones.colaboracionesHumanas.DonacionDeViandas;
import colaboraciones.colaboracionesHumanas.RegistroDePersonasVulnerables;
import colaboraciones.colaboracionesJuridicas.HacerseCargo;
import colaboraciones.colaboracionesJuridicas.OfrecerProductoReconocimiento;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import persona.personas.PersonaFisica;
import persona.roles.Usuario;
import persona.roles.colaborador.Colaborador;
import persona.roles.personaEnSituacionVulnerable.PersonaEnSituacionVulnerable;
import repository.RepositoryColaborador;
import repository.RepositoryPersonasVulnerables;
import repository.RepositoryUsuario;

import java.time.format.DateTimeFormatter;

public class ColaboracionesRealizadas {
    public static void ejecutarColaboracionesRealizadas(Javalin app) {
        app.post("/colaboracionesRealizadas", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            // Parsear el JSON a un objeto genérico usando Gson o JsonParser
            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();

            Usuario usuarioColaborador = RepositoryUsuario.getInstance().getUsuarios().get(jsonObject.get("userId").getAsInt() - 1);
            Colaborador colaborador = RepositoryColaborador.getInstance().getColaboradoresDelSistema().stream()
                    .filter(c -> c.getPersona().equals(usuarioColaborador.getPersona()))
                    .findFirst()
                    .orElse(null);

            JsonObject jsonListColaboraciones = new JsonObject();

            int index = 0;
            for (Colaboracion colaboracion : colaborador.getColaboracionesRealizadas()) {
                JsonObject jsonColaboracion = new JsonObject();

                jsonColaboracion.addProperty("id", String.valueOf(index + 1));
                jsonColaboracion.addProperty("tipo", colaboracion.getTipoColaboracion().toString());

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                jsonColaboracion.addProperty("fecha", colaboracion.getFechaDeEjecucion().format(formatter));

                switch (colaboracion.getTipoColaboracion()) {
                    case DONACION_DE_DINERO:
                        DonacionDeDinero donacionDeDinero = (DonacionDeDinero) colaboracion;
                        jsonColaboracion.addProperty("tipo", "Donación de Dinero");
                        jsonColaboracion.addProperty("monto", donacionDeDinero.getMonto());
                        jsonColaboracion.addProperty("frecuencia", donacionDeDinero.getFrecuencia().toString());
                        break;
                    case HACERSE_CARGO:
                        HacerseCargo hacerseCargo = (HacerseCargo) colaboracion;
                        jsonColaboracion.addProperty("tipo", "Hacerse Cargo");
                        jsonColaboracion.addProperty("heladera", hacerseCargo.getHeladeraParaHacerseCargo().getNombreHeladera());
                        String direccion = hacerseCargo.getUbicacionNueva().getCalle() + " "
                                            + hacerseCargo.getUbicacionNueva().getAltura() + ", "
                                            + hacerseCargo.getUbicacionNueva().getCiudad().getNombre() + ", "
                                            + hacerseCargo.getUbicacionNueva().getCiudad().getPais().getNombre();
                        jsonColaboracion.addProperty("ubicacion", direccion);
                        break;
                    case DONACION_DE_VIANDAS:
                        DonacionDeViandas donacionDeViandas = (DonacionDeViandas) colaboracion;
                        jsonColaboracion.addProperty("tipo", "Donación de Viandas");
                        jsonColaboracion.addProperty("cantidadViandas", "1");
                        if (donacionDeViandas.getHeladera() == null) {
                            jsonColaboracion.addProperty("heladera", "Heladera desconocida");
                        } else {
                            jsonColaboracion.addProperty("heladera", donacionDeViandas.getHeladera().getNombreHeladera());
                        }
                        break;
                    case DISTRIBUCION_DE_VIANDAS:
                        DistribucionDeViandas distribucionDeViandas = (DistribucionDeViandas) colaboracion;
                        jsonColaboracion.addProperty("tipo", "Distribución de Viandas");
                        jsonColaboracion.addProperty("cantidadViandas", distribucionDeViandas.getCantidadDeViandas());
                        if (distribucionDeViandas.getMotivoDistribucion() == null) {
                            jsonColaboracion.addProperty("motivo", "Motivo desconocido");
                        } else {
                            jsonColaboracion.addProperty("motivo", distribucionDeViandas.getMotivoDistribucion().toString());
                        }
                        if (distribucionDeViandas.getHeladeraOrigen() == null) {
                            jsonColaboracion.addProperty("heladeraOrigen", "Heladera desconocida");
                        } else {
                            jsonColaboracion.addProperty("heladeraOrigen", distribucionDeViandas.getHeladeraOrigen().getNombreHeladera());
                        }
                        if (distribucionDeViandas.getHeladeraDestino() == null) {
                            jsonColaboracion.addProperty("heladeraDestino", "Heladera desconocida");
                        } else {
                            jsonColaboracion.addProperty("heladeraDestino", distribucionDeViandas.getHeladeraDestino().getNombreHeladera());
                        }
                        break;
                    case OFRECER_PRODUCTO_RECONOCIMIENTO:
                        OfrecerProductoReconocimiento ofrecerProductoReconocimiento = (OfrecerProductoReconocimiento) colaboracion;
                        jsonColaboracion.addProperty("tipo", "Ofrecimiento de Producto");
                        jsonColaboracion.addProperty("producto", ofrecerProductoReconocimiento.getProducto().getNombre());
                        jsonColaboracion.addProperty("puntosNecesarios", ofrecerProductoReconocimiento.getProducto().getPuntos_necesarios());
                        jsonColaboracion.addProperty("categoria", ofrecerProductoReconocimiento.getProducto().getCategoria().toString());
                        jsonColaboracion.addProperty("descripcion", ofrecerProductoReconocimiento.getProducto().getDescripcion());
                        break;
                    case REGISTRO_DE_PERSONAS_VULNERABLES:
                        RegistroDePersonasVulnerables registroDePersonasVulnerables = (RegistroDePersonasVulnerables) colaboracion;
                        PersonaFisica personaVulnerable = (PersonaFisica) registroDePersonasVulnerables.getPersonaVulnerable().getPersona();
                        jsonColaboracion.addProperty("tipo", "Registro de Personas Vulnerables");
                        if (personaVulnerable == null) {
                            jsonColaboracion.addProperty("persona", "Persona vulnerable desconocida");
                        } else {
                            jsonColaboracion.addProperty("persona", personaVulnerable.getNombre() + " " + personaVulnerable.getApellido());
                            if (registroDePersonasVulnerables.getCodigo() == null) {
                                jsonColaboracion.addProperty("tutor", "No tiene tutor");
                            } else {
                                int idPersonaVulnerableTutor = Integer.parseInt(registroDePersonasVulnerables.getCodigo());
                                PersonaEnSituacionVulnerable tutor = RepositoryPersonasVulnerables.getInstance().getPersonasVulnerables().get(idPersonaVulnerableTutor - 1);
                                PersonaFisica personaFisicaTutor = (PersonaFisica) tutor.getPersona();
                                jsonColaboracion.addProperty("tutor", personaFisicaTutor.getNombre() + " " + personaFisicaTutor.getApellido());
                            }
                        }
                        break;
                }

                jsonListColaboraciones.add("colaboracion_" + index, jsonColaboracion);
                index++;
            }
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(jsonListColaboraciones));
        });
    }
}