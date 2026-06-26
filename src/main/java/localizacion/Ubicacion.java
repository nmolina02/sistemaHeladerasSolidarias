package localizacion;

import colaboraciones.colaboracionesJuridicas.HacerseCargo;
import lombok.Getter;
import persistencia.ClaseCRUD;
import persona.personas.Persona;
import repository.RepositoryUbicacion;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ubicacion")
@Getter
public class Ubicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ubic_id")
    private Long id;

    @Column(name = "ubic_latitud")
    private String latitud;

    @Column(name = "ubic_longitud")
    private String longitud;

    @ManyToOne
    @JoinColumn(name = "ciud_id")
    private Ciudad ciudad;

    @Column(name = "ubic_calle")
    private String calle;

    @Column(name = "ubic_altura")
    private String altura;

    @OneToMany(mappedBy = "direccion", cascade = CascadeType.ALL)
    private List<Persona> personas;

    @OneToMany(mappedBy = "heladeraParaHacerseCargo", cascade = CascadeType.ALL)
    private List<HacerseCargo> hacerseCargo;

    public Ubicacion(String latitud, String longitud, Ciudad ciudad, String calle, String altura) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.ciudad = ciudad;
        this.calle = calle;
        this.altura = altura;
        RepositoryUbicacion.getInstance().agregarUbicacion(this);
        ClaseCRUD.getInstance().add(this);
    }

    public Ubicacion() {}
}