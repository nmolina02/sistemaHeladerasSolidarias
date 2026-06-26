package Heladera.controladoresHeladera;

import Heladera.Heladera;
import lombok.Getter;
import lombok.Setter;
import persistencia.ClaseCRUD;
import persona.roles.colaborador.Colaborador;
import repository.RepositorySolicitud;


import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitud")
@Getter
public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "soli_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rol_id")
    private Colaborador solicitante;

    @ManyToOne
    @JoinColumn(name = "hela_id")
    private Heladera heladeraReservada;

    @Column(name = "soli_fecha")
    private LocalDateTime fechaSolicitud;

    @Column(name = "soli_tiempo")
    private int tiempoSolicitud; // medido en horas

    @Column(name = "soli_fehaciente")
    @Setter private boolean fehaciente = false;

    public Solicitud(Colaborador solicitante, Heladera heladeraReservada, int tiempoSolicitud) {
        this.solicitante = solicitante;
        this.heladeraReservada = heladeraReservada;
        this.fechaSolicitud = LocalDateTime.now();
        this.tiempoSolicitud = tiempoSolicitud;
        RepositorySolicitud.getInstance().addSolicitud(this);
        ClaseCRUD.getInstance().add(this);
    }

    public Solicitud() {}

    public Boolean esta_dentro_del_horario(LocalDateTime horario) {
        int tiempoSolicitudEnMinutos = this.tiempoSolicitud * 60;
        // se coloca horario inicial, y luego horario final
        Duration duration = Duration.between(fechaSolicitud, horario);
        long tiempoTranscurridoEnMinutos = (int) duration.toMinutes();
        return tiempoTranscurridoEnMinutos <= tiempoSolicitudEnMinutos;
    }


}
