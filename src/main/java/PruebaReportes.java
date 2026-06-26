import Heladera.Heladera;
import Heladera.Modelo;
import Heladera.Vianda;
import colaboraciones.TipoColaboracion;
import colaboraciones.colaboracionesHumanas.MotivoDistribucion;
import localizacion.Ciudad;
import localizacion.Pais;
import localizacion.Ubicacion;
import medioDeContacto.Mail;
import medioDeContacto.MedioDeContacto;
import medioDeContacto.Telefono;
import medioDeContacto.Whatsapp;
import persona.documentacion.Documentacion;
import persona.documentacion.TipoDocumentacion;
import persona.personas.PersonaFisica;
import persona.personas.PersonaJuridica;
import persona.personas.TipoJuridico;
import persona.roles.colaborador.Colaborador;
import reportes.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PruebaReportes {

    public static List<Heladera> createHeladeras() {
        List<Heladera> heladeras = new ArrayList<>();
        Pais pais = new Pais("Argentina");
        Ciudad ciudad = new Ciudad("CABA", pais);
        Ubicacion ubicacion = new Ubicacion("-34.60322", "-58.3816", ciudad, "Calle imaginaria", "12");
        Modelo modelo = new Modelo("modelo", "marca",10, 10f, 2f);

        Ciudad ciudad1 = new Ciudad("CABA", pais);
        Ubicacion ubicacionHum1 = new Ubicacion("11", "50", ciudad1, "Calle falsa", "1234");
        ArrayList<MedioDeContacto> mediosDeContacto2 = new ArrayList<>();
        Mail mail2 = new Mail("probando@gmail.com");
        mediosDeContacto2.add(mail2);
        PersonaJuridica persona2 = new PersonaJuridica(mediosDeContacto2, ubicacionHum1, "LoDePepe", TipoJuridico.EMPRESA , "Gastronomia", "123456789");
        Colaborador colaboradorJur1 = new Colaborador(persona2);

        for (int i = 1; i <= 10; i++) {
            heladeras.add(new Heladera(ubicacion, "Heladera" + i, modelo, colaboradorJur1));
        }

        return heladeras;
    }

    public static void createColaboraciones() {
        for (int i = 0; i < 5; i++) {
            Mail mail1 = new Mail("prueba@gmail.com");
            Whatsapp whatsapp1 = new Whatsapp("1149672345");
            Telefono telefono1 = new Telefono("1149672346");
            Pais pais = new Pais("Argentina");
            Ciudad ciudad1 = new Ciudad("CABA", pais);
            Ubicacion ubicacionHum1 = new Ubicacion("11", "50", ciudad1, "Calle falsa", "1234");
            Documentacion documentacion = new Documentacion(TipoDocumentacion.DNI, "20357934");
            LocalDate fechaNac = LocalDate.of(1988, 11, 10);
            ArrayList<MedioDeContacto> mediosDeContacto1 = new ArrayList<>();
            mediosDeContacto1.add(mail1);
            mediosDeContacto1.add(whatsapp1);
            mediosDeContacto1.add(telefono1);
            PersonaFisica persona1 = new PersonaFisica(mediosDeContacto1, ubicacionHum1, "Juan", "Perez", fechaNac, documentacion);
            Colaborador colaborador1 = new Colaborador(persona1);
            colaborador1.solicitar_tarjeta();

            ArrayList<MedioDeContacto> mediosDeContacto2 = new ArrayList<>();
            Mail mail2 = new Mail("probando@gmail.com");
            mediosDeContacto2.add(mail2);
            PersonaJuridica persona2 = new PersonaJuridica(mediosDeContacto2, ubicacionHum1, "LoDePepe", TipoJuridico.EMPRESA , "Gastronomia", "123456789");
            Colaborador colaboradorJur1 = new Colaborador(persona2);

            Ubicacion ubicacion1 = new Ubicacion("-34.60322", "-58.3816", ciudad1, "Calle imaginaria", "12");
            Modelo modelo = new Modelo("modelo", "marca",100, 10f, 2f );
            Heladera heladera1 =  new Heladera(ubicacion1, "HeladeraUTN", modelo, colaboradorJur1);
            Heladera heladera2 =  new Heladera(ubicacion1, "HeladeraUTN2", modelo, colaboradorJur1);

            String comida1 = "Arroz con pollo";
            Vianda vianda1 = new Vianda(comida1, 130, 500, LocalDate.of(2024,6,23));
            Vianda vianda2 = new Vianda(comida1, 130, 500, LocalDate.of(2024,6,23));
            Vianda vianda3 = new Vianda(comida1, 130, 500, LocalDate.of(2024,6,23));
            Vianda vianda4 = new Vianda(comida1, 130, 500, LocalDate.of(2024,6,23));
            Vianda vianda5 = new Vianda(comida1, 130, 500, LocalDate.of(2024,6,23));
            Vianda vianda6 = new Vianda(comida1, 130, 500, LocalDate.of(2024,6,23));
            Vianda vianda7 = new Vianda(comida1, 130, 500, LocalDate.of(2024,6,23));
            heladera1.getGestorDeViandas().agregar_vianda(vianda1);
            heladera1.getGestorDeViandas().agregar_vianda(vianda2);
            heladera1.getGestorDeViandas().agregar_vianda(vianda3);
            heladera1.getGestorDeViandas().agregar_vianda(vianda4);
            heladera1.getGestorDeViandas().agregar_vianda(vianda5);
            heladera1.getGestorDeViandas().agregar_vianda(vianda6);
            heladera1.getGestorDeViandas().agregar_vianda(vianda7);

            colaborador1.solicitar_apertura_heladera(heladera1);
            colaborador1.solicitar_apertura_heladera(heladera2);

            // Create DistribucionDeViandas
            colaborador1.realizar_colaboracion(TipoColaboracion.DISTRIBUCION_DE_VIANDAS, heladera1, heladera2, 2, MotivoDistribucion.DESPERFECTO, LocalDate.now());

            colaborador1.solicitar_apertura_heladera(heladera1);

            // Create DonacionDeViandas
            colaborador1.realizar_colaboracion(TipoColaboracion.DONACION_DE_VIANDAS, vianda1, heladera1);
        }
    }

    public static void main(String[] args) throws IOException {
        createHeladeras();
        createColaboraciones();
        ReporteFallasHeladera reporteFallasHeladera = new ReporteFallasHeladera();
        ReporteMovimientosHeladera reporteMovimientosHeladera = new ReporteMovimientosHeladera();
        ReporteViandasPorColaborador reporteViandasPorColaborador = new ReporteViandasPorColaborador();
        reporteFallasHeladera.generarReporteFisico();
        reporteMovimientosHeladera.generarReporteFisico();
        reporteViandasPorColaborador.generarReporteFisico();
    }
}
