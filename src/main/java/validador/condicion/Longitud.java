package validador.condicion;

public class Longitud implements Condicion{
    private int minLongitud = 8;
    private int maxLongitud = 64;


    public boolean verificar(String nombreUsuario, String contrasenia) throws Exception {
        int longitud = contrasenia.length();
        boolean esValida = this.between(longitud, minLongitud, maxLongitud);
        if(!esValida){
            throw new Exception("La contraseña debe tener entre 8 y 64 caracteres.Por favor, intente con otra contraseña");
        } else return esValida;
    }

    public boolean between(int value, int min, int max){
        return value >= min && value <= max;
    }
}
