package localizacion;

import lombok.Getter;
import persistencia.ClaseCRUD;
import repository.RepositoryCiudad;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ciudad")
@Getter
public class Ciudad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ciud_id")
    private Long id;

    @Column(name = "ciud_nombre")
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "pais_id")
    private Pais pais;

    @OneToMany(mappedBy = "ciudad")
    private List<Ubicacion> ubicaciones;

    public Ciudad(String nombre, Pais pais) {
        this.nombre = nombre;
        this.pais = pais;
        RepositoryCiudad.getInstance().agregarCiudad(this);
        ClaseCRUD.getInstance().add(this);
    }

    public Ciudad() {}
}
