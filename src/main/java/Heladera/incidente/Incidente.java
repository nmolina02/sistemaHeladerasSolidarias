package Heladera.incidente;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import Heladera.incidente.Alerta.TipoGravedad;
import lombok.Getter;
import lombok.Setter;
import Heladera.Heladera;
import Heladera.VisitaHeladera;
import persistencia.ClaseCRUD;
import persona.roles.tecnico.Tecnico;
import reportes.GeneradorDeReportes;
import repository.RepositoryIncidente;
import repository.RepositoryVisitaHeladera;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "incidente")
@Getter
public abstract class Incidente{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inci_id")
    private Long id;

    @Column(name = "inci_fecha_incidente")
    private LocalDateTime fechaIncidente;

    @ManyToOne
    @JoinColumn(name = "hela_id")
    private Heladera heladera;

    @ManyToOne
    @JoinColumn(name = "rol_id")
    @Setter private Tecnico tecnico;

    @Column(name = "inci_descripcion")
    private String descripcion;

    @Column(name = "inci_solucionado")
    @Setter private boolean solucionado = false;

    @Column(name = "inci_gravedad")
    @Enumerated(EnumType.STRING)
    @Setter protected TipoGravedad gravedad;

    @OneToMany(mappedBy = "incidente", cascade = CascadeType.ALL)
    private List<VisitaHeladera> visitas;

    public Incidente(LocalDateTime fechaIncidente, Heladera heladera, String descripcion){
        this.fechaIncidente = fechaIncidente;
        this.heladera = heladera;
        this.descripcion = descripcion;
        RepositoryIncidente.getInstance().getIncidentes().add(this);
        GeneradorDeReportes.getInstance().falla_heladera(heladera);
        ClaseCRUD.getInstance().add(this);
    }

    public Incidente() {}

    public abstract void determinarGravedad();

    public List<VisitaHeladera> buscarVisitasAsociadas(){
        List<VisitaHeladera> visitas = new ArrayList<>();
        for (VisitaHeladera visita : RepositoryVisitaHeladera.getInstance().getVisitas()){
            if (visita.getIncidente().equals(this)){
                visitas.add(visita);
            }
        }
        return visitas;
    }
}