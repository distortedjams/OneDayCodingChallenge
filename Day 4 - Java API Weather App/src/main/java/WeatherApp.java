import java.io.IOException;
import java.util.Scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.SimpleTimeZone;

public class WeatherApp {

    private static final String API_KEY = "cb10f707e277a7e0e065e6f5d6aece89";
    private static final String API_ENDPOINT = "https://api.openweathermap.org/data/2.5/weather";

    public static void main(String[] args) {
        while (true) {
            try {
            	Scanner scanner = new Scanner(System.in);
                System.out.print("Enter city name: ");
                String city = scanner.nextLine();
                displayWeather(city);
            } catch (IOException e) {
                System.err.println("Error fetching weather data: " + e.getMessage());
            } catch (InterruptedException e) {
                System.err.println("Sleep interrupted: " + e.getMessage());
            }
        }
    }

    private static void displayWeather(String city) throws IOException, InterruptedException {
        String response = getWeatherData(city);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);
        String description = root.path("weather").get(0).path("description").asText();
        double temp = root.path("main").path("temp").asDouble() * 9/5 - 459.67;
        double tempMin = root.path("main").path("temp_min").asDouble() * 9/5 - 459.67;
        double tempMax = root.path("main").path("temp_max").asDouble() * 9/5 - 459.67;
        double windSpeed = root.path("wind").path("speed").asDouble();
        double windDirection = root.path("wind").path("deg").asDouble();

        // Get the time zone for the location
        JsonNode timezoneNode = root.path("timezone");
        if (timezoneNode.isMissingNode()) {
            System.out.printf("Could not determine timezone for %s\n", city);
            return;
        }
        int offset = timezoneNode.asInt();
        String[] timezoneIds = TimeZone.getAvailableIDs(offset * 1000);
        if (timezoneIds.length == 0) {
            System.out.printf("Could not determine timezone for %s\n", city);
            return;
        }
        TimeZone timezone = new SimpleTimeZone(offset * 1000, timezoneIds[0]);

        // Get the current local time
        Calendar calendar = Calendar.getInstance(timezone);
        Date now = calendar.getTime();
        DateFormat timeFormat = new SimpleDateFormat("h:mm a");
        timeFormat.setTimeZone(timezone);
        String currentTime = timeFormat.format(now);
        
        String weatherEmoji = getWeatherEmoji(description, temp, tempMin, tempMax);
        // Display the weather data and local time
        System.out.printf("Current Data for %s:\n", city);
        System.out.printf("Current Time in %s: %s\n", city, currentTime);
        System.out.printf("Current weather in %s: %s %s, %.1f °F\n", city, description, weatherEmoji, temp);
        System.out.printf("Today's min/max temperatures in %s: %.1f/%.1f °F\n", city, tempMin, tempMax);
        System.out.printf("Current wind in %s: %.1f m/s at %.1f degrees\n", city, windSpeed, windDirection);
    }

    private static String getWeatherData(String city) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String url = String.format("%s?q=%s&APPID=%s", API_ENDPOINT, city, API_KEY);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected response code: " + response);
        }
        return response.body().string();
    }
    
    private static String getWeatherEmoji(String description, double temp, double tempMin, double tempMax) {
        String weatherEmoji = "";

        // Add emoji based on weather conditions
        if (description.contains("clear")) {
            weatherEmoji += "\u2600"; // sun
        } else if (description.contains("cloud")) {
            weatherEmoji += "\u2601"; // cloud
        } else if (description.contains("rain")) {
            weatherEmoji += "\u2614"; // umbrella with rain drops
        } else if (description.contains("snow")) {
            weatherEmoji += "\u2744"; // snowflake
        } else if (description.contains("thunderstorm")) {
            weatherEmoji += "\u26C8"; // cloud with lightning and rain
        } else if (description.contains("mist") || description.contains("fog")) {
            weatherEmoji += "\ud83c\udf2b"; // fog
        }
        
        // Add emoji based on temperature range
        if (temp < 32.0) {
            weatherEmoji += "❄️"; // snowflake
        } else if (tempMax < 50.0) {
            weatherEmoji += "\u2601"; // cloud
        }

        return weatherEmoji;
    }
}