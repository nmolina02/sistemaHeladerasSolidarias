package validador.condicion;

public class Secuencia implements Condicion{

    public boolean verificar(String nombreUsuario, String contrasenia) throws Exception {
        // Verifica si hay secuencias de números consecutivos
        if (esSecuenciaNumerica(contrasenia)) {
            throw new Exception("La contraseña contiene una secuencia. Por favor, intente con una contraseña diferente.");
        }

        // Verifica si hay secuencias de letras consecutivas
        if (esSecuenciaAlfabetica(contrasenia)) {
            throw new Exception("La contraseña contiene una secuencia. Por favor, intente con una contraseña diferente.");
        }

        return true; // La contraseña no tiene secuencias simples
    }

    private boolean esSecuenciaNumerica(String contrasenia) {
        String secuencia = "0123456789";
        return secuencia.contains(contrasenia);
    }

    private boolean esSecuenciaAlfabetica(String contrasenia) {
        String secuencia = "abcdefghijklmnopqrstuvwxyz";
        return secuencia.contains(contrasenia.toLowerCase());
    }

}
