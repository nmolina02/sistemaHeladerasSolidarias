package Heladera;

import java.time.LocalDate;
import java.util.List;

import Heladera.controladoresHeladera.*;
import Heladera.incidente.Incidente;
import colaboraciones.colaboracionesHumanas.DistribucionDeViandas;
import colaboraciones.colaboracionesHumanas.DonacionDeViandas;
import lombok.Getter;
import lombok.Setter;

import localizacion.Ubicacion;
import persistencia.ClaseCRUD;
import persona.roles.colaborador.Colaborador;
import reportes.FallasHeladera;
import reportes.MovimientosHeladera;
import repository.RepositoryHeladera;
import suscripciones.Suscripcion;
import tarjetas.ExtraccionDeVianda;
import tarjetas.Operacion;

import javax.persistence.*;

@Entity
@Table(name = "heladera")
@Getter
public class Heladera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hela_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ubic_id")
    @Setter private Ubicacion direccion;

    @ManyToOne
    @JoinColumn(name = "rol_id")
    private Colaborador propietario;

    @Column(name = "hela_nombre")
    @Setter private String nombreHeladera;

    @ManyToOne
    @JoinColumn(name = "mode_id")
    private Modelo modelo;

    @Column(name = "hela_fecha_inauguracion")
    private LocalDate fechaInauguracion;

    @Column(name = "hela_estado")
    @Enumerated(EnumType.STRING)
    @Setter private EstadoHeladera estadoHeladera;

    @Transient
    @Setter
    private HeladeraController controller;

    @Transient
    @Setter
    private GestorDeAlertas gestorDeAlertas;

    @Transient
    @Setter
    private GestorDeViandas gestorDeViandas;

    @Transient
    @Setter
    private GestorDeSolicitudes gestorDeSolicitudes;

    @Column(name = "hela_temperatura_max_user")
    private float tempMaxUser;

    @Column(name = "hela_temperatura_min_user")
    private float tempMinUser;

    @Column(name = "hela_cantidad_viandas")
    private int cantidadViandas;

    @OneToMany(mappedBy = "heladera", cascade = CascadeType.ALL)
    private List<DonacionDeViandas> donacionesDeViandas;

    @OneToMany(mappedBy = "heladeraOrigen", cascade = CascadeType.ALL)
    private List<DistribucionDeViandas> distribucionDeViandas1;

    @OneToMany(mappedBy = "heladeraDestino", cascade = CascadeType.ALL)
    private List<DistribucionDeViandas> distribucionDeViandas2;

    @OneToMany(mappedBy = "heladera", cascade = CascadeType.ALL)
    private List<Operacion> operaciones;

    @OneToMany(mappedBy = "heladera", cascade = CascadeType.ALL)
    private List<ExtraccionDeVianda> extraccionDeViandas;

    @OneToMany(mappedBy = "heladera", cascade = CascadeType.ALL)
    private List<Incidente> incidentes;

    @OneToMany(mappedBy = "heladeraReservada", cascade = CascadeType.ALL)
    private List<Solicitud> solicitudes;

    @ManyToMany(mappedBy = "heladeras")
    private List<Suscripcion> suscripciones;

    @OneToMany(mappedBy = "heladera", cascade = CascadeType.ALL)
    private List<Vianda> viandas;

    @OneToMany(mappedBy = "heladera", cascade = CascadeType.ALL)
    private List<FallasHeladera> fallasHeladera;

    @OneToMany(mappedBy = "heladera", cascade = CascadeType.ALL)
    private List<MovimientosHeladera> movimientosHeladeras;

    @Column(name = "hela_imagen")
    @Setter private String imagen;

    public Heladera(Ubicacion direccion, String nombreHeladera, Modelo modelo, Colaborador propietario){
        this.direccion = direccion;
        this.propietario = propietario;
        this.nombreHeladera = nombreHeladera;
        this.modelo = modelo;
        this.fechaInauguracion = LocalDate.now();
        this.estadoHeladera = EstadoHeladera.FUNCIONAMIENTO;
        RepositoryHeladera repositoryHeladera = RepositoryHeladera.getInstance();
        repositoryHeladera.getHeladerasDelSistema().add(this);
        this.controller = new HeladeraController(this);
        this.gestorDeAlertas = new GestorDeAlertas(this);
        this.gestorDeViandas = new GestorDeViandas(this);
        this.gestorDeSolicitudes = new GestorDeSolicitudes(this);
        ClaseCRUD.getInstance().add(this);
    }

    public Heladera() {}
}