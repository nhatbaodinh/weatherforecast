module dinhnhatbao.weatherforecast {
  requires javafx.controls;
  requires javafx.fxml;
  requires org.json;

  requires org.controlsfx.controls;
  requires org.kordamp.bootstrapfx.core;

  opens dinhnhatbao.weatherforecast to javafx.fxml;
  exports dinhnhatbao.weatherforecast;
}