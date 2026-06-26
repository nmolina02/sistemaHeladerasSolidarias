package Heladera.incidente;

import Heladera.incidente.Alerta.AlertaConexion;
import Heladera.incidente.Alerta.AlertaFraude;
import Heladera.incidente.Alerta.AlertaTemperatura;
import localizacion.APIUbicacion.APIUbicacion;
import localizacion.Ubicacion;
import persona.roles.tecnico.Tecnico;
import repository.RepositoryTecnicos;

import java.util.Timer;
import java.util.TimerTask;

public class GestorIncidentes {
    private static GestorIncidentes instancia = null;
    private Timer timer;

    private GestorIncidentes(){
        timer = new Timer();
    }

    public static GestorIncidentes getInstance(){
        if (instancia == null){
            instancia = new GestorIncidentes();
        }
        return instancia;
    }

    private void buscarTecnicoMasCercanoSincronico(Incidente incidente) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                buscarTecnicoMasCercano(incidente);
            }
        }, 0, 60 * 1000);  // Se ejecuta cada 1 minuto
    }

    public void gestionarIncidente(Incidente incidente) {
        if (incidente instanceof AlertaTemperatura || incidente instanceof AlertaFraude || incidente instanceof AlertaConexion){
            incidente.determinarGravedad();
        }
        this.buscarTecnicoMasCercanoSincronico(incidente);
    }

    private void buscarTecnicoMasCercano(Incidente incidente) {
        Ubicacion ubicacionHeladera = incidente.getHeladera().getDireccion();
        Tecnico tecnicoMasCercano = null;

        while (true) {
            for (Tecnico tecnico : RepositoryTecnicos.getInstance().getTecnicos()) {
                APIUbicacion apiUbicacion = APIUbicacion.getInstance();
                if (tecnicoMasCercano == null) {
                    tecnicoMasCercano = tecnico;
                } else if (!tecnico.getGestorTecnico().isEstadoTecnico() && (apiUbicacion.distancia_entre_dos_puntos(tecnico.getAreaDeCobertura(), ubicacionHeladera) <
                        apiUbicacion.distancia_entre_dos_puntos(tecnicoMasCercano.getAreaDeCobertura(), ubicacionHeladera))) {
                    tecnicoMasCercano = tecnico;
                }
            }

            if (tecnicoMasCercano != null) {
                this.asignarTecnico(tecnicoMasCercano, incidente);
                return;
            } else {
                System.out.println("No se ha encontrado ningun tecnico disponible");
            }
        }
    }

    public void asignarTecnico(Tecnico tecnico, Incidente incidente){
        incidente.setTecnico(tecnico);
    }
}
