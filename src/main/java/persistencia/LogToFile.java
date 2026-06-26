package persistencia;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LogToFile {
    private static final String LOG_DIRECTORY = "resources/logs/";
    private static final String LOG_FILE = "resources/logs/ram_data_log.txt";
    private static final List<String> ramLog = new ArrayList<>();

    // Método para agregar datos al log en memoria y en disco
    public static synchronized void logData(Object object) {
        File logDir = new File(LOG_DIRECTORY);
        if (!logDir.exists()) {
            if (logDir.mkdirs()) {
                System.out.println("Directorio de logs creado: " + LOG_DIRECTORY);
            } else {
                System.err.println("No se pudo crear el directorio de logs: " + LOG_DIRECTORY);
            }
        }
        ramLog.add(object.toString());
        appendToFile(object);
    }

    // Escribe en un archivo inmediatamente
    private static void appendToFile(Object object) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write("Object of type: " + object.getClass().getName());
            writer.newLine();

            // Obtener todos los campos del objeto (incluidos los privados)
            Field[] fields = object.getClass().getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true); // Permite acceder a campos privados
                writer.write(field.getName() + ": " + field.get(object));
                writer.newLine();
            }

            writer.newLine();
            writer.newLine();
            System.out.println("Data logged: " + object.getClass().getName());
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    // Obtener los datos del log (puede ser útil para procesar al reiniciar)
    public static synchronized List<String> getRamLog() {
        return new ArrayList<>(ramLog);
    }

    // Método para limpiar el log (después de persistir en disco, por ejemplo)
    public static synchronized void clearLog() {
        ramLog.clear();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE))) {
            writer.write(""); // Limpia el contenido del archivo
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Gson createGson() {
        return new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .create();
    }
}
