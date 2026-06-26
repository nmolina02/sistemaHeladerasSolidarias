package colaboraciones;

import colaboraciones.colaboracionesCompartidas.DonacionDeDinero;
import colaboraciones.colaboracionesCompartidas.Frecuencia;
import colaboraciones.colaboracionesHumanas.DistribucionDeViandas;
import colaboraciones.colaboracionesHumanas.DonacionDeViandas;
import colaboraciones.colaboracionesHumanas.RegistroDePersonasVulnerables;
import localizacion.Ubicacion;
import medioDeContacto.Mail;
import medioDeContacto.MedioDeContacto;
import persona.documentacion.Documentacion;
import persona.documentacion.TipoDocumentacion;
import persona.personas.PersonaFisica;
import persona.personas.PersonaJuridica;
import persona.roles.colaborador.Colaborador;
import repository.RepositoryColaborador;


import javax.swing.*;

import java.io.BufferedReader;

import java.io.FileReader;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class CargaMasivaDeColaboraciones {
    private static BufferedReader lector; //lee el archivo
    private static String linea; //recibe la linea de cada fila
    private static String[] partes = null; //almacena cada linea leida
    private static List<String[]> matrizLineas = new ArrayList<>(); //almacena todas las lineas leidas
    private static final String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern pattern = Pattern.compile(emailRegex);

    public static void cargarColaboracionesMatriz(String path){
        try{
            lector = new BufferedReader(new FileReader(path));
            while((linea = lector.readLine()) != null){
                partes = linea.split(",");
                matrizLineas.add(partes);
            }
            lector.close();
            linea=null;
            partes = null;
        } catch(Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private static void manejarLinea(String[] partes){
        TipoDocumentacion tipoDoc = TipoDocumentacion.valueOf(partes[0]);
        String documento = partes[1];
        String nombre = partes[2];
        String apellido = partes[3];
        String casilla = partes[4];
        String fecha = partes[5];
        String [] fechaPartes = fecha.split("/");
        int diaColaboracion = Integer.parseInt(fechaPartes[0]);
        int mesColaboracion = Integer.parseInt(fechaPartes[1]);
        int anioColaboracion = Integer.parseInt(fechaPartes[2]);
        LocalDate fechaColaboracion = LocalDate.of(anioColaboracion, mesColaboracion, diaColaboracion);
        String formaDeColaboracion = partes[6];
        int cantidad = Integer.parseInt(partes[7]);
        Colaborador colaborador = buscaColaborador(tipoDoc, documento, nombre, apellido);

        System.out.println("Colaborador encontrado: " + colaborador);

        if(colaborador == null){
            Mail mail = new Mail(casilla);
            if (!verificarCasilla(casilla)){
                throw new IllegalArgumentException("El mail no es valido");
            }
            List<MedioDeContacto> medioDeContactos = new ArrayList<>();
            medioDeContactos.add(mail);
            Documentacion documentacion = new Documentacion(tipoDoc, documento);
            Ubicacion ubicacion = new Ubicacion(null, null, null, null, null);
            PersonaFisica persona = new PersonaFisica(medioDeContactos, ubicacion, nombre, apellido, null, documentacion);
            colaborador = new Colaborador(persona);
        } else {
            Mail mail = colaborador.getPersona().getMediosDeContacto().stream()
                    .filter(medioDeContacto -> medioDeContacto instanceof Mail)
                    .map(medioDeContacto -> (Mail) medioDeContacto)
                    .findFirst()
                    .orElse(null);
            if(mail == null){
                mail = new Mail(casilla);
                colaborador.getPersona().getMediosDeContacto().add(mail);
            } else if (!mail.getCasilla().equals(casilla)){
                mail.setCasilla(casilla);
            }
        }

        Colaboracion colaboracion = null;
        switch(formaDeColaboracion){
            case "DINERO":
                Double cantidadDouble = (double) cantidad;
                colaboracion = new DonacionDeDinero(fechaColaboracion, cantidadDouble, Frecuencia.UNICO);
                colaboracion.setColaborador(colaborador);
                colaborador.getColaboracionesRealizadas().add(colaboracion);
                colaborador.getGestorDePuntaje().actualizar_puntaje(colaboracion);
                colaborador.setPuntos_acumulados(colaborador.getGestorDePuntaje().getPuntosTotales());
                break;
            case "DONACION_VIANDAS":
                for (int i = 0; i < cantidad; i++) {
                    colaboracion = new DonacionDeViandas(null, null);
                    colaboracion.setColaborador(colaborador);
                    colaborador.getColaboracionesRealizadas().add(colaboracion);
                    colaborador.getGestorDePuntaje().actualizar_puntaje(colaboracion);
                    colaborador.setPuntos_acumulados(colaborador.getGestorDePuntaje().getPuntosTotales());
                }
                break;
            case "REDISTRIBUCION_VIANDAS":
                colaboracion = new DistribucionDeViandas(null,null,cantidad,null,fechaColaboracion);
                colaboracion.setColaborador(colaborador);
                colaborador.getColaboracionesRealizadas().add(colaboracion);
                colaborador.getGestorDePuntaje().actualizar_puntaje(colaboracion);
                colaborador.setPuntos_acumulados(colaborador.getGestorDePuntaje().getPuntosTotales());
                break;
            case "ENTREGA_TARJETAS":
                for (int i = 0; i < cantidad; i++) {
                    colaboracion = new RegistroDePersonasVulnerables(null, colaborador, "");
                    colaboracion.setColaborador(colaborador);
                    colaborador.getColaboracionesRealizadas().add(colaboracion);
                    colaborador.getGestorDePuntaje().actualizar_puntaje(colaboracion);
                    colaborador.setPuntos_acumulados(colaborador.getGestorDePuntaje().getPuntosTotales());
                }
                break;
        }
    }

    public static void cargarColaboraciones(){
        for(String[] linea : matrizLineas){
            manejarLinea(linea);
        }
    }

    private static Colaborador buscaColaborador(TipoDocumentacion tipoDoc, String documento, String nombre, String apellido){
        for(Colaborador colaboradorLoop : RepositoryColaborador.getInstance().getColaboradoresDelSistema()){
            if (colaboradorLoop.persona instanceof PersonaJuridica){
                continue;
            }
            PersonaFisica personaLoop = (PersonaFisica) colaboradorLoop.persona;
            String nombreLoop = personaLoop.getNombre();
            String apellidoLoop = personaLoop.getApellido();
            TipoDocumentacion tipoDocLoop = personaLoop.getDocumento().getTipoDocumentacion();
            String documentoLoop = personaLoop.getDocumento().getNumero();
            if(tipoDocLoop == tipoDoc &&
                    documentoLoop.equals(documento) &&
                    nombreLoop.equals(nombre) &&
                    apellidoLoop.equals(apellido)){
                return colaboradorLoop;
            }
        }
        return null;
    }

    private static boolean verificarCasilla(String cadena){
        Matcher matcher = pattern.matcher(cadena);
        return matcher.matches();
    }
}