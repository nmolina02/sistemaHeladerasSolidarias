import Heladera.Heladera;
import Heladera.incidente.Alerta.AlertaConexion;
import Heladera.incidente.GestorIncidentes;
import localizacion.APIUbicacion.Punto;
import localizacion.Ciudad;
import localizacion.Pais;
import localizacion.Ubicacion;
import lombok.Getter;
import medioDeContacto.Mail;
import medioDeContacto.MedioDeContacto;
import persona.personas.PersonaJuridica;
import persona.personas.TipoJuridico;
import persona.roles.colaborador.Colaborador;
import Heladera.Modelo;
import localizacion.APIUbicacion.APIUbicacion;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Main {
    @Getter private ArrayList<Colaborador>colaboradores = new ArrayList<>();
    public ArrayList<Heladera>heladeras = new ArrayList<>();

    public static void main(String[] args) {
        Punto punto1 = new Punto("-34.603722", "-58.381592", "500");
        APIUbicacion apiUbicacion = APIUbicacion.getInstance();
        Modelo modelo = new Modelo("Modelo", "marca", 10, 10f, 2f );
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
        PersonaJuridica persona1 = new PersonaJuridica(mediosDeContacto2, ubicacionHum1, "Dabra.SA", TipoJuridico.EMPRESA , "Ventas", "30123456789");
        Colaborador colaboradorJur1 = new Colaborador(persona1);

        Heladera heladera10 = new Heladera(puntoElegido, "Heladera El Campeon", modelo, colaboradorJur1);

        GestorIncidentes gestorIncidentes = GestorIncidentes.getInstance();
        AlertaConexion incidente = new AlertaConexion(LocalDateTime.now(), heladera10, "Incidente", 10f);

        System.out.println("La gravedad del incidente es: " + incidente.getGravedad());
    }
}