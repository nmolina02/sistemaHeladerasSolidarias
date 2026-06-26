package reportes;

import lombok.Getter;
import lombok.Setter;
import persistencia.ClaseCRUD;
import persona.roles.Usuario;
import repository.RepositorySolicitudReporte;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Entity
@Table(name = "solicitud_reporte_individual")
@Getter
public class SolicitudReporteIndividual {
    @Id
    @Column(name = "soli_repo_id")
    @Setter private String id;

    @ManyToOne
    @JoinColumn(name = "usua_id")
    private Usuario usuario;

    @Column(name = "soli_repo_tipo")
    private String tipoSolicitud;

    @Column(name = "soli_repo_fecha")
    private LocalDateTime fechaSolicitud;

    @Column(name = "soli_repo_estado")
    @Setter private boolean solicitudExpirada = false;

    public SolicitudReporteIndividual(Usuario usuario, String tipoSolicitud, String id) {
        this.id = id;
        this.usuario = usuario;
        this.tipoSolicitud = tipoSolicitud;
        this.fechaSolicitud = LocalDateTime.now();
        RepositorySolicitudReporte.getInstance().agregarSolicitudReporteIndividual(this);
        ClaseCRUD.getInstance().add(this);
    }

    public SolicitudReporteIndividual() {}

    public boolean solicitudExpirada() {
        LocalDateTime domingoAnterior = fechaSolicitud
                                        .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                                        .withHour(0)
                                        .withMinute(0)
                                        .withSecond(0)
                                        .withNano(0);
        return LocalDateTime.now().isAfter(domingoAnterior.plusDays(7));
    }
}
