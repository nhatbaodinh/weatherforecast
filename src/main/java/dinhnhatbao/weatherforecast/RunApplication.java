package dinhnhatbao.weatherforecast;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RunApplication extends Application {
  @Override
  public void start(Stage stage) throws IOException {
    // Tải FXML và tạo Scene
    FXMLLoader fxmlLoader = new FXMLLoader(RunApplication.class.getResource("wf-view.fxml"));
    Scene scene = new Scene(fxmlLoader.load());

    // Áp dụng Light Theme mặc định
    scene.getStylesheets().add(getClass().getResource("light-theme.css").toExternalForm());

    // Đặt tiêu đề và Scene cho Stage
    stage.setTitle("Weather Forecast");
    stage.setScene(scene);
    stage.show();
    stage.toFront();
  }

  public static void main(String[] args) {
    launch();
  }
}