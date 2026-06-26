package factoryColaboracion;

import Heladera.Heladera;
import Heladera.Modelo;
import Heladera.Vianda;
import colaboraciones.*;
import colaboraciones.colaboracionesCompartidas.DonacionDeDinero;
import colaboraciones.colaboracionesCompartidas.Frecuencia;
import colaboraciones.colaboracionesHumanas.DistribucionDeViandas;
import colaboraciones.colaboracionesHumanas.DonacionDeViandas;
import colaboraciones.colaboracionesHumanas.MotivoDistribucion;
import colaboraciones.colaboracionesHumanas.RegistroDePersonasVulnerables;
import colaboraciones.colaboracionesJuridicas.HacerseCargo;
import colaboraciones.colaboracionesJuridicas.OfrecerProductoReconocimiento;
import localizacion.Ubicacion;
import persona.personas.PersonaFisica;
import persona.personas.PersonaJuridica;
import persona.roles.colaborador.Colaborador;
import persona.roles.personaEnSituacionVulnerable.PersonaEnSituacionVulnerable;
import premios.PremioColaboracion;
import reportes.GeneradorDeReportes;

import java.time.LocalDate;

public class FactoryColaboracion {

    public static Colaboracion crear_colaboracion(TipoColaboracion tipoColaboracion, Colaborador colaborador, Object...params) throws Exception {
        Colaboracion colaboracionInstancia = null;
        switch(tipoColaboracion) {
            case DONACION_DE_DINERO:
                if (params.length != 3 || !(params[0] instanceof LocalDate) || !(params[1] instanceof Double) || !(params[2] instanceof Frecuencia)) {
                    throw new IllegalArgumentException("Parametros invalidos para DonacionDeDinero");
                } else colaboracionInstancia = new DonacionDeDinero((LocalDate) params[0], (Double) params[1], (Frecuencia) params[2]);
                break;
            case DONACION_DE_VIANDAS:
                if (colaborador.persona.getClass() != PersonaFisica.class) {
                    throw new Exception();
                } else if (params.length != 2 || !(params[0] instanceof Vianda) || !(params[1] instanceof Heladera)) {
                    throw new IllegalArgumentException("Parametros invalidos para DonacionDeViandas");
                } else if (((Heladera) params[1]).getGestorDeSolicitudes().existe_solicitud_en_tiempo(colaborador) &&
                        colaborador.getTarjeta() != null) {
                    colaboracionInstancia = new DonacionDeViandas((Vianda) params[0], (Heladera) params[1]);
                    GeneradorDeReportes.getInstance().donacion_viandas(colaborador, 1);
                    GeneradorDeReportes.getInstance().movimientos_heladera((Heladera) params[1], 1);
                    colaborador.getTarjeta().registrar_operacion((Heladera) params[1]);
                    colaborador.getController().calcular_zonas_frecuentes();
                    ((Heladera) params[1]).getGestorDeSolicitudes().getSolicitudesPendientes().removeIf(solicitud -> solicitud.getSolicitante().equals(colaborador));
                } else if(colaborador.getTarjeta() == null) {
                    if (colaborador.persona.getDireccion() == null){
                        throw new IllegalArgumentException("No se generó la tarjeta ya que usted no tiene un domicilio registrado.");
                    }
                    System.out.println("No hay una tarjeta asociada, por favor transaccione una.");
                } else {
                    ((Heladera) params[1]).getGestorDeSolicitudes().getSolicitudesPendientes().removeIf(solicitud -> solicitud.getSolicitante().equals(colaborador));
                    throw new IllegalStateException("No se cumplen las condiciones para DonacionDeViandas, el colaborador debe crear una solicitud primero");
                }
                break;
            case DISTRIBUCION_DE_VIANDAS:
                if (colaborador.persona.getClass() != PersonaFisica.class) {
                    throw new Exception();
                } else if(params.length != 5 || !(params[0] instanceof Heladera) || !(params[1] instanceof Heladera) || !(params[2] instanceof Integer) || !(params[3] instanceof MotivoDistribucion) || !(params[4] instanceof LocalDate) ){
                    throw new IllegalArgumentException("Parametros invalidos para DistribucionDeViandas");
                } else if(((Heladera) params[1]).getGestorDeViandas().esta_en_capacidad_maxima()) {
                    throw new IllegalArgumentException("La segunda heladera esta en su capacidad maxima, no se puede realizar la distribucion de viandas");
                } else if(((Heladera) params[1]).getGestorDeViandas().capacidad_disponible() > 0 && ((Heladera) params[1]).getGestorDeViandas().capacidad_disponible() < (Integer) params[2]) {
                    throw new IllegalArgumentException("No se puede distribuir la cantidad total de viandas, solamente se pueden distribuir " + ((Heladera) params[1]).getGestorDeViandas().getViandas().size() + " viandas");
                } else if (((Heladera) params[0]).getGestorDeSolicitudes().existe_solicitud_en_tiempo(colaborador) &&
                        ((Heladera) params[1]).getGestorDeSolicitudes().existe_solicitud_en_tiempo(colaborador) &&
                        colaborador.getTarjeta() != null) {
                    colaboracionInstancia = new DistribucionDeViandas((Heladera) params[0], (Heladera) params[1], (Integer) params[2], (MotivoDistribucion) params[3], (LocalDate) params[4]);
                    GeneradorDeReportes.getInstance().movimientos_heladera((Heladera) params[0], -(Integer) params[2]);
                    GeneradorDeReportes.getInstance().movimientos_heladera((Heladera) params[1], (Integer) params[2]);
                    colaborador.getTarjeta().registrar_operacion((Heladera) params[0]);
                    colaborador.getController().calcular_zonas_frecuentes();
                    ((Heladera) params[0]).getGestorDeSolicitudes().getSolicitudesPendientes().removeIf(solicitud -> solicitud.getSolicitante().equals(colaborador));
                } else if(colaborador.getTarjeta() == null) {
                    if (colaborador.persona.getDireccion() == null){
                        throw new IllegalArgumentException("No se generó la tarjeta ya que usted no tiene un domicilio registrado.");
                    }
                    System.out.println("No hay una tarjeta asociada, Por favor transaccione una.");
                }else {
                    ((Heladera) params[0]).getGestorDeSolicitudes().getSolicitudesPendientes().removeIf(solicitud -> solicitud.getSolicitante().equals(colaborador));
                }
                break;
            case HACERSE_CARGO:
                if(colaborador.persona.getClass() != PersonaJuridica.class){
                    throw new Exception();
                } else if (params.length != 4 || !(params[0] instanceof String) || !(params[1] instanceof Modelo) || !(params[2] instanceof Ubicacion) || !(params[3] instanceof String)) {
                    throw new IllegalArgumentException("Parametros invalidos para HacerseCargo");
                } else colaboracionInstancia = new HacerseCargo((String) params[0], (Modelo) params[1], (Ubicacion) params[2], colaborador, (String) params[3]);
                break;
            case REGISTRO_DE_PERSONAS_VULNERABLES:
                if(colaborador.persona.getClass() != PersonaFisica.class){
                    throw new Exception();
                } else if (params.length != 2 || !(params[0] instanceof PersonaEnSituacionVulnerable) || !(params[1] instanceof String)) {
                    throw new IllegalArgumentException("Parametros invalidos para RegistroDePersonasVulnerables");
                } else colaboracionInstancia = new RegistroDePersonasVulnerables((PersonaEnSituacionVulnerable) params[0], colaborador, (String) params[1]);
                break;
            case OFRECER_PRODUCTO_RECONOCIMIENTO:
                System.out.println(params[0]);
                if(colaborador.persona.getClass() != PersonaJuridica.class){
                    throw new Exception();
                } else if (params.length != 1 || !(params[0] instanceof PremioColaboracion)) {
                    throw new IllegalArgumentException("Parametros invalidos para OfrecerProductoReconocimiento");
                } else {
                    colaboracionInstancia = new OfrecerProductoReconocimiento((PremioColaboracion) params[0]);
                    System.out.println("Se ejecuto la colaboracion: " + colaboracionInstancia);
                }
                break;
            default:
                throw new IllegalArgumentException("Tipo de colaboracion invalido");
        }
        return colaboracionInstancia;
    }
}
