package receptorDeJSON.Receptores;

import io.javalin.Javalin;
import localizacion.APIUbicacion.APIUbicacion;
import localizacion.APIUbicacion.Punto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import localizacion.Ciudad;
import localizacion.Pais;
import localizacion.Ubicacion;

import java.util.List;

public class RecomendadorDePuntosReceptor {
    public static void ejecutarRecomendadorDePuntosReceptor(Javalin app) {
        app.post("/localidadPais", ctx -> {
            // Obtener el cuerpo del request (JSON)
            String body = ctx.body();
            System.out.println("JSON recibido: " + body);  // Mostrar el JSON recibido

            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
            String direccion = jsonObject.get("direccion").getAsString();
            String radio = jsonObject.get("radio").getAsString();

            Pais pais = new Pais(direccion.split(", ")[2]);
            Ciudad ciudad = new Ciudad(direccion.split(", ")[1], pais);

            System.out.println("Pais: " + direccion.split(", ")[2]);
            System.out.println("Localidad: " + direccion.split(", ")[1]);

            APIUbicacion apiUbicacion = APIUbicacion.getInstance();
            Ubicacion ubicacion = apiUbicacion.buscar_latitud_longitud(direccion, ciudad);

            System.out.println("Latitud: " + ubicacion.getLatitud() + " Longitud: " + ubicacion.getLongitud());

            Punto punto = new Punto(ubicacion.getLatitud(), ubicacion.getLongitud(), radio);
            List<String[]> puntosRecomendados = apiUbicacion.sugerir_puntos(punto);

            // Respuesta al cliente
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(puntosRecomendados));
        });
    }
}