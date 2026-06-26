package Heladera;

import Heladera.incidente.Incidente;
import lombok.Getter;
import persistencia.ClaseCRUD;
import repository.RepositoryVisitaHeladera;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "visita_heladera")
@Getter
public class VisitaHeladera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visi_id")
    private Long id;

    @Column(name = "visi_fecha_visita")
    private LocalDateTime fechaDeVisita;

    @ManyToOne
    @JoinColumn(name = "inci_id")
    private Incidente incidente;

    @Column(name = "visi_descripcion_visita")
    private String descripcionVisita;

    @Column(name = "visi_foto_visita")
    private String fotoVisita;

    public VisitaHeladera(LocalDateTime fecha, Incidente incidente, String descripcion, String foto){
        this.fechaDeVisita = fecha;
        this.incidente = incidente;
        this.descripcionVisita = descripcion;
        this.fotoVisita = foto;
        RepositoryVisitaHeladera.getInstance().getVisitas().add(this);
        ClaseCRUD.getInstance().add(this);
    }

    public VisitaHeladera() {}
}