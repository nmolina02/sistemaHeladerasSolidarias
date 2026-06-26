package receptorDeJSON.Receptores;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.UploadedFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RecepcionDeArchivos {
    private static boolean exito = false;
    public static void ejecutarRecepcionDeArchivos(Javalin app) {
        app.post("/receptorDeArchivos", ctx -> {
            UploadedFile file = ctx.uploadedFile("file");
            String motivo = ctx.formParam("motivo");

            if (file != null && motivo != null) {
                try {
                    Path path = armarPathArchivo(file, motivo);
                    Files.createDirectories(path.getParent());
                    Files.copy(file.getContent(), path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    ctx.contentType("application/json");
                    ctx.result(new Gson().toJson("Archivo subido exitosamente"));
                    exito = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    ctx.contentType("application/json");
                    ctx.result(new Gson().toJson("Error al subir el archivo"));
                }
            } else {
                ctx.contentType("application/json");
                ctx.result(new Gson().toJson("No se ha subido ningún archivo"));
            }
        });

        app.get("/receptorDeArchivos/:motivo/:imagen", ctx -> {
            String motivo = ctx.pathParam("motivo");
            String imagen = ctx.pathParam("imagen");
            Path path = seleccionarPath(motivo, imagen);
            try {
                if (Files.exists(path)) {
                    ctx.result(Files.newInputStream(path));
                } else {
                    if (motivo.equals("imagenUsuario")){
                        path = seleccionarPath(motivo, "imagen_registro.png");
                    } else {
                        path = seleccionarPath(motivo, "heladeraSinImagen.jpg");
                    }
                        ctx.result(Files.newInputStream(path));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static Path seleccionarPath(String motivo, String imagen) {
        if (motivo.equals("creacionHeladera")) {
            return Paths.get("src/main/resources/mapaInteractivoHeladeras/sistema/images/heladeras/" + imagen);
        } else if (motivo.equals("reportarFallaHeladera")) {
            return Paths.get("src/main/resources/mapaInteractivoHeladeras/sistema/images/fallasReportadas/" + imagen);
        } else if (motivo.equals("premioCargado")) {
            return Paths.get("src/main/resources/mapaInteractivoHeladeras/sistema/images/premios/" + imagen);
        } else if (motivo.equals("arregloHeladera")) {
            return Paths.get("src/main/resources/mapaInteractivoHeladeras/sistema/images/imagenesVisitasHeladeras/" + imagen);
        } else if (motivo.equals("imagenUsuario")) {
            return Paths.get("src/main/resources/mapaInteractivoHeladeras/sistema/images/users_images/" + imagen);
        }
        return null;
    }

    public static boolean imagenDescargadaConExito() {
        return exito;
    }

    private static Path armarPathArchivo(UploadedFile file, String motivo) {
        String UPLOAD_DIR = "src/main/resources/mapaInteractivoHeladeras/sistema/images/heladeras/";
        String UPLOAD_DIR_FALLA = "src/main/resources/mapaInteractivoHeladeras/sistema/images/fallasReportadas/";
        String UPLOAD_DIR_PREMIO = "src/main/resources/mapaInteractivoHeladeras/sistema/images/premios/";
        String UPLOAD_DIR_VISITAS = "src/main/resources/mapaInteractivoHeladeras/sistema/images/imagenesVisitasHeladeras/";
        String UPLOAD_DIR_USUARIOS = "src/main/resources/mapaInteractivoHeladeras/sistema/images/users_images/";
        String fileName = file.getFilename();
        Path path = null;
        if (motivo.equals("creacionHeladera")) {
            path = Paths.get(UPLOAD_DIR + fileName);
        } else if (motivo.equals("reportarFallaHeladera")) {
            path = Paths.get(UPLOAD_DIR_FALLA + fileName);
        } else if (motivo.equals("premioCargado")) {
            path = Paths.get(UPLOAD_DIR_PREMIO + fileName);
        } else if (motivo.equals("arregloHeladera")) {
            path = Paths.get(UPLOAD_DIR_VISITAS + fileName);
        } else if (motivo.equals("imagenUsuario")) {
            path = Paths.get(UPLOAD_DIR_USUARIOS + fileName);
        }
        return path;
    }
}