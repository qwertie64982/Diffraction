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

    private double slitSeparation;
    private Color color;
    private double slitWidth;
    private double distance;

    // TODO: Make it so when the user enters a number too big/small into the TextFields, they move the slider to the max/min values

    public Controller() {
        // nothing here for now
    }

    @FXML public void initialize() {
        slitSeparationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double newSlitSeparation = slitSeparationSlider.getValue();
            updateSlitSeparation(newSlitSeparation);
        });

        wavelengthSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double newWavelength = wavelengthSlider.getValue();
            updateColor(wavelengthToColor(newWavelength));
        });

        slitWidthSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double newSlitWidth = slitWidthSlider.getValue();
            updateSlitWidth(newSlitWidth);
        });

        distanceSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double newDistance = distanceSlider.getValue();
            updateDistance(newDistance);
        });
    }

    @FXML private void handleSlitSeparationButton() {
        try {
            double newSlitSeparation = Double.parseDouble(slitSeparationTextField.getText());
            if (newSlitSeparation >= 0 && newSlitSeparation <= 10) {
                updateSlitSeparation(newSlitSeparation);
                slitSeparationSlider.setValue(newSlitSeparation);
            }
        } catch (NumberFormatException e) {
            // Ignore
        }
    }

    private void updateSlitSeparation(double newSlitSeparation) {
        slitSeparation = newSlitSeparation;
        updateGradient();
        updateGraph();
        updateImages();
    }

    @FXML private void handleWavelengthButton() {
        try {
            double newWavelength = Double.parseDouble(wavelengthTextField.getText());
            if (newWavelength >= 400 && newWavelength <= 700) {
                updateColor(wavelengthToColor(newWavelength));
                wavelengthSlider.setValue(newWavelength);
            }
        } catch (NumberFormatException e) {
            // Ignore
        }
    }

    private void updateColor(Color newColor) {
        color = newColor;
        updateGradient();
        updateGraph();
        updateImages();
    }

    @FXML private void handleSlitWidthButton() {
        try {
            double newSlitWidth = Double.parseDouble(slitWidthTextField.getText());
            if (newSlitWidth >= 0.5 && newSlitWidth <= 3) {
                updateSlitWidth(newSlitWidth);
                slitWidthSlider.setValue(newSlitWidth);
            }
        } catch (NumberFormatException e) {
            // Ignore
        }
    }

    private void updateSlitWidth(double newSlitWidth) {
        slitWidth = newSlitWidth;
        updateGradient();
        updateGraph();
        updateImages();
    }

    @FXML private void handleDistanceButton() {
        try {
            double newDistance = Double.parseDouble(distanceTextField.getText());
            if (newDistance >= 500 && newDistance <= 1000) {
                updateDistance(newDistance);
                distanceSlider.setValue(newDistance);
            }
        } catch (NumberFormatException e) {
            // Ignore
        }
    }

    private void updateDistance(double newDistance) {
        distance = newDistance;
        updateGradient();
        updateGraph();
        updateImages();
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

    private void updateGradient() {
        // TODO
    }

    private void updateGraph() {
        // TODO
    }

    private void updateImages() {
        // TODO
    }
}
