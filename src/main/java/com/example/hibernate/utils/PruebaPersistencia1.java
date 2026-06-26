package com.example.hibernate.utils;

import localizacion.Ciudad;
import localizacion.Pais;
import localizacion.Ubicacion;
import medioDeContacto.*;
import persona.documentacion.Documentacion;
import persona.documentacion.TipoDocumentacion;
import persona.personas.PersonaFisica;
import persona.personas.PersonaJuridica;
import persona.personas.TipoJuridico;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PruebaPersistencia1 {

    public static void main(String[] args) {
        EntityManager em = BDUtils.getEntityManager();
        BDUtils.comenzarTransaccion(em);

        Telefono telefono = new Telefono("+5491149672346");
        Whatsapp whatsapp = new Whatsapp("+5491149672346");
        Telegram telegram = new Telegram("+5491149672346");
        Mail mail = new Mail("carlitos@gmail.com");
        List<MedioDeContacto> mediosDeContacto = new ArrayList<>();

        Pais pais = new Pais("Argentina");
        Ciudad ciudad = new Ciudad("CABA", pais);
        Ubicacion ubicacion = new Ubicacion("111", "111", ciudad, "Calle falsa", "1234");

        PersonaJuridica personaJuridica = new PersonaJuridica(mediosDeContacto, ubicacion, "Pepito S.A.", TipoJuridico.EMPRESA, "e-commerce", "123456789");
        personaJuridica.agregar_medio_de_contacto(telefono);
        personaJuridica.agregar_medio_de_contacto(whatsapp);
        personaJuridica.agregar_medio_de_contacto(telegram);
        personaJuridica.agregar_medio_de_contacto(mail);

        Mail mail1 = new Mail("ricardo@gmail.com");
        List<MedioDeContacto> mediosDeContacto1 = new ArrayList<>();
        LocalDate fechaNacimiento1 = LocalDate.of(1990, 1, 1);
        Documentacion documento1 = new Documentacion(TipoDocumentacion.DNI, "12345678");
        PersonaFisica personaFisica = new PersonaFisica(mediosDeContacto1, ubicacion, "Ricardo", "Perez", fechaNacimiento1, documento1);
        personaFisica.agregar_medio_de_contacto(mail1);

        em.persist(pais);
        em.persist(ciudad);
        em.persist(ubicacion);
        em.persist(documento1);
        em.persist(personaJuridica);
        em.persist(personaFisica);
        em.persist(telefono);
        em.persist(whatsapp);
        em.persist(telegram);
        em.persist(mail);
        em.persist(mail1);

        BDUtils.commit(em);
    }
}
