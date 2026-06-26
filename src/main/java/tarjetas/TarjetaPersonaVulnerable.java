package tarjetas;
import Heladera.Heladera;
import lombok.Getter;
import persistencia.ClaseCRUD;
import persona.roles.personaEnSituacionVulnerable.PersonaEnSituacionVulnerable;
import Heladera.Vianda;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tarjeta_persona_vulnerable")
@Getter
public class TarjetaPersonaVulnerable extends Tarjeta {
    @OneToOne
    @JoinColumn(name = "rol_id")
    private PersonaEnSituacionVulnerable personaEnSituacionVulnerable;

    @OneToMany(mappedBy = "tarjeta", cascade = CascadeType.ALL)
    private List<ExtraccionDeVianda> extraccionesRealizadas = new ArrayList<>();

    @Column(name = "tarj_vuln_usos_maximos")
    private int usosMaximos;

    @Column(name = "tarj_vuln_usos_restantes")
    private int usosRestantes;

    //TODO Modificar la creacion de tarjetas en Colaboracion "Registrar Persona Vulnerable"
    // ya quedo hecho creo
    public TarjetaPersonaVulnerable(PersonaEnSituacionVulnerable unaPersonaVulnerable, String codigo) {
        super(unaPersonaVulnerable,codigo);
        this.usosMaximos = this.calcular_uso_maximo(unaPersonaVulnerable);
        this.usosRestantes = this.getUsosMaximos();
        ClaseCRUD.getInstance().add(this);
    }

    public TarjetaPersonaVulnerable() {}

    private int calcular_uso_maximo(PersonaEnSituacionVulnerable personaVulnerable){
        if(personaVulnerable.getMenoresACargo() == null) return 4;
        return 4 + 2 * personaVulnerable.getMenoresACargo().size();
    }

    //Si le quedan usos, crea una nueva operación, la agrega a operacionesRealizadas y le manda el mensaje "extraer_vianda".
    public void realizar_extraccion(Heladera heladera, Vianda vianda) {
        if (!this.extraccionesRealizadas.isEmpty()) { // si tiene extracciones
            this.reestablecer_usos_restantes();
        }

        if (this.usosRestantes > 0) {
            ExtraccionDeVianda nuevaExtraccion = new ExtraccionDeVianda(LocalDate.now(), heladera);
            this.getExtraccionesRealizadas().add(nuevaExtraccion);
            this.usosRestantes--;
            nuevaExtraccion.extraer_vianda(vianda);
            ClaseCRUD.getInstance().getObjectList().remove(vianda);
            ClaseCRUD.getInstance().remove(vianda);
            System.out.print("Se ha realizado la extracción de la vianda.");
        } else throw new RuntimeException("No quedan usos disponibles.");
    }

    private void reestablecer_usos_restantes(){
        LocalDate fechaActual = LocalDate.now();
        LocalDate fechaUltimaExtraccion = obtener_fecha_ultima_extraccion();

        if (!fechaActual.equals(fechaUltimaExtraccion)){
            this.usosRestantes = this.usosMaximos;
            System.out.print("Se ha reestablecido el valor de los usos restantes.");
        }
        else System.out.print("No se ha reestablecido el valor de los usos restantes.");
    }

    private LocalDate obtener_fecha_ultima_extraccion(){
        int cantidadExtracciones = this.extraccionesRealizadas.size();

        if(extraccionesRealizadas.isEmpty()){
            throw new RuntimeException("No hay extracciones para averiguar una fecha");
        }

        ExtraccionDeVianda ultimaExtraccion = this.extraccionesRealizadas.get(cantidadExtracciones - 1);
        return ultimaExtraccion.getFecha();
    }
}
