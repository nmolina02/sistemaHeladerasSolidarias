package persona.roles.tecnico;

import Heladera.EstadoHeladera;
import Heladera.VisitaHeladera;
import Heladera.incidente.Incidente;
import localizacion.APIUbicacion.Punto;
import lombok.Setter;
import persistencia.ClaseCRUD;
import persona.personas.PersonaFisica;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import persona.roles.Rol;
import persona.roles.Usuario;
import repository.RepositoryTecnicos;

import javax.persistence.*;

@Entity
@Table(name = "tecnico")
@Getter
public class Tecnico extends Rol {
    @ManyToOne
    @JoinColumn(name = "punt_id")
    @Setter private Punto areaDeCobertura;

    @Transient
    @Setter private GestorTecnico gestorTecnico;

    @OneToMany(mappedBy = "tecnico", cascade = CascadeType.ALL)
    private List<Incidente> incidentes;

    @ManyToOne
    @JoinColumn(name = "usua_id")
    @Setter private Usuario usuario;

    @Column(name = "tecn_fecha_ingreso")
    @Setter private LocalDateTime fechaIngreso;

    public Tecnico(PersonaFisica persona, Punto areaDeCobertura){
        super(persona);
        this.areaDeCobertura = areaDeCobertura;
        this.gestorTecnico = new GestorTecnico();
        RepositoryTecnicos.getInstance().getTecnicos().add(this);
        ClaseCRUD.getInstance().add(this);
    }

    public Tecnico() {}

    public void pasar_a_ocupado() {
        gestorTecnico.setEstadoTecnico(true);
    }

    public void arreglar_heladera(Incidente incidente, String imagen){
        EstadoHeladera estadoDespuesVisita = gestorTecnico.generarEstadoAleatorio();
        System.out.println("Arreglando heladera " + incidente.getHeladera().getNombreHeladera());
        new VisitaHeladera(LocalDateTime.now(), incidente, gestorTecnico.armar_descripcion_arreglo(estadoDespuesVisita), imagen);
        if (estadoDespuesVisita == EstadoHeladera.FUNCIONAMIENTO) {
            fin_arreglo(incidente);
        }
    }

    private void fin_arreglo(Incidente incidente){
        incidente.getHeladera().getGestorDeAlertas().cambiar_estado(EstadoHeladera.FUNCIONAMIENTO);
        incidente.getHeladera().getGestorDeAlertas().setAlertaActual("No hay alertas registradas");
        System.out.println("Heladera " + incidente.getHeladera().getGestorDeAlertas().getAlertaActual() + " arreglada");
        incidente.setSolucionado(true);
        gestorTecnico.setEstadoTecnico(false);
        System.out.println("Heladera " + incidente.getHeladera().getNombreHeladera() + " arreglada");
    }
}
