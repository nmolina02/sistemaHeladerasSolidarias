/*import colaboraciones.colaboracionesHumanas.MotivoDistribucion;
import repository.RepositoryColaborador;
import Heladera.*;
import localizacion.APIUbicacion.APIUbicacion;
import localizacion.APIUbicacion.Punto;
import colaboraciones.TipoColaboracion;
import colaboraciones.colaboracionesCompartidas.Frecuencia;
import generadorDeCodigosUnicos.GeneradorDeCodigosUnicos;
import localizacion.Ciudad;
import localizacion.Pais;
import localizacion.Ubicacion;
import medioDeContacto.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persona.personas.PersonaFisica;
import persona.personas.PersonaJuridica;
import persona.roles.colaborador.Colaborador;
import persona.personas.TipoJuridico;
import persona.documentacion.Documentacion;
import persona.documentacion.TipoDocumentacion;
import persona.roles.personaEnSituacionVulnerable.PersonaEnSituacionVulnerable;
import tarjetas.TarjetaPersonaVulnerable;

import java.time.LocalDate;
import java.util.*;

import static colaboraciones.CargaMasivaDeColaboraciones.cargarColaboraciones;
import static colaboraciones.CargaMasivaDeColaboraciones.cargarColaboracionesMatriz;

public class Tests2daEntrega {
    private PersonaFisica persona1;
    private PersonaJuridica persona2;
    private PersonaFisica persona3;
    private PersonaFisica persona4;
    private PersonaJuridica persona5;
    private Colaborador colaboradorHum1;
    private Colaborador colaboradorJur1;
    private Colaborador colaboradorJur2;
    private Colaborador colaboradorHum2;
    private Heladera heladera1;
    private Heladera heladera2;
    private Vianda vianda1;
    private Vianda vianda2;
    private Vianda vianda3;
    private Vianda vianda4;
    private Vianda vianda5;
    private Modelo modelo;
    private PersonaEnSituacionVulnerable personaEnSituacionVulnerable1;
    private TarjetaPersonaVulnerable tarjeta1;
    private Ubicacion ubicacionHum1;
    private Ciudad ciudad1;
    private Ciudad ciudad2;
    private GeneradorDeCodigosUnicos generadorDeCodigosUnicos;
    private APIUbicacion apiUbicacion;

    @BeforeEach
    public void  init(){
        Mail mail1 = new Mail("prueba@gmail.com");
        Whatsapp whatsapp1 = new Whatsapp("1149672345");
        Telefono telefono1 = new Telefono("1149672346");
        Pais pais = new Pais("Argentina");
        ciudad1 = new Ciudad("CABA", pais);
        ciudad2 = new Ciudad("Gran Buenos Aires", pais);
        ubicacionHum1 = new Ubicacion("11", "50", ciudad1, "Calle falsa", "1234");
        Documentacion documentacion = new Documentacion(TipoDocumentacion.DNI, "20357934");
        LocalDate fechaNac = LocalDate.of(1988, 11, 10);
        ArrayList<MedioDeContacto> mediosDeContacto1 = new ArrayList<>();
        mediosDeContacto1.add(mail1);
        mediosDeContacto1.add(whatsapp1);
        mediosDeContacto1.add(telefono1);
        ArrayList<MedioDeContacto> mediosDeContacto2 = new ArrayList<>();
        Mail mail2 = new Mail("probando@gmail.com");
        mediosDeContacto2.add(mail2);
        persona1 = new PersonaFisica(mediosDeContacto1, ubicacionHum1, "Juan", "Perez", fechaNac, documentacion);
        persona2 = new PersonaJuridica(mediosDeContacto2, ubicacionHum1, "Dabra.SA", TipoJuridico.EMPRESA , "Ventas", "123456789");
        persona5 = new PersonaJuridica(mediosDeContacto2, ubicacionHum1, "LoDePepe", TipoJuridico.EMPRESA , "Gastronomia", "987654321");
        colaboradorHum1 = new Colaborador(persona1);
        colaboradorJur1 = new Colaborador(persona2);
        colaboradorJur2 = new Colaborador(persona5);

        Whatsapp whatsapp2 = new Whatsapp("1149672349");
        Ubicacion ubicacionHum2 = new Ubicacion("8", "10", ciudad2, "Callesita", "1234");
        Documentacion documentacion2 = new Documentacion(TipoDocumentacion.DNI, "23695147");
        LocalDate fechaNac2 = LocalDate.of(1974, 9, 1);
        ArrayList<MedioDeContacto> mediosDeContacto3 = new ArrayList<>();
        mediosDeContacto3.add(whatsapp2);
        persona3 = new PersonaFisica(mediosDeContacto3, ubicacionHum2, "Rodolfo", "Gonzalez", fechaNac2, documentacion2);
        colaboradorHum2 = new Colaborador(persona3);

        Ubicacion ubicacion1 = new Ubicacion("10", "10", ciudad1, "Calle imaginaria", "12");
        modelo = new Modelo("modelo", "marca", 10, 10f, 2f );
        heladera1 =  new Heladera(ubicacion1, "HeladeraUTN", modelo, colaboradorJur1);

        String comida1 = "Arroz con pollo";
        vianda1 = new Vianda(comida1, 130, 500, LocalDate.of(2024,6,23));
        vianda2 = new Vianda(comida1, 130, 500, LocalDate.of(2024,6,23));
        vianda3 = new Vianda(comida1, 130, 500, LocalDate.of(2024,6,23));
        vianda4 = new Vianda(comida1, 130, 500, LocalDate.of(2024,6,23));
        vianda5 = new Vianda(comida1, 130, 500, LocalDate.of(2024,6,23));

        Ubicacion ubicacion2 = new Ubicacion("23", "4", ciudad1, "Calle falsa", "456");
        heladera2 =  new Heladera(ubicacion2, "HeladeraUP", modelo, colaboradorJur2);

        Ubicacion ubicacionPersona = new Ubicacion("25", "2", ciudad1, "Calle imaginaria", "789");
        Documentacion documentacion1 = new Documentacion(TipoDocumentacion.DNI, "42948291");
        persona4 = new PersonaFisica(mediosDeContacto2, ubicacionPersona, "Jose", "Gonzalez", fechaNac, documentacion1);
        personaEnSituacionVulnerable1 = new PersonaEnSituacionVulnerable(persona4, false, new ArrayList<>());

        apiUbicacion = APIUbicacion.getInstance();
        generadorDeCodigosUnicos = GeneradorDeCodigosUnicos.getInstance();
    }

    @Test
    public void realizarColaboracion(){
        colaboradorHum1.realizar_colaboracion(TipoColaboracion.DONACION_DE_DINERO, LocalDate.now(), 100.00, Frecuencia.ANUAL);
        Assertions.assertEquals(
                50f,
                colaboradorHum1.getGestorDePuntaje().getPuntosTotales()
        );
    }

    @Test
    public void ingresarVianda(){
        // Prueba para el caso de distribucion de viandas con TarjetaColaborador
        colaboradorHum1.persona.setDireccion(null);
        heladera1.getGestorDeViandas().agregar_vianda(vianda1);
        heladera1.getGestorDeViandas().agregar_vianda(vianda2);
        heladera1.getGestorDeViandas().agregar_vianda(vianda3);
        heladera1.getGestorDeViandas().agregar_vianda(vianda4);
        heladera1.getGestorDeViandas().agregar_vianda(vianda5);
        colaboradorHum1.solicitar_apertura_heladera(heladera1);
        colaboradorHum1.realizar_colaboracion(TipoColaboracion.DISTRIBUCION_DE_VIANDAS, heladera1, heladera2, 4, MotivoDistribucion.DESPERFECTO, LocalDate.of(2024,6,23));
        colaboradorHum1.persona.setDireccion(ubicacionHum1);
        colaboradorHum1.solicitar_tarjeta();
        colaboradorHum1.realizar_colaboracion(TipoColaboracion.DISTRIBUCION_DE_VIANDAS, heladera1, heladera2, 4, MotivoDistribucion.DESPERFECTO, LocalDate.of(2024,6,23));
        Assertions.assertEquals(
                4,
                (heladera2.getGestorDeViandas().getViandas().size())
        );

        Assertions.assertEquals(
                1,
                (heladera1.getGestorDeViandas().getViandas().size())
        );
    }

    @Test
    void cargarColaboracionesTest() {
        // para este test consideramos como que ningun colaborador fue creado
        RepositoryColaborador.getInstance().getColaboradoresDelSistema().clear();

        cargarColaboracionesMatriz("resources/tests_resources/archivoCSV.csv");
        cargarColaboraciones();

        Assertions.assertEquals(3, RepositoryColaborador.getInstance().getColaboradoresDelSistema().stream().filter(colaborador -> colaborador.getPersona() instanceof PersonaFisica).count());
    }

    @Test
    public void apiUbicacion2(){
        Punto punto1 = new Punto("-34.603722", "-58.381592", "500");

        apiUbicacion.sugerir_puntos(punto1);
        String[] opcionElegida = apiUbicacion.puntosRecomendados.get(0);
        Pais pais = new Pais(apiUbicacion.obtener_pais(opcionElegida));
        Ciudad ciudad = new Ciudad(apiUbicacion.obtener_ciudad(opcionElegida), pais);
        String calle = apiUbicacion.obtener_calle(opcionElegida);
        String altura = apiUbicacion.obtener_altura(opcionElegida);
        Ubicacion puntoElegido = apiUbicacion.elegir_opcion(opcionElegida, ciudad, calle, altura);
        Heladera heladera10 = new Heladera(puntoElegido, "Heladera10", modelo, colaboradorJur1);

        Assertions.assertEquals(1, 1);
    }

    @Test
    void cambiarTemperaturasMaximas(){
        Assertions.assertThrows(RuntimeException.class, () -> heladera2.getController().cambiar_temperatura_usuario(15f, 5f));
    }

    @Test
    void extraerViandaParaPersonaVulnerableNoValido(){
        generadorDeCodigosUnicos.crearNumeroTarjeta();

        tarjeta1 = new TarjetaPersonaVulnerable(personaEnSituacionVulnerable1, generadorDeCodigosUnicos.ultimoNumeroGenerado);

        heladera1.getGestorDeViandas().agregar_vianda(vianda1);
        heladera1.getGestorDeViandas().agregar_vianda(vianda2);
        heladera1.getGestorDeViandas().agregar_vianda(vianda3);
        heladera1.getGestorDeViandas().agregar_vianda(vianda4);
        heladera1.getGestorDeViandas().agregar_vianda(vianda5);

        personaEnSituacionVulnerable1.setTarjeta(tarjeta1);
        personaEnSituacionVulnerable1.retirarVianda(heladera1, vianda1);
        personaEnSituacionVulnerable1.retirarVianda(heladera1, vianda2);
        personaEnSituacionVulnerable1.retirarVianda(heladera1, vianda3);
        personaEnSituacionVulnerable1.retirarVianda(heladera1, vianda4);
        Assertions.assertThrows(RuntimeException.class, () -> personaEnSituacionVulnerable1.getTarjeta().realizar_extraccion(heladera1, vianda5));
    }

    @Test
    void extraerViandaParaPersonaVulnerableValido(){
        generadorDeCodigosUnicos.crearNumeroTarjeta();

        tarjeta1 = new TarjetaPersonaVulnerable(personaEnSituacionVulnerable1, generadorDeCodigosUnicos.ultimoNumeroGenerado);

        personaEnSituacionVulnerable1.setTarjeta(tarjeta1);
        personaEnSituacionVulnerable1.retirarVianda(heladera1, vianda1);

        Assertions.assertEquals(
                0,
                (heladera1.getGestorDeViandas().getViandas().size())
        );

        Assertions.assertEquals(
                1,
                (tarjeta1.getExtraccionesRealizadas().size())
        );
    }

}
*/