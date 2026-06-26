package validador.condicion;

public class Credencial implements Condicion{

    public boolean verificar(String nombreUsuario, String contrasenia) throws Exception {
        boolean esValida = !(contrasenia.contains(nombreUsuario));
        if(!esValida){
            throw new Exception("La contraseña es similar a su nombre de usuario. Por favor, intente con otra contraseña");
        } else return esValida;
    }
}
