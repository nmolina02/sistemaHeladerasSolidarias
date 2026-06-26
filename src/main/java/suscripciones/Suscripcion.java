package suscripciones;

import Heladera.Heladera;
import Heladera.EstadoHeladera;
import localizacion.APIUbicacion.Punto;
import enviadoresNotificaciones.EnviadorDeMails;
import enviadoresNotificaciones.EnviadorTelegram;
import enviadoresNotificaciones.EnviadorWhatsapp;
import lombok.Getter;
import lombok.Setter;
import medioDeContacto.Mail;
import medioDeContacto.MedioDeContacto;
import medioDeContacto.TipoMedioContacto;
import medioDeContacto.Whatsapp;
import persistencia.ClaseCRUD;
import persona.roles.colaborador.Colaborador;
import persona.roles.colaborador.OpcionesSuscripciones.OpcionSuscripcion;
import repository.RepositoryHeladera;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "suscripcion")
@Getter
public class Suscripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "susc_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "rol_id")
    private Colaborador colaborador;

    @ManyToMany
    @JoinTable(
            name = "suscripcion_por_heladera",
            joinColumns = @JoinColumn(name = "susc_id"),
            inverseJoinColumns = @JoinColumn(name = "hela_id")
    )
    private Set<Heladera> heladeras = new HashSet<Heladera>();

    @Column(name = "susc_medios_de_contacto")
    @Setter String mediosDeContactoParaSuscripcion;

    @Column(name = "susc_opciones_suscripcion")
    @Setter String opcionesSuscripcion;

    @Transient
    private List<String> mensajes = new ArrayList<>();

    public Suscripcion(Colaborador colaborador, String mediosDeContactoParaSuscripcion, String opcionesSuscripcion) {
        this.colaborador = colaborador;
        this.mediosDeContactoParaSuscripcion = mediosDeContactoParaSuscripcion;
        this.opcionesSuscripcion = opcionesSuscripcion;
        GestorSuscripcionesHeladeras gestorSuscripcionesHeladeras = GestorSuscripcionesHeladeras.getInstance();
        gestorSuscripcionesHeladeras.getSuscripciones().add(this);
        this.buscar_heladeras_cercanas();
        ClaseCRUD.getInstance().add(this);
    }

    public Suscripcion() {}

    public void buscar_heladeras_cercanas(){
        RepositoryHeladera repositoryHeladera = RepositoryHeladera.getInstance();
        for (Heladera heladera : repositoryHeladera.getHeladerasDelSistema()) { // lo hago sobre todas las heladeras del sistema
            for (Punto punto : this.colaborador.getController().getZonasFrecuentes()) { // me fijo de que la heladera este en una zona frecuente
                if (heladera.getController().heladera_dentro_del_rango(punto))
                    this.heladeras.add(heladera);
            }
        }
    }

    public void armar_mensajes_de_suscripcion() {
        StringBuilder mensaje = new StringBuilder();
        for (OpcionSuscripcion opcion : this.colaborador.getGestorSuscripciones().getOpcionesSuscripcion()) {
            switch (opcion.getOpcion()) {
                case CANT_VIANDAS_DISP:
                    for (Heladera heladera : this.heladeras) {
                        if (opcion.getValor() == heladera.getGestorDeViandas().getViandas().size()) {
                            mensaje.append("La heladera ").append(heladera.getNombreHeladera()).append(" tiene ").append(heladera.getGestorDeViandas().getViandas().size()).append(" viandas disponibles\n");
                        }
                    }
                    mensaje.append("OP1");
                    break;
                case CANT_VIANDAS_PARA_LLENAR:
                    for (Heladera heladera : this.heladeras) {
                        if (opcion.getValor() == heladera.getGestorDeViandas().capacidad_disponible()) {
                            mensaje.append("La heladera ").append(heladera.getNombreHeladera()).append(" tiene ").append(heladera.getGestorDeViandas().capacidad_disponible()).append(" lugares disponibles para estar llena\n");
                        }
                    }
                    mensaje.append("OP2");
                    break;
                case DESPERFECTO_HELADERA:
                    for (Heladera heladera : this.heladeras) {
                        if (heladera.getEstadoHeladera().equals(EstadoHeladera.DE_BAJA)) {
                            mensaje.append("La heladera ").append(heladera.getNombreHeladera()).append(" tiene un desperfecto\n");
                        }
                    }
                    mensaje.append("OP3");
                    break;
            }
            if (mensaje.length() > 0) {
                mensajes.add(mensaje.toString().trim());
                mensaje.setLength(0);
            }
        }
    }

    public void notificar_suscripcion(){
        this.armar_mensajes_de_suscripcion();
        List<String> componentes = this.armar_cuerpo_notificacion();
        for (MedioDeContacto medio : colaborador.getGestorSuscripciones().getMediosDeContactoParaSuscripcion()){
            switch (medio.getTipoMedioContacto()){
                case MAIL:
                    System.out.println("Enviando Mail");
                    String remitente = "heladeras.solidarias@gmail.com";
                    Mail mailColaborador = (Mail) colaborador.persona.getMediosDeContacto().stream()
                            .filter(medioContacto -> medioContacto.getTipoMedioContacto() == TipoMedioContacto.MAIL)
                            .collect(Collectors.toList()).get(0);
                    String destinatario = mailColaborador.getCasilla();
                    EnviadorDeMails enviadorDeMails = EnviadorDeMails.getInstance();
                    enviadorDeMails.enviar_email(remitente, destinatario, componentes.get(0), componentes.get(1));
                    break;
                case TELEGRAM:
                    System.out.println("Enviando Telegram");
                    String texto = componentes.get(0) + ": " + componentes.get(1);
                    EnviadorTelegram enviadorTelegram = EnviadorTelegram.getInstance();
                    enviadorTelegram.enviar_telegram(texto);
                    break;
                case WHATSAPP:
                    System.out.println("Enviando Whatsapp");
                    String message = "*" + componentes.get(0) + ":* " + componentes.get(1);
                    Whatsapp whatsappColaborador = (Whatsapp) colaborador.persona.getMediosDeContacto().stream()
                            .filter(medioContacto -> medioContacto.getTipoMedioContacto() == TipoMedioContacto.WHATSAPP)
                            .collect(Collectors.toList()).get(0);
                    String numeroWpp = whatsappColaborador.getNumero();
                    EnviadorWhatsapp enviadorWhatsapp = EnviadorWhatsapp.getInstance();
                    enviadorWhatsapp.enviar_whatsapp(numeroWpp, message);
                    break;
            }
        }
        // luego de enviar la suscripcion vacio la lista de mensajes
        mensajes.clear();
    }

    private List<String> armar_cuerpo_notificacion() {
        List<String> componentes = new ArrayList<>();
        String body = "";
        String asunto = "[Suscripción] ";
        String opcionAsunto = null;

        for (int i = 0; i < mensajes.size(); i++) {
            if (!mensajes.get(i).isEmpty()){
                body = body + mensajes.get(i);
                opcionAsunto = body.substring(body.length() - 3);
                body = body.substring(0, body.length() - 3) + " - ";
            }

            if (!body.isEmpty()){
                asunto = asunto + this.obtenerElemento(opcionAsunto) + " - ";
            }
        }

        body = body.substring(0, body.length() - 3);
        asunto = asunto.substring(0, asunto.length() - 3);

        componentes.add(asunto);
        componentes.add(body);
        return componentes;
    }

    private String obtenerElemento(String opcion) {
        switch (opcion) {
            case "OP1":
                return "Cantidad de viandas disponibles";
            case "OP2":
                return "Cantidad de lugares disponibles";
            case "OP3":
                return "Desperfecto en heladera";
            default:
                return "";
        }
    }
}
