package persona.roles;

import lombok.Getter;
import lombok.Setter;
import persistencia.ClaseCRUD;
import persona.personas.Persona;
import reportes.SolicitudReporteIndividual;
import repository.RepositoryUsuario;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "usuario")
@Setter
@Getter
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usua_id")
    private int id;

    @Column(name = "usua_username")
    private String username;

    @Column(name = "usua_password")
    private String password;

    @Column(name = "usua_descripcion")
    private String descripcion;

    @Column(name = "usua_imagen")
    private String imagen;

    @OneToOne
    @JoinColumn(name = "pers_id")
    private Persona persona;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<SolicitudReporteIndividual> solicitudReporteIndividual;

    @Transient
    @Setter private String codigoRecuperacion;

    public Usuario(String username, String password, String descripcion, String imagen, Persona persona) {
        this.username = username;
        this.password = password;
        this.persona = persona;
        this.descripcion = descripcion;
        this.imagen = imagen;
        RepositoryUsuario.getInstance().addUsuario(this);
        ClaseCRUD.getInstance().add(this);
    }

    public Usuario() {}
}
