package persona.roles.personaEnSituacionVulnerable;

import generadorDeCodigosUnicos.GeneradorDeCodigosUnicos;
import persistencia.ClaseCRUD;
import persona.roles.Rol;
import repository.RepositoryPersonasVulnerables;
import tarjetas.TarjetaPersonaVulnerable;
import Heladera.Heladera;
import Heladera.Vianda;
import lombok.Getter;
import lombok.Setter;
import persona.personas.PersonaFisica;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "persona_en_situacion_vulnerable")
@Getter
public class PersonaEnSituacionVulnerable extends Rol {
    @Column(name = "pers_vuln_fecha_registro")
    private LocalDate fechaDeRegistro;

    @Column(name = "pers_vuln_es_menor")
    private boolean esMenor;

    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL)
    private List<PersonaEnSituacionVulnerable> menoresACargo;

    @ManyToOne
    @JoinColumn(name = "tutor_id")
    @Setter private PersonaEnSituacionVulnerable tutor;

    @Transient
    @Setter private TarjetaPersonaVulnerable tarjeta;

    public PersonaEnSituacionVulnerable(PersonaFisica persona, boolean esMenor, List<PersonaEnSituacionVulnerable> menoresACargo){
        super(persona);
        this.fechaDeRegistro = LocalDate.now();
        this.esMenor = esMenor;
        this.menoresACargo = menoresACargo;
        RepositoryPersonasVulnerables.getInstance().agregarPersonaVulnerable(this);
        ClaseCRUD.getInstance().add(this);
    }

    public PersonaEnSituacionVulnerable() {}

    public void retirarVianda(Heladera heladera, Vianda vianda){
        try {
            this.tarjeta.realizar_extraccion(heladera, vianda);
        } catch (RuntimeException e) {
            System.out.println("No se pudo realizar la extracción: " + e.getMessage()); //para devolverle el estado de la opercion a la Persona
        }
    }

    public void agregar_menor_a_cargo(PersonaEnSituacionVulnerable hijo){
        this.menoresACargo.add(hijo);
    }

    public void convertirse_en_mayor(){
        PersonaFisica persona = (PersonaFisica) this.getPersona();
        if (persona.getFechaNacimiento().isBefore(LocalDate.now().minusYears(18)) || persona.getFechaNacimiento().isEqual(LocalDate.now().minusYears(18))){
            this.esMenor = false;
        }
        this.esMenor = false;
        GeneradorDeCodigosUnicos generadorDeCodigosUnicos = GeneradorDeCodigosUnicos.getInstance();
        generadorDeCodigosUnicos.crearNumeroTarjeta();
        TarjetaPersonaVulnerable nuevaTarjeta = new TarjetaPersonaVulnerable(this, generadorDeCodigosUnicos.getUltimoNumeroGenerado());
        this.setTarjeta(nuevaTarjeta);
        this.tutor = null;
    }
}
