package receptorDeJSON.Receptores;

import Heladera.Heladera;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import persona.personas.Persona;
import persona.roles.colaborador.Colaborador;
import persona.roles.Usuario;
import repository.RepositoryColaborador;
import repository.RepositoryHeladera;
import repository.RepositoryUsuario;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RecomendadorDonacionesReceptor {
    public static void ejecutarRecomendadorDonacionesReceptor(Javalin app) {
        app.post("/recomendadorDonaciones", ctx -> {
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

            String mensajeRespuesta = "";

            if (colaborador != null) {
                String latitud = jsonObject.get("latitud").getAsString();
                String longitud = jsonObject.get("longitud").getAsString();
                String radioCercania = "3000"; // consideramos que es cercano lo que este a un maximo de 3 km por persona

                JsonObject jsonListaHeladeras = new JsonObject();
                for (Heladera heladera : RepositoryHeladera.getInstance().getHeladerasDelSistema()) {
                    if (heladera.getEstadoHeladera().toString().equals("DE_BAJA") || heladera.getEstadoHeladera().toString().equals("EN_REPARACION")) {
                        continue;
                    }
                    JsonObject jsonHeladera = new JsonObject();
                    jsonHeladera.addProperty("nombre", heladera.getNombreHeladera());
                    jsonHeladera.addProperty("latitud", heladera.getDireccion().getLatitud());
                    jsonHeladera.addProperty("longitud", heladera.getDireccion().getLongitud());
                    jsonHeladera.addProperty("capacidad", heladera.getModelo().getCapacidadMaxima());
                    jsonHeladera.addProperty("viandas", heladera.getGestorDeViandas().getViandas().size());
                    jsonHeladera.addProperty("estado", heladera.getEstadoHeladera().toString());
                    jsonListaHeladeras.add(heladera.getNombreHeladera(), jsonHeladera);
                }

                JsonObject jsonParametrosAPI = new JsonObject();
                jsonParametrosAPI.addProperty("latitud", latitud);
                jsonParametrosAPI.addProperty("longitud", longitud);
                jsonParametrosAPI.addProperty("radioCercania", radioCercania);
                jsonParametrosAPI.addProperty("viandasADonar", 1);
                jsonParametrosAPI.add("jsonListaHeladeras", jsonListaHeladeras);

                // Llamar al recomendador de donaciones
                //String url = "http://localhost:4568/filtrarHeladerasRecomendadas";
                String url = "http://177.71.196.77:4568/filtrarHeladerasRecomendadas";
                JsonObject heladerasRecomendadas = null;

                try {
                    // Levanto la conexion para comunicarme con la api
                    URL serverUrl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) serverUrl.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    // envio el mensaje
                    try (OutputStream os = connection.getOutputStream()) {
                        os.write(jsonParametrosAPI.toString().getBytes());
                        os.flush();
                    }

                    // espero la respuesta del servidor
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null) {
                                response.append(line);
                            }
                            System.out.println("Respuesta del servidor: " + response.toString());
                            heladerasRecomendadas = JsonParser.parseString(response.toString()).getAsJsonObject();
                        }
                    } else {
                        System.out.println("Error al enviar mensaje. Código: " + responseCode);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (heladerasRecomendadas == null) {
                    System.out.println("No se encontraron heladeras recomendadas");
                    mensajeRespuesta = "No se encontraron heladeras recomendadas";
                    // Respuesta negativa al cliente
                    ctx.contentType("application/json");
                    ctx.result(new Gson().toJson(mensajeRespuesta));
                } else {
                    // Respuesta positiva al cliente
                    ctx.contentType("application/json");
                    ctx.result(new Gson().toJson(heladerasRecomendadas));
                }
            } else {
                System.out.println("No se encontró el colaborador");
                mensajeRespuesta = "No se encontró el colaborador";
                // Respuesta negativa al cliente
                ctx.contentType("application/json");
                ctx.result(new Gson().toJson(mensajeRespuesta));
            }
        });
    }
}
