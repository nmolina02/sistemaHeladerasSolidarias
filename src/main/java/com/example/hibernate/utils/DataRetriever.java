package com.example.hibernate.utils;

import Heladera.Heladera;
import Heladera.Modelo;
import Heladera.Vianda;
import Heladera.VisitaHeladera;
import Heladera.controladoresHeladera.Solicitud;
import Heladera.incidente.Alerta.AlertaConexion;
import Heladera.incidente.Alerta.AlertaFraude;
import Heladera.incidente.Alerta.AlertaTemperatura;
import Heladera.incidente.FallaTecnica;
import Heladera.incidente.Incidente;
import colaboraciones.Colaboracion;
import colaboraciones.colaboracionesCompartidas.DonacionDeDinero;
import colaboraciones.colaboracionesHumanas.DistribucionDeViandas;
import colaboraciones.colaboracionesHumanas.DonacionDeViandas;
import colaboraciones.colaboracionesHumanas.RegistroDePersonasVulnerables;
import colaboraciones.colaboracionesJuridicas.HacerseCargo;
import colaboraciones.colaboracionesJuridicas.OfrecerProductoReconocimiento;
import localizacion.APIUbicacion.Punto;
import localizacion.Ciudad;
import localizacion.Pais;
import localizacion.Ubicacion;
import medioDeContacto.MedioDeContacto;
import persona.documentacion.Documentacion;
import persona.personas.Persona;
import persona.personas.PersonaFisica;
import persona.personas.PersonaJuridica;
import persona.roles.colaborador.Colaborador;
import persona.roles.personaEnSituacionVulnerable.PersonaEnSituacionVulnerable;
import persona.roles.tecnico.Tecnico;
import premios.PremioColaboracion;
import persona.roles.Usuario;
import reportes.*;
import suscripciones.Suscripcion;
import tarjetas.ExtraccionDeVianda;
import tarjetas.Operacion;
import tarjetas.TarjetaColaborador;
import tarjetas.TarjetaPersonaVulnerable;

import javax.persistence.EntityManager;
import java.util.List;

public class DataRetriever {
    public DataRetriever() {}

    public static List<Usuario> getAllUsuarios(EntityManager em) {
        return em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();
    }

    public static List<Colaborador> getAllColaboradores(EntityManager em) {
        return em.createQuery("SELECT c FROM Colaborador c", Colaborador.class).getResultList();
    }

    public static List<Tecnico> getAllTecnicos(EntityManager em) {
        return em.createQuery("SELECT t FROM Tecnico t", Tecnico.class).getResultList();
    }

    public static List<PersonaFisica> getAllPersonasFisicas(EntityManager em) {
        return em.createQuery("SELECT p FROM PersonaFisica p", PersonaFisica.class).getResultList();
    }

    public static List<PersonaJuridica> getAllPersonasJuridicas(EntityManager em) {
        return em.createQuery("SELECT p FROM PersonaJuridica p", PersonaJuridica.class).getResultList();
    }

    public static List<Pais> getAllPaises(EntityManager em) {
        return em.createQuery("SELECT p FROM Pais p", Pais.class).getResultList();
    }

    public static List<Ciudad> getAllCiudades(EntityManager em) {
        return em.createQuery("SELECT c FROM Ciudad c", Ciudad.class).getResultList();
    }

    public static List<Ubicacion> getAllUbicaciones(EntityManager em) {
        return em.createQuery("SELECT u FROM Ubicacion u", Ubicacion.class).getResultList();
    }

    public static List<Persona> getAllPersonas(EntityManager em) {
        return em.createQuery("SELECT p FROM Persona p", Persona.class).getResultList();
    }

    public static List<MedioDeContacto> getAllMediosDeContacto(EntityManager em) {
        return em.createQuery("SELECT m FROM MedioDeContacto m", MedioDeContacto.class).getResultList();
    }

    public static List<Documentacion> getAllDocumentaciones(EntityManager em) {
        return em.createQuery("SELECT d FROM Documentacion d", Documentacion.class).getResultList();
    }

    public static List<Punto> getAllPuntos(EntityManager em) {
        return em.createQuery("SELECT p FROM Punto p", Punto.class).getResultList();
    }

    public static List<PersonaEnSituacionVulnerable> getAllPersonasEnSituacionVulnerable(EntityManager em) {
        return em.createQuery("SELECT p FROM PersonaEnSituacionVulnerable p", PersonaEnSituacionVulnerable.class).getResultList();
    }

    public static List<TarjetaColaborador> getAllTarjetasColaborador(EntityManager em) {
        return em.createQuery("SELECT t FROM TarjetaColaborador t", TarjetaColaborador.class).getResultList();
    }

    public static List<TarjetaPersonaVulnerable> getAllTarjetasPersonaVulnerable(EntityManager em) {
        return em.createQuery("SELECT t FROM TarjetaPersonaVulnerable t", TarjetaPersonaVulnerable.class).getResultList();
    }

    public static List<Heladera> getAllHeladeras(EntityManager em) {
        return em.createQuery("SELECT h FROM Heladera h", Heladera.class).getResultList();
    }

    public static List<Operacion> getAllOperaciones(EntityManager em) {
        return em.createQuery("SELECT o FROM Operacion o", Operacion.class).getResultList();
    }

    public static List<Solicitud> getAllSolicitudes(EntityManager em) {
        return em.createQuery("SELECT s FROM Solicitud s", Solicitud.class).getResultList();
    }

    public static List<Suscripcion> getAllSuscripciones(EntityManager em) {
        return em.createQuery("SELECT s FROM Suscripcion s", Suscripcion.class).getResultList();
    }

    public static List<ExtraccionDeVianda> getAllExtraccionesDeVianda(EntityManager em) {
        return em.createQuery("SELECT e FROM ExtraccionDeVianda e", ExtraccionDeVianda.class).getResultList();
    }

    public static List<Modelo> getAllModelos(EntityManager em) {
        return em.createQuery("SELECT m FROM Modelo m", Modelo.class).getResultList();
    }

    public static List<Vianda> getAllViandas(EntityManager em) {
        return em.createQuery("SELECT v FROM Vianda v", Vianda.class).getResultList();
    }

    public static List<Incidente> getAllIncidentes(EntityManager em) {
        return em.createQuery("SELECT i FROM Incidente i", Incidente.class).getResultList();
    }

    public static List<FallaTecnica> getAllFallasTecnicas(EntityManager em) {
        return em.createQuery("SELECT f FROM FallaTecnica f", FallaTecnica.class).getResultList();
    }

    public static List<AlertaConexion> getAllAlertasConexiones(EntityManager em) {
        return em.createQuery("SELECT a FROM AlertaConexion a", AlertaConexion.class).getResultList();
    }

    public static List<AlertaFraude> getAllAlertasFraudes(EntityManager em) {
        return em.createQuery("SELECT a FROM AlertaFraude a", AlertaFraude.class).getResultList();
    }

    public static List<AlertaTemperatura> getAllAlertasTemperaturas(EntityManager em) {
        return em.createQuery("SELECT a FROM AlertaTemperatura a", AlertaTemperatura.class).getResultList();
    }

    public static List<VisitaHeladera> getAllVisitasHeladeras(EntityManager em) {
        return em.createQuery("SELECT v FROM VisitaHeladera v", VisitaHeladera.class).getResultList();
    }

    public static List<Colaboracion> getAllColaboraciones(EntityManager em) {
        return em.createQuery("SELECT c FROM Colaboracion c", Colaboracion.class).getResultList();
    }

    public static List<DonacionDeDinero> getAllDonacionesDeDinero(EntityManager em) {
        return em.createQuery("SELECT c FROM DonacionDeDinero c", DonacionDeDinero.class).getResultList();
    }

    public static List<OfrecerProductoReconocimiento> getAllOfrecerProductosReconocimiento(EntityManager em) {
        return em.createQuery("SELECT c FROM OfrecerProductoReconocimiento c", OfrecerProductoReconocimiento.class).getResultList();
    }

    public static List<DonacionDeViandas> getAllDonacionesDeViandas(EntityManager em) {
        return em.createQuery("SELECT c FROM DonacionDeViandas c", DonacionDeViandas.class).getResultList();
    }

    public static List<DistribucionDeViandas> getAllDistribucionesDeViandas(EntityManager em) {
        return em.createQuery("SELECT c FROM DistribucionDeViandas c", DistribucionDeViandas.class).getResultList();
    }

    public static List<HacerseCargo> getAllHacerseCargos(EntityManager em) {
        return em.createQuery("SELECT c FROM HacerseCargo c", HacerseCargo.class).getResultList();
    }

    public static List<RegistroDePersonasVulnerables> getAllRegistrosDePersonasVulnerables(EntityManager em) {
        return em.createQuery("SELECT c FROM RegistroDePersonasVulnerables c", RegistroDePersonasVulnerables.class).getResultList();
    }

    public static List<PremioColaboracion> getAllPremiosColaboracion(EntityManager em) {
        return em.createQuery("SELECT p FROM PremioColaboracion p", PremioColaboracion.class).getResultList();
    }

    public static List<FallasHeladera> getAllFallasHeladera(EntityManager em) {
        return em.createQuery("SELECT f FROM FallasHeladera f", FallasHeladera.class).getResultList();
    }

    public static List<MovimientosHeladera> getAllMovimientosHeladera(EntityManager em) {
        return em.createQuery("SELECT m FROM MovimientosHeladera m", MovimientosHeladera.class).getResultList();
    }

    public static List<ViandasPorColaborador> getAllViandasPorColaborador(EntityManager em) {
        return em.createQuery("SELECT v FROM ViandasPorColaborador v", ViandasPorColaborador.class).getResultList();
    }

    public static List<Reporte> getAllReportes(EntityManager em) {
        return em.createQuery("SELECT r FROM Reporte r", Reporte.class).getResultList();
    }

    public static List<SolicitudReporteIndividual> getAllSolicitudesReportesVigentes(EntityManager em) {
        return em.createQuery("SELECT s FROM SolicitudReporteIndividual s WHERE s.solicitudExpirada = false", SolicitudReporteIndividual.class).getResultList();
    }
}