package premios;

import generadorDeCodigosUnicos.GeneradorDeCodigosUnicosPremios;
import lombok.Getter;
import lombok.Setter;
import persistencia.ClaseCRUD;
import persona.roles.colaborador.Colaborador;
import repository.RepositoryPremios;

import javax.persistence.*;

@Entity
@Table(name = "premio_colaboracion")
@Getter
public class PremioColaboracion {
    @Id
    @Column(name = "prem_id")
    private String id;

    @Column(name = "prem_nombre")
    private String nombre;

    @Column(name = "prem_puntos_necesarios")
    private int puntos_necesarios;

    @Column(name = "prem_imagen")
    private String imagen;

    @Column(name = "prem_categoria")
    @Enumerated(EnumType.STRING)
    private TipoCategoria categoria;

    @Column(name = "prem_descripcion")
    private String descripcion;

    @Column(name = "prem_canjeado")
    private boolean canjeado = false;

    @ManyToOne
    @JoinColumn(name = "rol_id")
    @Setter private Colaborador colaboradorAdquirido;

    public PremioColaboracion(String nombre, int puntos_necesarios, String imagen, TipoCategoria categoria, String descripcion) {
        GeneradorDeCodigosUnicosPremios generador = GeneradorDeCodigosUnicosPremios.getInstance();
        generador.crearNumeroPremio();
        this.id = generador.getUltimoNumeroGenerado();
        this.nombre = nombre;
        this.puntos_necesarios = puntos_necesarios;
        this.imagen = imagen;
        this.categoria = categoria;
        this.descripcion = descripcion;
        RepositoryPremios.getInstance().addPremio(this);
        ClaseCRUD.getInstance().add(this);
    }

    public PremioColaboracion() {}

    public void realizar_canje() {
        this.canjeado = true;
        RepositoryPremios.getInstance().getPremios().remove(this);
    }
}
