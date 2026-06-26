package validador.condicion;

public class Repeticiones implements Condicion{
    private  int maxRepeticiones = 3;
    public boolean verificar(String nombreUsuario, String contrasenia) throws Exception {
        // Verifica caracteres repetidos
        for (int i = 0; i < contrasenia.length() - maxRepeticiones + 1; i++) {
            boolean repetido = true;
            for (int j = 1; j < maxRepeticiones; j++) {
                if (contrasenia.charAt(i) != contrasenia.charAt(i + j)) {
                    repetido = false;
                    break;
                }
            }
            if (repetido) {
                throw new Exception("La contraseña contiene caracteres repetidos. Por favor, intente con una contraseña diferente.");
            }
        }

        return true; // La contraseña no contiene repeticiones
    }
}
