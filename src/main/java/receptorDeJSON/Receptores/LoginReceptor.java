package receptorDeJSON.Receptores;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import persona.personas.PersonaFisica;
import persona.roles.colaborador.Colaborador;
import persona.roles.Usuario;
import persona.roles.tecnico.Tecnico;
import repository.RepositoryColaborador;
import repository.RepositoryTecnicos;
import repository.RepositoryUsuario;

import java.util.List;
import java.util.stream.Collectors;

public class LoginReceptor {
    public static void ejecutarLoginReceptor(Javalin app) {
        app.post("/solicitudLogin", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
            String username = jsonObject.get("username").getAsString();
            String password = jsonObject.get("password").getAsString();

            JsonObject respuesta = new JsonObject();

            List<Usuario> administradores = RepositoryUsuario.getInstance().getUsuarios().stream()
                    .filter(u -> u.getUsername().contains("administrador"))
                    .collect(Collectors.toList());

            for (Usuario administrador : administradores) {
                if (administrador.getUsername().equals(username) && administrador.getPassword().equals(password)) {
                    respuesta.addProperty("userId", String.valueOf(RepositoryUsuario.getInstance().getUsuarios().indexOf(administrador) + 1));
                    respuesta.addProperty("userType", "Administrador");
                    ctx.result(new Gson().toJson(respuesta));
                    return;
                }
            }

            Usuario usuario = RepositoryUsuario.getInstance().getUsuarios().stream()
                    .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                    .findFirst()
                    .orElse(null);
            System.out.println("Usuario: " + usuario.getUsername());

            int posicionUsuario = RepositoryUsuario.getInstance().getUsuarios().indexOf(usuario);

            System.out.println("Posicion del usuario: " + posicionUsuario);

            Colaborador colaborador = RepositoryColaborador.getInstance().getColaboradoresDelSistema().stream()
                    .filter(c -> c.getPersona().equals(usuario.getPersona()))
                    .findFirst()
                    .orElse(null);

            Tecnico tecnico = RepositoryTecnicos.getInstance().getTecnicos().stream()
                    .filter(t -> t.getPersona().equals(usuario.getPersona()))
                    .findFirst()
                    .orElse(null);

            ctx.contentType("application/json");
            respuesta.addProperty("userId", String.valueOf(posicionUsuario + 1));
            if (colaborador != null && tecnico != null) {
                respuesta.addProperty("userColaboradorRol", "Colaborador");
                respuesta.addProperty("userTecnicoRol", "Técnico");
                respuesta.addProperty("userType", "H");
            } else if (colaborador != null) {
                respuesta.addProperty("userColaboradorRol", "Colaborador");
                respuesta.addProperty("userTecnicoRol", "");
                if (colaborador.getPersona() instanceof PersonaFisica) {
                    respuesta.addProperty("userType", "H");
                } else {
                    respuesta.addProperty("userType", "J");
                }
            } else if (tecnico != null) {
                respuesta.addProperty("userColaboradorRol", "");
                respuesta.addProperty("userTecnicoRol", "Técnico");
                respuesta.addProperty("userType", "H");
            } else {
                respuesta.addProperty("userType", "Usuario no autenticado");
            }
            // Respuesta al cliente
            ctx.result(new Gson().toJson(respuesta));
        });
    }
}