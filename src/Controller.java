import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class Controller {
    @FXML private Slider wavelengthSlider;
    @FXML private Slider slitWidthSlider;
    @FXML private Slider distanceSlider;
    @FXML private Slider slitSeparationSlider;
    @FXML private TextField wavelengthTextField;
    @FXML private TextField slitWidthTextField;
    @FXML private TextField distanceTextField;
    @FXML private TextField slitSeparationTextField;

    // TODO: Make it so when the user enters a number too big/small into the TextFields, they move the slider to the max/min values

    public Controller() {
        // nothing here for now
    }

    @FXML public void initialize() {
        wavelengthSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double wavelength = wavelengthSlider.getValue();
            updateColor(wavelengthToColor(wavelength));
        });

        slitWidthSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double slitWidth = slitWidthSlider.getValue();
            updateSlitWidth(slitWidth);
        });
    }

    @FXML private void handleSlitSeparationButton() {
        try {
            double slitSeparation = Double.parseDouble(slitSeparationTextField.getText());
            if (slitSeparation >= 0 && slitSeparation <= 10) {
                updateSlitSeparation(slitSeparation);
                slitSeparationSlider.setValue(slitSeparation);
            }
        } catch (NumberFormatException e) {
            // Ignore
        }
    }

    private void updateSlitSeparation(double newSlitSeparation) {
        // TODO: Update the UI with this new slit separation
        // graph, gradients, etc
    }

    @FXML private void handleWavelengthButton() {
        try {
            double wavelength = Double.parseDouble(wavelengthTextField.getText());
            if (wavelength >= 400 && wavelength <= 700) {
                updateColor(wavelengthToColor(wavelength));
                wavelengthSlider.setValue(wavelength);
            }
        } catch (NumberFormatException e) {
            // Ignore
        }
    }

    private void updateColor(Color newColor) {
        // TODO: Update the UI with this new color
        // graph, gradients, etc
    }

    @FXML private void handleSlitWidthButton() {
        try {
            double slitWidth = Double.parseDouble(slitWidthTextField.getText());
            if (slitWidth >= 0.5 && slitWidth <= 3) {
                updateSlitWidth(slitWidth);
                slitWidthSlider.setValue(slitWidth);
            }
        } catch (NumberFormatException e) {
            // Ignore
        }
    }

    private void updateSlitWidth(double newSlitWidth) {
        // TODO: Update the UI with this new slit width
        // graph, gradients, etc
    }

    @FXML private void handleDistanceButton() {
        try {
            double distance = Double.parseDouble(distanceTextField.getText());
            if (distance >= 500 && distance <= 1000) {
                updateDistance(distance);
                distanceSlider.setValue(distance);
            }
        } catch (NumberFormatException e) {
            // Ignore
        }
    }

    private void updateDistance(double distance) {
        // TODO: Update the UI with this new distance
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
