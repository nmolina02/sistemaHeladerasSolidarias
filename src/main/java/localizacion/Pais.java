package localizacion;

import lombok.Getter;
import persistencia.ClaseCRUD;
import repository.RepositoryPais;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "pais")
@Getter
public class Pais {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pais_id")
    private Long id;

    @Column(name = "pais_nombre")
    private String nombre;

    @OneToMany(mappedBy = "pais")
    private List<Ciudad> ciudades;

    public Pais(String nombre) {
        this.nombre = nombre;
        RepositoryPais.getInstance().agregarPais(this);
        ClaseCRUD.getInstance().add(this);
    }

    public Pais() {}
}
