package Heladera.sensoreo;

import Heladera.controladoresHeladera.GestorDeAlertas;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ObservableSensor {
    private final GestorDeAlertas gestorDeAlertas;
    @Getter private float ultimaTemperatura;
    @Getter @Setter private LocalDateTime ultimaHoraRegistrada = LocalDateTime.now();
    private Timer timer;
    Random randomMovimiento = new Random();
    Random randomTemperatura = new Random();

    public ObservableSensor(GestorDeAlertas gestorDeAlertas) {
        this.gestorDeAlertas = gestorDeAlertas;
        this.timer = new Timer();
        this.scheduleNotificarMovimiento();
        this.scheduleNotificarTemperatura();
    }

    private void scheduleNotificarMovimiento() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                notificarMovimiento();
            }
        }, 0, 5 * 60 * 1000);  // Se ejecuta cada 5 minutos
    }

    public void notificarMovimiento() {
        if (gestorDeAlertas.getHeladera().getGestorDeViandas() == null) {
            return;
        } else if (gestorDeAlertas.getHeladera().getGestorDeViandas().getViandas().isEmpty()) {
            return;
        }
        float ocurrencia = randomMovimiento.nextFloat();
        if(ocurrencia < 0.04) {
            this.gestorDeAlertas.notificar_fraude();
            this.gestorDeAlertas.sufrir_atraque();
        }
    }

    private void scheduleNotificarTemperatura() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                notificarTemperatura();
            }
        }, 0, 5 * 60 * 1000);  // Se ejecuta cada 5 minutos
    }

    public void notificarTemperatura() {
        float unaTemperatura = randomTemperatura.nextFloat() * 10 + (-4);
        if(unaTemperatura != this.ultimaTemperatura) {
            this.gestorDeAlertas.controlar_temperatura(unaTemperatura);
            this.ultimaTemperatura = unaTemperatura;
            this.ultimaHoraRegistrada = LocalDateTime.now();
        }
    }
}