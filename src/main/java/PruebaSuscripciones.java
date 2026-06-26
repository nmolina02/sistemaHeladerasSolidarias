import Heladera.Heladera;
import Heladera.EstadoHeladera;
import localizacion.APIUbicacion.Punto;
import localizacion.Ciudad;
import localizacion.Pais;
import localizacion.Ubicacion;
import medioDeContacto.*;
import persona.documentacion.Documentacion;
import persona.documentacion.TipoDocumentacion;
import persona.personas.PersonaFisica;
import persona.personas.PersonaJuridica;
import persona.personas.TipoJuridico;
import persona.roles.colaborador.Colaborador;
import persona.roles.colaborador.OpcionesSuscripciones.OpcionSuscripcion;
import persona.roles.colaborador.OpcionesSuscripciones.OpcionesSuscripcion;
import Heladera.Modelo;
import Heladera.Vianda;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PruebaSuscripciones {
    public static void main(String[] args) {
        Mail mail1 = new Mail("prueba@gmail.com"); // TODO tiene que ser una direccion existente
        Whatsapp whatsapp1 = new Whatsapp("+5491149692217"); // TODO tiene que ser un numero existente
        Telefono telefono1 = new Telefono("+5491149672346");
        Telegram telegram1 = new Telegram("+5491149672346");
        Pais pais = new Pais("Argentina");
        Ciudad ciudad1 = new Ciudad("CABA", pais);
        Ubicacion ubicacionHum1 = new Ubicacion("-34.6033829", "-58.3814415", ciudad1, "Avenida Corrientes", "857");
        Documentacion documentacion = new Documentacion(TipoDocumentacion.DNI, "20357934");
        LocalDate fechaNac = LocalDate.of(1988, 11, 10);
        ArrayList<MedioDeContacto> mediosDeContacto1 = new ArrayList<>();
        mediosDeContacto1.add(mail1);
        mediosDeContacto1.add(whatsapp1);
        mediosDeContacto1.add(telefono1);
        mediosDeContacto1.add(telegram1);
        PersonaFisica persona1 = new PersonaFisica(mediosDeContacto1, ubicacionHum1, "Juan", "Perez", fechaNac, documentacion);
        Colaborador colaborador = new Colaborador(persona1);

        colaborador.getController().getZonasFrecuentes().add(new Punto("-34.6033829", "-58.3814415", "5000"));
        OpcionSuscripcion opcion1 = new OpcionSuscripcion(OpcionesSuscripcion.CANT_VIANDAS_DISP, 4);
        OpcionSuscripcion opcion2 = new OpcionSuscripcion(OpcionesSuscripcion.CANT_VIANDAS_PARA_LLENAR, 5);
        OpcionSuscripcion opcion3 = new OpcionSuscripcion(OpcionesSuscripcion.DESPERFECTO_HELADERA, 0);

        List<OpcionSuscripcion> opciones = new ArrayList<>();
        opciones.add(opcion1);
        opciones.add(opcion2);
        opciones.add(opcion3);

        List<TipoMedioContacto > tiposMediosDeContacto = new ArrayList<>();
        tiposMediosDeContacto.add(TipoMedioContacto.WHATSAPP);
        tiposMediosDeContacto.add(TipoMedioContacto.MAIL);
        tiposMediosDeContacto.add(TipoMedioContacto.TELEGRAM);

        Ubicacion ubicacion1 = new Ubicacion("-34.6041246", "-58.3875188", ciudad1, "Avenida Corrientes", "1300");
        Modelo modelo = new Modelo("modelo", "marca",10, 10f, 2f );

        ArrayList<MedioDeContacto> mediosDeContacto2 = new ArrayList<>();
        Mail mail2 = new Mail("probando@gmail.com");
        mediosDeContacto2.add(mail2);
        PersonaJuridica persona2 = new PersonaJuridica(mediosDeContacto2, ubicacionHum1, "Dabra.SA", TipoJuridico.EMPRESA , "Ventas", "20357934");
        Colaborador colaboradorJur1 = new Colaborador(persona2);

        Heladera heladera1 =  new Heladera(ubicacion1, "HeladeraUTN", modelo, colaboradorJur1);

        colaborador.getGestorSuscripciones().suscribirse_a_notificaciones(opciones, tiposMediosDeContacto);

       String comida1 = "Arroz con pollo";
        Vianda vianda1 = new Vianda(comida1, 130, 500, LocalDate.of(2024,6,23));
        Vianda vianda2 = new Vianda(comida1, 130, 500, LocalDate.of(2024,6,23));
        Vianda vianda3 = new Vianda(comida1, 130, 500, LocalDate.of(2024,6,23));
        Vianda vianda4 = new Vianda(comida1, 130, 500, LocalDate.of(2024,6,23));
        Vianda vianda5 = new Vianda(comida1, 130, 500, LocalDate.of(2024,6,23));
        heladera1.getGestorDeViandas().agregar_vianda(vianda1);
        heladera1.getGestorDeViandas().agregar_vianda(vianda2);
        heladera1.getGestorDeViandas().agregar_vianda(vianda3);
        heladera1.getGestorDeViandas().agregar_vianda(vianda4);
        heladera1.getGestorDeViandas().agregar_vianda(vianda5);

        heladera1.getGestorDeAlertas().cambiar_estado(EstadoHeladera.DE_BAJA);
    }
}
