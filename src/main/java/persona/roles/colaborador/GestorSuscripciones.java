package persona.roles.colaborador;

import lombok.Getter;
import lombok.Setter;
import medioDeContacto.MedioDeContacto;
import medioDeContacto.TipoMedioContacto;
import persona.roles.colaborador.OpcionesSuscripciones.OpcionSuscripcion;
import persona.roles.colaborador.OpcionesSuscripciones.OpcionesSuscripcion;
import suscripciones.Suscripcion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class GestorSuscripciones {
    private Colaborador colaborador;
    @Setter private List<OpcionSuscripcion> opcionesSuscripcion = new ArrayList<OpcionSuscripcion>();
    @Setter private List<MedioDeContacto> mediosDeContactoParaSuscripcion = new ArrayList<MedioDeContacto>();
    @Setter private Suscripcion entidadNotificadora;

    public GestorSuscripciones(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public void suscribirse_a_notificaciones(List<OpcionSuscripcion> opciones, List<TipoMedioContacto> tiposMediosDeContacto){
        Map<OpcionesSuscripcion, OpcionSuscripcion> suscripcionMap = new HashMap<>();
        for (OpcionSuscripcion suscripcion : opciones) {
            suscripcionMap.putIfAbsent(suscripcion.getOpcion(), suscripcion);
        }
        this.opcionesSuscripcion.addAll(suscripcionMap.values());
        /* agregamos los medios de contacto donde nos vamos a comunicar por las suscripciones
        esto permite que un colaborador pueda tener muchos medios de contacto asociado pero nos vamos a comunicar con
        el (por el tema de las suscripciones) unicamente por los medios seleccionados */
        List<MedioDeContacto> mediosParaAgregar;
        for (TipoMedioContacto tipoMedioContacto : tiposMediosDeContacto){
            mediosParaAgregar = colaborador.getPersona().getMediosDeContacto().stream()
                    .filter(medioDeContacto -> medioDeContacto.getTipoMedioContacto() == tipoMedioContacto)
                    .collect(Collectors.toList());
            if(mediosParaAgregar.isEmpty()){
                throw new RuntimeException("No se encontraron medios de contacto del tipo " + tipoMedioContacto + ". Por favor, agregue un medio de contacto de ese tipo si quiere que se le notifique por este medio.");
            }
            this.mediosDeContactoParaSuscripcion.addAll(mediosParaAgregar);
        }
        colaborador.getController().calcular_zonas_frecuentes();
        String opcionesString = "[";
        for (OpcionSuscripcion opcion : opciones) {
            String opcionString = opcion.getOpcion().toString() + " " + opcion.getValor() + ", ";
            opcionesString = opcionesString.concat(opcionString);
        }
        opcionesString = opcionesString.substring(0, opcionesString.length() - 1);
        opcionesString = opcionesString.concat("]");
        entidadNotificadora = new Suscripcion(colaborador, tiposMediosDeContacto.toString(), opcionesString);
        colaborador.setSuscripcion(entidadNotificadora);
    }

    public void actualizarMediosDeContactoSuscripciones(List<TipoMedioContacto> tiposMediosDeContacto){
        List<MedioDeContacto> mediosParaAgregar;
        this.mediosDeContactoParaSuscripcion.clear();
        for (TipoMedioContacto tipoMedioContacto : tiposMediosDeContacto){
            mediosParaAgregar = colaborador.getPersona().getMediosDeContacto().stream()
                    .filter(medioDeContacto -> medioDeContacto.getTipoMedioContacto() == tipoMedioContacto)
                    .collect(Collectors.toList());
            if(mediosParaAgregar.isEmpty()){
                throw new RuntimeException("No se encontraron medios de contacto del tipo " + tipoMedioContacto + ". Por favor, agregue un medio de contacto de ese tipo si quiere que se le notifique por este medio.");
            }
            this.mediosDeContactoParaSuscripcion.addAll(mediosParaAgregar);
        }
        entidadNotificadora.setMediosDeContactoParaSuscripcion(tiposMediosDeContacto.toString());
    }

    public void actualizarOpcionesSuscripcion(List<OpcionSuscripcion> opciones){
        Map<OpcionesSuscripcion, OpcionSuscripcion> suscripcionMap = new HashMap<>();
        for (OpcionSuscripcion suscripcion : opciones) {
            suscripcionMap.putIfAbsent(suscripcion.getOpcion(), suscripcion);
        }
        this.opcionesSuscripcion.clear();
        this.opcionesSuscripcion.addAll(suscripcionMap.values());

        String opcionesString = "[";
        for (OpcionSuscripcion opcion : opciones) {
            String opcionString = opcion.getOpcion().toString() + " " + opcion.getValor() + ", ";
            opcionesString = opcionesString.concat(opcionString);
        }
        opcionesString = opcionesString.substring(0, opcionesString.length() - 1);
        opcionesString = opcionesString.concat("]");
        entidadNotificadora.setOpcionesSuscripcion(opcionesString);
    }
}
