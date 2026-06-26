package validador.verificador;

import validador.condicion.*;
import validador.condicion.Condicion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Verificador {
    private static Verificador instancia = null;
    private List<Condicion> condicionesContrasenia ;

    public static Verificador getInstance(){
        if (instancia == null){
            instancia = new Verificador();
        }
        return instancia;
    }

    private Verificador(){
        this.condicionesContrasenia = new ArrayList<>();
    }
    public boolean validarContrasenia(String nombreUsuario, String contrasenia){
        String contraseniaCompacta = borrarEspacios(contrasenia);
        boolean esValida = this.condicionesContrasenia.stream()
                .allMatch(condicion -> {
                    try {
                        return condicion.verificar(nombreUsuario, contraseniaCompacta);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        return esValida;
    }

    public String borrarEspacios(String contrasenia){
        while (contrasenia.contains(" ")){
            contrasenia = contrasenia.trim();
            //contrasenia = contrasenia.replaceAll(" ", "");//2 espacios por 1
            contrasenia = contrasenia.replaceAll("\\s+", " ");

        }
        return contrasenia;
    }

    public void agregarCondiciones(Condicion... condiciones){
        this.condicionesContrasenia.addAll(Arrays.asList(condiciones));
    }
}
