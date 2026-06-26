package Heladera;

import lombok.Getter;
import persistencia.ClaseCRUD;
import repository.RepositoryModelo;

import javax.persistence.*;

@Entity
@Table(name = "modelo")
@Getter
public class Modelo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mode_id")
    private Long id;

    @Column(name = "mode_nombre")
    private String nombre;

    @Column(name = "mode_marca")
    private String marca;

    @Column(name = "mode_capacidad_maxima")
    private int capacidadMaxima;

    @Column(name = "mode_temp_max_aceptable")
    private float tempMaxAceptable;

    @Column(name = "mode_temp_min_aceptable")
    private float tempMinAceptable;

    @Transient
    private static Modelo modeloTipo1;

    @Transient
    private static Modelo modeloTipo2;

    public Modelo(String nombre, String marca, int capacidadMaxima, float tempMaxAceptable, float tempMinAceptable){
        this.nombre = nombre;
        this.marca = marca;
        this.capacidadMaxima = capacidadMaxima;
        this.tempMaxAceptable = tempMaxAceptable;
        this.tempMinAceptable = tempMinAceptable;
        RepositoryModelo.getInstance().agregarModelo(this);
        ClaseCRUD.getInstance().add(this);
    }

    public Modelo() {}
}