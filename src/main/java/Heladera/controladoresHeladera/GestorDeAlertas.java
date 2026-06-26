package Heladera.controladoresHeladera;

import Heladera.EstadoHeladera;
import Heladera.Heladera;
import Heladera.Vianda;
import Heladera.incidente.Alerta.AlertaConexion;
import Heladera.incidente.Alerta.AlertaFraude;
import Heladera.incidente.Alerta.AlertaTemperatura;
import Heladera.incidente.GestorIncidentes;
import Heladera.sensoreo.ObservableSensor;
import lombok.Getter;
import lombok.Setter;
import persistencia.ClaseCRUD;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

@Getter
public class GestorDeAlertas {
    private Heladera heladera;
    private final ObservableSensor sensoreoHeladera;
    private Timer timer;
    @Setter private String alertaActual = "No hay alertas registradas";
    Random random = new Random();

    public GestorDeAlertas(Heladera heladera){
        this.heladera = heladera;
        this.sensoreoHeladera = new ObservableSensor(this);
        this.timer = new Timer();
        this.schedule_recibir_temperatura_actual();
    }

    public void controlar_temperatura(float unaTemperatura){
        heladera.getController().setTempActual(unaTemperatura);
        if (heladera.getController().getTempActual() < heladera.getController().getTempMinUser() || heladera.getController().getTempActual() > heladera.getController().getTempMaxUser() ){
            System.out.println("La heladera "+ heladera.getNombreHeladera() + " tiene una falla de temperatura");
            this.cambiar_estado(EstadoHeladera.DE_BAJA);
            AlertaTemperatura alertaTemp = new AlertaTemperatura(LocalDateTime.now(), heladera,"La heladera "+ heladera.getNombreHeladera() + " tiene una falla de temperatura",unaTemperatura);
            GestorIncidentes.getInstance().gestionarIncidente(alertaTemp);
            this.setAlertaActual("Falla de temperatura. Temperatura registrada: " + unaTemperatura);
        }
    }

    public void notificar_fraude(){
        this.cambiar_estado(EstadoHeladera.DE_BAJA);
        System.out.println("La heladera " + heladera.getNombreHeladera() + " ha sido atracada");
        AlertaFraude alertaFraude = new AlertaFraude(LocalDateTime.now(), heladera, "La heladera " + heladera.getNombreHeladera() + " ha sido atracada");
        GestorIncidentes.getInstance().gestionarIncidente(alertaFraude);
        this.setAlertaActual("Heladera atracada");
    }

    public void cambiar_estado(EstadoHeladera estado){
        heladera.setEstadoHeladera(estado);
        if (estado.equals(EstadoHeladera.DE_BAJA)){
            heladera.getController().dar_aviso();
        }
    }

    private void schedule_recibir_temperatura_actual() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                recibir_temperatura_actual();
            }
        }, 0, 5 * 60 * 1000);  // Se ejecuta cada 5 minutos
    }

    public void recibir_temperatura_actual() {
        int minutosActuales = expresar_horario_en_minutos(LocalDateTime.now());
        int minutosRegistrados = expresar_horario_en_minutos(sensoreoHeladera.getUltimaHoraRegistrada());
        if(minutosActuales - minutosRegistrados > 5){ // significa que no se ha registrado la última temperatura
            this.cambiar_estado(EstadoHeladera.DE_BAJA);
            System.out.println("La heladera " + heladera.getNombreHeladera() + " no ha registrado su última temperatura");
            AlertaConexion alertaConexion = new AlertaConexion(LocalDateTime.now(), heladera, "La heladera " + heladera.getNombreHeladera() + " no ha registrado su última temperatura", heladera.getController().getTempActual());
            GestorIncidentes.getInstance().gestionarIncidente(alertaConexion);
            sensoreoHeladera.notificarTemperatura();
            this.setAlertaActual("Pérdida de conexión con el sensor de temperatura");
        }
    }

    private int expresar_horario_en_minutos(LocalDateTime horario) {
        int horas = horario.getHour();
        int minutos = horario.getMinute();
        return horas * 60 + minutos;
    }

    public void ocurre_falla_tecnica(){
        this.cambiar_estado(EstadoHeladera.DE_BAJA);
    }

    public void sufrir_atraque() {
        int limite = heladera.getGestorDeViandas().getViandas().size();
        int viandasAtracadas = random.nextInt(limite + 1);
        for (int i = 0; i < viandasAtracadas; i++) {
            Vianda vianda = heladera.getGestorDeViandas().getViandas().get(i);
            ClaseCRUD.getInstance().getObjectList().remove(vianda);
            heladera.getGestorDeViandas().getViandas().remove(vianda);
            ClaseCRUD.getInstance().remove(vianda);
        }
    }
}
