package receptorDeJSON.Receptores;

import Heladera.Heladera;
import Heladera.Modelo;
import Heladera.Vianda;
import Heladera.VisitaHeladera;
import Heladera.controladoresHeladera.*;
import Heladera.incidente.GestorIncidentes;
import Heladera.incidente.Incidente;
import colaboraciones.Colaboracion;
import com.example.hibernate.utils.BDUtils;
import com.example.hibernate.utils.DataRetriever;
import io.javalin.Javalin;
import io.javalin.plugin.metrics.MicrometerPlugin;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import localizacion.Ciudad;
import localizacion.Pais;
import localizacion.Ubicacion;
import medioDeContacto.MedioDeContacto;
import medioDeContacto.TipoMedioContacto;
import persistencia.ClaseCRUD;
import persona.personas.PersonaFisica;
import persona.personas.PersonaJuridica;
import persona.roles.colaborador.Colaborador;
import persona.roles.colaborador.ColaboradorController;
import persona.roles.colaborador.GestorDePuntaje;
import persona.roles.colaborador.GestorSuscripciones;
import persona.roles.colaborador.OpcionesSuscripciones.OpcionSuscripcion;
import persona.roles.colaborador.OpcionesSuscripciones.OpcionesSuscripcion;
import persona.roles.personaEnSituacionVulnerable.PersonaEnSituacionVulnerable;
import persona.roles.tecnico.GestorTecnico;
import persona.roles.tecnico.Tecnico;
import premios.PremioColaboracion;
import persona.roles.Usuario;
import reportes.*;
import repository.*;
import suscripciones.GestorSuscripcionesHeladeras;
import suscripciones.Suscripcion;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static persistencia.LogToFile.clearLog;
import static receptorDeJSON.Receptores.ActualizacionConfiguraciones.ejecutarActualizacionConfiguraciones;
import static receptorDeJSON.Receptores.CanjearPremioReceptor.ejecutarCanjearPremioReceptor;
import static receptorDeJSON.Receptores.CargaDePersonasVulnerables.ejecutarCargaDePersonasVulnerables;
import static receptorDeJSON.Receptores.ColaboracionesRealizadas.ejecutarColaboracionesRealizadas;
import static receptorDeJSON.Receptores.ExportarReportePDF.ejecutarExportarReportePDF;
import static receptorDeJSON.Receptores.GenerarReporteIndividual.ejecutarGenerarReporteIndividual;
import static receptorDeJSON.Receptores.GoogleSSO.*;
import static receptorDeJSON.Receptores.IncidentesOcurridos.ejecutarIncidentesOcurridos;
import static receptorDeJSON.Receptores.IncidentesPendientesAsignados.ejecutarIncidentesPendientesAsignados;
import static receptorDeJSON.Receptores.LoginReceptor.ejecutarLoginReceptor;
import static receptorDeJSON.Receptores.PerfilReceptor.ejecutarActualizacionDescripcion;
import static receptorDeJSON.Receptores.PerfilReceptor.ejecutarPerfilReceptor;
import static receptorDeJSON.Receptores.RealizarVisitaHeladera.ejecutarRealizarVisitaHeladera;
import static receptorDeJSON.Receptores.RecepcionDeArchivos.ejecutarRecepcionDeArchivos;
import static receptorDeJSON.Receptores.RecomendadorDonacionesReceptor.ejecutarRecomendadorDonacionesReceptor;
import static receptorDeJSON.Receptores.CSVReceptor.ejecutarCSVReceptor;
import static receptorDeJSON.Receptores.EstadoHeladeraReceptor.ejecutarEstadoHeladeraReceptor;
import static receptorDeJSON.Receptores.FallaHeladeraReceptor.ejecutarFallaHeladeraReceptor;
import static receptorDeJSON.Receptores.MarcadoresReceptor.ejecutarMarcadoresReceptor;
import static receptorDeJSON.Receptores.PointReceptor.ejecutarPointReceptor;
import static receptorDeJSON.Receptores.RecomendadorDePuntosReceptor.ejecutarRecomendadorDePuntosReceptor;
import static receptorDeJSON.Receptores.RecuperarContrasenia.ejecutarConfirmacionContrasenia;
import static receptorDeJSON.Receptores.RecuperarContrasenia.ejecutarRecuperarContrasenia;
import static receptorDeJSON.Receptores.SolicitudAperturaHeladeraReceptor.ejecutarSolicitudAperturaHeladeraReceptor;
import static receptorDeJSON.Receptores.SolicitudDetalleReporte.ejecutarSolicitudDetalleReporte;
import static receptorDeJSON.Receptores.SolicitudDetalleVisitaHeladera.ejecutarSolicitudDetalleVisitaHeladera;
import static receptorDeJSON.Receptores.SolicitudPremiosExistentes.ejecutarSolicitudPremiosExistentes;
import static receptorDeJSON.Receptores.SolicitudPremiosReceptor.ejecutarSolicitudPremiosReceptor;
import static receptorDeJSON.Receptores.SolicitudReportesExistentesReceptor.ejecutarSolicitudReportesExistentesReceptor;
import static receptorDeJSON.Receptores.SolicitudTarjetaReceptor.ejecutarSolicitudTarjetaReceptor;
import static receptorDeJSON.Receptores.SuscripcionesReceptor.ejecutarSuscripcionUsuarioExistente;
import static receptorDeJSON.Receptores.SuscripcionesReceptor.ejecutarSuscripcionesReceptor;
import static receptorDeJSON.Receptores.UserReceptor.ejecutarUserReceptor;
import static receptorDeJSON.Receptores.UserSSOReceptor.ejecutarUserSSOReceptor;
import static receptorDeJSON.Receptores.ViandasReceptor.ejecutarViandasReceptor;

public class Receptor {

    public static void main(String[] args) {
        EntityManager em = BDUtils.getEntityManager();

        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        new JvmMemoryMetrics().bindTo(registry);
        new JvmThreadMetrics().bindTo(registry);
        new ProcessorMetrics().bindTo(registry);

        new ClassLoaderMetrics().bindTo(registry);
        new JvmGcMetrics().bindTo(registry);

        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/mapaInteractivoHeladeras/sistema");
            config.registerPlugin(new MicrometerPlugin(registry));
        }).start(4567);

        // Ruta principal que sirve la página
        app.get("/", ctx -> ctx.render("mapaInteractivoHeladeras/sistema/index.html"));

        app.get("/metrics", ctx -> {
            ctx.result(registry.scrape());
        });

        List<Pais> paises = DataRetriever.getAllPaises(em);
        List<Ciudad> ciudades = DataRetriever.getAllCiudades(em);
        List<Ubicacion> ubicaciones = DataRetriever.getAllUbicaciones(em);
        //List<Persona> personas = DataRetriever.getAllPersonas(em);
        //List<PersonaFisica> personasFisicas = DataRetriever.getAllPersonasFisicas(em);
        //List<PersonaJuridica> personasJuridicas = DataRetriever.getAllPersonasJuridicas(em);
        //List<MedioDeContacto> mediosDeContacto = DataRetriever.getAllMediosDeContacto(em);
        //List<Documentacion> documentaciones = DataRetriever.getAllDocumentaciones(em);
        //List<Punto> puntos = DataRetriever.getAllPuntos(em);
        List<Colaborador> colaboradores = DataRetriever.getAllColaboradores(em);
        List<Tecnico> tecnicos = DataRetriever.getAllTecnicos(em);
        List<Usuario> usuarios = DataRetriever.getAllUsuarios(em);
        List<PersonaEnSituacionVulnerable> personasEnSituacionVulnerable = DataRetriever.getAllPersonasEnSituacionVulnerable(em);
        //List<TarjetaColaborador> tarjetasColaborador = DataRetriever.getAllTarjetasColaborador(em);
        //List<TarjetaPersonaVulnerable> tarjetasPersonaVulnerable = DataRetriever.getAllTarjetasPersonaVulnerable(em);
        List<Modelo> modelos = DataRetriever.getAllModelos(em);
        List<Heladera> heladeras = DataRetriever.getAllHeladeras(em);
        //List<Vianda> viandas = DataRetriever.getAllViandas(em);
        //List<Solicitud> solicitudes = DataRetriever.getAllSolicitudes(em);
        List<Suscripcion> suscripciones = DataRetriever.getAllSuscripciones(em);
        //List<Operacion> operaciones = DataRetriever.getAllOperaciones(em);
        //List<ExtraccionDeVianda> extraccionesDeVianda = DataRetriever.getAllExtraccionesDeVianda(em);
        List<Incidente> incidentes = DataRetriever.getAllIncidentes(em);
        //List<FallaTecnica> fallasTecnicas = DataRetriever.getAllFallasTecnicas(em);
        //List<AlertaConexion> alertasConexion = DataRetriever.getAllAlertasConexiones(em);
        //List<AlertaTemperatura> alertasTemperatura = DataRetriever.getAllAlertasTemperaturas(em);
        //List<AlertaFraude> alertasFraude = DataRetriever.getAllAlertasFraudes(em);
        List<VisitaHeladera> visitasHeladera = DataRetriever.getAllVisitasHeladeras(em);
        List<Colaboracion> colaboraciones = DataRetriever.getAllColaboraciones(em);
        //List<DonacionDeDinero> donacionesDeDinero = DataRetriever.getAllDonacionesDeDinero(em);
        //List<DonacionDeViandas> donacionesDeViandas = DataRetriever.getAllDonacionesDeViandas(em);
        //List<DistribucionDeViandas> distribucionesDeViandas = DataRetriever.getAllDistribucionesDeViandas(em);
        //List<RegistroDePersonasVulnerables> registrosDePersonasVulnerables = DataRetriever.getAllRegistrosDePersonasVulnerables(em);
        //List<HacerseCargo> hacerseCargos = DataRetriever.getAllHacerseCargos(em);
        //List<OfrecerProductoReconocimiento> ofrecerProductosReconocimiento = DataRetriever.getAllOfrecerProductosReconocimiento(em);
        List<PremioColaboracion> premiosColaboracion = DataRetriever.getAllPremiosColaboracion(em);
        List<FallasHeladera> fallasHeladera = DataRetriever.getAllFallasHeladera(em);
        List<MovimientosHeladera> movimientosHeladera = DataRetriever.getAllMovimientosHeladera(em);
        List<ViandasPorColaborador> viandasPorColaborador = DataRetriever.getAllViandasPorColaborador(em);
        List<Reporte> reportes = DataRetriever.getAllReportes(em);
        List<SolicitudReporteIndividual> solicitudesReporteIndividualVigentes = DataRetriever.getAllSolicitudesReportesVigentes(em);

        RepositoryColaborador.getInstance().setColaboradoresDelSistema(colaboradores);
        RepositoryTecnicos.getInstance().setTecnicos(tecnicos);
        RepositoryUsuario.getInstance().setUsuarios(usuarios);

        boolean exists = RepositoryUsuario.getInstance().getUsuarios().stream().anyMatch(usuario -> "administrador01".equals(usuario.getUsername()));

        if (!exists) {
            Usuario administrador = new Usuario();
            administrador.setUsername("administrador01");
            administrador.setPassword("abfdd6ac483d501858880a029175cb743956ac6bdf91542d01a1a4219e343a56");
            ClaseCRUD.getInstance().add(administrador);
            RepositoryUsuario.getInstance().addUsuario(administrador);
        }

        RepositoryReportes.getInstance().setReportesHistoricos(reportes);
        RepositoryColaboracion.getInstance().setColaboraciones(colaboraciones);
        RepositoryPersonasVulnerables.getInstance().setPersonasVulnerables(personasEnSituacionVulnerable);
        RepositoryPais.getInstance().setPaises(paises);
        RepositoryCiudad.getInstance().setCiudades(ciudades);
        RepositoryUbicacion.getInstance().setUbicaciones(ubicaciones);
        RepositoryModelo.getInstance().setModelos(modelos);

        List<PremioColaboracion> premiosActivos = new ArrayList<>();
        for (PremioColaboracion premioColaboracion : premiosColaboracion) {
            premiosActivos = em.createQuery("SELECT p FROM PremioColaboracion p WHERE p.canjeado = :canjeado", PremioColaboracion.class)
                    .setParameter("canjeado", false)
                    .getResultList();
        }
        RepositoryPremios.getInstance().setPremios(premiosActivos);
        RepositoryIncidente.getInstance().setIncidentes(incidentes);
        RepositoryHeladera.getInstance().setHeladerasDelSistema(heladeras);
        RepositoryVisitaHeladera.getInstance().setVisitas(visitasHeladera);

        for (SolicitudReporteIndividual solicitudReporteIndividual : solicitudesReporteIndividualVigentes) {
            if (solicitudReporteIndividual.solicitudExpirada()) {
                solicitudReporteIndividual.setSolicitudExpirada(true);
            } else {
                RepositorySolicitudReporte.getInstance().agregarSolicitudReporteIndividual(solicitudReporteIndividual);
            }
        }

        for (Colaborador colaborador : RepositoryColaborador.getInstance().getColaboradoresDelSistema()) {
            if (colaborador.getGestorDePuntaje() == null) {
                colaborador.setGestorDePuntaje(new GestorDePuntaje(colaborador));
                colaborador.getGestorDePuntaje().setPuntosTotales(colaborador.getPuntos_acumulados());
            }
            if (colaborador.getController() == null) {
                colaborador.setController(new ColaboradorController(colaborador));
            }
            if (colaborador.getGestorSuscripciones() == null) {
                colaborador.setGestorSuscripciones(new GestorSuscripciones(colaborador));
                Suscripcion suscripcion = suscripciones.stream()
                        .filter(suscripcion1 -> suscripcion1.getColaborador().equals(colaborador))
                        .findFirst()
                        .orElse(null);
                if (suscripcion != null) {
                    colaborador.getGestorSuscripciones().setEntidadNotificadora(suscripcion);
                    String mediosDeContactoString = suscripcion.getMediosDeContactoParaSuscripcion();
                    String opcionesSuscripcionString = suscripcion.getOpcionesSuscripcion();
                    List<MedioDeContacto> mediosDeContactoParaSuscripcion = new ArrayList<>();
                    List<OpcionSuscripcion> opcionesSuscripcion = new ArrayList<>();
                    for (MedioDeContacto medioDeContacto : colaborador.getPersona().getMediosDeContacto()) {
                        if (mediosDeContactoString.contains(medioDeContacto.getTipoMedioContacto().toString())) {
                            mediosDeContactoParaSuscripcion.add(medioDeContacto);
                        }
                    }
                    for (OpcionSuscripcion opcionSuscripcion : colaborador.getGestorSuscripciones().getOpcionesSuscripcion()) {
                        if (opcionesSuscripcionString.contains(opcionSuscripcion.getOpcion().toString())) {
                            opcionesSuscripcion.add(opcionSuscripcion);
                        }
                    }
                    colaborador.getGestorSuscripciones().setMediosDeContactoParaSuscripcion(mediosDeContactoParaSuscripcion);
                    colaborador.getGestorSuscripciones().setOpcionesSuscripcion(opcionesSuscripcion);
                }
            }
        }

        for (Heladera heladera : RepositoryHeladera.getInstance().getHeladerasDelSistema()) {
            if (heladera.getController() == null) {
                heladera.setController(new HeladeraController(heladera));
            }
            if (heladera.getGestorDeAlertas() == null) {
                heladera.setGestorDeAlertas(new GestorDeAlertas(heladera));
            }
            if (heladera.getGestorDeViandas() == null) {
                List<Vianda> viandasHeladera = em.createQuery("SELECT v FROM Vianda v WHERE v.heladera.id = :heladeraId", Vianda.class)
                                                    .setParameter("heladeraId", heladera.getId())
                                                    .getResultList();
                heladera.setGestorDeViandas(new GestorDeViandas(heladera));
                heladera.getGestorDeViandas().setViandas(viandasHeladera);
            }
            if (heladera.getGestorDeSolicitudes() == null) {
                heladera.setGestorDeSolicitudes(new GestorDeSolicitudes(heladera));
            }
        }

        for (Suscripcion suscripcion : suscripciones) {
            suscripcion.buscar_heladeras_cercanas();
        }

        GestorSuscripcionesHeladeras gestorSuscripcionesHeladeras = GestorSuscripcionesHeladeras.getInstance();
        gestorSuscripcionesHeladeras.setSuscripciones(suscripciones);

        for (Colaborador colaborador : RepositoryColaborador.getInstance().getColaboradoresDelSistema()) {
            List<Colaboracion> colaboracionesPrevias = em.createQuery("SELECT c FROM Colaboracion c WHERE c.colaborador.id = :colaboradorId", Colaboracion.class)
                    .setParameter("colaboradorId", colaborador.getId())
                    .getResultList();
            if (colaboracionesPrevias != null && colaborador.getPersona() instanceof PersonaJuridica) {
                colaborador.getGestorDePuntaje().setPuntosTotales(0.0);
                for (Colaboracion colaboracion : colaboracionesPrevias) {
                    colaborador.getGestorDePuntaje().actualizar_puntaje(colaboracion);
                }
            }
            else if (colaboracionesPrevias != null && colaborador.getPersona() instanceof PersonaFisica) {
                colaborador.getController().calcular_zonas_frecuentes();
                if (colaborador.getGestorSuscripciones().getEntidadNotificadora() != null){
                    String opcionesString = colaborador.getGestorSuscripciones().getEntidadNotificadora().getOpcionesSuscripcion();
                    List<OpcionSuscripcion> opcionesSuscripcion = new ArrayList<>();
                    String[] opcionesArray = opcionesString.substring(1, opcionesString.length() - 1).split(",\\s*");
                    for (String opcion : opcionesArray) {
                        String[] parts = opcion.split("\\s+");
                        if (parts.length == 2) {
                            OpcionesSuscripcion opcionEnum = OpcionesSuscripcion.valueOf(parts[0]);
                            int valor = Integer.parseInt(parts[1]);
                            opcionesSuscripcion.add(new OpcionSuscripcion(opcionEnum, valor));
                        }
                    }
                    colaborador.getGestorSuscripciones().setOpcionesSuscripcion(opcionesSuscripcion);

                    String mediosString = colaborador.getGestorSuscripciones().getEntidadNotificadora().getMediosDeContactoParaSuscripcion();
                    List<MedioDeContacto> mediosDeContactoParaSuscripcion = new ArrayList<>();
                    String[] mediosArray = mediosString.substring(1, mediosString.length() - 1).split(", ");
                    for (String medio : mediosArray) {
                        TipoMedioContacto tipoMedioContacto = TipoMedioContacto.valueOf(medio);
                        for (MedioDeContacto medioDeContacto : colaborador.getPersona().getMediosDeContacto()) {
                            if (medioDeContacto.getTipoMedioContacto() == tipoMedioContacto) {
                                mediosDeContactoParaSuscripcion.add(medioDeContacto);
                            }
                        }
                    }
                    colaborador.getGestorSuscripciones().setMediosDeContactoParaSuscripcion(mediosDeContactoParaSuscripcion);
                }
            }
        }

        for (Tecnico tecnico : RepositoryTecnicos.getInstance().getTecnicos()) {
            if (tecnico.getGestorTecnico() == null) {
                tecnico.setGestorTecnico(new GestorTecnico());
            }
        }

        RepositoryIncidente.getInstance().getIncidentes().forEach(incidente -> {
            if (!incidente.isSolucionado()){
                GestorIncidentes.getInstance().gestionarIncidente(incidente);
            }
        });

        // hay que cargar las fallasHeladera, movimientosHeladera y viandasPorColaborador
        LocalDateTime ultimoDomingo = LocalDateTime.now()
                                        .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                                        .withHour(0)
                                        .withMinute(0)
                                        .withSecond(0)
                                        .withNano(0);
        List<FallasHeladera> fallasHeladeraAux = fallasHeladera.stream()
                                                    .filter(fallasHeladera1 -> fallasHeladera1.getHorario().isAfter(ultimoDomingo))
                                                    .collect(Collectors.toList());
        List<MovimientosHeladera> movimientosHeladeraAux = movimientosHeladera.stream()
                                                            .filter(movimientosHeladera1 -> movimientosHeladera1.getHorario().isAfter(ultimoDomingo))
                                                            .collect(Collectors.toList());
        List<ViandasPorColaborador> viandasPorColaboradorAux = viandasPorColaborador.stream()
                                                                    .filter(viandasPorColaborador1 -> viandasPorColaborador1.getHorario().isAfter(ultimoDomingo))
                                                                    .collect(Collectors.toList());

        GeneradorDeReportes.getInstance().setFallasPorHeladera(fallasHeladeraAux);
        GeneradorDeReportes.getInstance().setMovimientosPorHeladera(movimientosHeladeraAux);
        GeneradorDeReportes.getInstance().setViandasPorColaborador(viandasPorColaboradorAux);

        ejecutarGoogleSSO(app);
        ejecutarLoggedInUserGoogle(app);
        ejecutarSolicitudCurrentUserId(app);
        ejecutarUserReceptor(app);
        ejecutarUserSSOReceptor(app);
        ejecutarPointReceptor(app);
        ejecutarRecomendadorDePuntosReceptor(app);
        ejecutarSolicitudTarjetaReceptor(app);
        ejecutarSolicitudAperturaHeladeraReceptor(app);
        ejecutarMarcadoresReceptor(app);
        ejecutarViandasReceptor(app);
        ejecutarEstadoHeladeraReceptor(app);
        ejecutarSuscripcionesReceptor(app);
        ejecutarSuscripcionUsuarioExistente(app);
        ejecutarFallaHeladeraReceptor(app);
        ejecutarSolicitudPremiosReceptor(app);
        ejecutarSolicitudPremiosExistentes(app);
        ejecutarCanjearPremioReceptor(app);
        ejecutarCSVReceptor(app);
        ejecutarRecomendadorDonacionesReceptor(app);
        ejecutarLoginReceptor(app);
        ejecutarColaboracionesRealizadas(app);
        ejecutarSolicitudReportesExistentesReceptor(app);
        ejecutarPerfilReceptor(app);
        ejecutarCargaDePersonasVulnerables(app);
        ejecutarActualizacionConfiguraciones(app);
        ejecutarRecuperarContrasenia(app);
        ejecutarConfirmacionContrasenia(app);
        ejecutarRecepcionDeArchivos(app);
        ejecutarActualizacionDescripcion(app);
        ejecutarIncidentesOcurridos(app);
        ejecutarIncidentesPendientesAsignados(app);
        ejecutarRealizarVisitaHeladera(app);
        ejecutarSolicitudDetalleVisitaHeladera(app);
        ejecutarSolicitudDetalleReporte(app);
        ejecutarGenerarReporteIndividual(app);
        ejecutarExportarReportePDF(app);

        programarPersistenciaDatos(em);
        programarConvertirseEnMayor();
        programarGenerarReporte();
    }

    private static void programarPersistenciaDatos(EntityManager em) {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                persistirDatos(em);
                clearLog();
            }
        }, 0, 30000);  // Se ejecuta cada 30 segundos - 1 minuto == (60000 ms)
    }

    private static void persistirDatos(EntityManager em) {
        BDUtils.comenzarTransaccion(em);

        for (Object object : ClaseCRUD.getInstance().getObjectList()) {
            ClaseCRUD.getInstance().create(object, em);
        }

        for (Object object : ClaseCRUD.getInstance().getObjectListDelete()) {
            ClaseCRUD.getInstance().delete(object, em);
        }

        BDUtils.commit(em);
        System.out.println("Persistencia de datos completada.");
    }

    private static void programarConvertirseEnMayor() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (PersonaEnSituacionVulnerable personaEnSituacionVulnerable : RepositoryPersonasVulnerables.getInstance().getPersonasVulnerables()) {
                    personaEnSituacionVulnerable.convertirse_en_mayor();
                }
            }
        }, 0, 7 * 24 * 60 * 60 * 1000);  // Se ejecuta cada 1 semana
    }

    private static void programarGenerarReporte() {
        Timer timer = new Timer(true);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    GeneradorDeReportes.getInstance().generar_reporte();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        // Calculamos el delay inicial
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date now = new Date();
        if (calendar.getTime().before(now)) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
        }

        long initialDelay = calendar.getTimeInMillis() - now.getTime();
        long period = 7 * 24 * 60 * 60 * 1000;  // Se ejecuta cada 1 semana

        timer.scheduleAtFixedRate(task, initialDelay, period);
    }
}
