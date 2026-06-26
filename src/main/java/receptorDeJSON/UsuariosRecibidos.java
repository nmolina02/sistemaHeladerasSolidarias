package receptorDeJSON;

import localizacion.APIUbicacion.APIUbicacion;
import localizacion.Ciudad;
import localizacion.Pais;
import localizacion.Ubicacion;
import lombok.Getter;
import medioDeContacto.*;
import repository.RepositoryCiudad;
import repository.RepositoryPais;
import repository.RepositoryUbicacion;
import validador.verificador.Verificador;

import java.util.Arrays;
import java.util.List;

@Getter
public class UsuariosRecibidos {
    private String address;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String whatsapp;
    private String telegram;

    public UsuariosRecibidos(String address, String username, String password, String email, String phone, String whatsapp, String telegram) {
        this.address = address;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.whatsapp = whatsapp;
        this.telegram = telegram;
    }

    public UsuariosRecibidos() {}

    public void convertirMediosContactoAObjetos(List<MedioDeContacto> mediosDeContacto) throws Exception {
        if (this.validarContrasenia()) {
            if (whatsapp.equals("true")) {
                Whatsapp whatsapp = new Whatsapp(phone);
                mediosDeContacto.add(whatsapp);
            }
            if (telegram.equals("true")) {
                Telegram telegram = new Telegram(phone);
                mediosDeContacto.add(telegram);
            }
            if (!phone.equals("0")) {
                Telefono telefono = new Telefono(phone);
                mediosDeContacto.add(telefono);
            }
            if (!email.isEmpty()) {
                Mail mail = new Mail(email);
                mediosDeContacto.add(mail);
            }
        }
    }

    public Ubicacion convertirUbicacionAObjeto(String address) throws Exception {
        if (this.validarContrasenia()) {
            return this.solicitarUbicacion(address);
        }
        return null;
    }

    // si no existe, la crea y actualiza
    public Ubicacion solicitarUbicacion(String address) {
        String calleAltura = address.split(", ")[0];
        List<String> calleAlturaSeparada = Arrays.asList(calleAltura.split(" "));
        String calle = String.join(" ", calleAlturaSeparada.subList(0, calleAlturaSeparada.size() - 1));
        String altura = calleAlturaSeparada.get(calleAlturaSeparada.size() - 1);
        String ciudad = address.split(", ")[1];
        String pais = address.split(", ")[2];

        Pais paisObj = RepositoryPais.getInstance().getPaises().stream()
                .filter(p -> p.getNombre().equals(pais))
                .findFirst()
                .orElse(null);
        if (paisObj == null) {
            paisObj = new Pais(pais);
        }
        Ciudad ciudadObj = RepositoryCiudad.getInstance().getCiudades().stream()
                .filter(c -> c.getNombre().equals(ciudad))
                .findFirst()
                .orElse(null);
        if (ciudadObj == null) {
            ciudadObj = new Ciudad(ciudad, paisObj);
        }
        Ubicacion ubicacion = RepositoryUbicacion.getInstance().getUbicaciones().stream()
                .filter(u -> u.getCalle().equals(calle) && u.getAltura().equals(altura) && u.getCiudad().getNombre().equals(ciudad))
                .findFirst()
                .orElse(null);
        if (ubicacion == null) {
            APIUbicacion apiUbicacion = APIUbicacion.getInstance();
            return apiUbicacion.buscar_latitud_longitud(address, ciudadObj);
        }
        return ubicacion;
    }

    private boolean validarContrasenia() throws Exception {
        Verificador verificadorContrasenias = Verificador.getInstance();
        try {
            return verificadorContrasenias.validarContrasenia(username, password);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
