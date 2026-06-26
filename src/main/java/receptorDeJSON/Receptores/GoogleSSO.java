package receptorDeJSON.Receptores;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import config.EnvConfig;
import io.javalin.Javalin;
import org.json.JSONObject;
import persona.personas.PersonaFisica;
import persona.roles.Usuario;
import persona.roles.colaborador.Colaborador;
import persona.roles.tecnico.Tecnico;
import receptorDeJSON.UsuariosRecibidosSSO;
import repository.RepositoryColaborador;
import repository.RepositoryTecnicos;
import repository.RepositoryUsuario;
import repository.RepositoryUsuarioSSO;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class GoogleSSO {
    private static final String CLIENT_ID = EnvConfig.get("GOOGLE_CLIENT_ID");
    private static final String CLIENT_SECRET = EnvConfig.get("GOOGLE_CLIENT_SECRET");
    private static final String REDIRECT_URI = EnvConfig.get("GOOGLE_REDIRECT_URI");
    private static Colaborador colaboradorLogueadoGoogle;

    public static void ejecutarGoogleSSO(Javalin app) {
        app.post("/solicitudLoginGoogle", ctx -> {
            String message = ctx.body();
            System.out.println("Mensaje recibido: " + message);
            String authUrl = "https://accounts.google.com/o/oauth2/v2/auth"
                    + "?response_type=code"
                    + "&client_id=" + URLEncoder.encode(CLIENT_ID, "UTF-8")
                    + "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, "UTF-8")
                    + "&scope=" + URLEncoder.encode("https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email", "UTF-8");
            System.out.println("Redirigiendo a: " + authUrl);
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(authUrl));
        });

        app.get("/solicitudLoginGoogle", ctx -> {
            String code = ctx.queryParam("code");
            if (code != null) {
                String accessToken = getAccessToken(code);
                if (accessToken != null) {
                    JSONObject userInfo = getUserInfo(accessToken);
                    System.out.println("Inicio de sesión exitoso! Información del usuario: " + userInfo.toString(4));
                    Colaborador colaboradorExistente = RepositoryColaborador.getInstance().buscarColaborador(userInfo.getString("name"), userInfo.getString("email"));
                    if (colaboradorExistente != null) {
                        colaboradorLogueadoGoogle = colaboradorExistente;
                        ctx.redirect("http://heladerassolidarias.myvnc.com:4567/loading.html");
                        return;
                    }
                    UsuariosRecibidosSSO us = new UsuariosRecibidosSSO(userInfo, userInfo.getString("id"));
                    System.out.println("Usuario creado: " + us.getId());
                    ctx.contentType("application/json");
                    ctx.result(new Gson().toJson(userInfo.getString("id")));
                    ctx.redirect("http://heladerassolidarias.myvnc.com:4567/loading.html");
                } else {
                    ctx.status(400).result("No se pudo obtener el token de acceso.");
                }
            } else {
                ctx.status(400).result("No se recibió el código de autorización.");
            }
        });
    }

    private static String getAccessToken(String code) {
        try {
            URL url = new URL("https://oauth2.googleapis.com/token");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            String params = "code=" + code
                    + "&client_id=" + CLIENT_ID
                    + "&client_secret=" + CLIENT_SECRET
                    + "&redirect_uri=" + REDIRECT_URI
                    + "&grant_type=authorization_code";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(params.getBytes());
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getString("access_token");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static JSONObject getUserInfo(String accessToken) {
        try {
            URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            return new JSONObject(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void ejecutarLoggedInUserGoogle(Javalin app) {
        app.post("/loggedInUserGoogle", ctx -> {
            String message = ctx.body();
            System.out.println("Mensaje recibido: " + message);
            if (colaboradorLogueadoGoogle != null) {
                Usuario usuario = RepositoryUsuario.getInstance().getUsuarios().stream()
                        .skip(1)
                        .filter(u -> u.getPersona().equals(colaboradorLogueadoGoogle.getPersona()))
                        .findFirst()
                        .orElse(null);

                Tecnico tecnico = RepositoryTecnicos.getInstance().getTecnicos().stream()
                        .filter(t -> t.getPersona().equals(colaboradorLogueadoGoogle.getPersona()))
                        .findFirst()
                        .orElse(null);

                int posicionUsuario = RepositoryUsuario.getInstance().getUsuarios().indexOf(usuario);
                JsonObject respuesta = new JsonObject();
                respuesta.addProperty("userId", String.valueOf(posicionUsuario + 1));

                if (tecnico != null) {
                    respuesta.addProperty("userColaboradorRol", "Colaborador");
                    respuesta.addProperty("userTecnicoRol", "Técnico");
                    respuesta.addProperty("userType", "H");
                } else {
                    respuesta.addProperty("userColaboradorRol", "Colaborador");
                    respuesta.addProperty("userTecnicoRol", "");
                    if (colaboradorLogueadoGoogle.getPersona() instanceof PersonaFisica) {
                        respuesta.addProperty("userType", "H");
                    } else {
                        respuesta.addProperty("userType", "J");
                    }
                }
                colaboradorLogueadoGoogle = null;
                ctx.contentType("application/json");
                ctx.result(new Gson().toJson(respuesta));
            } else {
                ctx.contentType("application/json");
                ctx.result(new Gson().toJson("No se ha logueado algún colaborador por Google"));
            }
        });
    }

    public static void ejecutarSolicitudCurrentUserId(Javalin app) {
        app.post("/solicitudCurrentUserId", ctx -> {
            String message = ctx.body();
            System.out.println("Mensaje recibido: " + message);
            if (RepositoryUsuarioSSO.getInstance().getUsuariosSSO().isEmpty()) {
                ctx.contentType("application/json");
                ctx.result(new Gson().toJson("No se ha logueado algún colaborador por Google"));
                return;
            }
            String usuarioSSOId = RepositoryUsuarioSSO.getInstance().getUsuariosSSO().get(0).getId();
            JsonObject respuesta = new JsonObject();
            respuesta.addProperty("currentUserId", usuarioSSOId);
            if (colaboradorLogueadoGoogle != null) {
                respuesta.addProperty("colaboradorLogueadoGoogle", "true");
            } else {
                respuesta.addProperty("colaboradorLogueadoGoogle", "false");
            }
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(respuesta));
        });
    }
}
