package Heladera.controladoresHeladera;

import Heladera.Heladera;
import Heladera.Vianda;
import lombok.Getter;
import lombok.Setter;
import persistencia.ClaseCRUD;

import java.time.LocalDate;
import java.util.*;

@Getter
public class GestorDeViandas {
    private Heladera heladera;
    @Setter private List<Vianda> viandas;
    @Setter private int viandasUltimoRegistro;
    private Timer timer;

    public GestorDeViandas(Heladera heladera){
        this.heladera = heladera;
        this.viandas = new ArrayList<>();
        this.viandasUltimoRegistro = 0;
        this.timer = new Timer(true);
        this.scheduleSacarViandasVencidas(timer);
    }

    public void agregar_vianda(Vianda vianda){
        if(this.esta_en_capacidad_maxima()){
            throw new IllegalStateException("La heladera esta en su capacidad maxima");
        }
        viandas.add(vianda);
        heladera.getController().dar_aviso();
    }

    public void sacar_vianda(Vianda vianda){
        viandas.remove(vianda);
        heladera.getController().dar_aviso();
    }

    public int estimar_cantidad_viandas_atracadas(){
        return viandasUltimoRegistro - viandas.size();
    }

    public boolean esta_en_capacidad_maxima(){
        return heladera.getModelo().getCapacidadMaxima() - heladera.getGestorDeViandas().getViandas().size() == 0;
    }

    public int capacidad_disponible(){
        return heladera.getModelo().getCapacidadMaxima() - heladera.getGestorDeViandas().getViandas().size();
    }

    private void scheduleSacarViandasVencidas(Timer timer) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sacar_viandas_vencidas();
            }
        }, 0, 24 * 60 * 60 * 1000);  // Se ejecuta cada 1 dia
    }

    private void sacar_viandas_vencidas() {
        viandas.forEach(vianda -> {
            if (vianda.getFechaCaducidad().isBefore(LocalDate.now()) || vianda.getFechaCaducidad().isEqual(LocalDate.now())) {
                viandas.remove(vianda);
                ClaseCRUD.getInstance().getObjectList().remove(vianda);
                ClaseCRUD.getInstance().remove(vianda);
            }
        });
    }
}
