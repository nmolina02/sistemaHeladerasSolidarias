package localizacion.APIUbicacion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import localizacion.Ciudad;
import localizacion.Ubicacion;
import org.json.JSONArray;
import org.json.JSONObject;

public class APIUbicacion {
    private static final String[] tiposDeLugares = {"hospital", "school", "university", "restaurant", "cafe"};
    private static APIUbicacion instancia = null;
    public List<String[]> puntosRecomendados = new ArrayList<>();
    private String[] recomendacion = null;
    private static final double radioDeLaTierra = 6371.0; // medido en kms

    private APIUbicacion() {}

    public static APIUbicacion getInstance() {
        if (instancia == null) {
            instancia = new APIUbicacion();
        }
        return instancia;
    }

    public void buscar_lugares_cercanos(Punto punto, String tipo) {
        try {
            String consulta = String.format(
                    "[out:json];node[amenity=%s](around:%s,%s,%s);out;",
                    tipo, punto.getRadio(), punto.getLatitud(), punto.getLongitud()
            );

            String consultaCodificada = URLEncoder.encode(consulta, "UTF-8");
            String overpassUrl = "https://overpass-api.de/api/interpreter?data=" + consultaCodificada;

            URL url = new URL(overpassUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());

                JSONArray elements = jsonResponse.getJSONArray("elements");

                for (int i = 0; i < elements.length(); i++) {
                    try {
                        JSONObject lugar = elements.getJSONObject(i);
                        Double latitudObtenida = lugar.getDouble("lat");
                        Double longitudObtenida = lugar.getDouble("lon");
                        String latitud = String.valueOf(latitudObtenida);
                        String longitud = String.valueOf(longitudObtenida);
                        if (lugar.has("tags") && lugar.getJSONObject("tags").has("name")) {
                            String nombre = lugar.getJSONObject("tags").getString("name");
                            String pais = lugar.getJSONObject("tags").getString("addr:country");
                            String ciudad = lugar.getJSONObject("tags").getString("addr:city");
                            String calle = lugar.getJSONObject("tags").getString("addr:street");
                            String altura = lugar.getJSONObject("tags").getString("addr:housenumber");
                            recomendacion = new String[]{nombre, tipo, ciudad, pais, latitud, longitud, calle, altura};
                            puntosRecomendados.add(recomendacion);
                            // TODO: BORRAR LOS PRINTS??
                            System.out.println("Lugar recomendado: " + recomendacion[0] + " (" + recomendacion[1] + ") - Ciudad: " + recomendacion[2] + ", País: " + recomendacion[3] + " - Latitud: " + lugar.getDouble("lat") + ", Longitud: " + lugar.getDouble("lon"));
                        }
                    } catch (Exception e) {
                        // Ignoramos estos errores porque todos los elementos tienen estos campos, pero de todas formas siempre tira el error
                        if (!e.getMessage().contains("JSONObject[\"addr:city\"] not found")
                                && !e.getMessage().contains("JSONObject[\"addr:country\"] not found")
                                && !e.getMessage().contains("JSONObject[\"addr:street\"] not found")
                                && !e.getMessage().contains("JSONObject[\"addr:housenumber\"] not found")) {
                            System.err.println("Error al procesar el elemento " + i + ": " + e.getMessage());
                        }
                    }
                }
            } else {
                System.out.println("Error en la conexión: " + responseCode);
            }

        } catch (Exception e) {
            System.out.println("Error durante la solicitud a Overpass API:");
            e.printStackTrace();
        }
    }

    public String obtener_ciudad(String[] opcionElegida) {
        return opcionElegida[2];
    }

    public String obtener_pais(String[] opcionElegida) {
        return opcionElegida[3];
    }

    public String obtener_calle(String[] opcionElegida) {
        return opcionElegida[6];
    }

    public String obtener_altura(String[] opcionElegida) {
        return opcionElegida[7];
    }

    public Ubicacion elegir_opcion(String[] opcionElegida, Ciudad ciudad, String calle, String altura) {
        return new Ubicacion(opcionElegida[4], opcionElegida[5], ciudad, calle, altura);
    }

    public List<String[]> sugerir_puntos(Punto punto) {
        this.puntosRecomendados.clear(); // para limpiar la lista de recomendaciones en cada busqueda
        for (String tipo : tiposDeLugares) {
            this.buscar_lugares_cercanos(punto, tipo);
        }
        return this.puntosRecomendados;
    }

    // los dos siguientes metodos los usamos para ver si una ubicacion esta dentro de un rango pasado por parametro
    public Boolean esta_dentro_del_rango(Punto punto, Ubicacion ubicacion) {
        double distancia = distancia_entre_dos_puntos(punto, ubicacion) * 1000; // lo paso a metros
        double radio = Double.parseDouble(punto.getRadio());
        return distancia <= radio;
    }

    // este metodo me permite calcular la distancia entre dos puntos
    // lo devuelve en kms
    public double distancia_entre_dos_puntos(Punto punto, Ubicacion ubicacion) {
        double latitudPunto = Double.parseDouble(punto.getLatitud());
        double longitudPunto = Double.parseDouble(punto.getLongitud());
        double latitudUbicacion = Double.parseDouble(ubicacion.getLatitud());
        double longitudUbicacion = Double.parseDouble(ubicacion.getLongitud());
        double dLat = Math.toRadians(latitudUbicacion - latitudPunto);
        double dLon = Math.toRadians(longitudUbicacion - longitudPunto);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(latitudPunto)) * Math.cos(Math.toRadians(latitudUbicacion)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return radioDeLaTierra * c;
    }

    private JSONObject realizar_consulta_lugar(String direccion) {
        try {
            String consulta = String.format(
                    "https://nominatim.openstreetmap.org/search?q=%s&format=json&limit=1",
                    URLEncoder.encode(direccion, "UTF-8")
            );

            URL url = new URL(consulta);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONArray jsonResponse = new JSONArray(response.toString());
                return jsonResponse.getJSONObject(0);
            } else {
                System.out.println("Error en la conexión: " + responseCode);
            }
        } catch (Exception e) {
            System.out.println("Error durante la solicitud a Nominatim API:");
            e.printStackTrace();
        }
        return null;
    }

    public Ubicacion buscar_latitud_longitud(String direccion, Ciudad ciudad) {
        Ubicacion ubicacion = null;
        try {
            JSONObject lugar = this.realizar_consulta_lugar(direccion);
            Double latitudObtenida = lugar.getDouble("lat");
            Double longitudObtenida = lugar.getDouble("lon");
            String latitud = String.valueOf(latitudObtenida);
            String longitud = String.valueOf(longitudObtenida);
            String nombreCalle = this.removerAltura(direccion.split(",")[0]);
            List<String> direccionSpliteada = Arrays.asList(direccion.split(",")[0].split(" "));
            String alturaCalle = direccionSpliteada.get(direccionSpliteada.size() - 1);
            ubicacion = new Ubicacion(latitud, longitud, ciudad, nombreCalle, alturaCalle);
        } catch (Exception e) {
            System.out.println("Error al obtener el lugar:");
            e.printStackTrace();
        }
        return ubicacion;
    }

    public String removerAltura(String direccion) {
        // Buscar la última aparición de un número en la dirección
        String[] palabras = direccion.split(" ");
        StringBuilder nombreCalle = new StringBuilder();

        for (String palabra : palabras) {
            // Verifica si la palabra no es un número (es decir, la altura)
            if (!palabra.matches("\\d+")) {
                nombreCalle.append(palabra).append(" ");
            } else {
                break; // Rompe el ciclo cuando encuentra el primer número (la altura)
            }
        }

        // Eliminar el último espacio en blanco agregado al final
        return nombreCalle.toString().trim();
    }

    public String buscar_latitud_lugar(String direccion) {
        try {
            JSONObject lugar = this.realizar_consulta_lugar(direccion);
            Double latitudObtenida = lugar.getDouble("lat");
            return String.valueOf(latitudObtenida);
        } catch (Exception e) {
            System.out.println("Error al obtener el lugar:");
            e.printStackTrace();
        }
        return null;
    }

    public String buscar_longitud_lugar(String direccion) {
        try {
            JSONObject lugar = this.realizar_consulta_lugar(direccion);
            Double longitudObtenida = lugar.getDouble("lon");
            return String.valueOf(longitudObtenida);
        } catch (Exception e) {
            System.out.println("Error al obtener el lugar:");
            e.printStackTrace();
        }
        return null;
    }
}
