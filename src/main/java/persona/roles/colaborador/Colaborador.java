package persona.roles.colaborador;

import Heladera.controladoresHeladera.Solicitud;
import factoryColaboracion.FactoryColaboracion;
import Heladera.Heladera;
import Heladera.incidente.Alerta.TipoGravedad;
import Heladera.incidente.GestorIncidentes;
import colaboraciones.Colaboracion;
import colaboraciones.TipoColaboracion;
import Heladera.incidente.FallaTecnica;
import generadorDeCodigosUnicos.GeneradorDeCodigosUnicos;
import lombok.Getter;
import lombok.Setter;
import persistencia.ClaseCRUD;
import persona.personas.Persona;
import persona.roles.Rol;
import premios.PremioColaboracion;
import persona.roles.Usuario;
import reportes.ViandasPorColaborador;
import repository.RepositoryColaborador;
import suscripciones.Suscripcion;
import tarjetas.TarjetaColaborador;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "colaborador")
@Getter
public class Colaborador extends Rol {
    @OneToMany(mappedBy = "colaborador", cascade = CascadeType.ALL)
    @Setter private List<Colaboracion> colaboracionesRealizadas;

    @Transient
    @Setter
    private GestorDePuntaje gestorDePuntaje;

    @Transient
    @Setter
    private ColaboradorController controller;

    @Transient
    @Setter
    private GestorSuscripciones gestorSuscripciones;

    @OneToOne
    @JoinColumn(name = "tarj_id")
    @Setter private TarjetaColaborador tarjeta;

    @Column(name = "cola_puntos_acumulados")
    @Setter private double puntos_acumulados;

    @OneToOne
    @JoinColumn(name = "susc_id")
    @Setter private Suscripcion suscripcion;

    @OneToMany(mappedBy = "propietario", cascade = CascadeType.ALL)
    private List<Heladera> heladeras;

    @OneToMany(mappedBy = "colaborador", cascade = CascadeType.ALL)
    private List<FallaTecnica> fallaTecnicas;

    @OneToMany(mappedBy = "solicitante", cascade = CascadeType.ALL)
    private List<Solicitud> solicitudes;

    @OneToMany(mappedBy = "colaboradorHumano", cascade = CascadeType.ALL)
    private List<ViandasPorColaborador> viandasPorColaborador;

    @OneToOne
    @JoinColumn(name = "usua_id")
    @Setter private Usuario usuario;

    @Column(name = "cola_fecha_ingreso")
    @Setter private LocalDateTime fechaIngreso;

    @OneToMany(mappedBy = "colaboradorAdquirido", cascade = CascadeType.ALL)
    private List<PremioColaboracion> premios;

    public Colaborador(Persona persona){
        super(persona);
        this.colaboracionesRealizadas = new ArrayList<Colaboracion>();
        this.gestorDePuntaje = new GestorDePuntaje(this);
        this.controller = new ColaboradorController(this);
        this.gestorSuscripciones = new GestorSuscripciones(this);
        this.suscripcion = gestorSuscripciones.getEntidadNotificadora();
        this.puntos_acumulados = gestorDePuntaje.getPuntosTotales();
        RepositoryColaborador.getInstance().addColaborador(this);
        ClaseCRUD.getInstance().add(this);
    }

    public Colaborador() {}

    public void realizar_colaboracion(TipoColaboracion tipoColaboracion, Object ...params){
        try {
            Colaboracion colaboracion = FactoryColaboracion.crear_colaboracion(tipoColaboracion, this, params);
            colaboracion.ejecutar_colaboracion();
            colaboracion.setColaborador(this);
            System.out.println("Colaboracion realizada con exito: " + colaboracion.getTipoColaboracion());
            this.colaboracionesRealizadas.add(colaboracion);
            gestorDePuntaje.actualizar_puntaje(colaboracion);
            this.setPuntos_acumulados(gestorDePuntaje.getPuntosTotales());
        } catch (Exception e) {
            System.out.println("No se pudo crear la colaboracion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void canjear_premio(PremioColaboracion premio){
        if(!gestorDePuntaje.puede_realizar_el_canje(premio)) {
            throw new RuntimeException("Puntos insuficientes para realizar el canje");
        }
        double puntajeResultante = gestorDePuntaje.getPuntosTotales() - premio.getPuntos_necesarios();
        gestorDePuntaje.setPuntosTotales(puntajeResultante);
        this.setPuntos_acumulados(gestorDePuntaje.getPuntosTotales());
        premio.realizar_canje();
    }

    public void solicitar_apertura_heladera(Heladera heladera) {
        heladera.getGestorDeSolicitudes().generar_solicitud_apertura(this);
    }

    public void solicitar_tarjeta(){
        GeneradorDeCodigosUnicos generadorDeCodigosUnicos = GeneradorDeCodigosUnicos.getInstance();
        generadorDeCodigosUnicos.crearNumeroTarjeta();
        TarjetaColaborador tarjetaColaborador = new TarjetaColaborador(this, generadorDeCodigosUnicos.getUltimoNumeroGenerado());

        this.setTarjeta(tarjetaColaborador);
    }

    public void reportar_falla_tecnica(Heladera heladera, String descripcion, String foto, TipoGravedad gravedad){
        heladera.getGestorDeAlertas().ocurre_falla_tecnica();
        FallaTecnica fallaTecnica = new FallaTecnica(heladera, this, descripcion, foto, gravedad);
        GestorIncidentes.getInstance().gestionarIncidente(fallaTecnica);
        heladera.getGestorDeAlertas().setAlertaActual("Falla reportada por " + this.getUsuario().getUsername());
    }
}

