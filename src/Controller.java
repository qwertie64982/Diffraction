import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;

public class Controller {
    @FXML
    private Slider wavelengthSlider;

    public Controller() {
        // nothing here for now
    }

    @FXML
    public void initialize() {
        wavelengthSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double wavelength = wavelengthSlider.getValue();
            updateColor(wavelengthToColor(wavelength));
        });
    }

    private void updateColor(Color newColor) {
        // TODO: Update the UI with this new color
        // graph, gradients, etc
    }

    private Color wavelengthToColor(double wavelength) {
        // Credit for algorithm: http://www.physics.sfasu.edu/astro/color/spectra.html
        // FORTRAN is an adventure

        double red, green, blue;
        double opacity = 1;

        if (wavelength >= 380 && wavelength < 440) {
            red = -(wavelength - 440) / (440-380);
            green = 0;
            blue = 1;
        } else if (wavelength >= 440 && wavelength < 490) {
            red = 0;
            green = (wavelength - 440) / (490-440);
            blue = 1;
        } else if (wavelength >= 490 && wavelength < 510) {
            red = 0;
            green = 1;
            blue = -(wavelength - 510) / (510 - 490);
        } else if (wavelength >= 510 && wavelength < 580) {
            red = (wavelength - 510) / (580 - 510);
            green = 1;
            blue = 0;
        } else if (wavelength >= 580 && wavelength < 645) {
            red = 1;
            green = -(wavelength - 645) / (645 - 580);
            blue = 0;
        } else if (wavelength >= 645 && wavelength < 781) {
            red = 1;
            green = 0;
            blue = 0;
        } else {
            red = 0;
            green = 0;
            blue = 0;
            opacity = 0;
        }
        return new Color(red, green, blue, opacity);
    }
}
