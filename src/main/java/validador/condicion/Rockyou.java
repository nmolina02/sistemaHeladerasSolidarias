package validador.condicion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import config.Config;

public class Rockyou implements Condicion{
    private File archivoRockyou_p1 = new File(Config.PATH_ARCHIVO_ROCKYOU_P1);
    private File archivoRockyou_p2 = new File(Config.PATH_ARCHIVO_ROCKYOU_P2);
    private File archivoRockyou_p3 = new File(Config.PATH_ARCHIVO_ROCKYOU_P3);
    private File archivoRockyou_p4 = new File(Config.PATH_ARCHIVO_ROCKYOU_P4);

    public boolean buscarPalabra(String contrasenia, File archivo) throws Exception {
        boolean estaEnElArchivo = false;
        try{
            if(archivo.exists()){
                BufferedReader leerArchivo = new BufferedReader(new FileReader(archivo));
                String palabraLeida;
                while((palabraLeida = leerArchivo.readLine()) != null){
                    if(palabraLeida.equals(contrasenia)){
                        estaEnElArchivo = true;
                        break;
                    }
                }
                leerArchivo.close();
            }
        }catch (Exception e){
            throw new Exception("Error al buscar el archivo.", e);
        }
        return estaEnElArchivo;
    }

    public boolean verificar(String nombreUsuario, String contrasenia) throws Exception {
        boolean esValida = !this.buscarPalabra(contrasenia, archivoRockyou_p1)
                && !this.buscarPalabra(contrasenia, archivoRockyou_p2)
                && !this.buscarPalabra(contrasenia, archivoRockyou_p3)
                && !this.buscarPalabra(contrasenia, archivoRockyou_p4);

        if(!esValida){
            throw new Exception("Su contraseña no es segura. Por favor, intente con otra contraseña");
        }else return esValida;
    }
}