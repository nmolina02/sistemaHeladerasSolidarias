package repository;

import lombok.Getter;
import lombok.Setter;
import medioDeContacto.Mail;
import persona.personas.PersonaFisica;
import persona.roles.colaborador.Colaborador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class RepositoryColaborador {
    private static RepositoryColaborador instancia = null;
    @Setter private List<Colaborador> colaboradoresDelSistema;

    private RepositoryColaborador() {
        this.colaboradoresDelSistema = Collections.synchronizedList(new ArrayList<>());
    }

    public static synchronized RepositoryColaborador getInstance() {
        if(instancia == null){
            instancia = new RepositoryColaborador();
        }
        return instancia;
    }

    public synchronized void addColaborador(Colaborador colaborador) {
        colaboradoresDelSistema.add(colaborador);
    }

    public Colaborador buscarColaborador(String nombreCompleto, String mail) {
        for (Colaborador colaborador : colaboradoresDelSistema) {
            if (colaborador.getPersona() instanceof PersonaFisica) {
                PersonaFisica personaFisica = (PersonaFisica) colaborador.getPersona();
                String nombreApellido = personaFisica.getNombre() + " " + personaFisica.getApellido();
                Mail mailAsociado = personaFisica.getMediosDeContacto().stream()
                        .filter(m -> m instanceof Mail)
                        .map(m -> (Mail) m)
                        .findFirst()
                        .orElse(null);
                if (nombreApellido.equals(nombreCompleto) && mailAsociado != null && mailAsociado.getCasilla().equals(mail)) {
                    return colaborador;
                }
            }
        }
        return null;
    }
}