package Heladera.controladoresHeladera;

import java.time.Period;
import java.time.LocalDate;

import Heladera.Heladera;
import suscripciones.GestorSuscripcionesHeladeras;
import localizacion.APIUbicacion.APIUbicacion;
import localizacion.APIUbicacion.Punto;
import lombok.Getter;
import lombok.Setter;

@Getter
public class HeladeraController {
    private final Heladera heladera;
    @Setter private float tempMaxUser;
    @Setter private float tempMinUser;
    @Setter private float tempActual;
    @Setter private int mesesActiva = 0;

    public HeladeraController(Heladera heladera){
        this.heladera = heladera;
        this.tempMaxUser = heladera.getModelo().getTempMaxAceptable();
        this.tempMinUser = heladera.getModelo().getTempMinAceptable();
        this.tempActual = 5.0f;
    }

    public void calcular_meses_activa() {
        LocalDate fechaActual = LocalDate.now();
        Period periodo = Period.between(heladera.getFechaInauguracion(), fechaActual);
        this.setMesesActiva(periodo.getYears() * 12 + periodo.getMonths());
    }

    public Punto calcular_punto_ubicacion() { //consideramos como radio unos 5 km a partir de la ubicacion de la heladera
        return new Punto(heladera.getDireccion().getLatitud(), heladera.getDireccion().getLongitud(), "5000");
    }

    public Boolean heladera_dentro_del_rango (Punto punto){
        APIUbicacion apiUbicacion = APIUbicacion.getInstance();
        return apiUbicacion.esta_dentro_del_rango(punto, heladera.getDireccion());
    }

    public void dar_aviso(){
        GestorSuscripcionesHeladeras gestorSuscripcionesHeladeras = GestorSuscripcionesHeladeras.getInstance();
        gestorSuscripcionesHeladeras.recibir_aviso(heladera);
    }

    public void cambiar_temperatura_usuario(float tempMax, float tempMin){
        if(tempMax > heladera.getModelo().getTempMaxAceptable() || tempMin < heladera.getModelo().getTempMinAceptable()){
            throw new RuntimeException("Las temperaturas de usuario superan o estan por debajo de las temperaturas aceptadas por el modelo");
        }
        else{
            this.setTempMaxUser(tempMax);
            this.setTempMinUser(tempMin);
        }
    }
}
