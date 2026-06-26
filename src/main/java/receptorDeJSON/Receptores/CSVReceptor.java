package receptorDeJSON.Receptores;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.UploadedFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static colaboraciones.CargaMasivaDeColaboraciones.cargarColaboraciones;
import static colaboraciones.CargaMasivaDeColaboraciones.cargarColaboracionesMatriz;

public class CSVReceptor {
    public static void ejecutarCSVReceptor(Javalin app) {
        app.post("/cargarCSV", ctx -> {
            String UPLOAD_DIR = "src/main/resources/csvs/";
            UploadedFile uploadedFile = ctx.uploadedFile("file");

            if (uploadedFile != null) {
                Path pathCSV = Paths.get(UPLOAD_DIR + uploadedFile.getFilename());
                try {
                    Files.createDirectories(pathCSV.getParent());
                    Files.copy(uploadedFile.getContent(), pathCSV, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Path del archivo CSV: " + pathCSV);
                    cargarColaboracionesMatriz(pathCSV.toString());
                    cargarColaboraciones();
                    ctx.contentType("application/json");
                    ctx.result(new Gson().toJson("Archivo CSV procesado con exito"));
                } catch (IOException e) {
                    e.printStackTrace();
                    ctx.contentType("application/json");
                    ctx.result(new Gson().toJson("Error al procesar el archivo CSV"));
                }
            } else {
                ctx.contentType("application/json");
                ctx.result(new Gson().toJson("No se ha subido ningún archivo"));
            }
        });
    }
}
