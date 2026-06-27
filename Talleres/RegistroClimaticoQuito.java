import java.net.URL;
import java.net.HttpURLConnection;
import java.io.*;
import org.json.*;
import java.time.LocalDateTime;

public class RegistroClimaticoQuito{

    //API KEY generado de OpenWeatherMap
    private static final String API_KEY = "f0a67d31e3a1532c0e0404b8b5b2525f";
    //Latitud y Longitud de Quito
    private static final double LATITUD = -0.2299;
    private static final double LONGITUD = -78.5249;

    //Nombre del archivo en donde se escribiran los resultados
    private static final String ARCHIVO = "clima-quito-hoy.csv";

    public static void main(String[] args) {
	try{ //Controlar errores y que el programa no quede sin finalizar
	    String url = construirURL(); //Se construye el URL Del API
	    String respuesta = consultarAPI(url); //Se consulta el API y se obtiene la respuesta JSON Como texto
	    JSONObject datos = convertirJSON(respuesta); //Se convierte de String-JSON a objeto JSON
	    guardarCSV(datos); //Se guardan los datos en clima-quito-hoy.csv

            System.out.println("Datos guardados correctamente.");

        }catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static String construirURL(){
        // se arma la URL con latitud, longitud de Quito, unidades(C°) y API key
        String url =
                "https://api.openweathermap.org/data/2.5/weather"
                + "?lat=" + LATITUD
                + "&lon=" + LONGITUD
                + "&units=metric"
                + "&lang=es"
                + "&appid=" + API_KEY;

        // se retorna la URL completa
        return url;
    }

    public static String consultarAPI(String urlString) throws Exception {

        URL url = new URL(urlString);// crear objeto URL apartir del string URL
        HttpURLConnection con = (HttpURLConnection) url.openConnection(); // abrir conexión HTTP
        con.setRequestMethod("GET");// definir método GET
	
        // Se lee respuesta del servidor
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

        StringBuilder respuesta = new StringBuilder();// almacenar respuesta completa
        String linea; // variable para lectura línea por línea
        // leer todo el contenido de la respuesta
        while ((linea = reader.readLine()) != null) {
            respuesta.append(linea);
        }
        
        reader.close();// cerrar lector
        return respuesta.toString();// devolver JSON como texto
    }

    public static JSONObject convertirJSON(String respuesta) {
        // convertir string JSON a objeto JSON para los datos al .csv
        JSONObject obj = new JSONObject(respuesta);
        // retornar objeto JSON
        return obj;
    }

    public static void guardarCSV(JSONObject obj) throws Exception {

        // verifica si el archivo ya existe cuando se coloque el encabezado
        File file = new File(ARCHIVO);
        FileWriter csvWriter = new FileWriter(ARCHIVO, true); //se abre el archivo en modo append (no borra datos anteriores)

        // si el archivo está vacío/nuevo,se escribe el encabezado
        if (file.length() == 0) {
            csvWriter.append("fecha,ciudad,temperatura,humedad,presion,descripcion\n");
        }

	String fechaHora = LocalDateTime.now().toString(); //Fecha de guardado
        //SE EXTRAE DATOS DEL JSON:
        String ciudad = obj.getString("name"); //el nombre de la ciudad
        JSONObject main = obj.getJSONObject("main"); // el conjunto main del JSON de lo retornado a la llamada API (dado que contiene temp, feels_like, temp_min, etc..)
        double temp = main.getDouble("temp"); // temperatura del conjunto main
        int humedad = main.getInt("humidity");// humedad del conjunto main
        int presion = main.getInt("pressure");// presión del conjunto main
        JSONArray weather = obj.getJSONArray("weather");//el arreglo weather del JSON de lo retornado a la llamada API
        String descripcion = weather.getJSONObject(0).getString("description");//descripción del clima (guardado en el primer valor, por eso el 0)
	
        // ESCRIBIR EN CSV usando coma como separador
        csvWriter.append(fechaHora + "," +
                         ciudad + "," +
                         temp + "," +
                         humedad + "," +
                         presion + "," +
                         descripcion + "\n");
        csvWriter.close();//se cierra el archivo
    }
}