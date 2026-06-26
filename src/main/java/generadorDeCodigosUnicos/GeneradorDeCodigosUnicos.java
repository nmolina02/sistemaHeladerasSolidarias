package generadorDeCodigosUnicos;

import lombok.Getter;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GeneradorDeCodigosUnicos {
    private static final String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private Set<String> numerosTarjetasCreadas = new HashSet<>();
    private static GeneradorDeCodigosUnicos instancia = null;
    @Getter public String ultimoNumeroGenerado = null;

    private GeneradorDeCodigosUnicos(){}

    public static GeneradorDeCodigosUnicos getInstance() {
        if(instancia == null)
            instancia = new GeneradorDeCodigosUnicos();
        return instancia;
    }

    public void crearNumeroTarjeta() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(11);
        String nuevoCodigo;
        while (true) {
            for (int i = 0; i < 11; i++) {
                int index = random.nextInt(caracteres.length());
                sb.append(caracteres.charAt(index));
            }
            nuevoCodigo = sb.toString();
            if (!numerosTarjetasCreadas.contains(nuevoCodigo)) {
                numerosTarjetasCreadas.add(nuevoCodigo);
                ultimoNumeroGenerado = nuevoCodigo;
                break;
            }
            sb.setLength(0);
        }
    }
}
