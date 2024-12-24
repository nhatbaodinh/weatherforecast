package dinhnhatbao.weatherforecast;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class WFController {
  @FXML
  private TextField cityTextField;
  @FXML
  private Button searchButton;
  @FXML
  private ImageView weatherIcon;
  @FXML
  private Label cityNameLabel;
  @FXML
  private Label temperatureLabel;
  @FXML
  private Label humidityLabel;
  @FXML
  private Label pressureLabel;
  @FXML
  private Label windSpeedLabel;
  @FXML
  private Label descriptionLabel;
  @FXML
  private Button historyButton;
  @FXML
  private Button formatButton;
  @FXML
  private Label accessDateTimeLabel;
  @FXML
  private Button themeButton;
  @FXML
  private Button detailButton;

  private static final String API_KEY = "7c1388f022de59cbcc9be442d5fcc866";
  private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=%s";
  private static final String ICON_URL = "https://openweathermap.org/img/wn/%s@2x.png";

  private boolean isMetric = true;
  private List<String> searchHistory = new ArrayList<>(); // Store search history

  @FXML
  public void initialize() {
    Scene scene = themeButton.getScene();
    if (scene != null) {
      scene.getStylesheets().add(getClass().getResource("light-theme.css").toExternalForm());
    }
    themeButton.setText("Light");

    formatButton.setText("Metric");
    searchButton.setOnAction(event -> searchWeather());
    historyButton.setOnAction(event -> onHistoryButtonClick());
  }

  private void searchWeather() {
    String city = cityTextField.getText().trim();
    if (!city.isEmpty()) {
      fetchWeatherData(city);
      searchHistory.add(city);
    } else {
      cityNameLabel.setText("Please enter a city!");
    }
  }

  private void fetchWeatherData(String city) {
    new Thread(() -> {
      try {
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8.toString());
        String units = isMetric ? "metric" : "imperial";
        String urlString = String.format(API_URL, encodedCity, API_KEY, units);
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }
        in.close();

        JSONObject jsonResponse = new JSONObject(response.toString());

        // Extract data
        String cityName = jsonResponse.getString("name");
        String countryName = jsonResponse.getJSONObject("sys").getString("country");
        double temperature = jsonResponse.getJSONObject("main").getDouble("temp");
        int humidity = jsonResponse.getJSONObject("main").getInt("humidity");
        double windSpeed = jsonResponse.getJSONObject("wind").getDouble("speed");
        int pressure = jsonResponse.getJSONObject("main").getInt("pressure");
        String description = jsonResponse.getJSONArray("weather").getJSONObject(0).getString("description");
        String iconCode = jsonResponse.getJSONArray("weather").getJSONObject(0).getString("icon");
        int timezoneOffset = jsonResponse.getInt("timezone");

        // Convert pressure based on units
        double displayPressure = pressure;
        if (!isMetric) {
          displayPressure = pressure * 0.02953; // Convert hPa to inHg
        }

        // Calculate local time
        LocalDateTime localDateTime = LocalDateTime.ofInstant(
            Instant.now().plusSeconds(timezoneOffset),
            ZoneId.of("UTC")
        );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Update UI
        final double finalDisplayPressure = displayPressure;
        javafx.application.Platform.runLater(() -> {
          cityNameLabel.setText(cityName + ", " + countryName);
          temperatureLabel.setText(String.format("Temperature: %.1f%s", temperature, isMetric ? "°C" : "°F"));
          humidityLabel.setText("Humidity: " + humidity + "%");
          windSpeedLabel.setText(String.format("Wind Speed: %.1f %s", windSpeed, isMetric ? "m/s" : "mph"));
          pressureLabel.setText("Pressure: " + String.format("%.2f", finalDisplayPressure) + (isMetric ? " hPa" : " inHg"));
          descriptionLabel.setText("Description: " + description);
          accessDateTimeLabel.setText("Local Time: " + localDateTime.format(formatter));
          weatherIcon.setImage(new Image(String.format(ICON_URL, iconCode)));
        });

      } catch (Exception e) {
        e.printStackTrace();
        javafx.application.Platform.runLater(() -> cityNameLabel.setText("Error fetching data!"));
      }
    }).start();
  }

  @FXML
  private void onHistoryButtonClick() {
    // Create a new window (Stage)
    Stage historyStage = new Stage();
    historyStage.setTitle("Search History");

    // Create a ListView to show the history
    ListView<String> listView = new ListView<>();
    listView.getItems().addAll(searchHistory);

    // Create a VBox layout to hold the ListView
    VBox vbox = new VBox(listView);
    vbox.setPadding(new javafx.geometry.Insets(10));
    vbox.setSpacing(8);

    // Create a Scene for the new window and set it on the Stage
    Scene scene = new Scene(vbox, 300, 200);  // Set window size
    historyStage.setScene(scene);

    // Show the window
    historyStage.show();
  }

  private boolean isLightTheme = true;

  @FXML
  private void onThemeButtonClick() {
    Scene scene = themeButton.getScene();
    if (isLightTheme) {
      scene.getStylesheets().clear();
      scene.getStylesheets().add(getClass().getResource("dark-theme.css").toExternalForm());
      themeButton.setText("Dark");
    } else {
      scene.getStylesheets().clear();
      scene.getStylesheets().add(getClass().getResource("light-theme.css").toExternalForm());
      themeButton.setText("Light");
    }
    isLightTheme = !isLightTheme;
  }

  @FXML
  private void onFormatButtonClick() {
    isMetric = !isMetric;
    formatButton.setText(isMetric ? "Metric" : "Imperial");
    searchWeather();
  }

  @FXML
  private void onDetailButtonClick() {
    try {
      String cityName = cityNameLabel.getText();
      String weatherDetails = descriptionLabel.getText();
      String[] forecast = fetchForecastData(cityName);

      FXMLLoader loader = new FXMLLoader(getClass().getResource("detail-view.fxml"));
      Parent detailRoot = loader.load();

      DetailController detailController = loader.getController();
      detailController.setCityData(cityName, weatherDetails, forecast);

      Stage currentStage = (Stage) cityTextField.getScene().getWindow();
      currentStage.setScene(new Scene(detailRoot));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String[] fetchForecastData(String city) {
    String[] forecast = new String[3];
    try {
      String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8.toString());
      String urlString = String.format("https://api.openweathermap.org/data/2.5/forecast?q=%s&appid=%s&units=%s",
          encodedCity, API_KEY, isMetric ? "metric" : "imperial");

      URL url = new URL(urlString);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      StringBuilder response = new StringBuilder();
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();

      JSONObject jsonResponse = new JSONObject(response.toString());
      for (int i = 0; i < 3; i++) {
        JSONObject dayData = jsonResponse.getJSONArray("list").getJSONObject(i * 8);
        String date = dayData.getString("dt_txt");
        double temp = dayData.getJSONObject("main").getDouble("temp");
        String description = dayData.getJSONArray("weather").getJSONObject(0).getString("description");
        forecast[i] = String.format("%s: %s, %.1f%s", date, description, temp, isMetric ? "°C" : "°F");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return forecast;
  }
}