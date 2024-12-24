package dinhnhatbao.weatherforecast;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DetailController {

  @FXML
  private Label cityLabel;

  @FXML
  private Label weatherDetailLabel;

  @FXML
  private Label day1Label;

  @FXML
  private Label day2Label;

  @FXML
  private Label day3Label;

  @FXML
  private LineChart<String, Number> weatherChart;

  @FXML
  private void onBackButtonClick() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("wf-view.fxml"));
      Parent mainRoot = loader.load();
      Stage currentStage = (Stage) cityLabel.getScene().getWindow();
      currentStage.setScene(new Scene(mainRoot));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setCityData(String cityName, String weatherDetails, String[] forecast) {
    cityLabel.setText(cityName);
    weatherDetailLabel.setText(weatherDetails);

    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    day1Label.setText("Day 1: " + formatForecast(forecast[0], inputFormatter, outputFormatter));
    day2Label.setText("Day 2: " + formatForecast(forecast[1], inputFormatter, outputFormatter));
    day3Label.setText("Day 3: " + formatForecast(forecast[2], inputFormatter, outputFormatter));

    updateWeatherChart(forecast);
  }

  private String formatForecast(String forecast, DateTimeFormatter inputFormatter, DateTimeFormatter outputFormatter) {
    String[] parts = forecast.split(": ");
    LocalDate date = LocalDate.parse(parts[0], inputFormatter);
    return date.format(outputFormatter) + ": " + parts[1];
  }

  private void updateWeatherChart(String[] forecast) {
    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.setName("Temperature Forecast");

    for (int i = 0; i < forecast.length; i++) {
      String[] parts = forecast[i].split(": ");
      String day = "Day " + (i + 1);
      double temperature = Double.parseDouble(parts[1].split(", ")[1].replace("°C", "").replace("°F", ""));
      series.getData().add(new XYChart.Data<>(day, temperature));
    }

    weatherChart.getData().clear();
    weatherChart.getData().add(series);
  }
}