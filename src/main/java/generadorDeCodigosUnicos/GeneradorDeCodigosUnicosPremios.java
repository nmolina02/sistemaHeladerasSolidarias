package generadorDeCodigosUnicos;

import lombok.Getter;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GeneradorDeCodigosUnicosPremios {
    private static final String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private Set<String> numerosPremiosCreados = new HashSet<>();
    private static GeneradorDeCodigosUnicosPremios instancia = null;
    @Getter public String ultimoNumeroGenerado = null;

    private GeneradorDeCodigosUnicosPremios(){}

    public static GeneradorDeCodigosUnicosPremios getInstance() {
        if(instancia == null)
            instancia = new GeneradorDeCodigosUnicosPremios();
        return instancia;
    }

    public void crearNumeroPremio() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(11);
        String nuevoCodigo;
        while (true) {
            for (int i = 0; i < 11; i++) {
                int index = random.nextInt(caracteres.length());
                sb.append(caracteres.charAt(index));
            }
            nuevoCodigo = sb.toString();
            if (!numerosPremiosCreados.contains(nuevoCodigo)) {
                numerosPremiosCreados.add(nuevoCodigo);
                ultimoNumeroGenerado = nuevoCodigo;
                break;
            }
            sb.setLength(0);
        }
    }
}
