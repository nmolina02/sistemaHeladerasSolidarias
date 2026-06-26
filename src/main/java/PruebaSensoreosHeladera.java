import Heladera.Heladera;
import Heladera.Modelo;
import Heladera.incidente.Alerta.TipoGravedad;
import Heladera.incidente.FallaTecnica;
import Heladera.incidente.GestorIncidentes;
import Heladera.incidente.Incidente;
import Heladera.EstadoHeladera;
import localizacion.APIUbicacion.Punto;
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
import persona.roles.tecnico.Tecnico;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class PruebaSensoreosHeladera {
    public static void main(String[] args) throws InterruptedException {
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
        Ubicacion ubicacion1 = new Ubicacion("-34.60322", "-58.3816", ciudad1, "Calle imaginaria", "12");
        ArrayList<MedioDeContacto> mediosDeContacto2 = new ArrayList<>();
        Mail mail2 = new Mail("probando@gmail.com");
        mediosDeContacto2.add(mail2);
        PersonaJuridica persona2 = new PersonaJuridica(mediosDeContacto2, ubicacionHum1, "Dabra.SA", TipoJuridico.EMPRESA , "Ventas", "123456789");
        Colaborador colaboradorJur1 = new Colaborador(persona2);
        Modelo modelo = new Modelo("modelo", "marca",10, 10f, 2f );
        Heladera heladera1 =  new Heladera(ubicacion1, "HeladeraUTN", modelo, colaboradorJur1);
        Tecnico tecnico = new Tecnico(persona1, new Punto("-34.6037", "-58.3816", "5000"));
        Incidente incidente = new FallaTecnica(heladera1, colaboradorJur1, "Falla tecnica", "foto", TipoGravedad.ALTA);
        GestorIncidentes.getInstance().gestionarIncidente(incidente);
        tecnico.pasar_a_ocupado();
        while (incidente.getHeladera().getEstadoHeladera() != EstadoHeladera.FUNCIONAMIENTO){
            tecnico.arreglar_heladera(incidente, "imagen");
        }
        heladera1.getGestorDeAlertas().getSensoreoHeladera().notificarMovimiento();
        heladera1.getGestorDeAlertas().getSensoreoHeladera().notificarTemperatura();
        heladera1.getGestorDeAlertas().recibir_temperatura_actual();
    }
}
