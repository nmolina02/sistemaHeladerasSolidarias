package localizacion.APIUbicacion;

import lombok.Getter;
import persistencia.ClaseCRUD;
import persona.roles.tecnico.Tecnico;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "punto")
@Getter
public class Punto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "punt_id")
    private Long id;

    @Column(name = "punt_latitud")
    private String latitud;

    @Column(name = "punt_longitud")
    private String longitud;

    @Column(name = "punt_radio")
    private String radio;

    @OneToMany(mappedBy = "areaDeCobertura", cascade = CascadeType.ALL)
    private List<Tecnico> tecnicos;

    public Punto(String latitud, String longitud, String radio) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.radio = radio;
        ClaseCRUD.getInstance().add(this);
    }

    public Punto() {}
}
