package com.example.hibernate.utils;

import Heladera.Heladera;
import Heladera.Vianda;
import Heladera.Modelo;
import colaboraciones.TipoColaboracion;
import colaboraciones.colaboracionesHumanas.MotivoDistribucion;
import localizacion.Ciudad;
import localizacion.Pais;
import localizacion.Ubicacion;
import medioDeContacto.*;
import persistencia.ClaseCRUD;
import persona.documentacion.Documentacion;
import persona.documentacion.TipoDocumentacion;
import persona.personas.PersonaFisica;
import persona.personas.PersonaJuridica;
import persona.personas.TipoJuridico;
import persona.roles.colaborador.Colaborador;
import persona.roles.colaborador.OpcionesSuscripciones.OpcionSuscripcion;
import persona.roles.colaborador.OpcionesSuscripciones.OpcionesSuscripcion;
import reportes.GeneradorDeReportes;
import repository.RepositoryHeladera;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PruebaPersistencia2 {

    public static void main(String[] args) throws IOException {
        EntityManager em = BDUtils.getEntityManager();
        BDUtils.comenzarTransaccion(em);

        Pais pais = new Pais("Argentina");
        Ciudad ciudad = new Ciudad("CABA", pais);
        Ubicacion ubicacion1 = new Ubicacion("-34.598502564124125", "-58.420096875135954", ciudad, "Avenida Medrano", "951");
        Ubicacion ubicacion2 = new Ubicacion("-34.59685106577028", "-58.42203879438658", ciudad, "Avenida Córdoba", "3933");
        Ubicacion ubicacion3 = new Ubicacion("-34.59716849458736", "-58.41591005705868", ciudad, "Mario Bravo", "1050");
        Ubicacion ubicacion4 = new Ubicacion("-34.54727442153583", "-58.49050914236015", ciudad, "Tronador", "4890");

        Mail mail1 = new Mail("ricardo@gmail.com");
        List<MedioDeContacto> mediosDeContacto1 = new ArrayList<>();
        LocalDate fechaNacimiento1 = LocalDate.of(1990, 1, 1);
        Documentacion documento1 = new Documentacion(TipoDocumentacion.DNI, "12345678");
        PersonaFisica personaFisica = new PersonaFisica(mediosDeContacto1, ubicacion1, "Ricardo", "Perez", fechaNacimiento1, documento1);
        personaFisica.agregar_medio_de_contacto(mail1);
        Colaborador colaborador = new Colaborador(personaFisica);

        Mail mail2 = new Mail("meli@gmail.com");
        List<MedioDeContacto> mediosDeContacto2 = new ArrayList<>();
        PersonaJuridica personaJuridica = new PersonaJuridica(mediosDeContacto2, ubicacion4, "Mercado Libre", TipoJuridico.EMPRESA, "e-commerce", "123456789");
        personaJuridica.agregar_medio_de_contacto(mail2);
        Colaborador colaborador2 = new Colaborador(personaJuridica);
        Modelo modelo = new Modelo("modelo", "marca",10, 3.5f, 1.5f);
        colaborador2.realizar_colaboracion(TipoColaboracion.HACERSE_CARGO, "Heladera1", modelo, ubicacion2);
        colaborador2.realizar_colaboracion(TipoColaboracion.HACERSE_CARGO, "Heladera2", modelo, ubicacion3);
        Heladera heladera1 = RepositoryHeladera.getInstance().getHeladerasDelSistema().get(0);
        Heladera heladera2 = RepositoryHeladera.getInstance().getHeladerasDelSistema().get(1);

        Vianda vianda1 = new Vianda("Milanesa", 150, 200, LocalDate.of(2024, 12, 1));
        Vianda vianda2 = new Vianda("Milanesa", 150, 200, LocalDate.of(2024, 12, 1));
        Vianda vianda3 = new Vianda("Milanesa", 150, 200, LocalDate.of(2024, 12, 1));
        Vianda vianda4 = new Vianda("Milanesa", 150, 200, LocalDate.of(2024, 12, 1));
        Vianda vianda5 = new Vianda("Milanesa", 150, 200, LocalDate.of(2024, 12, 1));

        colaborador.solicitar_tarjeta();

        colaborador.solicitar_apertura_heladera(heladera1);
        colaborador.realizar_colaboracion(TipoColaboracion.DONACION_DE_VIANDAS, vianda1, heladera1);

        colaborador.solicitar_apertura_heladera(heladera1);
        colaborador.realizar_colaboracion(TipoColaboracion.DONACION_DE_VIANDAS, vianda2, heladera1);

        colaborador.solicitar_apertura_heladera(heladera1);
        colaborador.realizar_colaboracion(TipoColaboracion.DONACION_DE_VIANDAS, vianda3, heladera1);

        colaborador.solicitar_apertura_heladera(heladera1);
        colaborador.realizar_colaboracion(TipoColaboracion.DONACION_DE_VIANDAS, vianda4, heladera1);

        colaborador.solicitar_apertura_heladera(heladera1);
        colaborador.realizar_colaboracion(TipoColaboracion.DONACION_DE_VIANDAS, vianda5, heladera1);

        colaborador.solicitar_apertura_heladera(heladera1);
        colaborador.solicitar_apertura_heladera(heladera2);
        colaborador.realizar_colaboracion(TipoColaboracion.DISTRIBUCION_DE_VIANDAS, heladera1, heladera2, 3, MotivoDistribucion.FALTA_DE_VIANDAS, LocalDate.now());

        OpcionSuscripcion opcionSuscripcion = new OpcionSuscripcion(OpcionesSuscripcion.CANT_VIANDAS_DISP, 2);
        OpcionSuscripcion opcionSuscripcion2 = new OpcionSuscripcion(OpcionesSuscripcion.CANT_VIANDAS_PARA_LLENAR, 3);
        List<OpcionSuscripcion> opciones = new ArrayList<>();
        opciones.add(opcionSuscripcion);
        opciones.add(opcionSuscripcion2);
        List<TipoMedioContacto> tiposMediosDeContacto = new ArrayList<>();
        tiposMediosDeContacto.add(TipoMedioContacto.MAIL);

        colaborador.getGestorSuscripciones().suscribirse_a_notificaciones(opciones, tiposMediosDeContacto);

        heladera1.getController().setTempMaxUser(2f);
        heladera2.getController().setTempMinUser(1.7f);

        // para probar (es scheduler igual)
        GeneradorDeReportes.getInstance().generar_reporte();

        System.out.println(ClaseCRUD.getInstance().getObjectList());

        for (Object object : ClaseCRUD.getInstance().getObjectList()){
            ClaseCRUD.getInstance().create(object, em);
        }

        for (Object object : ClaseCRUD.getInstance().getObjectListDelete()){
            ClaseCRUD.getInstance().delete(object, em);
        }

        BDUtils.commit(em);

        // borra todas las tablas, es como dropear la bd
        //em.clear();
    }
}
