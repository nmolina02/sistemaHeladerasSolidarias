import Heladera.Heladera;
import Heladera.Modelo;
import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;
import localizacion.APIUbicacion.APIUbicacion;
import localizacion.APIUbicacion.Punto;
import localizacion.Ciudad;
import localizacion.Pais;
import localizacion.Ubicacion;
import medioDeContacto.Mail;
import medioDeContacto.MedioDeContacto;
import persona.personas.PersonaJuridica;
import persona.personas.TipoJuridico;
import persona.roles.colaborador.Colaborador;

import java.util.ArrayList;

import static receptorDeJSON.Receptores.MarcadoresReceptor.ejecutarMarcadoresReceptor;

public class PruebaAgregarHeladeras {
    public static void main(String[] args) {
        Javalin app = Javalin.create(JavalinConfig::enableCorsForAllOrigins).start(4567);

        Punto punto1 = new Punto("-34.603722", "-58.381592", "500");
        APIUbicacion apiUbicacion = APIUbicacion.getInstance();
        Modelo modelo = new Modelo("modelo","marca",10, 10f, 2f );
        apiUbicacion.sugerir_puntos(punto1);
        String[] opcionElegida = apiUbicacion.puntosRecomendados.get(0);
        Pais pais = new Pais(apiUbicacion.obtener_pais(opcionElegida));
        Ciudad ciudad = new Ciudad(apiUbicacion.obtener_ciudad(opcionElegida), pais);
        String calle = apiUbicacion.obtener_calle(opcionElegida);
        String altura = apiUbicacion.obtener_altura(opcionElegida);
        Ubicacion puntoElegido = apiUbicacion.elegir_opcion(opcionElegida, ciudad, calle, altura);

        Pais pais1 = new Pais("Argentina");
        Ciudad ciudad1 = new Ciudad("CABA", pais1);
        Ubicacion ubicacionHum1 = new Ubicacion("-34.6033829", "-58.3814415", ciudad1, "Avenida Corrientes", "857");
        ArrayList<MedioDeContacto> mediosDeContacto2 = new ArrayList<>();
        Mail mail2 = new Mail("probando@gmail.com");
        mediosDeContacto2.add(mail2);
        PersonaJuridica persona1 = new PersonaJuridica(mediosDeContacto2, ubicacionHum1, "Dabra.SA", TipoJuridico.EMPRESA , "Ventas", "123456789");
        PersonaJuridica persona2 = new PersonaJuridica(mediosDeContacto2, ubicacionHum1, "LoDePepe", TipoJuridico.EMPRESA , "Gastronomia", "987654321");
        Colaborador colaboradorJur1 = new Colaborador(persona1);
        Colaborador colaboradorJur2 = new Colaborador(persona2);

        Heladera heladera10 = new Heladera(puntoElegido, "Heladera El Ganador", modelo, colaboradorJur1);

        String[] opcionElegida2 = apiUbicacion.puntosRecomendados.get(1);
        Pais pais2 = new Pais(apiUbicacion.obtener_pais(opcionElegida2));
        Ciudad ciudad2 = new Ciudad(apiUbicacion.obtener_ciudad(opcionElegida2), pais2);
        String calle2 = apiUbicacion.obtener_calle(opcionElegida2);
        String altura2 = apiUbicacion.obtener_altura(opcionElegida2);
        Ubicacion puntoElegido2 = apiUbicacion.elegir_opcion(opcionElegida2, ciudad2, calle2, altura2);
        Heladera heladera20 = new Heladera(puntoElegido2, "Heladera Corrientes", modelo, colaboradorJur2);

        ejecutarMarcadoresReceptor(app);
    }
}
