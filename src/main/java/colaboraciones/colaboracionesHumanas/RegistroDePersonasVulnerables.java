package colaboraciones.colaboracionesHumanas;

import lombok.Getter;
import lombok.Setter;
import persistencia.ClaseCRUD;
import tarjetas.TarjetaPersonaVulnerable;
import repository.RepositoryPersonasVulnerables;
import colaboraciones.Colaboracion;
import colaboraciones.TipoColaboracion;
import generadorDeCodigosUnicos.GeneradorDeCodigosUnicos;
import persona.roles.colaborador.Colaborador;
import persona.roles.personaEnSituacionVulnerable.PersonaEnSituacionVulnerable;

import javax.persistence.*;

@Entity
@Table(name = "registro_de_personas_vulnerables")
@Getter
public class RegistroDePersonasVulnerables extends Colaboracion {
    @Transient
    private Colaborador colaborador;

    @OneToOne
    @JoinColumn(name = "rol_id")
    private PersonaEnSituacionVulnerable personaVulnerable;

    @Transient
    @Setter private String codigo;

    public RegistroDePersonasVulnerables(PersonaEnSituacionVulnerable personaVulnerable, Colaborador colaborador, String idTutor){
        super(TipoColaboracion.REGISTRO_DE_PERSONAS_VULNERABLES);
        this.personaVulnerable = personaVulnerable;
        this.colaborador = colaborador;
        if (!idTutor.isEmpty())
            this.codigo = idTutor;
        ClaseCRUD.getInstance().add(this);
    }

    public RegistroDePersonasVulnerables(){}

    //SI ESTA REGISTRADA TIENE TARJETA, SI NO ESTA REGISTRADA ENTONCES NO TIENE TARJETA
    @Override
    public void ejecutar_colaboracion(){
        //me parece que no podemos implementar esto donde se chequearia que si una persona ya tiene una tarjeta,
        //es decir si ya esta registrado, entonces no lo registras, por lo que no le das la tarjeta.
        //como no tenemos base de datos, no podemos realmente consultar si ya esta registrado
        //habria que hacer una funcion para generar el codigo y que no se repita, pero por ahora lo dejamos hardcodeado

        if (personaVulnerable.isEsMenor()){
            int idPersonaVulnerableTutor = Integer.parseInt(codigo);
            PersonaEnSituacionVulnerable tutor = RepositoryPersonasVulnerables.getInstance().getPersonasVulnerables().get(idPersonaVulnerableTutor - 1);
            tutor.agregar_menor_a_cargo(personaVulnerable);
            personaVulnerable.setTutor(tutor);
            return;
        }

        GeneradorDeCodigosUnicos generadorDeCodigosUnicos = GeneradorDeCodigosUnicos.getInstance();
        generadorDeCodigosUnicos.crearNumeroTarjeta();

        TarjetaPersonaVulnerable nuevaTarjeta = new TarjetaPersonaVulnerable(this.personaVulnerable, generadorDeCodigosUnicos.getUltimoNumeroGenerado());
        this.personaVulnerable.setTarjeta(nuevaTarjeta);
    }

    @Override
    public double calcular_puntos(){
        return 1;
    }
}
