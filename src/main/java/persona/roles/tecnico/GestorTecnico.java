package persona.roles.tecnico;

import Heladera.EstadoHeladera;
import Heladera.VisitaHeladera;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
public class GestorTecnico {
    @Setter private boolean estadoTecnico = false;
    private List<VisitaHeladera> visitas = new ArrayList<>();
    Random random = new Random();
    EstadoHeladera[] estados = EstadoHeladera.values();

    public GestorTecnico() {}

    public EstadoHeladera generarEstadoAleatorio() {
        return estados[random.nextInt(estados.length)];
    }

    public String armar_descripcion_arreglo(EstadoHeladera estado){
        switch (estado){
            case FUNCIONAMIENTO:
                return "La heladera ya está en funcionamiento";
            case DE_BAJA:
                return "La heladera aún no ha sido arreglada";
            case EN_REPARACION:
                return "La heladera está siendo reparada";
            default:
                return "";
        }
    }
}
