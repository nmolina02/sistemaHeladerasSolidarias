package Heladera;

import lombok.Getter;
import lombok.Setter;
import persistencia.ClaseCRUD;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "vianda")
@Getter
public class Vianda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vian_id")
    private Long id;

    @Column(name = "vian_comida")
    private String comida;

    @Column(name = "vian_calorias")
    private int calorias;

    @Column(name = "vian_peso")
    private int peso;

    @Column(name = "vian_fecha_caducidad")
    private LocalDate fechaCaducidad;

    @Column(name = "vian_fecha_donacion")
    private LocalDate fechaDonacion;

    @ManyToOne
    @JoinColumn(name = "hela_id")
    @Setter private Heladera heladera;

    @Transient
    private boolean entregada;

    public Vianda(String comida, int calorias, int peso, LocalDate fechaCaducidad) {
        this.comida = comida;
        this.calorias = calorias;
        this.peso = peso;
        this.fechaCaducidad = fechaCaducidad;
        this.fechaDonacion = LocalDate.now();
        this.heladera = null;
        this.entregada = false;
        ClaseCRUD.getInstance().add(this);
    }

    public Vianda() {}

    public void agregar_a(Heladera heladera){ // hacemos que la vianda conozca la heladera en la que esta
        this.setHeladera(heladera);
    }
}